package i.mrhua269.moliatopiabot.eventsystem

import net.mamoe.mirai.event.Event
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object EventHub {
    private val logger : Logger = LogManager.getLogger()
    private val dispatcher : ExecutorService = Executors.newCachedThreadPool()
    private val listeners : MutableSet<Listener> = ConcurrentHashMap.newKeySet()

    fun callEventSync(event : Event) : Boolean{
        var flag = true
        for (listener : Listener in this.listeners){
            var shouldCancel = false
            try {
                shouldCancel = listener.onEvent(event)
            }catch (ex : Exception){
                logger.error("Failed to process event!,Exception:",ex)
                ex.printStackTrace()
            }

            if (!shouldCancel){
                flag = false
                break
            }
        }
        return flag
    }

    fun getDispatcher() : Executor{
        return this.dispatcher
    }

    fun callEventAsync(event : Event){
        this.dispatcher.execute{
            for (listener : Listener in this.listeners){
                try {
                    listener.onEvent(event)
                }catch (ex : Exception){
                    logger.error("Failed to process event!,Exception:",ex)
                    ex.printStackTrace()
                }
            }
        }
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
        this.dispatcher.shutdown()
        this.dispatcher.awaitTermination(3,TimeUnit.SECONDS)
    }
}