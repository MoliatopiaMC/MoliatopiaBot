package i.mrhua269.moliatopiabot

import i.mrhua269.moliatopiabot.command.CommandList
import i.mrhua269.moliatopiabot.command.CommandParser
import i.mrhua269.moliatopiabot.command.commandentry.*
import i.mrhua269.moliatopiabot.eventsystem.EventHub
import i.mrhua269.moliatopiabot.eventsystem.misc.MiscBotEventHandler
import i.mrhua269.moliatopiabot.extra.AIConvertorEventHandler
import i.mrhua269.moliatopiabot.manager.BotManager
import i.mrhua269.moliatopiabot.manager.ConfigManager
import i.mrhua269.moliatopiabot.manager.DataManager
import i.mrhua269.moliatopiabot.scripting.JSCommandLoader
import java.io.File

object Bootstrapper {
    var BASE_DIR = File(File("plugins"),"MoliatopiaBot")

    fun baseDir(dir: File): Bootstrapper{
        BASE_DIR = dir

        return this
    }

    fun shutdownBot(){
        BotManager.shutdownAllBot()
        CommandList.clearAll()
        EventHub.shutdownDispatcher()
        JSCommandLoader.clearAll()
    }

    fun runBot(){
        BASE_DIR.mkdirs()

        val scriptFolder = File(BASE_DIR, "jsscripts")

        if (!scriptFolder.exists()){
            scriptFolder.mkdir()
        }

        ConfigManager.initConfig()
        DataManager.initOrRead()
        JSCommandLoader.loadAll(scriptFolder)
        EventHub.register(CommandParser)
        EventHub.register(MiscBotEventHandler)

        if (ConfigManager.getReadConfig().aiConversationEnabled) {
            EventHub.register(AIConvertorEventHandler)
        }

        BotManager.readConfig()
        BotManager.initAllBot()

        //Add shutdown hook
        Runtime.getRuntime().addShutdownHook(Thread {
            EventHub.shutdownDispatcher()
            JSCommandLoader.clearAll()
        })
        //Register commands

        EventHub.notifyAllLoaded()

        CommandList.regCommand(RPIC3Command())
        CommandList.regCommand(RPICCommand())
        CommandList.regCommand(ReloadCommand())
        CommandList.regCommand(DebugCommand())
        //CommandList.regCommand(AIConversationCommand())
    }
}