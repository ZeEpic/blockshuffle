package me.zeepic.blockshuffle

import api.EventListener
import io.papermc.paper.event.entity.EntityPortalReadyEvent
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerRespawnEvent

@EventListener
class PlayerActionListener : Listener {
    @EventHandler
    fun onDeath(event: PlayerRespawnEvent) {
        if (event.player.uniqueId !in Game.players) return
        if (event.respawnLocation.world == BlockShuffle.gameWorld) return
        event.respawnLocation = BlockShuffle.gameWorld?.spawnLocation ?: BlockShuffle.lobbyLocation
    }

    @EventHandler
    fun onPvp(event: EntityDamageByEntityEvent) {
        if (event.entity.uniqueId !in Game.players) return
        if (event.damager.uniqueId !in Game.players) return
        if (Settings.pvp) return
        event.isCancelled = true
    }

    @EventHandler
    fun onPortal(event: EntityPortalReadyEvent) {
        val player = event.entity as? Player ?: return
        if (player.uniqueId !in Game.players) return
        when (event.targetWorld?.environment) {
            World.Environment.NORMAL -> {
                event.targetWorld = BlockShuffle.gameWorld
            }
            World.Environment.NETHER -> {
                event.targetWorld = BlockShuffle.gameNetherWorld
            }
            null -> {
                return
            }

            else -> {
                return
            }
        }
    }
}