package api.commands.parser

import api.commands.ParseResult
import api.commands.Parser
import org.bukkit.command.CommandSender
import kotlin.reflect.KParameter

@Parser(Int::class)
class IntParser : api.commands.ArgumentParser<Int> {
    override fun parse(
        input: String,
        sender: CommandSender,
        parameter: KParameter,
        callback: (ParseResult<Int>) -> Unit
    ): Boolean {
        val number = input.toIntOrNull()
        if (number == null ) {
            callback(ParseResult(success = false, message = api.commands.CommandParser.getArgError(
                parameter.index,
                input,
                "a whole number"
            )))
            return false
        }
        callback(ParseResult(success = true, value = number))
        return true
    }
}