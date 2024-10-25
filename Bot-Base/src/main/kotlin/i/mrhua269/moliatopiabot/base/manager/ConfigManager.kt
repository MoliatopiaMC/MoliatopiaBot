package i.mrhua269.moliatopiabot.base.manager

import com.google.gson.GsonBuilder
import i.mrhua269.moliatopiabot.Bootstrapper
import i.mrhua269.moliatopiabot.base.data.ConfigFile
import org.apache.logging.log4j.LogManager
import java.io.File
import java.nio.file.Files

object ConfigManager {
    private var configFile = ConfigFile()
    private val configFileEntry = File(Bootstrapper.BASE_DIR, "config.json")
    private val logger = LogManager.getLogger(this::class.java)
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun initConfig() {
        if (configFileEntry.exists()) {
            val readBytes = Files.readAllBytes(configFileEntry.toPath())
            configFile = gson.fromJson(String(readBytes), ConfigFile::class.java)
            logger.info("Read config file!")
            return
        }

        logger.info("Creating config file")
        val bytes = configFile.toString().toByteArray()
        Files.write(configFileEntry.toPath(), bytes)
    }

    fun getReadConfig(): ConfigFile {
        return configFile
    }
}