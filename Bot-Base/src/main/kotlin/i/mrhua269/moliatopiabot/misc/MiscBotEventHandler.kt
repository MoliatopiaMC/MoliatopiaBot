package i.mrhua269.moliatopiabot.misc

import i.mrhua269.moliatopiabot.base.event.Listener
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import org.apache.logging.log4j.LogManager

object MiscBotEventHandler: Listener {
    private val logger = LogManager.getLogger(this::class.java)

    override suspend fun onEvent(event: Event): Boolean {
        if (event is BotInvitedJoinGroupRequestEvent){
            logger.info("Bot has joined group ${event.groupName}")
            event.accept()
        }

        return true
    }

    override fun listenerName(): String {
        return "misc_bot_handler"
    }
}