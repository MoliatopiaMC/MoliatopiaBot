package i.mrhua269.moliatopiabot.extra.aistorage.impl

import i.mrhua269.moliatopiabot.extra.OpenAIAPIRequester
import i.mrhua269.moliatopiabot.extra.aistorage.IAIMemoryCleaner

class DefaultBotMemoryCleaner : IAIMemoryCleaner {
    override suspend fun cleanMemory(input: List<OpenAIAPIRequester.MemoryInstance>): List<OpenAIAPIRequester.MemoryInstance> {
        return input //TODO : 遗忘机制
    }
}