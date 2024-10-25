package i.mrhua269.moliatopiabot.base.manager

import com.google.gson.GsonBuilder
import i.mrhua269.moliatopiabot.Bootstrapper
import i.mrhua269.moliatopiabot.base.data.DataFile
import org.apache.logging.log4j.LogManager
import java.io.File
import java.nio.file.Files

object DataManager {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val logger = LogManager.getLogger(this::class.java)
    private val dataFile = File(Bootstrapper.BASE_DIR, "botdata.json")
    private var currentDataFile = DataFile(0)


    fun initOrRead() {
        logger.info("Reading data file")

        if (dataFile.exists()) {
            val readBytes: ByteArray = Files.readAllBytes(dataFile.toPath())
            currentDataFile = gson.fromJson(String(readBytes), DataFile::class.java)
            logger.info("Data file read!")
            return
        }

        val bytes: ByteArray = currentDataFile.toString().toByteArray()
        Files.write(dataFile.toPath(), bytes)
        logger.info("Data file created!")
    }

    fun save() {
        val bytes: ByteArray = currentDataFile.toString().toByteArray()
        Files.write(dataFile.toPath(), bytes)
    }

    fun getReadData(): DataFile {
        return currentDataFile
    }
}