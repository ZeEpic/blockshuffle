package me.zeepic.blockshuffle

import api.EventListener
import api.helpers.broadcast
import api.helpers.title
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

@EventListener
class BlockStandListener : Listener {
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (!Game.players.containsKey(event.player.uniqueId)) return
        val player = event.player
        val blocks = listOf(
            player.location.block,
            player.location.block.getRelative(BlockFace.DOWN)
        )
        val targetBlock = Game.players[player.uniqueId] ?: return
        if (blocks.any { it.type == targetBlock }) {
            if (player.uniqueId !in Game.hasFoundBlock) {
                broadcast("&a${player.name} &7has found their block! It was &6${targetBlock.name.lowercase().replace("_", " ").title()}&7.")
                Game.hasFoundBlock[player.uniqueId] = true
                if (Game.hasFoundBlock.size == Game.players.size) {
                    Game.startNextRound()
                }
            }
        }
    }
}