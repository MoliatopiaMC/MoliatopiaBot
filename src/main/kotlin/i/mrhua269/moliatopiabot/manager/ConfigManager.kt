package i.mrhua269.moliatopiabot.manager

import i.mrhua269.moliatopiabot.Bootstrapper
import com.google.gson.Gson
import i.mrhua269.moliatopiabot.data.ConfigFile
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.nio.file.Files

object ConfigManager {
    private var configFile: ConfigFile = ConfigFile(
        114514,
        false,
        "114.5.1.4",
        11451
    )
    private val configFileEntry: File = File(Bootstrapper.BASE_DIR, "config.json")
    private val logger: Logger = LogManager.getLogger()
    private val gson: Gson = Gson()

    fun initConfig() {
        if (this.configFileEntry.exists()) {
            val readBytes: ByteArray = Files.readAllBytes(this.configFileEntry.toPath())
            this.configFile = this.gson.fromJson(String(readBytes), ConfigFile::class.java)
            this.logger.info("Read config file!")
            return
        }

        this.logger.info("Creating config file")
        val bytes: ByteArray = this.configFile.toString().toByteArray()
        Files.write(this.configFileEntry.toPath(), bytes)
    }

    fun getReadConfig(): ConfigFile {
        return this.configFile
    }
}