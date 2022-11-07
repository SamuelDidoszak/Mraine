package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition

class CrateBigger: Entity() {
    override val allowOnTop = false
    override val allowCharacterOnTop = false
    override val name = "CrateBigger"
    override val description = "A bigger crate. Surprisingly, it has more volume than a smaller one"

    override val textureNames: List<String> = listOf("crateBiggerDark")
    override var texture: TextureAtlas.AtlasRegion = setTexture()
    override fun pickTexture(onMapPosition: OnMapPosition) {
        texture = getTexture(textureNames[0])
    }
}