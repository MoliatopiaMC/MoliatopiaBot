package i.mrhua269.moliatopiabot.eventsystem

import i.mrhua269.moliatopiabot.utils.PackagedDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.mamoe.mirai.event.Event
import org.apache.logging.log4j.LogManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object EventHub {
    private val logger = LogManager.getLogger()
    private val workerExecutor = Executors.newCachedThreadPool()
    private val dispatcher = PackagedDispatcher(this.workerExecutor)
    private val listeners: MutableSet<Listener> = ConcurrentHashMap.newKeySet()

    fun getDispatcher(): CoroutineDispatcher {
        return this.dispatcher
    }

    fun notifyAllLoaded() {
        for (listener : Listener in this.listeners){
            try {
                listener.onLoaded()
            }catch (ex : Exception){
                logger.error("Failed to process event!,Exception:",ex)
            }
        }
    }

    private suspend fun callEventInternal(
        event: Event,
        listenersArray: Array<Listener> = this.listeners.toTypedArray(),
        listenerIdx: Int = 0
    ) {
        withContext(this.dispatcher) {
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
        this.callEventInternal(event)
    }

    fun register(listener: Listener){
        this.listeners.add(listener)
    }

    fun deRegister(listener: Listener){
        this.listeners.remove(listener)
    }

    fun deRegister(name: String){
        var listenerToRemove: Listener? = null

        for (listener : Listener in this.listeners){
            if (listener.listenerName() == name){
                listenerToRemove = listener
            }
            break
        }

        listenerToRemove?.let {
            this.listeners.remove(it)
        }
    }

    fun shutdownDispatcher(){
        this.workerExecutor.shutdown()
        this.workerExecutor.awaitTermination(3, TimeUnit.SECONDS)
    }
}