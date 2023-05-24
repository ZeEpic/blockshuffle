package me.zeepic.blockshuffle

import api.commands.Command
import api.commands.CommandGroup
import api.commands.CommandResult
import api.helpers.send
import org.bukkit.command.CommandSender

@CommandGroup("game.settings")
class SettingsCommand {

    @Command("settings", "Change game settings.")
    fun settingsCommand(sender: CommandSender, setting: String, value: String): CommandResult {
        when (setting) {
            "skips" -> {
                val newSkips = value.toIntOrNull()
                if (newSkips == null) {
                    sender.send("&cInvalid value!")
                    return CommandResult.SILENT_FAILURE
                }
                Settings.skipsAllowed = newSkips
                sender.send("&aSkips allowed has been set to &6$newSkips&a!")
            }
            "revealbiome" -> {
                val newReveal = value.toBooleanStrictOrNull()
                if (newReveal == null) {
                    sender.send("&cInvalid value!")
                    return CommandResult.SILENT_FAILURE
                }
                Settings.revealDesertBiome = newReveal
                sender.send("&aReveal desert biome has been set to &6$newReveal&a!")
            }
            "coop" -> {
                val coOpMode = value.toBooleanStrictOrNull()
                if (coOpMode == null) {
                    sender.send("&cInvalid value!")
                    return CommandResult.SILENT_FAILURE
                }
                Settings.coOpMode = coOpMode
                sender.send("&aCo-op mode has been set to &6$coOpMode&a!")
            }
            "lives" -> {
                val newLives = value.toIntOrNull()
                if (newLives == null) {
                    sender.send("&cInvalid value!")
                    return CommandResult.SILENT_FAILURE
                }
                Settings.lives = newLives
                sender.send("&aLives has been set to &6$newLives&a!")
            }
            "bundle" -> {
                val newBundle = value.toBooleanStrictOrNull()
                if (newBundle == null) {
                    sender.send("&cInvalid value!")
                    return CommandResult.SILENT_FAILURE
                }
                Settings.bundle = newBundle
                sender.send("&aBundle has been set to &6$newBundle&a!")
            }
            else -> {
                sender.send("&cInvalid setting!")
                return CommandResult.SILENT_FAILURE
            }
        }
        return CommandResult.SUCCESS
    }

}