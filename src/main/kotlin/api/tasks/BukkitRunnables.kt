package api.tasks

import me.zeepic.blockshuffle.BlockShuffle
import org.bukkit.Bukkit

fun runAsync(runnable: () -> Unit) {
    Bukkit.getScheduler().runTaskAsynchronously(BlockShuffle.instance, runnable)
}

