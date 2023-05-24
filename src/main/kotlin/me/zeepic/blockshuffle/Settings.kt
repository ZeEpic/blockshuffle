package me.zeepic.blockshuffle

import org.bukkit.Material

object Settings {
    var roundTimeMinutes = 5
    var coOpMode = true
        set(value) {
            pvp = !value
            field = value
        }
    var easyRounds = 4
    var mediumRounds = 3
    var endGameStartRound = 30
    var lives = -1
    var pvp = !coOpMode
    var skipsAllowed = 3
    var villageDistance = 150
    var bundle = true
    var revealDesertBiome = false
        set(value) {
            if (value) {
                hardBlocks.addAll(greenBlocks)
            } else {
                hardBlocks.removeAll(greenBlocks)
            }
            field = value
        }

    var isGamePaused = false

    val easyBlocks = mutableListOf(
        Material.GRANITE,
        Material.DIORITE,
        Material.ANDESITE,
        Material.DIRT,
        Material.COARSE_DIRT,
        Material.GRASS_BLOCK,
        Material.BARREL,
        Material.CHEST,
        Material.FURNACE,
        Material.CRAFTING_TABLE,
        Material.DEEPSLATE,
        Material.COBBLED_DEEPSLATE,
        Material.POLISHED_ANDESITE,
        Material.POLISHED_DIORITE,
        Material.SMITHING_TABLE,
        Material.STONECUTTER,
        Material.STONE,
        Material.COBBLESTONE,
        Material.FLETCHING_TABLE,
        Material.GRINDSTONE,
        Material.LOOM,
        Material.CARTOGRAPHY_TABLE,
        Material.COMPOSTER,
        Material.BRICKS,
        Material.SAND,
        Material.SANDSTONE,
        Material.GLASS,
        Material.CLAY,
        Material.FARMLAND,
        Material.CHAIN,
        Material.LIGHTNING_ROD,
        Material.COAL_BLOCK,
        Material.COAL_ORE,
        Material.COPPER_ORE,
        Material.IRON_ORE,
        Material.COPPER_BLOCK,
        Material.GRAVEL,
        Material.STONE_BRICKS,
        Material.SMOOTH_STONE,
        Material.CAMPFIRE,
        Material.TRAPPED_CHEST,
        Material.LANTERN,
        Material.COBBLESTONE_WALL,
        Material.COBBLESTONE_STAIRS,
        Material.STONE_BRICK_STAIRS,
        Material.STONE_SLAB,
        Material.FLOWER_POT,
        Material.SMOKER,
        // Wools
        Material.WHITE_WOOL,
        Material.ORANGE_WOOL,
        Material.RED_WOOL,
        Material.BLACK_WOOL,
        Material.YELLOW_WOOL,
        Material.LIGHT_GRAY_WOOL,
        // Same colors in concrete
        Material.ORANGE_CONCRETE,
        Material.BLACK_CONCRETE,
        Material.RED_CONCRETE,
        Material.YELLOW_CONCRETE,
        Material.LIGHT_GRAY_CONCRETE,
        // Same colors in glass
        Material.ORANGE_STAINED_GLASS,
        Material.BLACK_STAINED_GLASS,
        Material.RED_STAINED_GLASS,
        Material.YELLOW_STAINED_GLASS,
        Material.LIGHT_GRAY_STAINED_GLASS,
    )

