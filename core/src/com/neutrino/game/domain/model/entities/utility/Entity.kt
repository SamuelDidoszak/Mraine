package com.neutrino.game.domain.model.entities.utility

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants

abstract class Entity: TextureHaver {
    abstract val name: String
    open val description: String? = ""
    abstract val allowOnTop: Boolean
    abstract val allowCharacterOnTop: Boolean

    abstract fun pickTexture(onMapPosition: OnMapPosition)

    override fun getTexture(name: String): TextureAtlas.AtlasRegion {
        return Constants.DefaultEntityTexture.findRegion(name)
    }

    /**
     * Picks one texture name from provided with and equal probability
     * Returns null if $to value was higher than value
     */
    fun getTextureFromEqualRange(randomValue: Float, from: Float = 0f, until: Float, textures: List<String>, weights: List<Float> = listOf()): String? {
        val increment = (until - from) / textures.size
        var max = from + increment
        for (texture in textures) {
            if (randomValue < max)
                return texture
            max += increment
        }
        return null
    }
}