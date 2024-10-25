package i.mrhua269.moliatopiabot.base.event

import net.mamoe.mirai.event.Event

interface Listener {
    suspend fun onEvent(event : Event): Boolean

    fun onLoaded() {}

    fun listenerName(): String
}