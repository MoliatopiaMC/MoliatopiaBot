package i.mrhua269.moliatopiabot.extra

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.net.URL
import java.time.Duration

class OpenAIAPIRequester (
    private val apiKey: String,
    private val apiUrl: String = "https://api.openai.com/v1/chat/completions"
){
    data class MemoryInstance(
        val role: String,
        val content: String
    )

    companion object {
        fun JsonObject.toMemoryInstance(): MemoryInstance {
            val choices = this["choices"].asJsonArray
            val firstChoice = choices[0].asJsonObject
            val message = firstChoice["message"].asJsonObject

            return MemoryInstance(
                message["role"].asString,
                message["content"].asString
            )
        }
    }

    suspend fun requestAPI(
        messages: List<MemoryInstance>,
        model: String = "chatgpt-4o-latest",
        temperature: Double = 0.8,
        maxTokens: Int = 3248,
        topP: Double = 1.0,
        frequencyPenalty: Double = 0.0,
        presencePenalty: Double = 0.0
    ): JsonObject {
        val memoryRecords = JsonArray(messages.size)

        for (botMemory in messages){
            val memoryRecord = JsonObject()

            memoryRecord.add("role", JsonPrimitive(botMemory.role))
            memoryRecord.add("content", JsonPrimitive(botMemory.content))

            memoryRecords.add(memoryRecord)
        }

        val builtJsonData = JsonObject()

        builtJsonData.add("model", JsonPrimitive(model))
        builtJsonData.add("temperature", JsonPrimitive(temperature))
        builtJsonData.add("top_p", JsonPrimitive(topP))
        builtJsonData.add("max_tokens", JsonPrimitive(maxTokens))
        builtJsonData.add("presence_penalty", JsonPrimitive(presencePenalty))
        builtJsonData.add("frequency_penalty", JsonPrimitive(frequencyPenalty))
        builtJsonData.add("messages", memoryRecords)

        val url = URL(this.apiUrl)

        return withContext(Dispatchers.IO){
            val client = OkHttpClient.Builder()
                .callTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(30))
                .build()
            val mediaType: MediaType = "application/json".toMediaTypeOrNull()!!

            val body: RequestBody = RequestBody.create(mediaType, builtJsonData.toString())
            val request: Request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            val call: Call = client.newCall(request)
            val response: Response = call.execute()
            val parsed = JsonParser.parseString(response.body!!.string()).asJsonObject

            return@withContext parsed
        }
    }
}