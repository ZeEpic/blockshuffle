package me.zeepic.blockshuffle

import api.helpers.*
import api.helpers.component
import api.helpers.readableTimeLength
import api.tasks.Task
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit

@Task(1.0)
class BlockShowTask : Runnable {
    override fun run() {
        // Cumulative time - including rushed rounds where all players found block before 5 minutes are up
        val timeLeftCoOp = (Settings.roundTimeMinutes * 60_000L * Game.round) - timeSinceGameStarted()
        // Rushed time - only time actually spent playing
        val timeLeftVersus = (60_000L * 5L) - (now() - Game.roundStartTime - (Game.roundPauseSeconds * 1000L)) + Game.bonusTime
        val millisecondsLeft = if (Settings.coOpMode) timeLeftCoOp else timeLeftVersus
        if (Game.players.isEmpty()) return
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
            if (!Settings.isGamePaused) {
                ObjectiveManager.setScores(
                    mapOf(
                        "Time in Round: ${millisecondsLeft.coerceAtLeast(0).readableTimeLength()}" to 0,
                        "Round: ${Game.round}" to 1,
                        "Co-op Mode: ${if (Settings.coOpMode) "Enabled" else "Disabled"}" to 2,
                        "Skips Remaining: ${Settings.skipsAllowed - skipsUsed}" to 3,
                        "Players Remaining: ${Game.players.size}" to 4,
                    )
                )
                ObjectiveManager.setBoard(player)
            }
        }
        if (!Settings.isGamePaused) {
            Bukkit.getOnlinePlayers().filter { it.uniqueId !in Game.players }
                .forEach {
                    ObjectiveManager.setScores(
                        mapOf(
                            "Time in Round: ${millisecondsLeft.coerceAtLeast(0).readableTimeLength()}" to 0,
                            "Round: ${Game.round}" to 1,
                        )
                    )
                    ObjectiveManager.setBoard(it)
                }
        }
        if (millisecondsLeft <= 0) {
            Game.startNextRound()
        }

    }
}