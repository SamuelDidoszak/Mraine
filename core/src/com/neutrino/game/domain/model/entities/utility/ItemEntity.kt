package com.neutrino.game.domain.model.entities.utility

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.items.Item

class ItemEntity(val item: Item): Entity(), HasAction {
    override val name: String = item.name
    override var allowOnTop: Boolean = true
    override var allowCharacterOnTop: Boolean = true

    override val textureNames: List<String> = item.textureNames
    override var texture: TextureAtlas.AtlasRegion = item.texture
    // Unnecessarily required for entity

    override fun pickTexture(onMapPosition: OnMapPosition) { }

    override val action: Action = Action("Pick up", 0) {

    }
}