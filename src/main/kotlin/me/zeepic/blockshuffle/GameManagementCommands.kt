package me.zeepic.blockshuffle

import api.commands.Command
import api.commands.CommandGroup
import api.commands.CommandResult
import api.helpers.broadcast
import api.helpers.now
import api.helpers.send
import me.zeepic.blockshuffle.Game.startNextRound
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*


val skips = mutableMapOf<UUID, Boolean>()
var skipsUsed = 0

@CommandGroup("game.manage")
class GameManagementCommands {

    @Command("start", "Deletes the current game world and starts a new one.")
    fun startCommand(sender: CommandSender): CommandResult {
        sender.send("Starting game...")
        if (BlockShuffle.gameWorld != null) {
            Bukkit.getOnlinePlayers().forEach {
                it.teleport(BlockShuffle.lobbyLocation)
                it.inventory.clear()
                it.gameMode = GameMode.ADVENTURE
            }
            Bukkit.unloadWorld(BlockShuffle.gameWorld!!, false)
            BlockShuffle.gameNetherWorld?.let { Bukkit.unloadWorld(it, false) }
        }
        val worldFolder = BlockShuffle.gameWorld?.worldFolder
        val netherFolder = BlockShuffle.gameNetherWorld?.worldFolder
        val result = (worldFolder?.deleteRecursively() ?: false) && (netherFolder?.deleteRecursively() ?: false)
        if (!result) {
            sender.send("&cFailed to delete old game world!")
        }
        Game.generateNewWorld(sender) { success ->
            if (!success) return@generateNewWorld
            Game.players.clear()
            Bukkit.getOnlinePlayers().forEach { player ->
                player.teleport(BlockShuffle.gameWorld!!.spawnLocation)
                player.send("&7&oTeleported! &aA new game is starting.")
                Game.players[player.uniqueId] = Material.values().random()
                player.gameMode = GameMode.SURVIVAL
                player.inventory.clear()
                player.health = 20.0
                player.foodLevel = 20
                Bukkit.getServer().advancementIterator().forEachRemaining {
                    val progress = player.getAdvancementProgress(it)
                    progress.awardedCriteria.forEach(progress::revokeCriteria)
                }
            }
            Game.startTime = now()
            skipsUsed = 0
            skips.clear()
            sender.send("&aGame started!")
            Game.round = 0
            Settings.isGamePaused = false
            startNextRound(first = true)
        }
        return CommandResult.SUCCESS
    }

    @Command("kickplayer", "Kicks a player from the game.")
    fun kickCommand(sender: CommandSender, target: OfflinePlayer): CommandResult {
        if (target.uniqueId !in Game.players) {
            sender.send("&cThat player is not in the game!")
            return CommandResult.SILENT_FAILURE
        }
        broadcast("&c${target.name} has been kicked from the game.")
        Game.players -= target.uniqueId
        if (target.isOnline) {
            target.player?.teleport(BlockShuffle.lobbyLocation)
        }
        return CommandResult.SUCCESS
    }

    @Command("pause", "Pauses the game.")
    fun pauseCommand(sender: CommandSender): CommandResult {
        if (BlockShuffle.gameWorld == null) {
            sender.send("&cThere is no game running!")
            return CommandResult.SILENT_FAILURE
        }
        if (Settings.isGamePaused) {
            Settings.isGamePaused = false
            broadcast("&aThe game has been un-paused.")
            Game.players.mapNotNull { Bukkit.getPlayer(it.key) }.forEach {
                it.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 10, true, false, false))
                it.remainingAir = it.maximumAir
            }
            return CommandResult.SUCCESS
        }
        Settings.isGamePaused = true
        broadcast("&cThe game has been paused.")

        return CommandResult.SUCCESS
    }

    @Command("skip", "Votes to skip this round. You only get a few chances so use this wisely!")
    fun skipCommand(sender: Player): CommandResult {
        if (sender.uniqueId !in Game.players) {
            sender.send("&cYou are not in the game!")
            return CommandResult.SILENT_FAILURE
        }
        if (skipsUsed >= Settings.skipsAllowed) {
            sender.send("&cAll skips have been used (${Settings.skipsAllowed})!")
            return CommandResult.SILENT_FAILURE
        }
        if (sender.uniqueId in skips) {
            sender.send("&cSkip vote removed.")
            skips -= sender.uniqueId
            return CommandResult.SUCCESS
        }
        skips[sender.uniqueId] = true
        broadcast("&a${sender.name} &7has voted to skip this round! &7[&c${skips.size}&7/&c${Game.players.size}&7]")
        if (skips.size == Game.players.size) {
            broadcast("&aAll players have voted to skip this round!")
            skipsUsed += 1
            startNextRound()
            skips.clear()
        }
        return CommandResult.SUCCESS
    }

}