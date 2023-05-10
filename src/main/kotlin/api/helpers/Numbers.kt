package api.helpers

import me.zeepic.aiparkour.AIParkour
import java.util.*

fun randomBool() = AIParkour.random.nextBoolean()

fun randomInt(till: Int = 100) = AIParkour.random.nextInt(till)

fun chance(percent: Double) = randomInt(200) <= (percent * 2)

fun <T> Array<T>.random(random: Random) = this[random.nextInt(size)]

fun <T> MutableList<T>.random(random: Random) = this[random.nextInt(size)]

fun <K, V> Map<K, V>.random() = entries.toList()[randomInt(entries.size)]

fun Int.coerceAtMostIf(max: Int, predicate: (Int) -> Boolean)
        = if (predicate(this)) coerceAtMost(max) else this

operator fun Number.times(s: String) = s.repeat(this.toInt())
operator fun String.times(i: Number) = repeat(i.toInt())
