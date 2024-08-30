package i.mrhua269.moliatopiabot.manager

import com.google.gson.GsonBuilder
import i.mrhua269.moliatopiabot.Bootstrapper
import i.mrhua269.moliatopiabot.data.DataFile
import org.apache.logging.log4j.LogManager
import java.io.File
import java.nio.file.Files

object DataManager {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val logger = LogManager.getLogger(this::class.java)
    private val dataFile = File(Bootstrapper.BASE_DIR, "botdata.json")
    private var currentDataFile = DataFile(0)


    fun initOrRead() {
        this.logger.info("Reading data file")

        if (this.dataFile.exists()) {
            val readBytes: ByteArray = Files.readAllBytes(this.dataFile.toPath())
            this.currentDataFile = this.gson.fromJson(String(readBytes), DataFile::class.java)
            this.logger.info("Data file read!")
            return
        }

        val bytes: ByteArray = this.currentDataFile.toString().toByteArray()
        Files.write(this.dataFile.toPath(), bytes)
        logger.info("Data file created!")
    }

    fun save() {
        val bytes: ByteArray = this.currentDataFile.toString().toByteArray()
        Files.write(this.dataFile.toPath(), bytes)
    }

    fun getReadData(): DataFile {
        return this.currentDataFile
    }
}