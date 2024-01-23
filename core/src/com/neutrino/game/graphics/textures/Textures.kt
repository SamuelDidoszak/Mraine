package com.neutrino.game.graphics.textures

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import kotlin.random.Random

object Textures {
    val atlases: HashMap<String, TextureAtlas> = HashMap()
    private val textures: HashMap<String, TextureSprite> = HashMap()

    infix fun add(textureSprite: TextureSprite) {
        if (textures[textureSprite.texture.name] != null) {
            try {
                throw Exception("Texture name \"${textureSprite.texture.name}\" already exists!")
            } catch (e: Exception) {e.toString()}
        }
        textures[textureSprite.texture.name] = textureSprite
    }

    infix fun add(textureSprite: AnimatedTextureSprite) {
        val name = textureSprite.texture.name.substringBefore('#')
        if (textures[name] != null) {
            try {
                throw Exception("Texture name \"${name.substringBefore('#')}\" already exists!")
            } catch (e: Exception) {e.toString()}
        }
        else
            textures[name] = textureSprite
    }

    infix fun addOrReplace(textureSprite: TextureSprite) {
        if (textureSprite is AnimatedTextureSprite)
            textures[textureSprite.texture.name.substringBefore('#')] = textureSprite
        else
            textures[textureSprite.texture.name] = textureSprite
    }

    private fun new(name: String): TextureSprite? {
        val tex = textures[name] ?: return null
        if (tex is AnimatedTextureSprite)
            return AnimatedTextureSprite(
                tex.getTextureList(),
                tex.getLooping(),
                tex.animationSpeed,
                tex.lights?.copy(),
                tex.x,
                tex.y,
                tex.z
            )
        return TextureSprite(
            tex.texture,
            tex.lights?.copy(),
            tex.x,
            tex.y,
            tex.z
        )
    }

    infix fun get(name: String): TextureSprite {
        return new(name)!!
    }

    infix fun getOrNull(name: String?): TextureSprite? {
        if (name == null)
            return null
        return new(name)
    }

    fun getOrNull(random: Random, probability: Float, texture: String): TextureSprite? {
        if (random.nextFloat() * 100f <= probability)
            return new(texture)
        return null
    }

    /**
     * Picks one texture name from provided with and equal probability
     * Returns null if $to value was higher than value
     */
    fun getRandomTexture(random: Random, until: Float = 100f, textures: List<String>): TextureSprite? {
        val randVal = random.nextFloat() * 100f
        val increment = until / textures.size
        var max = increment
        for (texture in textures) {
            if (randVal < max)
                return new(texture)
            max += increment
        }
        return null
    }

    /**
     * Picks one texture name from provided with and equal probability
     * Returns the first texture if wrong data was provided
     */
    fun getRandomTexture(random: Random, texturesPercent: List<Pair<Float, List<String>>>): TextureSprite? {
        val texturesPercent = texturesPercent.sortedBy { it.first }
        val randVal = random.nextFloat() * 100f
        var texture: String? = null
        var from = 0f
        for (textureMap in texturesPercent) {
            val step = textureMap.first / textureMap.second.size
            if (randVal <= from + textureMap.first) {
                texture = textureMap.second[((randVal - from) / step).toInt()]
                break
            }

            from += textureMap.first
        }
        return getOrNull(texture)
    }
}