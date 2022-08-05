package com.neutrino.game.domain.model.entities.utility

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion

abstract class Entity (

) {
    abstract val id: Int
    abstract val name: String
    open val description: String? = ""
    abstract var textureSrc: String
    abstract val textureNames: List<String>
    open var textureList: List<TextureAtlas.AtlasRegion> = listOf()
    abstract var texture: TextureRegion
    abstract val allowOnTop: Boolean
    abstract val allowCharacterOnTop: Boolean

    fun loadTextures(atlas: TextureAtlas) {
        textureList = buildList {
            for (name in textureNames) {
                add(atlas.findRegion(name))
            } }

    }

    abstract fun pickTexture(onMapPosition: OnMapPosition)

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

    fun getTexture(name: String): TextureAtlas.AtlasRegion {
        return textureList.find { it.name.toString() == name }!!
    }
}