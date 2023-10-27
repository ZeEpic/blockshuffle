package me.zeepic.blockshuffle

import api.helpers.*
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.World.Environment
import org.bukkit.command.CommandSender
import org.bukkit.potion.PotionEffect
import java.util.*
import java.util.concurrent.CompletableFuture

object Game {
    var round = 0
    var startTime = -1L
    val players = mutableMapOf<UUID, Material>()
    val hasFoundBlock = mutableMapOf<UUID, Boolean>()
    val lives = mutableMapOf<UUID, Int>()
    val easyMode = mutableListOf<UUID>()
    var pauseSeconds = 0L
    var bonusTime = 0L

    var roundStartTime = -1L
    var roundPauseSeconds = 0L

    fun startNextRound(first: Boolean = false) {
        if (players.isEmpty()) return
        round += 1
        roundStartTime = now()
        roundPauseSeconds = 0L
        if (!first && skips.size != players.size) {
            val removeMe = mutableListOf<UUID>()
            val preRemovalPlayerCount = players.size
            players.forEach { (uuid, block) ->
                if (uuid !in hasFoundBlock) {
                    Bukkit.getPlayer(uuid)?.send("&7Time's up!")
                    removeMe += uuid
                    broadcast("&a${Bukkit.getPlayer(uuid)?.name} &7has been \"a lemon ate it\" (block was ${block.name.lowercase().replace("_", " ").title()})!")
                    Bukkit.getPlayer(uuid)?.gameMode = GameMode.SPECTATOR
                }
            }
            removeMe.forEach { removePlayer(it, removeMe, preRemovalPlayerCount) }
        }
        skips.clear()
        hasFoundBlock.clear()
        broadcast("&aRound $round has started!")
        Bukkit.getOnlinePlayers()
            .filter { it.uniqueId in players }
            .forEach {
                var block = if (round <= Settings.easyRounds) {
                    Settings.easyBlocks[randomInt(Settings.easyBlocks.size)]
                } else if (round <= Settings.mediumRounds + Settings.easyRounds) {
                    if (randomInt(4) == 1) {
                        Settings.easyBlocks[randomInt(Settings.easyBlocks.size)]
                    } else {
                        Settings.mediumBlocks[randomInt(Settings.mediumBlocks.size)]
                    }
                } else if (round >= Settings.endGameStartRound) {
                    if (randomInt(5) == 1) {
                        Settings.endBlocks[randomInt(Settings.endBlocks.size)]
                    } else {
                        hardRoundsBlock()
                    }
                } else {
                    hardRoundsBlock()
                }
                if (it.uniqueId in easyMode) {
                    it.send("&7You are in easy mode! You will only get easy blocks.")
                    block = if (round <= Settings.easyRounds) {
                        Settings.easiestBlocks[randomInt(Settings.easiestBlocks.size)]
                    } else {
                        Settings.easyBlocks[randomInt(Settings.easyBlocks.size)]
                    }
                }
                players[it.uniqueId] = block
                val blockName = block.name.lowercase().replace("_", " ").title()
                it.send("&7You have &6${Settings.roundTimeMinutes}&7 minutes to stand on your block.")
                if (Settings.coOpMode) {
                    broadcast("&a${it.name}&7's block is $blockName.")
                } else {
                    it.send("Your block is $blockName.")
                }
                it.showTitle(
                    Title.title(
                        "&aYour block is $blockName".component,
                        "&7Round $round".component
                    )
                )
            }
    }

    private fun hardRoundsBlock() = if (randomInt(8) == 1) {
        Settings.easyBlocks[randomInt(Settings.easyBlocks.size)]
    } else if (randomInt(4) == 1) {
        Settings.mediumBlocks[randomInt(Settings.mediumBlocks.size)]
    } else {
        Settings.hardBlocks[randomInt(Settings.hardBlocks.size)]
    }

