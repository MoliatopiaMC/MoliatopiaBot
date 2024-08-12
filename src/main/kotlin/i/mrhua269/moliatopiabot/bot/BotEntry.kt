package i.mrhua269.moliatopiabot.bot

import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.Event
import org.jetbrains.annotations.NotNull
import top.mrxiaom.overflow.BotBuilder
import java.util.concurrent.atomic.AtomicBoolean

abstract class BotEntry {
    @Volatile
    var bot: Bot? = null
        private set
    private var configEntry: BotConfigEntry? = null
    private val connected = AtomicBoolean(false)

    suspend fun runBot(@NotNull configEntry: BotConfigEntry) {
        this.configEntry = configEntry

        this.bot = configEntry.getToken().let {
            if (it == "none"){
                return@let BotBuilder.positive(configEntry.getWebsocketUrl()).connect()
            }

            return@let BotBuilder.positive(configEntry.getWebsocketUrl()).token(configEntry.getToken()).connect()
        }

        if (!configEntry.getEnableBotLog()){
            this.bot!!.configuration.noBotLog()
            this.bot!!.configuration.noNetworkLog()
        }

        this.bot!!.eventChannel.subscribeAlways<Event> { event ->
            processEvent(event)
        }

        this.connected.set(true)
    }

    fun getConfigEntry(): BotConfigEntry? {
        return this.configEntry
    }

    abstract fun processEvent(event: Event)

    fun isConnected(): Boolean {
        return this.connected.get()
    }

    val currentQid: Long? get() = this.bot?.id
}