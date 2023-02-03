package com.neutrino.game.domain.model.entities

import com.neutrino.game.domain.model.entities.utility.*
import com.neutrino.game.utility.serialization.AtlasRegion
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class CrateDoor: Entity(), ChangesImpassable, Interactable {
    override val name: String = "Crate door"
    override var allowOnTop: Boolean = false
    override var allowCharacterOnTop: Boolean = false

    override val textureNames: List<String> = listOf(
        "crateDoor", "crateDoorClosed"
    )
    override var texture: AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        val entityChecker = EntityChecker(onMapPosition, "DungeonWall", skipList = listOf(1, 3, 7, 9))

        val textureName =
            if (entityChecker.checkAllTiles(listOf(4, 6))) "crateDoorClosed" else
                textureNames[0]
        texture = getTexture(textureName)
    }

    override val interactionList: List<Interaction> = List(1) {
        Interaction.DOOR(this)
    }
}