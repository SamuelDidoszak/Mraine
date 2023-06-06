package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import com.neutrino.game.utility.Serialize
import kotlin.random.Random

class DungeonStairsUp: Entity() {
    @Transient
    override var allowOnTop = false
    @Transient
    override var allowCharacterOnTop = true
    @Transient
    override val name = "Dungeon stairs up"
    @Transient
    override val description = "Go upwards"

    @Transient
    override val textureNames: List<String> = listOf("dungeonStairsUp")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        texture = getTexture(textureNames[0])
    }
}