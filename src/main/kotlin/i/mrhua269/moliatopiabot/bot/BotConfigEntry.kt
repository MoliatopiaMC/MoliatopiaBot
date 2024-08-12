package i.mrhua269.moliatopiabot.bot


import com.google.gson.Gson


class BotConfigEntry(
    private val token: String,
    private val websocketUrl: String,
    private val enableBotLog: Boolean
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
        private val GSON: Gson = Gson()
    }
}