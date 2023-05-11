package me.zeepic.blockshuffle

import api.helpers.ObjectiveManager
import api.helpers.component
import api.helpers.readableTimeLength
import api.helpers.title
import api.tasks.Task
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit

@Task(1.0)
class BlockShowTask : Runnable {
    override fun run() {
        val millisecondsLeft = Settings.roundTimeMinutes * 60_000 - (System.currentTimeMillis() - Game.lastRoundStarted)
        Game.players.forEach { (uuid, block) ->
            val player = Bukkit.getPlayer(uuid) ?: return@forEach
            if (player.uniqueId in Game.hasFoundBlock) {
                player.sendActionBar("&eWaiting for next round to start.".component)
            } else if (millisecondsLeft > 10_000) {
                player.sendActionBar("&aYour block is &6${block.name.lowercase().replace("_", " ").title()}&a.".component)
            } else {
                player.sendActionBar("&cYou have &6${(millisecondsLeft / 1000).toInt()}&c seconds left to stand on your block!".component)
                player.showTitle(
                    Title.title(
                        (millisecondsLeft / 1000).toInt().coerceAtLeast(0).toString().component.color(NamedTextColor.RED),
                        Component.empty()
                    )
                )
            }
            ObjectiveManager.setScores(mapOf(
                "Time in Round: ${millisecondsLeft.coerceAtLeast(0).readableTimeLength()}" to 0,
                "Round: ${Game.round}" to 1,
            ))
            ObjectiveManager.setBoard(player)
        }
        if (millisecondsLeft <= 0 && Game.players.isNotEmpty()) {
            Game.startNextRound()
        }

    }
}