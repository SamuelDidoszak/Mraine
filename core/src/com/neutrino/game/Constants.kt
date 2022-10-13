package com.neutrino.game

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import kotlin.math.abs
import kotlin.random.Random

fun Double.equalsDelta(other: Double) = abs(this - other) <= 0.005
fun Double.lessThanDelta(other: Double) = (this - other) < -0.0000001
fun Float.equalsDelta(other: Float) = abs(this - other) <= 0.005f
fun Float.lessThanDelta(other: Float) = (this - other) < -0.0000001

fun Float.compareDelta(other: Float) = if (this.equalsDelta(other)) 0
    else if (this.lessThanDelta(other)) -1 else 1

object Constants {
    const val AnimationSpeed: Float = 0.1666666666666666f
    const val MoveSpeed: Float = 0.5f
    const val RunSpeed: Float = 0.2f

    //  Global textures for items and entities

    val DefaultItemTexture: TextureAtlas = TextureAtlas("textures/items.atlas")
    val DefaultEntityTexture: TextureAtlas = TextureAtlas("textures/entities.atlas")
    val DefaultIconTexture: TextureAtlas = TextureAtlas("textures/icons.atlas")

    // Level constants
    val LevelChunkSize: Int = 100

    const val IsSeeded: Boolean = false
    const val Seed: Long = 2137213721372137
    val RandomGenerator: Random = if (IsSeeded) Random(Seed) else Random.Default
}