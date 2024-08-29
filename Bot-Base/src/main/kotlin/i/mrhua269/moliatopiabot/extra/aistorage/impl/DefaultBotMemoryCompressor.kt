package i.mrhua269.moliatopiabot.extra.aistorage.impl

import i.mrhua269.moliatopiabot.extra.OpenAIAPIRequester
import i.mrhua269.moliatopiabot.extra.OpenAIAPIRequester.Companion.toMemoryInstance
import i.mrhua269.moliatopiabot.extra.aistorage.IAIMemoryCompressor
import i.mrhua269.moliatopiabot.manager.ConfigManager

class DefaultBotMemoryCompressor : IAIMemoryCompressor {
    override suspend fun compressMemory(input: List<OpenAIAPIRequester.MemoryInstance>): List<OpenAIAPIRequester.MemoryInstance> {
        if (input.size < ConfigManager.getReadConfig().aiCompressionThreshold){
            return input
        }

        (input as MutableList<OpenAIAPIRequester.MemoryInstance>).add(OpenAIAPIRequester.MemoryInstance("user", "请用一句话尽可能详细地总结上述对话,注意人物与对话的对应关系"))

        val memoriesArray = input.toTypedArray()
        val resultResponse = OpenAIAPIRequester(ConfigManager.getReadConfig().aiAPIToken, ConfigManager.getReadConfig().aiAPIUrl)
            .requestAPI(memoriesArray.toList(), ConfigManager.getReadConfig().aiModel)
        val resultMessage = resultResponse.toMemoryInstance().content

        return listOf(OpenAIAPIRequester.MemoryInstance("assistant", resultMessage))
    }
}