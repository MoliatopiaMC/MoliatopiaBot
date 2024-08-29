package i.mrhua269.moliatopiabot.bot


import com.google.gson.Gson
import com.google.gson.GsonBuilder


class BotConfigEntry(
    private val token: String = "none",
    private val websocketUrl: String = "ws://127.0.0.1:3001",
    private val enableBotLog: Boolean = false
) {
    fun getWebsocketUrl(): String {
        return this.websocketUrl
    }

    fun getEnableBotLog(): Boolean{
        return this.enableBotLog
    }

    fun getToken(): String {
        return this.token
    }

    override fun toString(): String {
        return GSON.toJson(this)
    }

    companion object {
        private val GSON: Gson = GsonBuilder().setPrettyPrinting().create()
    }
}