package i.mrhua269.moliatopiabot.extra.ai.aistorage

import i.mrhua269.moliatopiabot.extra.ai.OpenAIAPIRequester

interface IAIMemoryCompressor {
    suspend fun compressMemory(input: List<OpenAIAPIRequester.MemoryInstance>): List<OpenAIAPIRequester.MemoryInstance>
}