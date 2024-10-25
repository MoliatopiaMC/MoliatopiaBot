package i.mrhua269.moliatopiabot.extra.ai.aistorage.impl

import i.mrhua269.moliatopiabot.extra.ai.OpenAIAPIRequester
import i.mrhua269.moliatopiabot.extra.ai.aistorage.IAIMemoryCompressor

class DefaultBotMemoryCompressor : IAIMemoryCompressor {
    override suspend fun compressMemory(input: List<OpenAIAPIRequester.MemoryInstance>): List<OpenAIAPIRequester.MemoryInstance> {
        return input //TODO Unsupported
    }
}