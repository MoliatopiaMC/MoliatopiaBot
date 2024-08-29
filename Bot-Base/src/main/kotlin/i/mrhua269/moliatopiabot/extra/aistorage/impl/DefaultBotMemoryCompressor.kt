package i.mrhua269.moliatopiabot.extra.aistorage.impl

import i.mrhua269.moliatopiabot.extra.OpenAIAPIRequester
import i.mrhua269.moliatopiabot.extra.OpenAIAPIRequester.Companion.toMemoryInstance
import i.mrhua269.moliatopiabot.extra.aistorage.IAIMemoryCompressor
import i.mrhua269.moliatopiabot.manager.ConfigManager

class DefaultBotMemoryCompressor : IAIMemoryCompressor {
    override suspend fun compressMemory(input: List<OpenAIAPIRequester.MemoryInstance>): List<OpenAIAPIRequester.MemoryInstance> {
        return input //Unsupported
    }
}