package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.DefaultTextures
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition

class Bush(
    override val id: Int,
    override val textureList: List<TextureRegion>
) : Entity() {
    override val name = "Bush"
    override val description = "A little bush"
    override val allowOnTop = true
    override val allowCharacterOnTop = true

    override var textureSrc = "environment/entities.png"
    override var texture: TextureRegion = textureList[0] ?: DefaultTextures[6][5]

    override fun pickTexture(onMapPosition: OnMapPosition) {
        if (checkTile(onMapPosition, listOf(2, 4)) == null)
            texture = textureList[1]
    }
}