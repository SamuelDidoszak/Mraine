package com.neutrino.game.domain.model.entities.utility

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion

interface TextureHaver {
    var textureSrc: String
    val textureNames: List<String>
    var textureList: List<TextureAtlas.AtlasRegion>
    var texture: TextureAtlas.AtlasRegion

    fun loadTextures(atlas: TextureAtlas) {
        textureList = buildList {
            for (name in textureNames) {
                add(atlas.findRegion(name))
            } }
    }

    fun getTexture(name: String): TextureAtlas.AtlasRegion {
        return textureList.find { it.name.toString() == name }!!
    }

    fun setTexture(name: String) {
        texture = textureList.find { it.name.toString() == name }!!
    }
}