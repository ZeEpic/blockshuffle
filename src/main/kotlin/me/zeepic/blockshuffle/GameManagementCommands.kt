package me.zeepic.blockshuffle

import api.commands.Command
import api.commands.CommandGroup
import api.commands.CommandResult
import api.helpers.*
import org.bukkit.*
import org.bukkit.block.Biome
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.CookingRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*


val skips = mutableMapOf<UUID, Boolean>()
var skipsUsed = 0

@CommandGroup("game.manage")
class GameManagementCommands {

    @Command("start", "Deletes the current game world and starts a new one.")
    fun startCommand(sender: CommandSender, deleteWorld: Boolean): CommandResult {
        sender.send("Starting game...")
        if (BlockShuffle.gameWorld != null && deleteWorld) {
            Bukkit.getOnlinePlayers().forEach {
                it.teleport(BlockShuffle.lobbyLocation)
                if (!Settings.preserveInventory) it.inventory.clear()
                it.gameMode = GameMode.ADVENTURE
            }
            Bukkit.unloadWorld(BlockShuffle.gameWorld!!, false)
            BlockShuffle.gameNetherWorld?.let { Bukkit.unloadWorld(it, false) }
            BlockShuffle.gameEndWorld?.let { Bukkit.unloadWorld(it, false) }
        }
        if (deleteWorld) {
            val worldFolder = BlockShuffle.gameWorld?.worldFolder
            val netherFolder = BlockShuffle.gameNetherWorld?.worldFolder
            //val endFolder = BlockShuffle.gameEndWorld?.worldFolder
            val result = (worldFolder?.deleteRecursively() ?: false) &&
                    (netherFolder?.deleteRecursively() ?: false) //&&
                    //(endFolder?.deleteRecursively() ?: false)
            if (!result) {
                sender.send("&cFailed to delete old game world!")
            }
        }
        Game.generateNewWorld(sender) { success ->
            if (!success) return@generateNewWorld
            Game.players.clear()
            Game.lives.clear()
            Game.hasFoundBlock.clear()
            Game.bonusTime = 0L
            val spawnLocation = BlockShuffle.gameWorld!!.spawnLocation
            val recipeKeys = recipeKeys()
            Bukkit.getOnlinePlayers().forEach {
                addPlayer(it, spawnLocation, recipeKeys)
            }
            Game.startTime = now()
            skipsUsed = 0
            skips.clear()
            sender.send("&aGame started!")
            if (Settings.revealDesertBiome) {
                // Find nearest desert biome
                val desertBiome = BlockShuffle.gameWorld!!.locateNearestBiome(spawnLocation, Biome.DESERT, 5_000)
                if (desertBiome == null) {
                    broadcast("&cNo desert biome was found near spawn!")
                } else {
                    broadcast(" ")
                    broadcast(
                        "&7The nearest desert biome is at &6${desertBiome.x}, ${desertBiome.z}&7(${
                            desertBiome.distance(
                                spawnLocation
                            )
                        } blocks away)."
                    )
                    broadcast(" ")
                }
            }
            Game.round = 0
            Settings.isGamePaused = false
            Game.startNextRound(first = true)
        }
        return CommandResult.SUCCESS
    }

    private fun recipeKeys(): List<NamespacedKey> {
        val recipeKeys = mutableListOf<NamespacedKey>()
        Bukkit.recipeIterator().forEachRemaining {
            when (it) {
                is ShapedRecipe -> {
                    recipeKeys += it.key
                }
                is ShapelessRecipe -> {
                    recipeKeys += it.key
                }
                is CookingRecipe<*> -> {
                    recipeKeys += it.key
                }
            }
        }
        return recipeKeys
    }

    private fun addPlayer(player: Player, spawnLocation: Location, recipeKeys: List<NamespacedKey>) {
        player.teleport(spawnLocation)
        player.send("&7&oTeleported! &aA new game is starting.")
        player.gameMode = GameMode.SURVIVAL
        if (!Settings.preserveInventory) player.inventory.clear()
        player.clearTitle()
        player.activePotionEffects.map(PotionEffect::getType).forEach(player::removePotionEffect)
        player.health = 20.0
        player.foodLevel = 20
        player.exp = 0f
        player.bedSpawnLocation = null
        Game.players[player.uniqueId] = Settings.easyBlocks[randomInt(Settings.easyBlocks.size)]
        recipeKeys.forEach(player::discoverRecipe)
        if (Settings.bundle) {
            player.inventory.addItem(ItemStack(Material.BUNDLE).named("Backpack"))
        }
        Bukkit.getServer().advancementIterator().forEachRemaining {
            val progress = player.getAdvancementProgress(it)
            progress.awardedCriteria.forEach(progress::revokeCriteria)
        }
    }

    @Command("kickplayer", "Kicks a player from the game.")
    fun kickCommand(sender: CommandSender, target: OfflinePlayer): CommandResult {
        if (target.uniqueId !in Game.players) {
            sender.send("&cThat player is not in the game!")
            return CommandResult.SILENT_FAILURE
        }
        broadcast("&c${target.name} has been kicked from the game.")
        Game.removePlayer(target.uniqueId)
        if (target.isOnline) {
            target.player?.teleport(BlockShuffle.lobbyLocation)
        }
        if (Game.players.size == Game.hasFoundBlock.size) {
            Game.startNextRound()
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
            Game.startNextRound()
        }
        return CommandResult.SUCCESS
    }

    @Command("addplayer", "Adds a player to the game, if possible.")
    fun addPlayerCommand(sender: CommandSender, target: Player): CommandResult {
        if (target.uniqueId in Game.players) {
            sender.send("&cThat player is already in the game!")
            return CommandResult.SILENT_FAILURE
        }
        if (Game.players.isEmpty()) {
            sender.send("&cThere is no game running!")
            return CommandResult.SILENT_FAILURE
        }
        addPlayer(target, BlockShuffle.gameWorld!!.spawnLocation, recipeKeys())
        broadcast("&a${target.name} has been added to the game!")
        return CommandResult.SUCCESS
    }

    @Command("easymode", "Toggles easy mode.")
    fun easyModeCommand(sender: CommandSender, target: Player): CommandResult {
        if (target.uniqueId !in Game.easyMode) {
            Game.easyMode += target.uniqueId
        } else {
            Game.easyMode -= target.uniqueId
        }
        sender.send("&aEasy mode for ${target.name} has been toggled.")
        if ((sender as? Player)?.uniqueId != target.uniqueId) {
            target.sendMessage(" ")
            target.send("You are now ${if (target.uniqueId in Game.easyMode) "" else "not "}in easy mode.")
            target.sendMessage(" ")
        }
        return CommandResult.SUCCESS
    }

    @Command("round", "Gets the current round.")
    fun roundCommand(sender: CommandSender, round: Int): CommandResult {
        if (Game.round <= 0) {
            sender.send("&cThere is no game running!")
            return CommandResult.SILENT_FAILURE
        }
        Game.round = round
        sender.send("&7The current round is &6${Game.round}&7.")
        return CommandResult.SUCCESS
    }

    @Command("addtime", "Adds time to the current round.")
    fun addTimeCommand(sender: CommandSender, seconds: Int): CommandResult {
        if (Game.round <= 0) {
            sender.send("&cThere is no game running!")
            return CommandResult.SILENT_FAILURE
        }
        Game.bonusTime += seconds * 1000L
        sender.send("&7Bonus time is now &6${Game.bonusTime / 1000L} seconds&7.")
        sender.send("&7Added &6${(seconds * 1000L).readableTimeLength()} &7to the current round.")
        return CommandResult.SUCCESS
    }

    @Command("setgameblock", "Sets the block of a player.")
    fun setBlockCommand(sender: CommandSender, target: Player, block: Material): CommandResult {
        if (target.uniqueId !in Game.players) {
            sender.send("&cThat player is not in the game!")
            return CommandResult.SILENT_FAILURE
        }
        Game.players[target.uniqueId] = block
        broadcast("&7${target.name}'s new block is &6${Game.players[target.uniqueId]?.name?.lowercase()?.replace("_", " ") ?: "none"}&7.")
        return CommandResult.SUCCESS
    }

}