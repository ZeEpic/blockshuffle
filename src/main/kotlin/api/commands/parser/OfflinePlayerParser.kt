package api.commands.parser

import api.commands.ParseResult
import api.commands.Parser
import me.zeepic.aiparkour.players.OfflineManager
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import kotlin.reflect.KParameter

@Parser(OfflinePlayer::class)
class OfflinePlayerParser : api.commands.ArgumentParser<OfflinePlayer> {
    override fun parse(
        input: String,
        sender: CommandSender,
        parameter: KParameter,
        callback: (ParseResult<OfflinePlayer>) -> Unit
    ): Boolean {
        OfflineManager.getOfflinePlayer(input) {
            if (it == null) {
                callback(ParseResult(success = false, message = "&c$input hasn't played on this server yet."))
            } else {
                callback(ParseResult(success = true, value = it))
            }
        }
        return true
    }
}