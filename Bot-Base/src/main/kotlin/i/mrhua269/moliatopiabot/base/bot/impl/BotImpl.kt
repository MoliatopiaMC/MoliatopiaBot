package i.mrhua269.moliatopiabot.base.bot.impl

import i.mrhua269.moliatopiabot.base.bot.BotEntry
import i.mrhua269.moliatopiabot.base.event.EventHub
import net.mamoe.mirai.event.Event

class BotImpl : BotEntry() {
    override suspend fun processEvent(event: Event) {
        EventHub.callEvent(event)
    }
}