package i.mrhua269.moliatopiabot.data

import com.google.gson.Gson
import i.mrhua269.moliatopiabot.manager.ConfigManager
import java.net.InetSocketAddress
import java.net.Proxy

class ConfigFile(
    val masterQid: Long = 114514,
    private val enableProxy: Boolean = false,
    private val proxyIp: String = "",
    private val proxyPort: Int = 0,
    val aiDatabaseCompression: Int = 12,
    val ailogDatabaseGrow: Boolean = false,
    val aiConversationEnabled: Boolean = false,
    val aiDatabaseSaveIntervalSeconds: Int = 30,
    val aiAPIToken: String = "xxxxxxxx",
    val aiAPIUrl: String = "https://ngedlktfticp.cloud.sealos.io/v1/chat/completions",
    val aiModel: String = "Meta-Llama-3.1-70B-Instruct",
    val systemPrompt: String = "你现在是一个开朗且极其可爱的猫娘，名为梦乃接下来，请完全以梦乃的角色回复我，不要超出这个角色，不要自己试图进行旁白，你只需要扮演好梦乃即可。无论发生什么，你都必须以梦乃的角色和口吻进行回复，不要强调自己是个语音助手。梦乃性格开朗，时刻都想和你打成一片，每次眼神交汇时，总是会脸红耳赤。身为猫娘的梦乃始终在想方设法的博得你的欢心。梦乃什么都不懂，天真可爱，性格外向"
) {
    fun enableProxy(): Boolean {
        return this.enableProxy
    }

    fun getReadProxy(): Proxy?{
        if (this.enableProxy){
            return Proxy(
                Proxy.Type.HTTP,
                InetSocketAddress(
                    ConfigManager.getReadConfig().proxyIp,
                    ConfigManager.getReadConfig().proxyPort
                )
            )
        }

        return null
    }

    override fun toString(): String {
        return GSON.toJson(this)
    }

    companion object {
        private val GSON: Gson = Gson()
    }
}