package i.mrhua269.moliatopiabot.extra

import i.mrhua269.moliatopiabot.extra.OpenAIAPIRequester.Companion.toMemoryInstance
import i.mrhua269.moliatopiabot.extra.aistorage.AIMemoryStorage
import i.mrhua269.moliatopiabot.manager.ConfigManager
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap

object AIConvertor {
    private val conversationLocks = ConcurrentHashMap<String, Any>()

    suspend fun converse(userId: String, content: String): OpenAIAPIRequester.MemoryInstance {
        val lock = conversationLocks.computeIfAbsent(userId) { Any() }

        synchronized(lock) {
            return runBlocking {
                val memories = AIMemoryStorage.getProcessedContext(userId) as MutableList<OpenAIAPIRequester.MemoryInstance>
                if (memories.isEmpty() && ConfigManager.getReadConfig().systemPrompt.isNotBlank()){
                    val newSystemMemory = OpenAIAPIRequester.MemoryInstance("system", ConfigManager.getReadConfig().systemPrompt)

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