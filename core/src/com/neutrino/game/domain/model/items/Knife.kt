package com.neutrino.game.domain.model.items

import com.badlogic.gdx.graphics.g2d.TextureAtlas

class Knife: Item(), ItemType.WEAPON {
    override val name: String = "Knife"
    override val description: String = "Stabby stab stab"
    override val stackable: Boolean = false

    override val textureNames: List<String> = listOf("knife")
    override var texture: TextureAtlas.AtlasRegion = setTexture()
}