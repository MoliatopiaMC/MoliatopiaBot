package i.mrhua269.moliatopiabot.command

import i.mrhua269.moliatopiabot.eventsystem.Listener
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import org.apache.logging.log4j.LogManager
import java.util.concurrent.atomic.AtomicReference

object CommandParser : Listener {
    private val logger = LogManager.getLogger(this::class.java)
    private val currListener = AtomicReference<Bot>()

    private suspend fun processEvent(event: Event) {
        if (event is MessageEvent) {
            if (event is GroupMessageEvent){
                if (currListener.get() == null || !currListener.get().isOnline) {
                    currListener.set(event.bot)
                }

                if (currListener.get().equals(event.bot)) {
                    fireProcess(event)
                }
            }else {
                fireProcess(event)
            }
        }
    }

    private suspend fun fireProcess(event: MessageEvent) {
        val commandInfo = PackagedCommandInfo(event.message)

        if (commandInfo.getHead() == null) {
            return
        }

        logger.info("Processing command:{}", commandInfo)

        commandInfo.getHead()?.let {
            CommandList.search(it)?.process(commandInfo, event)
        }
    }

    override suspend fun onEvent(event: Event): Boolean {
        processEvent(event)

        return true
    }

    override fun listenerName(): String {
        return "CommandProcessor"
    }
}