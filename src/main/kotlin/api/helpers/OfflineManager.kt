package api.helpers

import api.tasks.runAsync
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.util.*

private typealias Name = String

object OfflineManager {
    private val offlinePlayerCache = mutableMapOf<Name, UUID>()

    fun getOfflinePlayer(name: Name, callback: (OfflinePlayer?) -> Unit) {
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