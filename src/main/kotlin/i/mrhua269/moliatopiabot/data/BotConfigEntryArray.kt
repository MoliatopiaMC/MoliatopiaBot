package i.mrhua269.moliatopiabot.data

import i.mrhua269.moliatopiabot.bot.BotConfigEntry
import com.google.gson.Gson

@Deprecated("Unsupported currently")
class BotConfigEntryArray(
    private val entries: Array<i.mrhua269.moliatopiabot.bot.BotConfigEntry>
) {
    override fun toString(): String {
        return GSON.toJson(this)
    }

    fun getConfigEntries(): Array<i.mrhua269.moliatopiabot.bot.BotConfigEntry> {
        return this.entries
    }

    companion object {
        private val GSON = Gson()
    }
}