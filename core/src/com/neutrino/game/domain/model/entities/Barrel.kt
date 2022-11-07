package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition

class Barrel: Entity() {
    override val allowOnTop = false
    override val allowCharacterOnTop = false
    override val name = "Barrel"
    override val description = "A barrel"

    override val textureNames: List<String> = listOf("barrel")
    override var texture: TextureAtlas.AtlasRegion = setTexture()
    override fun pickTexture(onMapPosition: OnMapPosition) {
        texture = getTexture(textureNames[0])
    }
}