package api.commands

import org.bukkit.command.CommandSender
import kotlin.reflect.KParameter

fun interface ArgumentParser<T : Any> {
    fun parse(
        input: String,
        sender: CommandSender,
        parameter: KParameter,
        callback: (ParseResult<T>) -> Unit
    ): Boolean
}
