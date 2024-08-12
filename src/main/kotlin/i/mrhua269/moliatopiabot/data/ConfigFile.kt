package i.mrhua269.moliatopiabot.data

import com.google.gson.Gson
import i.mrhua269.moliatopiabot.manager.ConfigManager
import java.net.InetSocketAddress
import java.net.Proxy

class ConfigFile(
    private val masterQid: Long,
    private val enableProxy: Boolean,
    private val proxyIp: String,
    private val proxyPort: Int
) {
    fun enableProxy(): Boolean {
        return this.enableProxy
    }

    fun getReadProxy(): Proxy?{
        if (this.enableProxy){
            return Proxy(
                Proxy.Type.HTTP,
                InetSocketAddress(
                    ConfigManager.getReadConfig().getProxyIp(),
                    ConfigManager.getReadConfig().getProxyPort()
                )
            )
        }
        return null
    }

    private fun getProxyIp(): String {
        return this.proxyIp
    }

    private fun getProxyPort(): Int {
        return this.proxyPort
    }


    fun getMasterQid(): Long {
        return this.masterQid
    }

    override fun toString(): String {
        return GSON.toJson(this)
    }

    companion object {
        private val GSON: Gson = Gson()
    }
}