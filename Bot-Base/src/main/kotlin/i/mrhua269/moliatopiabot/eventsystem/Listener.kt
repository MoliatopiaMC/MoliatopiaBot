package i.mrhua269.moliatopiabot.eventsystem

import net.mamoe.mirai.event.Event

interface Listener {
    suspend fun onEvent(event : Event): Boolean

    fun onLoaded() {}

    fun listenerName(): String
}