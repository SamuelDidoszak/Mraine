package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.DefaultTextures
import com.neutrino.game.RandomGenerator
import com.neutrino.game.Seed
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import java.util.*
import kotlin.random.Random

class DungeonFloor(
) : Entity() {
    override val id: Int = 1
    override val allowOnTop = true
    override val allowCharacterOnTop = true
    override val name = "Dungeon floor"
    override val description = "The floor of a dungeon. You can walk on it and stuff"

    override var textureSrc = "environment/tiles.png"
    override val textureNames: List<String> = listOf("basicFloor", "basicFloor2", "basicFloorDirty", "basicFloorDirty2", "crossRoadFloor")
    override var texture: TextureRegion = if(textureList.isNotEmpty()) textureList[0] else DefaultTextures[6][5]

    override fun pickTexture(onMapPosition: OnMapPosition) {
        val randVal = RandomGenerator.nextInt(0, 100)
        when (randVal) {
            in 0 until 47 -> {
                texture = getTexture("basicFloor")
            } in 47 until 90 -> {
                texture = getTexture("basicFloor2")
            } in 90 until 94 -> {
                texture = getTexture("crossRoadFloor")
            } in 94 until 97 -> {
                texture = getTexture("basicFloorDirty")
            } in 97 until 100 -> {
                texture = getTexture("basicFloorDirty2")
            }
        }
    }
}