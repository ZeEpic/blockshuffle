package me.zeepic.aiparkour.players

import api.tasks.runAsync
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

object OfflineManager {
    private val offlinePlayerCache = mutableMapOf<String, UUID>() // Name to UUID

    fun getOfflinePlayer(name: String, callback: (OfflinePlayer?) -> Unit) {
        if (name in offlinePlayerCache) return callback(Bukkit.getOfflinePlayer(offlinePlayerCache[name]!!))
        runAsync {
            val offlinePlayer = Bukkit.getOfflinePlayer(name)
            if (offlinePlayer.hasPlayedBefore()) {
                offlinePlayerCache[name] = offlinePlayer.uniqueId
                callback(offlinePlayer)
            } else {
                callback(null)
            }
        }
    }
}