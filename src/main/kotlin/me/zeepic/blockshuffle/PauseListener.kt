package me.zeepic.blockshuffle

import api.EventListener
import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.GameMode
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.FluidLevelChangeEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.event.entity.ItemDespawnEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent

@EventListener
class PauseListener : Listener {
    private fun Cancellable.cancelIfPaused() {
        if (Settings.isGamePaused) {
            isCancelled = true
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        event.cancelIfPaused()
    }
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (event.player.gameMode != GameMode.SURVIVAL) return
        event.cancelIfPaused()
    }
    @EventHandler
    fun onEntityMove(event: EntityMoveEvent) {
        event.cancelIfPaused()
    }
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (event.player.gameMode != GameMode.SURVIVAL) return
        event.cancelIfPaused()
    }
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.whoClicked.gameMode != GameMode.SURVIVAL) return
        event.cancelIfPaused()
    }
    @EventHandler
    fun onEntityInteract(event: EntityInteractEvent) {
        event.cancelIfPaused()
    }
    @EventHandler
    fun onEntityTick(event: EntityTargetEvent) {
        event.cancelIfPaused()
    }
    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        event.cancelIfPaused()
    }
    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        event.cancelIfPaused()
    }
    @EventHandler
    fun onItemDespawn(event: ItemDespawnEvent) {
        event.cancelIfPaused()
    }
    @EventHandler
    fun onFireTick(event: BlockIgniteEvent) {
        event.cancelIfPaused()
    }
    @EventHandler
    fun onFluidTick(event: FluidLevelChangeEvent) {
        event.cancelIfPaused()
    }
}