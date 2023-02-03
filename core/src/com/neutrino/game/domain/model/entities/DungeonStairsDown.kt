package com.neutrino.game.domain.model.entities

import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import com.neutrino.game.utility.serialization.AtlasRegion
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class DungeonStairsDown: Entity() {
    override var allowOnTop = false
    override var allowCharacterOnTop = true
    override val name = "Dungeon stairs down"
    override val description = "You can go deeper"

    override val textureNames: List<String> = listOf("dungeonStairsDown")
    override var texture: AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        texture = getTexture(textureNames[0])
    }
}