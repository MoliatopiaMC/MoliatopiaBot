package i.mrhua269.moliatopiabot.extra.aistorage

import i.mrhua269.moliatopiabot.Bootstrapper
import i.mrhua269.moliatopiabot.extra.OpenAIAPIRequester
import i.mrhua269.moliatopiabot.extra.aistorage.impl.DefaultBotMemoryCleaner
import i.mrhua269.moliatopiabot.extra.aistorage.impl.DefaultBotMemoryCompressor
import i.mrhua269.moliatopiabot.extra.cesiumstorage.api.database.DatabaseSpec
import i.mrhua269.moliatopiabot.extra.cesiumstorage.common.lmdb.LMDBInstance
import i.mrhua269.moliatopiabot.manager.ConfigManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

object AIMemoryStorage {
    private val memoryDatabaseSpec = DatabaseSpec(
        "memory",
        String::class.java,
        Array<OpenAIAPIRequester.MemoryInstance>::class.java,
        8 * 1024
    )
    private val memoryDatabase = LMDBInstance(
        Bootstrapper.BASE_DIR.toPath(),
        "memory",
        arrayOf(memoryDatabaseSpec)
    )
    private val saveScheduler = Executors.newScheduledThreadPool(1)
    @Volatile
    private var closed: Boolean = false
    @Volatile
    private var scheduledSaveTask: ScheduledFuture<*>? = null

    private val memoryCompressor = DefaultBotMemoryCompressor()
    private val memoryCleaner = DefaultBotMemoryCleaner()

    init {
        scheduledSaveTask = saveScheduler.scheduleAtFixedRate({
            if (closed){
                return@scheduleAtFixedRate
            }

            memoryDatabase.flushChanges()
        }, ConfigManager.getReadConfig().aiDatabaseSaveIntervalSeconds.toLong(), ConfigManager.getReadConfig().aiDatabaseSaveIntervalSeconds.toLong(), TimeUnit.SECONDS)
    }

    suspend fun getProcessedContext(userId: String): List<OpenAIAPIRequester.MemoryInstance>{
        val actuallyData = memoryDatabase.getDatabase(memoryDatabaseSpec).getValue(userId) ?: return arrayListOf()

        val cleaner = getMemoryCleaner()
        val cleaned = cleaner.cleanMemory(arrayListOf(*actuallyData))
        val compressed = ArrayList(getMemoryCompressor().compressMemory(cleaned))

        return compressed
    }

    suspend fun logToMemory(userId: String, role: String, content: String){
        val data = memoryDatabase.getDatabase(memoryDatabaseSpec).getValue(userId)
        val dataList = if (data == null){
            arrayListOf()
        }else{
            arrayListOf(*data)
        }

        dataList.add(OpenAIAPIRequester.MemoryInstance(role, content))

        memoryDatabase.getTransaction(memoryDatabaseSpec).add(userId, dataList.toTypedArray())

        withContext(Dispatchers.IO){
            memoryDatabase.flushChanges()
        }
    }

    private fun getMemoryCleaner(): IAIMemoryCleaner {
        return memoryCleaner
    }

    private fun getMemoryCompressor(): IAIMemoryCompressor {
        return memoryCompressor
    }

    fun close() {
        closed = true
        scheduledSaveTask?.cancel(true)

        memoryDatabase.flushChanges()
        memoryDatabase.close()
    }
}