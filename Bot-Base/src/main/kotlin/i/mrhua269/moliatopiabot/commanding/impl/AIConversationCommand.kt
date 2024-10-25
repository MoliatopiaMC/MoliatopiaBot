package i.mrhua269.moliatopiabot.commanding.impl

import i.mrhua269.moliatopiabot.commanding.PackagedCommandInfo
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent

class AIConversationCommand: CommandEntry {
    override fun getName(): String {
        return "convert"
    }

    override suspend fun process(commandArg: PackagedCommandInfo, firedEvent: MessageEvent) {
        val feedback = this.getSenderForFeedback(firedEvent)

        if (firedEvent !is GroupMessageEvent){
            feedback.sendMessage("You can only convert in groups!")
            return
        }

        val senderMember = firedEvent.sender

        if (commandArg.getArgs().isEmpty()){
            feedback.sendMessage("You must provide a string to convert!")
        }

        val joinedArgs = commandArg.getArgs().joinToString(" ")


    }
}