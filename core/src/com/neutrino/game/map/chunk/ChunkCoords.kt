package com.neutrino.game.map.chunk

import com.neutrino.game.utility.Serialize

@Serialize
class ChunkCoords(
    val x: Int,
    val y: Int,
    val z: Int
) {
    fun toHash(): Int = "$x-$y-$z".hashCode()

    override fun toString(): String {
        return "$x-$y-$z"
    }
}