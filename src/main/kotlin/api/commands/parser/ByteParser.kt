package api.commands.parser

import api.commands.ParseResult
import api.commands.Parser
import org.bukkit.command.CommandSender
import kotlin.reflect.KParameter

@Parser(Byte::class)
class ByteParser : api.commands.ArgumentParser<Byte> {
    override fun parse(
        input: String,
        sender: CommandSender,
        parameter: KParameter,
        callback: (ParseResult<Byte>) -> Unit
    ): Boolean {
        val number = input.toByteOrNull()
        if (number == null ) {
            callback(
                ParseResult(success = false, message = api.commands.CommandParser.getArgError(
                parameter.index,
                input,
                "a whole number between 0 and 255"
            ))
            )
            return false
        }
        callback(ParseResult(success = true, value = number))
        return true
    }
}