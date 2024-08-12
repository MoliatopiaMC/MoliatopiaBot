package i.mrhua269.moliatopiabot.bot.impl

import i.mrhua269.moliatopiabot.bot.BotEntry
import i.mrhua269.moliatopiabot.eventsystem.EventHub
import net.mamoe.mirai.event.Event

class BotImpl : BotEntry() {

    override fun processEvent(event: Event) {
        EventHub.callEventAsync(event)
    }
}