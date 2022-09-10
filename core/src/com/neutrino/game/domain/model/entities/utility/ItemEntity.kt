package com.neutrino.game.domain.model.entities.utility

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.items.Item

class ItemEntity(val item: Item): Entity() {
    override val id: Int = 0
    override val name: String = item.name
    override val allowOnTop: Boolean = true
    override val allowCharacterOnTop: Boolean = true


    override var textureSrc: String = item.textureSrc
    override val textureNames: List<String> = item.textureNames
    override var texture: TextureAtlas.AtlasRegion = item.texture

    val pickedTexture: String? = item.pickedTexture

    override fun pickTexture(onMapPosition: OnMapPosition) {
        if (pickedTexture != null)
            texture = getTexture(pickedTexture)
        else if (textureNames.isNotEmpty())
            texture = getTexture(textureNames[0])
    }
}