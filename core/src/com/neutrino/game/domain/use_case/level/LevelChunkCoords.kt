package com.neutrino.game.domain.use_case.level

import kotlinx.serialization.Serializable

@Serializable
data class LevelChunkCoords(
    val x: Int,
    val y: Int,
    val z: Int
) {
    fun toHash(): Int = "$x-$y-$z".hashCode()

    override fun toString(): String {
        return "$x-$y-$z"
    }
}
