package api.commands

import me.zeepic.aiparkour.messaging.send
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import kotlin.reflect.KParameter

class FunctionCommand<G>(
    name: String,
    val function: KCommand,
    private val commandGroup: Class<G>,
    private val parsers: Map<KParameter, ArgumentParser<*>>,
    permission: String = "",
    description: String = "",
    usage: String = "",
    aliases: List<String> = listOf()
) : BukkitCommand(name, description, usage, aliases) {

    init {
        this.permission = permission
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>?): Boolean {
        if (!testPermission(sender)) {
            sender.send("&cYou do not have permission to use this command!")
            return true
        }

        // The arg stack is used to store arguments that are not yet parsed
        val argStack = args?.toMutableList() ?: mutableListOf()
        // Params is a map of the function's parameters and their parsed values, according to the ArgumentParsers
        val params = mutableMapOf<KParameter, Any?>()

        params[function.parameters[0]] = commandGroup.getConstructor().newInstance()
        params[function.parameters[1]] = sender
        val successes = parsers.toList().drop(1).map { (parameter, parser) ->

            val arg = argStack.firstOrNull()
            if (arg == null) {
                sender.send("&cIncorrect usage! &7&oYou did not provide enough arguments.")
                sender.send("&cUsage: &7$usage")
                return false
            }

            parser.parse(arg, sender, parameter) {
                if (it.success && argStack.isNotEmpty()) {
                    // When the parsing was a success, the argument is no longer needed
                    argStack.removeAt(0)
                }
                params[parameter] = if (parameter.isVararg && it.success) {
                    // Parse and save arguments left in argStack until one fails the parse
                    val parsedValues = argStack.takeWhile { a -> parser.parse(a, sender, parameter) {} }.toTypedArray()
                    (0..parsedValues.size).forEach { _ -> argStack.removeFirst() }
                    parsedValues
                } else {
                    // This branch either occurs when the parse was perfectly fine,
                    // or when an optional argument was allowed to be null
                    handleOptionalResult(it, parameter, sender) ?: return@parse
                }
            }

        }

        if (successes.any { !it }) {
            // If any of the arguments failed to parse, return
            return false
        }

        return when (function.callBy(params)) {
            CommandResult.SUCCESS -> true
            CommandResult.FAILURE -> false
            CommandResult.INVALID_ARGS -> {
                sender.send("&cInvalid arguments!")
                true
            }
            CommandResult.NO_PERMISSION -> {
                sender.send("&cYou do not have permission to execute this command.")
                true
            }
            CommandResult.SILENT_FAILURE -> true
        }
    }

    private fun handleOptionalResult(result: ParseResult<*>, parameter: KParameter, sender: CommandSender): Any? {
        if (result.success) {
            return result.value
        }
        if (parameter.isOptional) {
            return null
        }
        if (parameter.type.isMarkedNullable) {
            return null
        }
        sender.send(result.message)
        return null
    }

}
