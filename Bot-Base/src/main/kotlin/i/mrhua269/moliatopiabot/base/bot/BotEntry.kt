package i.mrhua269.moliatopiabot.base.bot

import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.Event
import org.apache.logging.log4j.LogManager
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

        val botBuilderPre = configEntry.getToken().let {
            if (it == "none"){
                return@let BotBuilder.positive(configEntry.getWebsocketUrl())
            }

            return@let BotBuilder.positive(configEntry.getWebsocketUrl()).token(configEntry.getToken())
        }

        this.bot = configEntry.getEnableBotLog().let {
            if (it){
                return@let botBuilderPre.connect()
            }

            botBuilderPre.modifyBotConfiguration { configuration ->
                configuration.noBotLog()
                configuration.noNetworkLog()
            }

            return@let botBuilderPre.noPrintInfo().connect()
        }

        this.bot!!.eventChannel.subscribeAlways<Event> { event ->
            processEvent(event)
        }

        this.connected.set(true)
    }

    fun getConfigEntry(): BotConfigEntry? {
        return this.configEntry
    }

    abstract suspend fun processEvent(event: Event)

    fun isConnected(): Boolean {
        return this.connected.get()
    }

    val currentQid: Long? get() = this.bot?.id
}