    val easiestBlocks = mutableListOf(
        Material.GRANITE,
        Material.DIORITE,
        Material.ANDESITE,
        Material.DIRT,
        Material.COARSE_DIRT,
        Material.GRASS_BLOCK,
        Material.BARREL,
        Material.CHEST,
        Material.FURNACE,
        Material.CRAFTING_TABLE,
        Material.POLISHED_ANDESITE,
        Material.POLISHED_DIORITE,
        Material.STONE,
        Material.COBBLESTONE,
        Material.CARTOGRAPHY_TABLE,
        Material.COMPOSTER,
        Material.BRICKS,
        Material.SAND,
        Material.SANDSTONE,
        Material.GLASS,
        Material.CLAY,
        Material.FARMLAND,
        Material.COAL_ORE,
        Material.COPPER_ORE,
        Material.GRAVEL,
        Material.SMOOTH_STONE,
        Material.COBBLESTONE_WALL,
        Material.COBBLESTONE_STAIRS,
        Material.STONE_BRICK_STAIRS,
        Material.COBBLESTONE_SLAB,
        Material.ANDESITE_STAIRS,
        Material.GRANITE_STAIRS,
        Material.DIORITE_STAIRS,
        Material.STONE_SLAB,
        Material.FLOWER_POT,
        Material.SMOKER,
        Material.WHITE_WOOL,
        Material.RED_WOOL,
        Material.YELLOW_WOOL,
        Material.RED_CONCRETE,
        Material.YELLOW_CONCRETE,
    )
    val mediumBlocks = mutableListOf(
        Material.DRIPSTONE_BLOCK,
        Material.COPPER_BLOCK,
        Material.IRON_BLOCK,
        Material.GOLD_BLOCK,
        Material.RAIL,
        Material.POWERED_RAIL,
        Material.DETECTOR_RAIL,
        Material.ACTIVATOR_RAIL,
        Material.REDSTONE_BLOCK,
        Material.LAPIS_BLOCK,
        Material.LAPIS_ORE,
        Material.JUKEBOX,
        Material.NOTE_BLOCK,
        Material.BLUE_WOOL,
        Material.BROWN_WOOL,
        Material.MAGENTA_WOOL,
        Material.PURPLE_WOOL,
        Material.PINK_WOOL,
        Material.BLUE_CONCRETE,
        Material.BROWN_CONCRETE,
        Material.MAGENTA_CONCRETE,
        Material.PURPLE_CONCRETE,
        Material.PINK_CONCRETE,
        Material.BLUE_STAINED_GLASS,
        Material.BROWN_STAINED_GLASS,
        Material.MAGENTA_STAINED_GLASS,
        Material.PURPLE_STAINED_GLASS,
        Material.PINK_STAINED_GLASS,
        Material.RAW_IRON_BLOCK,
        Material.RAW_COPPER_BLOCK,
        Material.RAW_GOLD_BLOCK,
        Material.BOOKSHELF,
        Material.OBSIDIAN,
        Material.CAULDRON,
        Material.REPEATER,
        Material.DROPPER,
        Material.DISPENSER,
        Material.BONE_BLOCK,
        Material.TNT,
        Material.OAK_LOG,
        Material.SPRUCE_LOG,
        Material.BEDROCK,
        Material.LECTERN,
        Material.BLAST_FURNACE,
        Material.GRAY_WOOL,
        Material.WHITE_CONCRETE,
        Material.GRAY_CONCRETE,
        Material.GRAY_STAINED_GLASS,
        Material.WHITE_STAINED_GLASS,
    )
    val greenBlocks = mutableListOf(
        Material.CACTUS,
        Material.GREEN_WOOL,
        Material.GREEN_CONCRETE,
        Material.GREEN_STAINED_GLASS,
        Material.LIME_WOOL,
        Material.LIME_CONCRETE,
        Material.LIME_STAINED_GLASS,
        Material.CYAN_WOOL,
        Material.CYAN_CONCRETE,
        Material.CYAN_STAINED_GLASS
    )
    val hardBlocks = mutableListOf(
        Material.COMPARATOR,
        Material.NETHERRACK,
        Material.NETHER_BRICKS,
        Material.SOUL_SAND,
        Material.SOUL_SOIL,
        Material.MAGMA_BLOCK,
        Material.DEEPSLATE_DIAMOND_ORE,
        Material.DIAMOND_BLOCK,
        Material.ANVIL,
        Material.SOUL_LANTERN,
        Material.SOUL_CAMPFIRE,
        Material.CALCITE,
        Material.AMETHYST_BLOCK,
        Material.TNT,
        Material.QUARTZ_BLOCK,
        Material.SMOOTH_BASALT,
        Material.SMOOTH_QUARTZ,
        Material.SMOOTH_QUARTZ_STAIRS,
        Material.GLOWSTONE,
        Material.CRIMSON_STEM,
        Material.CRIMSON_PLANKS,
        Material.CRIMSON_HYPHAE,
        Material.CRIMSON_NYLIUM,
        Material.WARPED_STEM,
        Material.WARPED_PLANKS,
        Material.WARPED_NYLIUM,
        Material.WARPED_HYPHAE,
        Material.SHROOMLIGHT
    )
    val endBlocks = mutableListOf(
        Material.PURPUR_BLOCK,
        Material.CHORUS_FLOWER,
        Material.CHORUS_PLANT,
        Material.END_STONE,
        Material.END_STONE_BRICKS
    )
}