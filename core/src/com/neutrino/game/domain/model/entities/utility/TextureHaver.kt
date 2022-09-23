package com.neutrino.game.domain.model.entities.utility

import com.badlogic.gdx.graphics.g2d.TextureAtlas

interface TextureHaver {
    val textureNames: List<String>
    var texture: TextureAtlas.AtlasRegion

    fun getTexture(name: String): TextureAtlas.AtlasRegion
    fun setTexture(name: String) {}
    fun setTexture(): TextureAtlas.AtlasRegion {
        return getTexture(textureNames[0])
    }
}