package i.mrhua269.moliatopiabot.manager

import i.mrhua269.moliatopiabot.Bootstrapper
import i.mrhua269.moliatopiabot.bot.BotConfigEntry
import i.mrhua269.moliatopiabot.bot.BotEntry
import com.google.gson.Gson
import i.mrhua269.moliatopiabot.bot.impl.BotImpl
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.nio.file.Files
import java.util.*
import kotlin.system.exitProcess

object BotManager {
    private val configArrayFile: File = File(Bootstrapper.BASE_DIR, "bot.json")
    private val loadedBots: MutableList<BotEntry> = Collections.synchronizedList(ArrayList())
    private lateinit var currentConfig: BotConfigEntry
    private val gson: Gson = Gson()
    private val logger: Logger = LogManager.getLogger()

    fun readConfig() {
        if (this.configArrayFile.exists()) {
            val readBytes: ByteArray = Files.readAllBytes(this.configArrayFile.toPath())
            this.currentConfig = this.gson.fromJson(String(readBytes), BotConfigEntry::class.java)
            this.logger.info("Bot config loaded!")
        } else {
            this.currentConfig = BotConfigEntry(
                "none",
                "ws://192.168.5.227:3001",
                true
            )

            val bytes: ByteArray = this.currentConfig.toString().toByteArray()
            Files.write(this.configArrayFile.toPath(), bytes)
            this.logger.info(
                "Please complete your config and start the bot again.Config file:{}",
                this.configArrayFile.toPath()
            )
            exitProcess(0)
        }
    }

    fun shutdownAllBot() {
        for (bot in this.loadedBots){
            bot.bot?.close()
        }
    }

    fun initAllBot() {
        this.logger.info("Init bot")

        runBlocking {
            val wrappedBot = BotImpl()
            wrappedBot.runBot(currentConfig)
            loadedBots.add(wrappedBot)
        }

        this.logger.info("Inited {} bots", this.loadedBots.size)
    }
}