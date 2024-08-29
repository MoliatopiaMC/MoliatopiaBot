package i.mrhua269.moliatopiabot.extra.aistorage

import i.mrhua269.moliatopiabot.extra.OpenAIAPIRequester

interface IAIMemoryCompressor {
    suspend fun compressMemory(input: List<OpenAIAPIRequester.MemoryInstance>): List<OpenAIAPIRequester.MemoryInstance>
}