    private fun endGame() {
        broadcast("&aThe game has ended!")
        if (hasFoundBlock.all { false } || hasFoundBlock.isEmpty()) {
            broadcast("It's a tie! No one found their block.")
        } else if (players.size == 1) {
            broadcast("&a${Bukkit.getOfflinePlayer(players.keys.first()).name} &7is the winner!")
        }
        val rounds = round - 2 - skipsUsed
        broadcast("You've reached round ${round - 1}! That means you've completed a total of $rounds round${if (rounds == 1) "" else "s"}!")
        players.clear()
        hasFoundBlock.clear()
        round = 0
        Bukkit.getOnlinePlayers().forEach {
            it.teleport(BlockShuffle.lobbyLocation)
            it.send("Teleported to the lobby!")
            if (!Settings.preserveInventory) it.inventory.clear()
            it.clearTitle()
            it.bedSpawnLocation = null
            it.exp = 0f
            it.activePotionEffects.map(PotionEffect::getType).forEach(it::removePotionEffect)
            it.gameMode = GameMode.ADVENTURE
        }
    }

    private fun createWorldAsync(seed: Long, environment: Environment, callback: (String, World?) -> Unit) {
        val world = WorldCreator(BlockShuffle.gameWorldName + "_" + environment.name.lowercase())
            .seed(seed)
            .environment(environment)
            .createWorld()
        val environmentName = environment.name.lowercase().replace("_", " ")
        if (world == null) {
            callback("&cFailed to create a new game world ($environmentName)!", null)
            return
        }
        world.setGameRule(GameRule.DO_INSOMNIA, false)
        world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 1)
        val loadingRadius = 16
        val futures = mutableListOf<CompletableFuture<Chunk>>()
        for (x in -loadingRadius..loadingRadius) {
            for (z in -loadingRadius.. loadingRadius) {
                futures += world.getChunkAtAsync(x, z, true)
            }
        }
        CompletableFuture.allOf(*futures.toTypedArray()).thenRun {
            callback("Created new $environmentName world!", world)
        }
    }

    fun generateNewWorld(creator: CommandSender, callback: (Boolean) -> Unit) {
        val seed = generateSeed()
        if (seed == null) {
            creator.send("&cFailed to generate a new seed (API request did not succeed)!")
            callback(false)
            return
        }
        createWorldAsync(seed, Environment.NORMAL) { message, world ->
            if (world == null) {
                creator.send(message)
                callback(false)
            } else {
                broadcast(message)
                BlockShuffle.gameWorld = world
                createWorldAsync(seed, Environment.NETHER) { message2, nether ->
                    if (nether == null) {
                        creator.send(message2)
                        callback(false)
                    } else {
                        broadcast(message2)
                        BlockShuffle.gameNetherWorld = nether
                        callback(true)
                        /*
                        createWorldAsnyc(seed, Environment.THE_END) { message3, end ->
                            if (end == null) {
                                creator.send(message3)
                                callback(false)
                            } else {
                                broadcast(message3)
                                BlockShuffle.gameEndWorld = end
                                callback(true)
                            }
                        }
                        */
                    }
                }
            }
        }
    }


    private fun generateSeed(): Long? {
        return Random().nextLong()
//        // Check the seed hunt api for a seed with a village near spawn
//        val response = Unirest.post("https://seedhunt.net/api/verified?limit=50&page=0")
//            .header("Content-Type", "application/json")
//            .body("{\r\n    \"filter\": \"village_distance<=${Settings.villageDistance}\",\r\n    \"sort\": [\r\n        {\r\n            \"field\": \"\",\r\n            \"direction\": \"desc\"\r\n        }\r\n    ]\r\n}")
//            .asJson()
//        if (response.status != 200) return null
//        val seeds = response.body.getObject().getJSONArray("seeds")
//        val seed = seeds.getJSONObject(BlockShuffle.random.nextInt(seeds.length()))
//        return seed.getLong("seed")
    }

    fun removePlayer(id: UUID, removeMe: List<UUID> = emptyList(), preRemovalPlayerCount: Int = players.size) {
        players -= id
        hasFoundBlock -= id
        skips -= id
        lives -= id
        if (preRemovalPlayerCount == 1) { // Single-player game?
            if (removeMe.isNotEmpty()) {
                broadcast("&aYou lost the game!")
                endGame()
                return
            }
        } else {
            if (Settings.coOpMode && removeMe.isNotEmpty()) {
                broadcast("Co-op mode is enabled, and the game will not continue without all players.")
                endGame()
                return
            }
            if (players.size <= 1) {
                endGame()
                return
            }
            if (hasFoundBlock.isEmpty()) {
                endGame()
                return
            }
        }
    }
}