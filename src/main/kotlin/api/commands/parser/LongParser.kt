package api.commands.parser

import api.commands.ParseResult
import api.commands.Parser
import org.bukkit.command.CommandSender
import kotlin.reflect.KParameter

@Parser(Long::class)
class LongParser : api.commands.ArgumentParser<Long> {
    override fun parse(
        input: String,
        sender: CommandSender,
        parameter: KParameter,
        callback: (ParseResult<Long>) -> Unit
    ): Boolean {
        val number = input.toLongOrNull()
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