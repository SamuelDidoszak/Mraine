package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import kotlin.random.Random

class DungeonFloor: Floor() {
    @Transient
    override var allowOnTop = true
    @Transient
    override var allowCharacterOnTop = true
    @Transient
    override val name = "Dungeon floor"
    @Transient
    override val description = "The floor of a dungeon. You can walk on it and stuff"

    @Transient
    override val textureNames: List<String> = listOf("dungeonFloorBasic$1", "dungeonFloorBasic$2","dungeonFloorBasic$3","dungeonFloorBasic$4","dungeonFloorBasic$5",
        "dungeonFloorBasic$6","dungeonFloorBasic$7","dungeonFloorBasic$8","dungeonFloorBasic$9", "dungeonFloorBasic$10",
        "dungeonFloorBasic$11","dungeonFloorBasic$12","dungeonFloorBasic$13","dungeonFloorBasic$14","dungeonFloorBasic$15",
        "dungeonFloorBasic$16","dungeonFloorBasic$17","dungeonFloorBasic$18",
        "dungeonFloorCracked$1","dungeonFloorCracked$2","dungeonFloorCracked$3","dungeonFloorCracked$4",
        "dungeonFloorLights$1","dungeonFloorLights$2","dungeonFloorLights$3","dungeonFloorLights$4","dungeonFloorLights$5",
        "dungeonFloorSmaller$1","dungeonFloorSmaller$2","dungeonFloorSmaller$3","dungeonFloorSmaller$4","dungeonFloorSmaller$5","dungeonFloorSmaller$6"
        )
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        val randVal = randomGenerator.nextFloat() * 100

        // new
        val textureName: String =
            getTextureFromEqualRange(randVal, until = 70f, textures = textureNames.filter { it.startsWith("dungeonFloorBasic$") }) ?:
            getTextureFromEqualRange(randVal, from = 70f, until = 80f, textures = textureNames.filter { it.startsWith("dungeonFloorCracked$") }) ?:
            getTextureFromEqualRange(randVal, from = 80f, until = 90f, textures = textureNames.filter { it.startsWith("dungeonFloorLights$") }) ?:
            getTextureFromEqualRange(randVal, from = 90f, until = 100f, textures = textureNames.filter { it.startsWith("dungeonFloorSmaller$") }) ?:
            textureNames[0]

        texture = getTexture(textureName)
    }
}