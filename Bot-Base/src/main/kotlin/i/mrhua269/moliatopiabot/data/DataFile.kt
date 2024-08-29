package i.mrhua269.moliatopiabot.data

import com.google.gson.Gson

class DataFile(
    var wocPicIndex: Int = 0
) {
    override fun toString(): String {
        return gson.toJson(this)
    }

    companion object {
        private val gson: Gson = Gson()
    }
}