package me.zeepic.blockshuffle

import api.helpers.*
import kong.unirest.Unirest
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.command.CommandSender
import java.util.*
import java.util.concurrent.CompletableFuture

object Game {
    var round = 0
    val players = mutableMapOf<UUID, Material>()
    val hasFoundBlock = mutableMapOf<UUID, Boolean>()
    var lastRoundStarted = -1L
    fun startNextRound(first: Boolean = false) {
        round += 1
        lastRoundStarted = now()
        if (!first) {
            players.forEach { (uuid, block) ->
                if (uuid !in hasFoundBlock) {
                    Bukkit.getPlayer(uuid)?.send("&7Time's up!")
                    players.remove(uuid)
                    broadcast("&a${Bukkit.getPlayer(uuid)?.name} &7has been \"a lemon ate it\" (block was ${block.name.lowercase().replace("_", " ").title()})!")
                    if (players.size <= 1 && Bukkit.getOnlinePlayers().size != 1) {
                        endGame()
                        return
                    }
                    if (Settings.coOpMode) {
                        broadcast("Co-op mode is enabled, and the game will not continue without all players.")
                        endGame()
                        return
                    }
                    Bukkit.getPlayer(uuid)?.gameMode = GameMode.SPECTATOR
                }
            }
        }
        hasFoundBlock.clear()
        broadcast("&aRound $round has started!")
        Bukkit.getOnlinePlayers()
            .filter { it.uniqueId in players }
            .forEach {
                val block = if (round <= Settings.easyRounds) {
                    Settings.easyBlocks[randomInt(Settings.easyBlocks.size)]
                } else if (round <= Settings.mediumRounds) {
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
        if (players.size == 1) {
            broadcast("&a${Bukkit.getOfflinePlayer(players.keys.first()).name} &7is the winner!")
        }
        players.clear()
        hasFoundBlock.clear()
        round = 0
        lastRoundStarted = -1L
        Bukkit.getOnlinePlayers().forEach {
            it.teleport(BlockShuffle.lobbyLocation)
            it.send("Teleported to the lobby!")
            it.inventory.clear()
            it.gameMode = GameMode.ADVENTURE
        }
    }

    fun generateNewWorld(creator: CommandSender, callback: (Boolean) -> Unit) {
        val seed = generateSeed()
        if (seed == null) {
            creator.send("&cFailed to generate a new seed (API request did not succeed)!")
            callback(false)
            return
        }
        val world = WorldCreator(BlockShuffle.gameWorldName)
            .seed(seed)
            .createWorld()
        if (world == null) {
            creator.send("&cFailed to create a new game world!")
            callback(false)
            return
        }
        world.setGameRule(GameRule.DO_INSOMNIA, false)
        BlockShuffle.gameWorld = world
        creator.send("Created new world with seed $seed!")
        val loadingRadius = 16
        val futures = mutableListOf<CompletableFuture<Chunk>>()
        for (x in -loadingRadius..loadingRadius) {
            for (z in -loadingRadius.. loadingRadius) {
                futures += world.getChunkAtAsync(x, z, true)
            }
        }
        CompletableFuture.allOf(*futures.toTypedArray()).thenRun {
            val nether = WorldCreator(BlockShuffle.gameNetherWorldName)
                .seed(seed)
                .environment(World.Environment.NETHER)
                .createWorld()
            if (nether == null) {
                creator.send("&cFailed to create a new game nether!")
                callback(false)
                return@thenRun
            }
            BlockShuffle.gameNetherWorld = nether
            creator.send("Created new nether world!")
            futures.clear()
            for (x in -loadingRadius..loadingRadius) {
                for (z in -loadingRadius.. loadingRadius) {
                    futures += world.getChunkAtAsync(x, z, true)
                }
            }
            CompletableFuture.allOf(*futures.toTypedArray()).thenRun {
                broadcast("&aNew world has been generated!")
                callback(true)
            }
        }
    }


    private fun generateSeed(): Long? {
        // Check the seed hunt api for a seed with a village near spawn
        val response = Unirest.post("https://seedhunt.net/api/verified?limit=50&page=0")
            .header("Content-Type", "application/json")
            .body("{\r\n    \"filter\": \"village_distance<=${Settings.villageDistance}\",\r\n    \"sort\": [\r\n        {\r\n            \"field\": \"\",\r\n            \"direction\": \"desc\"\r\n        }\r\n    ]\r\n}")
            .asJson()
        if (response.status != 200) return null
        val seeds = response.body.getObject().getJSONArray("seeds")
        val seed = seeds.getJSONObject(BlockShuffle.random.nextInt(seeds.length()))
        return seed.getLong("seed")
    }
}