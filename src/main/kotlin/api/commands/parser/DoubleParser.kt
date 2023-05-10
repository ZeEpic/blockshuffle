package api.commands.parser

import api.commands.ParseResult
import api.commands.Parser
import org.bukkit.command.CommandSender
import kotlin.reflect.KParameter

@Parser(Double::class)
class DoubleParser : api.commands.ArgumentParser<Double> {
    override fun parse(
        input: String,
        sender: CommandSender,
        parameter: KParameter,
        callback: (ParseResult<Double>) -> Unit
    ): Boolean {
        val number = input.toDoubleOrNull()
        if (number == null ) {
            callback(ParseResult(success = false, message = api.commands.CommandParser.getArgError(
                parameter.index,
                input,
                "a decimal or whole number"
            )))
            return false
        }
        callback(ParseResult(success = true, value = number))
        return true
    }
}