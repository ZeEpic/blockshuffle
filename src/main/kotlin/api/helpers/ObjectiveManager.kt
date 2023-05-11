package api.helpers

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.*


object ObjectiveManager {
    private var objective: Objective? = null
    private lateinit var board: Scoreboard
    private var scoreValue = 0
    private val resultFormat = ChatColor.GRAY.toString() + "%s"// + ChatColor.WHITE + " | " + ChatColor.GOLD + "%s"

    /**
     * Adds a new line to the scoreboard.
     * @param number The number to put on the right side. This shouldn't be used to display a value, just incremented.
     * @param name The name of the new line that will be added.
     * @param value The value of the new line that will be added. Formatted as, "&7name &f| &6value"
     */
    private fun addScore(number: Int, name: String, value: Int) {
        var result = resultFormat.format(name)//, value)
        if (result.length > 40) result = result.substring(0, 39)
        val score = objective!!.getScore(result)
        score.score = number
    }

    /**
     * Sets a player's scoreboard to this one.
     * @param player The game player to modify.
     */
    fun setBoard(player: Player) {
        player.scoreboard = board
    }

    /**
     * Sets the lines of this scoreboard according to the map. It's title will always be the same.
     * @param scores The names and values of the lines in the scoreboard. Values are always integers.
     */
    fun setScores(scores: Map<String, Int>) {
        val manager = Bukkit.getScoreboardManager()
        board = manager.newScoreboard
        objective = board.registerNewObjective(
            "BlockShuffle-1",
            "dummy",
            "&3&lBlock &b&lShuffle".component
        )
        objective!!.displaySlot = DisplaySlot.SIDEBAR
        scoreValue = scores.size
        scores.forEach { (name: String, value: Int) ->
            scoreValue -= 1
            addScore(scoreValue, name, value)
        }
    }
}
