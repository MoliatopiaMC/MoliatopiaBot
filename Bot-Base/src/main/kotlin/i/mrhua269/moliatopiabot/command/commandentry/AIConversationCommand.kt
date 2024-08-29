package i.mrhua269.moliatopiabot.command.commandentry

import i.mrhua269.moliatopiabot.command.PackagedCommandInfo
import i.mrhua269.moliatopiabot.extra.AIConvertor
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.PlainText
import java.lang.reflect.Member

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