package i.mrhua269.moliatopiabot

import i.mrhua269.moliatopiabot.commanding.CommandList
import i.mrhua269.moliatopiabot.commanding.CommandParser
import i.mrhua269.moliatopiabot.base.event.EventHub
import i.mrhua269.moliatopiabot.misc.MiscBotEventHandler
import i.mrhua269.moliatopiabot.extra.ai.AIConvertorEventHandler
import i.mrhua269.moliatopiabot.base.manager.BotManager
import i.mrhua269.moliatopiabot.base.manager.ConfigManager
import i.mrhua269.moliatopiabot.base.manager.DataManager
import i.mrhua269.moliatopiabot.extra.commands.DebugCommand
import i.mrhua269.moliatopiabot.extra.commands.RPIC3Command
import i.mrhua269.moliatopiabot.extra.commands.RPICCommand
import i.mrhua269.moliatopiabot.extra.commands.ReloadCommand
import i.mrhua269.moliatopiabot.extra.scripting.JSCommandLoader
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