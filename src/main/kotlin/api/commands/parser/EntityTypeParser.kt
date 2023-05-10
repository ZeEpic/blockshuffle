package api.commands.parser

import api.commands.ParseResult
import api.commands.Parser
import me.zeepic.aiparkour.messaging.toEntityType
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import kotlin.reflect.KParameter

@Parser(EntityType::class)
class EntityTypeParser : api.commands.ArgumentParser<EntityType> {
    override fun parse(
        input: String,
        sender: CommandSender,
        parameter: KParameter,
        callback: (ParseResult<EntityType>) -> Unit
    ): Boolean {
        val entityType = input.toEntityType()
        if (entityType == null ) {
            callback(ParseResult(success = false, message = api.commands.CommandParser.getArgError(
                parameter.index,
                input,
                "an entity type",
                "Use underscores for mobs like elder_guardian."
            )))
            return false
        }
        callback(ParseResult(success = true, value = entityType))
        return true
    }
}