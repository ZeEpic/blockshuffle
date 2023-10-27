package me.zeepic.blockshuffle

import api.API
import api.EventListener
import api.commands.ArgumentParser
import api.commands.CommandGroup
import api.commands.CommandParser
import api.commands.Parser
import api.helpers.component
import api.helpers.typesAnnotatedWith
import api.tasks.Task
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class BlockShuffle : JavaPlugin() {
    override fun onEnable() {

        instance = this
        lobbyLocation = Location(Bukkit.getWorld("world"), 0.5, 120.0, 0.5)

        API.typesAnnotatedWith<ArgumentParser<*>>(Parser::class)
            .forEach(CommandParser::registerArgumentParser)

        val methods = typesAnnotatedWith<Any>(CommandGroup::class)
            .associate { it.kotlin to it.methods.toList() }
        CommandParser.generateCommandMap(methods, server)

        typesAnnotatedWith<Listener>(EventListener::class).forEach {
            server.pluginManager.registerEvents(it.getConstructor().newInstance(), this)
        }

        typesAnnotatedWith<Runnable>(Task::class).forEach {
            server.scheduler.runTaskTimer(
                this,
                it.getConstructor().newInstance(),
                0L,
                (it.getAnnotation(Task::class.java).periodSeconds * 20).toLong()
            )
        }
        getCommand("settings")?.setTabCompleter { _, _, _, args ->
            if (args.size == 1) listOf("skips", "revealbiome", "coop", "lives")
            else emptyList()
        }
    }

    companion object {
        val messagePrefix = "&3Block &bShuffle &7➮ &f".component
        lateinit var instance: BlockShuffle
        const val shortName = "blockshuffle"
        lateinit var lobbyLocation: Location

        const val gameWorldName = "game"
        var gameWorld: World? = null
        var gameNetherWorld: World? = null
        var gameEndWorld: World? = null

        val random = Random()
    }
}