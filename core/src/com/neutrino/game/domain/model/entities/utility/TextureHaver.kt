package com.neutrino.game.domain.model.entities.utility

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants

interface TextureHaver {
    val textureNames: List<String>
    var texture: TextureAtlas.AtlasRegion
    var mirrored: Boolean

    fun getTexture(name: String): TextureAtlas.AtlasRegion
    fun setTexture(name: String) {}
    fun setTexture(): TextureAtlas.AtlasRegion {
        return getTexture(textureNames[0])
    }
    fun mirror() {
        mirrored = !mirrored
    }
    fun mirror(probability: Float) {
        if (Constants.RandomGenerator.nextFloat() * 100 < probability)
            mirrored = !mirrored
    }
}