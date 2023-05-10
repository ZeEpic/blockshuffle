package api.commands.parser

import api.commands.ParseResult
import api.commands.Parser
import org.bukkit.command.CommandSender
import kotlin.reflect.KParameter

@Parser(Float::class)
class FloatParser : api.commands.ArgumentParser<Float> {
    override fun parse(
        input: String,
        sender: CommandSender,
        parameter: KParameter,
        callback: (ParseResult<Float>) -> Unit
    ): Boolean {
        val number = input.toFloatOrNull()
        if (number == null ) {
            callback(
                ParseResult(success = false, message = api.commands.CommandParser.getArgError(
                parameter.index,
                input,
                "a decimal or whole number"
            ))
            )
            return false
        }
        callback(ParseResult(success = true, value = number))
        return true
    }
}