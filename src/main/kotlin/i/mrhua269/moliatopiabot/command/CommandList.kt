package i.mrhua269.moliatopiabot.command

import i.mrhua269.moliatopiabot.command.commandentry.CommandEntry
import java.util.concurrent.ConcurrentHashMap

object CommandList {
    private val registedCommands: MutableSet<CommandEntry> = ConcurrentHashMap.newKeySet()

    fun getAllCommands(): Set<CommandEntry> {
        return this.registedCommands
    }

    fun search(name: String): CommandEntry? {
        for (command in this.registedCommands) {
            if (command.getName() == name) {
                return command
            }
        }
        return null
    }

    fun removeCommand(commandEntry: CommandEntry) {
        this. registedCommands.remove(commandEntry)
    }

    fun regCommand(commandEntry: CommandEntry) {
        this.registedCommands.add(commandEntry)
    }

    fun clearAll(){
        this.registedCommands.clear()
    }
}