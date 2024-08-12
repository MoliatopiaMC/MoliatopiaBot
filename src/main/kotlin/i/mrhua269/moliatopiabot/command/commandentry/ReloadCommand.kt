package i.mrhua269.moliatopiabot.command.commandentry

import i.mrhua269.moliatopiabot.command.PackagedCommandInfo
import i.mrhua269.moliatopiabot.manager.ConfigManager
import i.mrhua269.moliatopiabot.scripting.JSCommandLoader
import net.mamoe.mirai.event.events.MessageEvent

class ReloadCommand : CommandEntry {
    override fun getName(): String {
        return "reload"
    }

    override suspend fun process(commandArg: PackagedCommandInfo, firedEvent: MessageEvent) {
        if (firedEvent.sender.id == ConfigManager.getReadConfig().getMasterQid()){
            JSCommandLoader.reload()
        }
    }
}