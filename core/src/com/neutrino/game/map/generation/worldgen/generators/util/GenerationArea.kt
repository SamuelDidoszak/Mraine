package com.neutrino.game.map.generation.worldgen.generators.util

data class GenerationArea(
    val minX: Int,
    val minY: Int,
    val maxX: Int,
    val maxY: Int,
    val mask: GenerationMask? = null
) {
    fun contains(x: Int, y: Int): Boolean {
        return mask?.contains(x, y) ?: true
    }

    inline fun forEachTile(action: (x: Int, y: Int) -> Unit) {
        for (x in minX until maxX)
            for (y in minY until maxY)
                if (mask?.contains(x, y) != false)
                    action.invoke(x, y)
    }
}