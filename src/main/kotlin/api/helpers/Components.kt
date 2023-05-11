package api.helpers

import me.zeepic.blockshuffle.BlockShuffle
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType

fun String.title()
        = split(" ").joinToString(" ") { it[0].uppercase() + it.drop(1) }

fun String.color() = LegacyComponentSerializer.legacyAmpersand().deserialize(this)

fun String.toEntityType() = EntityType.values().firstOrNull { this == it.name.lowercase() }

fun String.toMaterial() = Material.values().firstOrNull { this == it.name.lowercase() }

fun Component.serialize() = PlainTextComponentSerializer.plainText().serialize(this)

val String.component get() = this.color()

fun String.component(color: NamedTextColor) = this.component.color(color)

fun Component.hoverText(text: String)
    = this.hoverEvent(HoverEvent.showText(text.component(NamedTextColor.GRAY)))

operator fun Component.plus(other: Component) = this.append(other)

operator fun Collection<Component>.plus(other: Component): List<Component> {
    val mut = this.toMutableList()
    mut += other
    return mut.toList()
}

fun <M> CommandSender.send(message: M) {
    val component = if (message is Component) message
                  else message.toString().component
    this.sendMessage(
        BlockShuffle.messagePrefix
            .append(component)
            .hoverText("Server Message")
    )
}

fun <M> log(message: M) = Bukkit.getLogger().info(message.toString())

fun <M> broadcast(message: M) {
    Bukkit.broadcast(
        if (message is Component) message
        else message.toString().component
    )
}
