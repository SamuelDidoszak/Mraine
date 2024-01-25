package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.util.Constants
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.domain.use_case.Shaderable
import com.neutrino.game.graphics.shaders.ShaderParametered
import com.neutrino.game.utility.Serialize
import kotlin.random.Random

@Serialize
abstract class Entity: TextureHaver, Shaderable {
    abstract val name: String
    open val description: String? = ""
    abstract var allowOnTop: Boolean
    abstract var allowCharacterOnTop: Boolean

    override var mirrored: Boolean = false

    @Transient
    override var shaders: ArrayList<ShaderParametered?> = ArrayList(1)

    abstract fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random)

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