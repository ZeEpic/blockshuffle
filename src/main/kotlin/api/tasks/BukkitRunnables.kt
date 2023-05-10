package api.tasks

import me.zeepic.aiparkour.AIParkour
import org.bukkit.Bukkit


/*
    These are some functions that simplify the Bukkit scheduler system
        and take advantage of Kotlin's lambda functions
*/

fun doLater(seconds: Int, task: () -> Unit) {
    doLaterPrecise(seconds * 20L, task)
}

fun doLaterPrecise(ticks: Long, task: () -> Unit) {
    Bukkit
        .getScheduler()
        .runTaskLater(
            AIParkour.instance,
            Runnable(task),
            ticks
        )
}

fun afterTick(task: () -> Unit) {
    Bukkit
        .getScheduler()
        .runTaskLater(
            AIParkour.instance,
            Runnable(task),
            1
        )
}

fun now(task: () -> Unit) {
    Bukkit
        .getScheduler()
        .runTask(
            AIParkour.instance,
            Runnable(task)
        )
}

fun runAsync(runnable: () -> Unit) {
    Bukkit.getScheduler().runTaskAsynchronously(AIParkour.instance, runnable)
}

fun <T> bukkitSuspend(task: () -> T, onComplete: (T) -> Unit = {}) {

    val scheduler = Bukkit.getScheduler()

    // Create a runnable to tell us when the task is completed
    val runnable = Runnable {
        val taskComplete = task()
        scheduler.runTask(AIParkour.instance, Runnable { onComplete(taskComplete) })
    }

    // Run the async task
    scheduler.runTaskAsynchronously(AIParkour.instance, runnable)

}
