package api.helpers

import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta


fun ItemStack.named(name: String): ItemStack {
    val meta = itemMeta
    meta.displayName("&f$name"
        .component
        .decoration(TextDecoration.ITALIC, false)
    )
    return withMeta(meta)
}

fun ItemStack.description(description: String): ItemStack {
    val meta = itemMeta
    meta.lore(
        description
            .split("\n")
            .map { "&7&o$it".component }
    )
    return withMeta(meta)
}

fun ItemStack.withMeta(meta: ItemMeta): ItemStack {
    itemMeta = meta
    return this
}

