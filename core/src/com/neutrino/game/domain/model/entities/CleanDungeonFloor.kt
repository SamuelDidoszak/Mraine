package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.entities.utility.Floor
import com.neutrino.game.domain.model.entities.utility.OnMapPosition

class CleanDungeonFloor: Floor() {
    override var allowOnTop = true
    override var allowCharacterOnTop = true
    override val name = "Dungeon floor"
    override val description = "The floor of a cleanDungeon. You can walk on it and stuff"

    override val textureNames: List<String> = listOf("cleanDungeonFloorBasic$1", "cleanDungeonFloorBasic$2","cleanDungeonFloorBasic$3","cleanDungeonFloorBasic$4","cleanDungeonFloorBasic$5",
        "cleanDungeonFloorBasic$6","cleanDungeonFloorBasic$7","cleanDungeonFloorBasic$8","cleanDungeonFloorBasic$9", "cleanDungeonFloorBasic$10",
        "cleanDungeonFloorBasic$11","cleanDungeonFloorBasic$12","cleanDungeonFloorBasic$13","cleanDungeonFloorBasic$14","cleanDungeonFloorBasic$15",
        "cleanDungeonFloorBasic$16","cleanDungeonFloorBasic$17","cleanDungeonFloorBasic$18",
        "cleanDungeonFloorCracked$1","cleanDungeonFloorCracked$2","cleanDungeonFloorCracked$3","cleanDungeonFloorCracked$4",
//        "cleanDungeonFloorLights$1","cleanDungeonFloorLights$2","cleanDungeonFloorLights$3","cleanDungeonFloorLights$4","cleanDungeonFloorLights$5",
        "cleanDungeonFloorSmaller$1","cleanDungeonFloorSmaller$2","cleanDungeonFloorSmaller$3","cleanDungeonFloorSmaller$4","cleanDungeonFloorSmaller$5","cleanDungeonFloorSmaller$6"
    )
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition) {
        val randVal = Constants.RandomGenerator.nextFloat() * 100

        // new
        val textureName: String =
            getTextureFromEqualRange(randVal, until = 70f, textures = textureNames.filter { it.startsWith("cleanDungeonFloorBasic$") }) ?:
//            getTextureFromEqualRange(randVal, from = 70f, until = 80f, textures = textureNames.filter { it.startsWith("cleanDungeonFloorCracked$") }) ?:
            getTextureFromEqualRange(randVal, from = 70f, textures = textureNames.filter { it.startsWith("cleanDungeonFloorBasic$") }) ?:
//            getTextureFromEqualRange(randVal, from = 80f, until = 90f, textures = textureNames.filter { it.startsWith("cleanDungeonFloorLights$") }) ?:
//            getTextureFromEqualRange(randVal, from = 90f, until = 100f, textures = textureNames.filter { it.startsWith("cleanDungeonFloorSmaller$") }) ?:
            textureNames[0]

        texture = getTexture(textureName)
    }
}