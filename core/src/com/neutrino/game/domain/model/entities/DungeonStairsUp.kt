package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import kotlin.random.Random

class DungeonStairsUp: Entity() {
    override var allowOnTop = false
    override var allowCharacterOnTop = true
    override val name = "Dungeon stairs up"
    override val description = "Go upwards"

    override val textureNames: List<String> = listOf("dungeonStairsUp")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        texture = getTexture(textureNames[0])
    }
}