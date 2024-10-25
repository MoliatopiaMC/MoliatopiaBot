package i.mrhua269.moliatopiabot.commanding

import i.mrhua269.moliatopiabot.commanding.impl.CommandEntry
import java.util.concurrent.ConcurrentHashMap

object CommandList {
    private val registedCommands: MutableSet<CommandEntry> = ConcurrentHashMap.newKeySet()

    fun getAllCommands(): Set<CommandEntry> {
        return registedCommands
    }

    fun search(name: String): CommandEntry? {
        for (command in registedCommands) {
            if (command.getName() == name) {
                return command
            }
        }
        return null
    }

    fun removeCommand(commandEntry: CommandEntry) {
        registedCommands.remove(commandEntry)
    }

    fun regCommand(commandEntry: CommandEntry) {
        registedCommands.add(commandEntry)
    }

    fun clearAll(){
        registedCommands.clear()
    }
}