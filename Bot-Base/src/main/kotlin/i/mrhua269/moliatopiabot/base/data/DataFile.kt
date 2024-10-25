package i.mrhua269.moliatopiabot.base.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class DataFile(
    @SerializedName("woc_picture_index")
    var wocPicIndex: Int = 0
) {
    override fun toString(): String {
        return gson.toJson(this)
    }

    companion object {
        private val gson: Gson = Gson()
    }
}