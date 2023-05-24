package me.zeepic.blockshuffle

import api.EventListener
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ItemSpawnEvent

@EventListener
class ItemDropListener : Listener {
    @EventHandler
    fun onItemSpawn(event: ItemSpawnEvent) {
        event.entity.ticksLived = -6000 // Give items an additional 5 minutes till they despawn
    }

}