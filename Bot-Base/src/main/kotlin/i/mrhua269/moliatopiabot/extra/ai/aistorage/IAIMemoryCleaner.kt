package i.mrhua269.moliatopiabot.extra.ai.aistorage

import i.mrhua269.moliatopiabot.extra.ai.OpenAIAPIRequester


interface IAIMemoryCleaner {
    suspend fun cleanMemory(input: List<OpenAIAPIRequester.MemoryInstance>): List<OpenAIAPIRequester.MemoryInstance>
}