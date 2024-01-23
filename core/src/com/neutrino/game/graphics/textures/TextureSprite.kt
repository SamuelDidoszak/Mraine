package com.neutrino.game.graphics.textures

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import kotlin.random.Random

open class TextureSprite(
    var texture: TextureAtlas.AtlasRegion,
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Int = 1
) {
    constructor(
        texture: TextureAtlas.AtlasRegion,
        lights: LightSources?,
        x: Float = 0f,
        y: Float = 0f,
        z: Int = 1): this(texture, x, y, z) {
        this.lights = lights
    }

    var lights: LightSources? = null
    var mirrorX: Boolean = false
    var mirrorY: Boolean = false

    fun mirrorX(): TextureSprite {
        mirrorX = !mirrorX
        return this
    }

    fun mirrorY(): TextureSprite {
        mirrorY = !mirrorY
        return this
    }

    fun mirrorX(probability: Float, randomGenerator: Random): TextureSprite {
        if (randomGenerator.nextFloat() * 100 < probability)
            mirrorX = !mirrorX
        return this
    }

    fun width(): Int {
        return texture.regionWidth
    }

    fun height(): Int {
        return texture.regionHeight
    }

    fun xy(x: Float, y: Float): TextureSprite {
        this.x = x
        this.y = y
        return this
    }
}