package i.mrhua269.moliatopiabot.eventsystem

import net.mamoe.mirai.event.Event

interface Listener {
    fun onEvent(event : Event): Boolean

    fun listenerName(): String
}