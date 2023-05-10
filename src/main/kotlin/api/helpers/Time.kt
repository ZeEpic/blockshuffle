package api.helpers

import java.text.SimpleDateFormat
import java.time.Duration
import java.time.format.DateTimeParseException
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

val Long.dateFormat: String
    get() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this
        if (calendar.get(Calendar.YEAR) > 2033) return "forever"
        return SimpleDateFormat("MMM d, yyyy 'at' HH:mm:ss").format(Date(this))
    }

fun String.milliseconds(): Long {
    // PnDTnHnMn.nS
    if (equals("forever")) return Long.MAX_VALUE
    val text = if ("smh".any { it in this }) "T$this" else this
    val parsed: Duration
    try {
        parsed = Duration.parse("P${text.uppercase()}")
    } catch (e: DateTimeParseException) {
        return Long.MAX_VALUE
    }
    return parsed.toMillis()
}

fun Long.readableTimeLength(): String {
    if (milliseconds.isInfinite() || milliseconds.inWholeDays > 1000) return "a very long time"
    val str = milliseconds.toString()
    return str.split(" ")
        .mapNotNull {
            val longer = when (it.last()) {
                'd' -> "day"
                'h' -> "hour"
                'm' -> "minute"
                's' -> "second"
                else -> return@mapNotNull null
            }
            val amount = it.dropLast(1)
                .split(".")
                .getOrElse(0) { "" }
                .takeIf { amount -> amount.toLongOrNull() != null }
                ?: "0"
            "$amount $longer${if (amount == "1") "" else "s"}" // i.e. "10 days" or "1 hour"
        }
        .joinToString()
}

fun now() = System.currentTimeMillis()
