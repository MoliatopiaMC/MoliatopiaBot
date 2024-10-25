package i.mrhua269.moliatopiabot.extra.commands

import i.mrhua269.moliatopiabot.commanding.PackagedCommandInfo
import i.mrhua269.moliatopiabot.base.event.EventHub
import i.mrhua269.moliatopiabot.base.manager.ConfigManager
import i.mrhua269.moliatopiabot.base.manager.DataManager
import i.mrhua269.moliatopiabot.base.utils.BaseUtils
import i.mrhua269.moliatopiabot.extra.utils.Woc2UrlUtil
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.ForwardMessage
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import java.io.ByteArrayInputStream
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class RPIC3Command : CommandEntry {
    private val lastWocUrls: Queue<String> = ConcurrentLinkedQueue()

    override fun getName(): String {
        return "rpic3"
    }

    override suspend fun process(commandArg: PackagedCommandInfo, firedEvent: MessageEvent) {
        //自动装弹(bushi)
        if (lastWocUrls.isEmpty()) {
            //Add lock around.Because the wocPixIndex is not thread safe
            synchronized(this) {
                Woc2UrlUtil.getNewWocPicList(DataManager.getReadData().wocPicIndex)?.let { this.lastWocUrls.addAll(it) }
                DataManager.getReadData().wocPicIndex++
                //Save it because we already edit them
                DataManager.save()
            }
        }

        //Get the message nodes
        val nodes: List<ForwardMessage.Node> = getNewNodes(firedEvent.sender, System.currentTimeMillis().toInt(), firedEvent.sender)
        val preview: MutableList<String> = ArrayList()

        for (i in nodes.indices) {
            preview.add(firedEvent.sender.nameCardOrNick + ":" + "[图片]")
        }

        val message = ForwardMessage(preview, "Yee", "Yee", "Yee", "Yee", nodes)

        //Send it
        getSenderForFeedback(firedEvent).sendMessage(message)
    }


    private suspend fun getNewNodes(sender: User, time: Int, target: Contact): List<ForwardMessage.Node> {
        return withContext(EventHub.getDispatcher()) {
            val toDownload = mutableListOf<String>()
            val downloadTasks = ConcurrentLinkedQueue<Deferred<ByteArray?>>()
            val nodes = mutableListOf<ForwardMessage.Node>()

            synchronized(this) {
                for (i in 1..6) {
                    toDownload.add(lastWocUrls.poll() ?: break)
                }
            }

            for (polled in toDownload) {
                downloadTasks.add(async(EventHub.getDispatcher()) {
                    val bytes = if (ConfigManager.getReadConfig().enableProxy()) {
                        val proxy = ConfigManager.getReadConfig().getReadProxy()

                        BaseUtils.getBytes(polled, proxy!!)
                    } else {
                        BaseUtils.getBytes(polled)
                    }

                    bytes?.let {
                        return@async it
                    }

                    return@async null
                })
            }

            var downloaded: ByteArray?

            while (downloadTasks.poll().also { downloaded = it?.await() } != null) {
                downloaded?.let {
                    val picInputStream  = ByteArrayInputStream(it)

                    val message: Image =  picInputStream.uploadAsImage(target, null)
                    nodes.add(ForwardMessage.Node(sender.id, time, sender.nameCardOrNick, message))
                }
            }

            return@withContext nodes
        }
    }
}