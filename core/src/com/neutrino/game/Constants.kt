package com.neutrino.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import kotlin.random.Random

const val AnimationSpeed: Float = 0.1666666666666666f
    //  Textures are provided here to omit the need of loading them multiple times
val DefaultTextures: Array<Array<TextureRegion>> = TextureRegion.split(Texture("environment/raw/tiles.png"), 16, 16)

// Level constants

const val LevelChunkSize: Int = 100

const val isSeeded: Boolean = false
const val Seed: Long = 2137213721372137
val RandomGenerator: Random = if (isSeeded) Random(Seed) else Random.Default