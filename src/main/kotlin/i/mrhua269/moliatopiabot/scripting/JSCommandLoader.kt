package i.mrhua269.moliatopiabot.scripting

import i.mrhua269.moliatopiabot.command.CommandList
import i.mrhua269.moliatopiabot.command.commandentry.javascrpit.PackagedJSCommand
import org.apache.logging.log4j.LogManager
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import javax.script.Invocable
import javax.script.ScriptEngineManager


class JSCommandLoader {
    companion object{
        private val loadedCommands: MutableSet<PackagedJSCommand> = ConcurrentHashMap.newKeySet()
        private val logger = LogManager.getLogger()
        private var currentScriptDir: File? = null

        private fun registerToCommandList(){
            for (command in this.loadedCommands){
                CommandList.regCommand(command)
            }
        }

        private fun loadSingleJavaScript(dataArray: ByteArray) {
            try {
                val engine = ScriptEngineManager().getEngineByName("javascript")

                if (engine == null){
                    logger.warn("Skip loading script because javascript engine is not available!")
                    return
                }

                engine.eval(String(dataArray, StandardCharsets.UTF_8))
                val inv = engine as Invocable
                this.loadedCommands.add(PackagedJSCommand(inv))
            } catch (e: Exception) {
                this.logger.error("Error in loading script!", e)
                e.printStackTrace()
            }
        }

        @Synchronized
        fun loadAll(scriptsDir: File?) {
            if (scriptsDir!!.exists()) {
                CompletableFuture.allOf(
                    *Arrays.stream(scriptsDir.listFiles())
                    .map { singleFile ->
                        CompletableFuture.runAsync {
                            try {
                                if (singleFile.name.endsWith(".js")) {
                                    logger.info("Loading script {}", singleFile.name)
                                    val read = Files.readAllBytes(singleFile.toPath())
                                    loadSingleJavaScript(read)
                                }
                            } catch (e: Exception) {
                                this.logger.error("Error in reading file!", e)
                                e.printStackTrace()
                            }
                        }
                    }.toArray {
                        length -> arrayOfNulls<CompletableFuture<Any>>(length)
                    }).join()
                registerToCommandList()
                this.logger.info("Load {} javascripts!" , this.loadedCommands.size)
                this.currentScriptDir = scriptsDir
            }
        }

        @Synchronized
        fun clearAll(){
            for (singleCommand in CommandList.getAllCommands()){
                if (singleCommand is PackagedJSCommand){
                    CommandList.removeCommand(singleCommand)
                }
            }
            this.loadedCommands.clear()
        }

        @Synchronized
        fun reload() {
            clearAll()
            loadAll(this.currentScriptDir)
        }
    }
}