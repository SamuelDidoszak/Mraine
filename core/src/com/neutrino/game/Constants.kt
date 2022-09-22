package com.neutrino.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import kotlin.math.abs
import kotlin.random.Random

fun Double.equalsDelta(other: Double) = abs(this - other) <= 0.005
fun Double.lessThanDelta(other: Double) = (this - other) < -0.0000001
fun Float.equalsDelta(other: Float) = abs(this - other) <= 0.005f
fun Float.lessThanDelta(other: Float) = (this - other) < -0.0000001

object Constants {
    const val AnimationSpeed: Float = 0.1666666666666666f
    const val MoveSpeed: Float = 0.5f
    const val RunSpeed: Float = 0.2f

    //  Textures are provided here to omit the need of loading them multiple times
    val DefaultTextures: Array<Array<TextureRegion>> =
        TextureRegion.split(Texture("environment/raw/tiles.png"), 16, 16)

    val DefaultItemTexture: TextureAtlas = TextureAtlas("items/items.atlas")

    // Level constants
    val LevelChunkSize: Int = 100

    const val IsSeeded: Boolean = true
    const val Seed: Long = 2137213721372137
    val RandomGenerator: Random = if (IsSeeded) Random(Seed) else Random.Default
}