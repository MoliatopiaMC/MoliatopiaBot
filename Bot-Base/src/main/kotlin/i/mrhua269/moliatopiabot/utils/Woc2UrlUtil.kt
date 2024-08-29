package i.mrhua269.moliatopiabot.utils

import java.net.URLDecoder

object Woc2UrlUtil {
    fun getNewWocPicList(page: Int): List<String>? {
        try {
            val json = Utils.getBytes("https://yingtall.com/wp-json/wp/v2/posts?page=$page")?.let { String(it) }
            val split = json?.split("src=".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
            val done: MutableList<String> = ArrayList()

            if (split != null) {
                for ((counter, singlePart) in split.withIndex()) {
                    if (counter > 0) {
                        val removeHead = singlePart.substring(2)
                        val retainArg = removeHead.substring(0, removeHead.indexOf("\\\""))
                        done.add(URLDecoder.decode(retainArg, "UTF-8").replace("\\/\\/", "//").replace("\\/", "/"))
                    }
                }
            }

            return done
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}