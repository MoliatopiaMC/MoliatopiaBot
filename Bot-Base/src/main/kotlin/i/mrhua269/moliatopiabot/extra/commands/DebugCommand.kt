package i.mrhua269.moliatopiabot.extra.commands


import i.mrhua269.moliatopiabot.commanding.PackagedCommandInfo
import net.mamoe.mirai.event.events.MessageEvent

class DebugCommand : CommandEntry {
    override fun getName(): String {
        return "debug"
    }

    override suspend fun process(commandArg: PackagedCommandInfo, firedEvent: MessageEvent) {
        getSenderForFeedback(firedEvent).sendMessage("Scanned command argument is $commandArg")
    }
}