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
            val choices = this["choices"]?.asJsonArray ?: throw RuntimeException("No choices found in the response: $this")

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
        temperature: Double = 1.0,
        maxTokens: Int = 4096,
        topP: Double = 1.0,
        frequencyPenalty: Double = 0.0,
        presencePenalty: Double = 0.0,
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
        builtJsonData.add("stream", JsonPrimitive(false)) //Force disable stream responsing

        val url = URL(this.apiUrl)

        return withContext(Dispatchers.IO){
            val client = OkHttpClient.Builder()
                .callTimeout(Duration.ofSeconds(30))
                .readTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(30))
                .build()
            val mediaType = "application/json".toMediaTypeOrNull()!!

            val body = RequestBody.create(mediaType, builtJsonData.toString())
            var requestBuilder = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")

            if (apiKey != "none"){
                requestBuilder = requestBuilder.addHeader("Authorization", "Bearer $apiKey")
            }

            val request = requestBuilder.build()

            val call = client.newCall(request)
            val response = call.execute()
            val str = response.body!!.string()
            val parsed = JsonParser.parseString(str).asJsonObject

            return@withContext parsed
        }
    }
}