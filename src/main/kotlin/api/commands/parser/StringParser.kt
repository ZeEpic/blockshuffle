package api.commands.parser

import api.commands.ParseResult
import api.commands.Parser
import org.bukkit.command.CommandSender
import kotlin.reflect.KParameter

@Parser(String::class)
class StringParser : api.commands.ArgumentParser<String> {

    override fun parse(
        input: String,
        sender: CommandSender,
        parameter: KParameter,
        callback: (ParseResult<String>) -> Unit
    ): Boolean {
        callback(ParseResult(success = true, value = input))
        return true
    }

}