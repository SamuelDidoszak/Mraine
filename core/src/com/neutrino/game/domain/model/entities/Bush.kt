package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition

class Bush: Entity() {
    override val name = "Bush"
    override val description = "A little bush"
    override val allowOnTop = true
    override val allowCharacterOnTop = true

    override val textureNames: List<String> = listOf()
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition) {

    }
}