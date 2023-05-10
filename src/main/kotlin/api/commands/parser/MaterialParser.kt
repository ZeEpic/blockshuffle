package api.commands.parser

import api.commands.ParseResult
import api.commands.Parser
import me.zeepic.aiparkour.messaging.toMaterial
import org.bukkit.Material
import org.bukkit.command.CommandSender
import kotlin.reflect.KParameter

@Parser(Material::class)
class MaterialParser : api.commands.ArgumentParser<Material> {
    override fun parse(
        input: String,
        sender: CommandSender,
        parameter: KParameter,
        callback: (ParseResult<Material>) -> Unit
    ): Boolean {
        val material = input.toMaterial()
        if (material == null ) {
            callback(ParseResult(success = false, message = api.commands.CommandParser.getArgError(
                parameter.index,
                input,
                "an item/block",
                "Use underscores for blocks like grass_block."
            )))
            return false
        }
        callback(ParseResult(success = true, value = material))
        return true
    }
}