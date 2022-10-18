package com.neutrino.game.domain.model.items.equipment

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.ItemType

class Knife: Item(), ItemType.WEAPON {
    override val name: String = "Knife"
    override val description: String = "Stabby stab stab"

    override val textureNames: List<String> = listOf("knife")
    override var texture: TextureAtlas.AtlasRegion = setTexture()
}