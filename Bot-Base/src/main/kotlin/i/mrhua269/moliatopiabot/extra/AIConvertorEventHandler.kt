package i.mrhua269.moliatopiabot.extra

import i.mrhua269.moliatopiabot.eventsystem.EventHub
import i.mrhua269.moliatopiabot.eventsystem.Listener
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.Event
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.PlainText
import org.apache.logging.log4j.LogManager

object AIConvertorEventHandler : Listener {
    private val logger = LogManager.getLogger()

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

                    withContext(EventHub.getDispatcher()) {
                        logger.info("[AIConvertor] ${targetFeedback.id}(in ${group.id}) -> AI : $messageFullyProcessed")

                        val buildContent = "${targetFeedback.nameCardOrNick} 说: $messageFullyProcessed"
                        val convertFeedback = AIConvertor.converse(group.id.toString(), buildContent)

                        val builtReply = MessageChainBuilder()
                        builtReply.add(At(targetFeedback.id))
                        builtReply.add(convertFeedback.content)

                        logger.info("[AIConvertor] AI -> ${targetFeedback.id}(in ${group.id}) : ${convertFeedback.content}")
                        group.sendMessage(builtReply.build())
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