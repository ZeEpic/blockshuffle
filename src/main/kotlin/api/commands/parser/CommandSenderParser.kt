package api.commands.parser

import api.commands.ParseResult
import api.commands.Parser
import org.bukkit.command.CommandSender
import kotlin.reflect.KParameter

@Parser(CommandSender::class)
class CommandSenderParser : api.commands.ArgumentParser<CommandSender> {

    override fun parse(
        input: String,
        sender: CommandSender,
        parameter: KParameter,
        callback: (ParseResult<CommandSender>) -> Unit
    ): Boolean {
        callback(ParseResult(success = true, value = sender))
        return true
    }
}