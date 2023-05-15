package me.zeepic.blockshuffle

import api.helpers.now
import api.tasks.Task

fun timeSinceGameStarted() = now() - Game.startTime - (Game.pauseSeconds * 1000L)

@Task(1.0)
class PauseTickTask : Runnable {
    override fun run() {
        if (Settings.isGamePaused) {
            Game.pauseSeconds += 1L
        }
    }
}