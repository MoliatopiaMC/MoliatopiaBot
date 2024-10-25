package i.mrhua269.moliatopiabot.base.manager

import com.google.gson.GsonBuilder
import i.mrhua269.moliatopiabot.Bootstrapper
import i.mrhua269.moliatopiabot.base.bot.BotConfigEntry
import i.mrhua269.moliatopiabot.base.bot.BotEntry
import i.mrhua269.moliatopiabot.base.bot.impl.BotImpl
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
    private val logger = LogManager.getLogger(this::class.java)

    fun readConfig() {
        if (configArrayFile.exists()) {
            val readBytes: ByteArray = Files.readAllBytes(configArrayFile.toPath())
            currentConfig = gson.fromJson(String(readBytes), BotConfigEntry::class.java)
            logger.info("Bot config loaded!")
        } else {
            currentConfig = BotConfigEntry()

            val bytes: ByteArray = currentConfig.toString().toByteArray()
            Files.write(configArrayFile.toPath(), bytes)
            logger.info(
                "Please complete your config and start the bot again.Config file:{}",
                configArrayFile.toPath()
            )

            exitProcess(0)
        }
    }

    fun forEachAllBot(act: Consumer<Bot>) {
        for (bot in loadedBots){
            act.accept(bot.bot!!)
        }
    }

    fun shutdownAllBot() {
        for (bot in loadedBots){
            bot.bot?.close()
        }
    }

    fun initAllBot() {
        logger.info("Init bot")

        runBlocking {
            //TODO Multi bot support
            val wrappedBot = BotImpl()

            wrappedBot.runBot(currentConfig)

            loadedBots.add(wrappedBot)
        }

        logger.info("Inited {} bots", loadedBots.size)
    }
}