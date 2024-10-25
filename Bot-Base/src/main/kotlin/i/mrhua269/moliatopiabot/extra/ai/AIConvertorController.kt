package i.mrhua269.moliatopiabot.extra.ai

import i.mrhua269.moliatopiabot.base.event.EventHub
import i.mrhua269.moliatopiabot.extra.ai.OpenAIAPIRequester.Companion.toMemoryInstance
import i.mrhua269.moliatopiabot.extra.ai.aistorage.AIMemoryStorage
import i.mrhua269.moliatopiabot.base.manager.ConfigManager
import i.mrhua269.moliatopiabot.extra.utils.RateLimiter
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

object AIConvertorController {
    private val rateLimitNs = ConfigManager.getReadConfig().getComputedSpeedLimit()
    private val rateLimitUnit = TimeUnit.NANOSECONDS

    private val conversationLocks = ConcurrentHashMap<String, Any>()

    private val delayedHubExecutor = CompletableFuture.delayedExecutor(rateLimitNs + 500, rateLimitUnit, EventHub.getWorkerExecutor()) //Offset 500ns

    private val conversationInboundLimiters = ConcurrentHashMap<String, RateLimiter>()
    private val conversationOutboundLimiters = ConcurrentHashMap<String, RateLimiter>()

    fun converse(userId: String, content: String): CompletableFuture<OpenAIAPIRequester.MemoryInstance?>{
        val inboundLimiter = conversationInboundLimiters.computeIfAbsent(userId) { RateLimiter(rateLimitNs, rateLimitUnit) }
        val outboundLimiter = conversationOutboundLimiters.computeIfAbsent(userId) { RateLimiter(rateLimitNs, rateLimitUnit) }
        val future = CompletableFuture<OpenAIAPIRequester.MemoryInstance?>()

        if (!inboundLimiter.canPass()) {
            future.complete(null)
        }

        EventHub.getWorkerExecutor().execute {
            val response = runBlocking { converseInternal(userId, content) }

            if (outboundLimiter.canPass()) {
                future.complete(response)
                return@execute
            }

            delayedHubExecutor.execute {
                future.complete(response)
            }
        }

        return future
    }

    private suspend fun converseInternal(userId: String, content: String): OpenAIAPIRequester.MemoryInstance {
        val lock = conversationLocks.computeIfAbsent(userId) { Any() }

        synchronized(lock) {
            return runBlocking {
                val memories = AIMemoryStorage.getProcessedContext(userId) as MutableList<OpenAIAPIRequester.MemoryInstance>

                if (memories.isEmpty() && ConfigManager.getReadConfig().systemPrompt.isNotBlank()){
                    val newSystemMemory =
                        OpenAIAPIRequester.MemoryInstance("system", ConfigManager.getReadConfig().systemPrompt)

                    memories.add(newSystemMemory)
                    AIMemoryStorage.logToMemory(userId, newSystemMemory.role, newSystemMemory.content)
                }

                val newMemory = OpenAIAPIRequester.MemoryInstance("user", content)

                AIMemoryStorage.logToMemory(userId, newMemory.role, newMemory.content)

                memories.add(newMemory)

                val response = OpenAIAPIRequester(ConfigManager.getReadConfig().aiAPIToken, ConfigManager.getReadConfig().aiAPIUrl).requestAPI(memories, ConfigManager.getReadConfig().aiModel)
                val newMemory2 = response.toMemoryInstance()

                AIMemoryStorage.logToMemory(userId, newMemory2.role, newMemory2.content)

                return@runBlocking response.toMemoryInstance()
            }
        }
    }

}