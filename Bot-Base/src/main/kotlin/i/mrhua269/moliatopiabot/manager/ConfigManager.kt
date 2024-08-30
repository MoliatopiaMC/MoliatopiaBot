package i.mrhua269.moliatopiabot.manager

import com.google.gson.GsonBuilder
import i.mrhua269.moliatopiabot.Bootstrapper
import i.mrhua269.moliatopiabot.data.ConfigFile
import org.apache.logging.log4j.LogManager
import java.io.File
import java.nio.file.Files

object ConfigManager {
    private var configFile = ConfigFile()
    private val configFileEntry = File(Bootstrapper.BASE_DIR, "config.json")
    private val logger = LogManager.getLogger(this::class.java)
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun initConfig() {
        if (this.configFileEntry.exists()) {
            val readBytes = Files.readAllBytes(this.configFileEntry.toPath())
            this.configFile = this.gson.fromJson(String(readBytes), ConfigFile::class.java)
            this.logger.info("Read config file!")
            return
        }

        this.logger.info("Creating config file")
        val bytes = this.configFile.toString().toByteArray()
        Files.write(this.configFileEntry.toPath(), bytes)
    }

    fun getReadConfig(): ConfigFile {
        return this.configFile
    }
}