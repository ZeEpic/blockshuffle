package api.commands.parser

import api.commands.ParseResult
import api.commands.Parser
import api.helpers.anyNull
import org.bukkit.command.CommandSender
import java.util.*
import kotlin.reflect.KParameter

@Parser(Date::class)
class DateParser : api.commands.ArgumentParser<Date> {

    override fun parse(
        input: String,
        sender: CommandSender,
        parameter: KParameter,
        callback: (ParseResult<Date>) -> Unit
    ): Boolean {
        // mm/dd/yyyy or mm-dd-yyyy
        val regex = Regex("(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4})")
        if (!regex.matches(input)) {
            callback(ParseResult(false, message = api.commands.CommandParser.getArgError(
                parameter.index,
                input,
                "a date",
                "Use mm/dd/yyyy or mm-dd-yyyy."
            )))
            return false
        }
        val groups = regex.find(input)!!.groupValues.map { date -> date.toIntOrNull() }
        if (groups.anyNull() || groups.size != 3) {
            callback(ParseResult(false, message = api.commands.CommandParser.getArgError(
                parameter.index,
                input,
                "a date",
                "Use either mm/dd/yyyy or mm-dd-yyyy."
            )))
            return false
        }
        val calendar = Calendar.getInstance()
        calendar.set(groups[2]!!, groups[0]!!, groups[1]!!)
        callback(ParseResult(true, calendar.time))
        return true
    }
}