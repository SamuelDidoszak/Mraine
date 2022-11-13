package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.*

class WoodenDoorArched: Entity(), ChangesImpassable, Interactable {
    override val name: String = "Wooden door arched"
    override var allowOnTop: Boolean = false
    override var allowCharacterOnTop: Boolean = false

    override val textureNames: List<String> = listOf(
        "woodenDoorArched", "woodenDoorArchedClosed", "woodenDoorArchedVertical", "woodenDoorArchedVerticalClosed"
    )
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition) {
        val entityChecker = EntityChecker(onMapPosition, "DungeonWall", skipList = listOf(1, 3, 7, 9))

        val textureName =
            if (entityChecker.checkAllTiles(listOf(4, 6))) "woodenDoorArchedClosed" else
            if (entityChecker.checkAllTiles(listOf(2, 8))) "woodenDoorArchedVerticalClosed" else
                textureNames[0]
        texture = getTexture(textureName)
    }

    override val interactionList: List<Interaction> = List(1) {
        Interaction.DOOR(this)
    }
}