package i.mrhua269.moliatopiabot.eventsystem.misc

import i.mrhua269.moliatopiabot.eventsystem.Listener
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent

object MiscBotEventHandler: Listener {
    override suspend fun onEvent(event: Event): Boolean {
        if (event is BotInvitedJoinGroupRequestEvent){
            event.accept()
        }

        return true
    }

    override fun listenerName(): String {
        return "MiscBotEventHandler"
    }
}