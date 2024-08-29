package i.mrhua269.moliatopiabot.manager

import com.google.gson.GsonBuilder
import i.mrhua269.moliatopiabot.Bootstrapper
import i.mrhua269.moliatopiabot.bot.BotConfigEntry
import i.mrhua269.moliatopiabot.bot.BotEntry
import i.mrhua269.moliatopiabot.bot.impl.BotImpl
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import org.apache.logging.log4j.LogManager
import java.io.File
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer
import kotlin.system.exitProcess

object BotManager {
    private val configArrayFile = File(Bootstrapper.BASE_DIR, "bot.json")
    private val loadedBots: MutableSet<BotEntry> = ConcurrentHashMap.newKeySet()
    private lateinit var currentConfig: BotConfigEntry
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val logger = LogManager.getLogger()

    fun readConfig() {
        if (this.configArrayFile.exists()) {
            val readBytes: ByteArray = Files.readAllBytes(this.configArrayFile.toPath())
            this.currentConfig = this.gson.fromJson(String(readBytes), BotConfigEntry::class.java)
            this.logger.info("Bot config loaded!")
        } else {
            this.currentConfig = BotConfigEntry()

            val bytes: ByteArray = this.currentConfig.toString().toByteArray()
            Files.write(this.configArrayFile.toPath(), bytes)
            this.logger.info(
                "Please complete your config and start the bot again.Config file:{}",
                this.configArrayFile.toPath()
            )

            exitProcess(0)
        }
    }

    fun forEachAllBot(act: Consumer<Bot>) {
        for (bot in this.loadedBots){
            act.accept(bot.bot!!)
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
            //TODO Multi bot support
            val wrappedBot = BotImpl()

            wrappedBot.runBot(currentConfig)

            loadedBots.add(wrappedBot)
        }

        this.logger.info("Inited {} bots", this.loadedBots.size)
    }
}