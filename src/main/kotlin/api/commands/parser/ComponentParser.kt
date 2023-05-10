package api.commands.parser

import api.commands.ParseResult
import api.commands.Parser
import me.zeepic.aiparkour.messaging.component
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import kotlin.reflect.KParameter

@Parser(Component::class)
class ComponentParser : api.commands.ArgumentParser<Component> {

    override fun parse(
        input: String,
        sender: CommandSender,
        parameter: KParameter,
        callback: (ParseResult<Component>) -> Unit
    ): Boolean {
        callback(ParseResult(success = true, value = input.component))
        return true
    }
}