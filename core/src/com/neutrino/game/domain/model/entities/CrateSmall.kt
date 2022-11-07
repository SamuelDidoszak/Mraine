package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition

class CrateSmall: Entity() {
    override val allowOnTop = false
    override val allowCharacterOnTop = false
    override val name = "CrateSmall"
    override val description = "A small, little crate"

    override val textureNames: List<String> = listOf("crateSmall")
    override var texture: TextureAtlas.AtlasRegion = setTexture()
    override fun pickTexture(onMapPosition: OnMapPosition) {
        texture = getTexture(textureNames[0])
    }
}