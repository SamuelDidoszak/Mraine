package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.DefaultTextures
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition

class Bush(
) : Entity() {
    override val id: Int = 2
    override val name = "Bush"
    override val description = "A little bush"
    override val allowOnTop = true
    override val allowCharacterOnTop = true

    override var textureSrc = "environment/raw/entities.png"
    override val textureNames: List<String> = listOf()
    override var texture: TextureRegion = if(textureList.isNotEmpty()) textureList[0] else DefaultTextures[6][5]

    override fun pickTexture(onMapPosition: OnMapPosition) {
        if (checkTile(onMapPosition, listOf(2, 4)) == null)
            texture = textureList[1]
    }
}