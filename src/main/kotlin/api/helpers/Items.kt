package api.helpers

import me.zeepic.aiparkour.AIParkour
import me.zeepic.aiparkour.messaging.component
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.CreatureSpawner
import org.bukkit.block.banner.Pattern
import org.bukkit.block.banner.PatternType
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.*
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType

fun ItemStack.unbreakable(): ItemStack {
    itemMeta = itemMeta.apply {
        isUnbreakable = true
    }
    return this
}

fun ItemStack.color(color: Color): ItemStack {
    itemMeta = itemMeta.apply {
        if (this is LeatherArmorMeta) {
            this.setColor(color)
        }
    }
    return this
}


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

fun ItemStack.description(description: Component): ItemStack {
    val meta = itemMeta
    meta.lore(listOf(description))
    return withMeta(meta)
}

fun ItemStack.glowing(): ItemStack {
    addUnsafeEnchantment(Enchantment.BINDING_CURSE, 0)
    val meta = itemMeta
    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
    return withMeta(meta)
}

fun ItemStack.isGlowing(): Boolean {
    if (enchantments.containsKey(Enchantment.BINDING_CURSE)) return enchantments[Enchantment.BINDING_CURSE] == 0
    return false
}

fun ItemStack.potionTypeOf(type: PotionEffectType, seconds: Int): ItemStack {
    val meta = itemMeta as PotionMeta
    meta.basePotionData = PotionData(PotionType.AWKWARD)
    meta.addCustomEffect(PotionEffect(type, seconds * 20, 0, false, true, true), true)
    return withMeta(meta)
}

fun ItemStack.awkwardPotion(): ItemStack {
    val meta = itemMeta as PotionMeta
    meta.basePotionData = PotionData(PotionType.AWKWARD)
    return withMeta(meta)
}

fun ItemStack.isAwkwardPotion(): Boolean {
    val meta = itemMeta
    if (meta !is PotionMeta) return false
    return meta.basePotionData == PotionData(PotionType.AWKWARD)
}

fun ItemStack.withMeta(meta: ItemMeta): ItemStack {
    itemMeta = meta
    return this
}

fun ItemStack.withMeta(meta: (ItemMeta) -> Unit): ItemStack {
    editMeta {
        meta(it)
    }
    return this
}

val uniqueKey = NamespacedKey(AIParkour.instance, "unique")

fun ItemStack.unique(name: String): ItemStack {
    val meta = itemMeta
    meta.persistentDataContainer.set(uniqueKey, PersistentDataType.STRING, name)
    return withMeta(meta)
}

fun ItemStack.asSpawnerOf(type: EntityType): ItemStack {
    val meta = itemMeta
    if (meta !is BlockStateMeta) return this
    val state = meta.blockState
    if (state !is CreatureSpawner) return this
    state.spawnedType = type
    state.maxNearbyEntities = 10
    meta.blockState = state
    return withMeta(meta)
}

fun ItemStack.damaged(amount: Int): ItemStack {
    val meta = itemMeta
    if (meta !is Damageable) return this
    if (meta.isUnbreakable) return this
    meta.damage += amount
    if (meta.damage >= type.maxDurability) {
        return ItemStack(Material.AIR)
    }
    return withMeta(meta)
}

val Material.isArmor: Boolean
    get() = name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE") || name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS")

fun ominousBannerPatterns(): List<Pattern> {
    return listOf(
        Pattern(DyeColor.CYAN, PatternType.RHOMBUS_MIDDLE),
        Pattern(DyeColor.LIGHT_GRAY, PatternType.STRIPE_BOTTOM),
        Pattern(DyeColor.GRAY, PatternType.STRIPE_CENTER),
        Pattern(DyeColor.LIGHT_GRAY, PatternType.BORDER),
        Pattern(DyeColor.BLACK, PatternType.STRIPE_MIDDLE),
        Pattern(DyeColor.LIGHT_GRAY, PatternType.HALF_HORIZONTAL),
        Pattern(DyeColor.LIGHT_GRAY, PatternType.CIRCLE_MIDDLE),
        Pattern(DyeColor.BLACK, PatternType.BORDER)
    )
}

fun raidCaptainBanner(): ItemStack {
    val item = ItemStack(Material.WHITE_BANNER)
    val meta = item.itemMeta as BannerMeta
    meta.patterns = ominousBannerPatterns()
    item.addItemFlags(ItemFlag.HIDE_DYE)
    meta.displayName(Component.translatable("block.minecraft.ominous_banner"))
    return item.withMeta(meta)
}

fun ItemStack.lootingLevel() = enchantments[Enchantment.LOOT_BONUS_MOBS]?.toDouble() ?: 0.0
