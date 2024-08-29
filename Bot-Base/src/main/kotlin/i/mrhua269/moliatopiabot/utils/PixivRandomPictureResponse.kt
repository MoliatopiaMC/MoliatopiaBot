package i.mrhua269.moliatopiabot.utils

import com.alibaba.fastjson2.JSONArray
import com.alibaba.fastjson2.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.Proxy
import java.net.URL
import java.util.stream.Stream

object PixivRandomPictureResponse {

    fun getNewLink(rType: Int, num: Int): JSONObject? {
        val url = URL("https://setu.yuban10703.xyz/setu?num=$num&r18=$rType")
        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"

        connection.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:108.0) Gecko/20100101 Firefox/108.0"
        )

        connection.setRequestProperty("Referer", "https://www.pixiv.net/")
        connection.connectTimeout = 3000
        connection.readTimeout = 3000
        connection.connect()

        if (connection.responseCode == 200) {
            val data: ByteArray = Utils.readInputStreamToByte(connection.inputStream)
            connection.disconnect()
            val jsonText = String(data)
            return JSONObject.parse(jsonText)
        }

        throw IOException("Unmatched response code:" + connection.responseCode)
    }

    fun downloadFromLink(url1: String?): ByteArray{
        val url = URL(url1)
        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"

        connection.setRequestProperty(
            "User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:108.0) Gecko/20100101 Firefox/108.0"
        )

        connection.setRequestProperty("Referer", "https://www.pixiv.net/")
        connection.connectTimeout = 3000
        connection.readTimeout = 3000
        connection.connect()

        if (connection.responseCode == 200) {
            try {
                return Utils.readInputStreamToByte(connection.inputStream)
            }finally {
                connection.disconnect()
            }
        }

        throw IOException("Unmatched response code:" + connection.responseCode)
    }

    fun downloadFromLink(url1: String?,proxy: Proxy): ByteArray{
        val url = URL(url1)
        val connection = url.openConnection(proxy) as HttpURLConnection
        connection.requestMethod = "GET"

        connection.setRequestProperty(
            "User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:108.0) Gecko/20100101 Firefox/108.0"
        )

        connection.setRequestProperty("Referer", "https://www.pixiv.net/")
        connection.connectTimeout = 3000
        connection.readTimeout = 3000
        connection.connect()

        if (connection.responseCode == 200) {
            try {
                return Utils.readInputStreamToByte(connection.inputStream)
            }finally {
                connection.disconnect()
            }
        }
        throw IOException("Unmatched response code:" + connection.responseCode)
    }

    fun getAllLinks(link: JSONObject): Stream<String>{
        val links: MutableList<String> = ArrayList()
        val dataArray: JSONArray = link.getJSONArray("data")

        for (o in dataArray) {
            val single: JSONObject = o as JSONObject
            val urls: JSONArray = single.getJSONArray("urls")
            for (o2 in urls) {
                val singleUrl: JSONObject = o2 as JSONObject
                val original: String = singleUrl.getString("original")
                links.add(original)
            }
        }

        return links.stream()
    }
}