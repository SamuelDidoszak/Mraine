package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.DefaultTextures
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition

class DungeonFloor(
    override val id: Int,
    override val textureList: List<TextureRegion>
) : Entity() {
    override val allowOnTop = true
    override val allowCharacterOnTop = true
    override val name = "Dungeon floor"
    override val description = "The floor of a dungeon. You can walk on it and stuff"

    override var textureSrc = "environment/tiles.png"
    override var texture: TextureRegion = textureList[0] ?: DefaultTextures[6][5]

    override fun pickTexture(onMapPosition: OnMapPosition) {
        texture = textureList[0]
    }
}