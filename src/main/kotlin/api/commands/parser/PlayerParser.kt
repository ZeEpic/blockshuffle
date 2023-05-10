package api.commands.parser

import api.commands.ParseResult
import api.commands.Parser
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.reflect.KParameter

@Parser(Player::class)
class PlayerParser : api.commands.ArgumentParser<Player> {

    override fun parse(input: String, sender: CommandSender, parameter: KParameter, callback: (ParseResult<Player>) -> Unit): Boolean {
        val result = if (parameter.index == 1) {
            if (sender !is Player) {
                ParseResult(success = false, message = "&cYou must be a player to use this command!")
            } else {
                ParseResult(success = true, value = sender)
            }
        } else {
            val player = Bukkit.getPlayer(input)
            if (player == null) ParseResult(success = false, message = "&c$input isn't on the server right now.")
            else ParseResult(success = true, value = player)
        }
        callback(result)
        return result.success
    }

}