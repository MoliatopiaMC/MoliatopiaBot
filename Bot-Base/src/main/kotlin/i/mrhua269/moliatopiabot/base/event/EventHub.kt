package i.mrhua269.moliatopiabot.base.event

import i.mrhua269.moliatopiabot.base.utils.PackagedDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.mamoe.mirai.event.Event
import org.apache.logging.log4j.LogManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object EventHub {
    private val logger = LogManager.getLogger(this::class.java)
    private val workerExecutor = Executors.newCachedThreadPool()
    private val dispatcher = PackagedDispatcher(workerExecutor)
    private val listeners: MutableSet<Listener> = ConcurrentHashMap.newKeySet()

    fun getDispatcher(): CoroutineDispatcher {
        return dispatcher
    }

    fun getWorkerExecutor(): ExecutorService {
        return workerExecutor
    }

    fun notifyAllLoaded() {
        for (listener : Listener in listeners){
            try {
                listener.onLoaded()
            }catch (ex : Exception){
                logger.error("Failed to process event!,Exception:",ex)
            }
        }
    }

    private suspend fun callEventInternal(
        event: Event,
        listenersArray: Array<Listener> = listeners.toTypedArray(),
        listenerIdx: Int = 0
    ) {
        withContext(dispatcher) {
            val targetListener = listenersArray[listenerIdx]
            var shouldFireNext = true

            try {
                shouldFireNext = targetListener.onEvent(event)
            } catch (e: Exception) {
                logger.error("Failed to process event!,Exception:", e)
            }

            if (shouldFireNext && listenerIdx < listenersArray.lastIndex) {
                withContext(dispatcher) {
                    callEventInternal(event, listenersArray, listenerIdx + 1)
                }
            }
        }
    }

    suspend fun callEvent(event : Event){
        callEventInternal(event)
    }

    fun register(listener: Listener){
        listeners.add(listener)
    }

    fun deRegister(listener: Listener){
        listeners.remove(listener)
    }

    fun deRegister(name: String){
        var listenerToRemove: Listener? = null

        for (listener : Listener in listeners){
            if (listener.listenerName() == name){
                listenerToRemove = listener
            }
            break
        }

        listenerToRemove?.let {
            listeners.remove(it)
        }
    }

    fun shutdownDispatcher(){
        workerExecutor.shutdown()
        workerExecutor.awaitTermination(3, TimeUnit.SECONDS)
    }
}