package com.neutrino.game.map.generation.worldgen.util

import com.neutrino.game.util.Constants
import com.neutrino.game.utility.Serialize

@Serialize
class ChunkCoords(
    val x: Int,
    val y: Int,
    val z: Int
) {
    fun toHash(): Int = "$x-$y-$z".hashCode()

    fun worldMin(): WorldTilePos =
        WorldTilePos(x * Constants.ChunkSize, y * Constants.ChunkSize, z)

    override fun toString(): String {
        return "$x-$y-$z"
    }
}
