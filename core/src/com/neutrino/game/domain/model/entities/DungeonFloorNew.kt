package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition

class DungeonFloorNew: Entity() {
    override val allowOnTop = true
    override val allowCharacterOnTop = true
    override val name = "Dungeon floor"
    override val description = "The floor of a dungeon. You can walk on it and stuff"

    override val textureNames: List<String> = listOf("dungeonFloorBasic1", "dungeonFloorBasic2","dungeonFloorBasic3","dungeonFloorBasic4","dungeonFloorBasic5",
        "dungeonFloorBasic6","dungeonFloorBasic7","dungeonFloorBasic8","dungeonFloorBasic9", "dungeonFloorBasic10",
        "dungeonFloorBasic11","dungeonFloorBasic12","dungeonFloorBasic13","dungeonFloorBasic14","dungeonFloorBasic15",
        "dungeonFloorBasic16","dungeonFloorBasic17","dungeonFloorBasic18",
        "dungeonFloorCracked1","dungeonFloorCracked2","dungeonFloorCracked3","dungeonFloorCracked4",
        "dungeonFloorLights1","dungeonFloorLights2","dungeonFloorLights3","dungeonFloorLights4","dungeonFloorLights5",
        "dungeonFloorSmaller1","dungeonFloorSmaller2","dungeonFloorSmaller3","dungeonFloorSmaller4","dungeonFloorSmaller5","dungeonFloorSmaller6"
        )
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition) {
        val randVal = Constants.RandomGenerator.nextFloat() * 100

        // new
        val textureName: String =
            getTextureFromEqualRange(randVal, until = 70f, textures = textureNames.subList(0, 18)) ?:
            getTextureFromEqualRange(randVal, from = 70f, until = 80f, textures = textureNames.subList(18, 22)) ?:
            getTextureFromEqualRange(randVal, from = 80f, until = 90f, textures = textureNames.subList(22, 27)) ?:
            getTextureFromEqualRange(randVal, from = 90f, until = 100f, textures = textureNames.subList(27, 32)) ?:
            textureNames[0]

        texture = getTexture(textureName)
    }

    override fun getTexture(name: String): TextureAtlas.AtlasRegion {
        return Constants.DefaultEntityNewTexture.findRegion(name)
    }
}