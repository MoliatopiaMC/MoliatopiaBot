package i.mrhua269.moliatopiabot.extra.ai

import i.mrhua269.moliatopiabot.base.event.Listener
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.PlainText
import org.apache.logging.log4j.LogManager

object AIConvertorEventHandler : Listener {
    private val logger = LogManager.getLogger(this::class.java)

    override suspend fun onEvent(event: Event): Boolean {
        if (event is GroupMessageEvent) {
            val rawMessage = event.message
            val targetFeedback = event.sender
            val group = event.group
            val currentBot = event.bot

            if (rawMessage.size >= 3){
                val firstMsg = rawMessage[1]

                if (firstMsg is At && firstMsg.target == currentBot.id){
                    val messageFullyProcessed = StringBuilder()

                    for (i in 2 until rawMessage.size){
                        val currentMessage = rawMessage[i]

                        if (currentMessage is At){
                            val targetPersonId = currentMessage.target
                            val targetPerson = group[targetPersonId]

                            targetPerson?.let {
                                messageFullyProcessed.append(it.nameCardOrNick)
                            }

                            continue
                        }

                        if (currentMessage is PlainText){
                            messageFullyProcessed.append(currentMessage.content)
                            continue
                        }
                    }

                    logger.info("[AIConvertor] ${targetFeedback.id}(in ${group.id}) -> AI : $messageFullyProcessed")

                    val buildContent = "${targetFeedback.nameCardOrNick} 说: $messageFullyProcessed"
                    val convertFuture = AIConvertorController.converse(group.id.toString(), buildContent)

                    convertFuture.whenComplete { result, _ ->
                        if (result == null){
                            runBlocking { group.sendMessage("Out of speed limit!") }
                            return@whenComplete
                        }

                        val builtReply = MessageChainBuilder()
                        builtReply.add(At(targetFeedback.id))
                        builtReply.add(result.content)

                        logger.info("[AIConvertor] AI -> ${targetFeedback.id}(in ${group.id}) : ${result.content}")
                        runBlocking { group.sendMessage(builtReply.build()) }
                    }
                }
            }
        }

        return true
    }

    override fun listenerName(): String {
        return "ai_convertor"
    }
}