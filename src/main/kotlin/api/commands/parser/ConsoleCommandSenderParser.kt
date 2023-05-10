package api.commands.parser

import api.commands.ParseResult
import api.commands.Parser
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import kotlin.reflect.KParameter

@Parser(ConsoleCommandSender::class)
class ConsoleCommandSenderParser : api.commands.ArgumentParser<ConsoleCommandSender> {
    override fun parse(
        input: String,
        sender: CommandSender,
        parameter: KParameter,
        callback: (ParseResult<ConsoleCommandSender>) -> Unit
    ): Boolean {
        if (sender !is ConsoleCommandSender && parameter.index == 1) {
            callback(ParseResult(success = false, message = "&cYou must be the server console to use this command!"))
            return false
        }
        callback(ParseResult(success = true, value = sender as ConsoleCommandSender))
        return true
    }
}
