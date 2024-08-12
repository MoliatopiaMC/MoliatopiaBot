package i.mrhua269.moliatopiabot.manager

import i.mrhua269.moliatopiabot.Bootstrapper
import com.google.gson.Gson
import i.mrhua269.moliatopiabot.data.DataFile
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.nio.file.Files

object DataManager {
    private val gson: Gson = Gson()
    private val logger: Logger = LogManager.getLogger()
    private val dataFile: File = File(Bootstrapper.BASE_DIR, "botdata.json")
    private var currentDataFile: DataFile = DataFile(0)


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