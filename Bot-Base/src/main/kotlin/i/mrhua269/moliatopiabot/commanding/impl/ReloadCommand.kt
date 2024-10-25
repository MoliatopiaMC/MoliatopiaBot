package i.mrhua269.moliatopiabot.commanding.impl

import i.mrhua269.moliatopiabot.commanding.PackagedCommandInfo
import i.mrhua269.moliatopiabot.base.manager.ConfigManager
import i.mrhua269.moliatopiabot.extra.scripting.JSCommandLoader
import net.mamoe.mirai.event.events.MessageEvent

class ReloadCommand : CommandEntry {
    override fun getName(): String {
        return "reload"
    }

    override suspend fun process(commandArg: PackagedCommandInfo, firedEvent: MessageEvent) {
        if (firedEvent.sender.id == ConfigManager.getReadConfig().masterQid){
            JSCommandLoader.reload()
        }
    }
}