package i.mrhua269.moliatopiabot

import java.nio.file.Path

fun main() {
    Bootstrapper.baseDir(Path.of("data").toFile())
    Bootstrapper.runBot()

    while (true){
        val input = readlnOrNull()

        if (input == "exit") break
    }

    Bootstrapper.shutdownBot()
}