package i.mrhua269.moliatopiabot.extra.utils

import java.util.concurrent.TimeUnit

class RateLimiter(
    private val minDelay: Long,
    private val timeUnit: TimeUnit
) {
    private val minDelayConverted = this.timeUnit.toNanos(this.minDelay)
    private var lastTime = System.nanoTime()

    fun canPass(): Boolean {
        var result = true

        synchronized(this){
            val currTime = System.nanoTime()
            val delay = currTime - this.lastTime

            if (delay < this.minDelayConverted) {
                result = false
            }

            this.lastTime = currTime
        }

        return result
    }
}