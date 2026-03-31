package com.neutrino.game.map.generation.worldgen.generators.util

import com.neutrino.game.util.Constants

data class GenerationArea(
    val minX: Int,
    val minY: Int,
    val maxX: Int,
    val maxY: Int,
    val mask: GenerationMask? = null
) {
    constructor(x: Int, y: Int): this(x, y, x + 1, y + 1)

    fun contains(x: Int, y: Int): Boolean {
        return mask?.contains(x, y) ?: true
    }

    inline fun forEachTile(action: (x: Int, y: Int) -> Unit) {
        for (x in minX until maxX)
            for (y in minY until maxY)
                if (mask?.contains(x, y) != false)
                    action.invoke(x, y)
    }

    companion object {
        val Default = GenerationArea(0, 0, Constants.ChunkSize, Constants.ChunkSize)
    }
}