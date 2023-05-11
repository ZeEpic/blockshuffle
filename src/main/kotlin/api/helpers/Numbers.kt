package api.helpers

import me.zeepic.blockshuffle.BlockShuffle

fun randomInt(till: Int = 100) = BlockShuffle.random.nextInt(till)

operator fun Number.times(s: String) = s.repeat(this.toInt())
operator fun String.times(i: Number) = repeat(i.toInt())
