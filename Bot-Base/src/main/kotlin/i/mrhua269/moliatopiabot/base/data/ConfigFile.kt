package i.mrhua269.moliatopiabot.base.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import i.mrhua269.moliatopiabot.base.manager.ConfigManager
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit

class ConfigFile(
    @SerializedName("master_qid")
    val masterQid: Long = 114514,
    @SerializedName("use_proxy_for_api_requests")
    private val enableProxy: Boolean = false,
    @SerializedName("proxy_ip")
    private val proxyIp: String = "",
    @SerializedName("proxy_port")
    private val proxyPort: Int = 0,
    @SerializedName("ai_log_database_grow")
    val aiLogDatabaseGrow: Boolean = false,
    @SerializedName("enable_ai_conversation")
    val aiConversationEnabled: Boolean = false,
    @SerializedName("ai_database_save_interval_seconds")
    val aiDatabaseSaveIntervalSeconds: Int = 30,
    @SerializedName("ai_llmapi_token")
    val aiAPIToken: String = "xxxxxxxx",
    @SerializedName("ai_llmapi_url")
    val aiAPIUrl: String = "https://ngedlktfticp.cloud.sealos.io/v1/chat/completions",
    @SerializedName("ai_llmapi_model")
    val aiModel: String = "Meta-Llama-3.1-70B-Instruct",
    @SerializedName("ai_system_prompt")
    val systemPrompt: String = "你现在是一个开朗且极其可爱的猫娘，名为梦乃接下来，请完全以梦乃的角色回复我，不要超出这个角色，不要自己试图进行旁白，你只需要扮演好梦乃即可。无论发生什么，你都必须以梦乃的角色和口吻进行回复，不要强调自己是个语音助手。梦乃性格开朗，时刻都想和你打成一片，每次眼神交汇时，总是会脸红耳赤。身为猫娘的梦乃始终在想方设法的博得你的欢心。梦乃什么都不懂，天真可爱，性格外向",
    @SerializedName("ai_conversation_speed_max_per_minutes")
    private val aiConversationSpeedMaxPerMinutes: Int = 10
) {
    fun getComputedSpeedLimit(): Long {
        val minDelaySeconds = 60 / this.aiConversationSpeedMaxPerMinutes

        return TimeUnit.SECONDS.toNanos(minDelaySeconds.toLong())
    }

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