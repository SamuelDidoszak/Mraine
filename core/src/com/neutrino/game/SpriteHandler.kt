package com.neutrino.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

class SpriteHandler (
    private val texture: Texture
) {
    private val textureRegions:  Array<Array<TextureRegion>> = TextureRegion.split(texture, 16, 16)

    val numberOfTextures = textureRegions.size * textureRegions[0].size

    fun getAnimation(animationNumber: Int): Animation<TextureRegion> {
        val number = if(animationNumber < textureRegions.size) animationNumber else 0
        val frames = com.badlogic.gdx.utils.Array<TextureRegion>(textureRegions[number].size)
        for(i in 0 until textureRegions[number].size) {
            frames.add(textureRegions[number][i])
        }
        return Animation<TextureRegion>(AnimationSpeed, frames)
    }

    fun getTexture(textureNumber: Int): TextureRegion {
        if (numberOfTextures != 0)
            return textureRegions[textureNumber / 16][textureNumber % textureRegions[0].size]
        else return TextureRegion(Texture(""))
    }
}