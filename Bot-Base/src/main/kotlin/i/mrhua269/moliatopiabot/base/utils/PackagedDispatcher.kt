package i.mrhua269.moliatopiabot.base.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext

class PackagedDispatcher(
    private val internal: Executor
) : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        this.internal.execute(block)
    }
}