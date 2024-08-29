package i.mrhua269.moliatopiabot.extra.aistorage

import i.mrhua269.moliatopiabot.extra.OpenAIAPIRequester


interface IAIMemoryCleaner {
    suspend fun cleanMemory(input: List<OpenAIAPIRequester.MemoryInstance>): List<OpenAIAPIRequester.MemoryInstance>
}