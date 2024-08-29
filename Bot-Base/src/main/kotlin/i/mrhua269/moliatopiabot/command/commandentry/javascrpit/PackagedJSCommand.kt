package i.mrhua269.moliatopiabot.command.commandentry.javascrpit

import i.mrhua269.moliatopiabot.command.PackagedCommandInfo
import i.mrhua269.moliatopiabot.command.commandentry.CommandEntry
import net.mamoe.mirai.event.events.MessageEvent
import javax.script.Invocable
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class PackagedJSCommand(
    private val script : Invocable
) : CommandEntry {
    override fun getName(): String {
        return this.script.invokeFunction("getName", arrayOfNulls<JvmType.Object>(0)) as String
    }

    override suspend fun process(commandArg: PackagedCommandInfo, firedEvent: MessageEvent) {
        script.invokeFunction("process", arrayOf(commandArg, firedEvent))
    }
}