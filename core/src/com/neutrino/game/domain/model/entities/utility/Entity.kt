package com.neutrino.game.domain.model.entities.utility

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants
import com.neutrino.game.domain.use_case.Shaderable
import com.neutrino.game.graphics.shaders.ShaderParametered

abstract class Entity: TextureHaver, Shaderable {
    abstract val name: String
    open val description: String? = ""
    abstract var allowOnTop: Boolean
    abstract var allowCharacterOnTop: Boolean

    override var mirrored: Boolean = false

    override var shaders: ArrayList<ShaderParametered?> = ArrayList(1)

    abstract fun pickTexture(onMapPosition: OnMapPosition)

    override fun getTexture(name: String): TextureAtlas.AtlasRegion {
        try {
            return Constants.DefaultEntityTexture.findRegion(name)
        } catch (e: NullPointerException) {
            println("TextureName:\t$name\tdoesn't exist")
            // Default texture
            return Constants.DefaultEntityTexture.findRegion(textureNames[0])
        }
    }

    /**
     * Picks one texture name from provided with and equal probability
     * Returns null if $to value was higher than value
     */
    fun getTextureFromEqualRange(randomValue: Float, from: Float = 0f, until: Float = 100f, textures: List<String>, weights: List<Float> = listOf()): String? {
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