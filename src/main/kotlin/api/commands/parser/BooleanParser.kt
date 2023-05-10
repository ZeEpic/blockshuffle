package api.commands.parser

import api.commands.ArgumentParser
import api.commands.ParseResult
import api.commands.Parser
import org.bukkit.command.CommandSender
import kotlin.reflect.KParameter

@Parser(Boolean::class)
class BooleanParser : ArgumentParser<Boolean> {
    override fun parse(
        input: String,
        sender: CommandSender,
        parameter: KParameter,
        callback: (ParseResult<Boolean>) -> Unit
    ): Boolean {
        val result = when (input) {
            "true", "yes" -> ParseResult(success = true, value = true)
            "false", "no" -> ParseResult(success = true, value = false)
            else -> ParseResult(success = false, message = api.commands.CommandParser.getArgError(parameter.index, input, "yes/no"))
        }
        callback(result)
        return result.success
    }
}