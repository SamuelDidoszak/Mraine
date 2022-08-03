package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.DefaultTextures
import com.neutrino.game.RandomGenerator
import com.neutrino.game.Seed
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.Flammable
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import java.util.*
import kotlin.random.Random

class DungeonGrass(
    override val isBurnt: Boolean = false
) : Entity(), Flammable {
    override val id: Int = 1
    override val allowOnTop = true
    override val allowCharacterOnTop = true
    override val name = "Dungeon grass"
    override val description = "A short grass finding it's way to grow on the pavement"

    // Textures
    override var textureSrc = "environment/tiles.png"
    override val textureNames: List<String> = listOf("basicFloorGrass", "basicFloorGrass2", "basicFloorGrassBurnt", "basicFloorGrassBurnt2")
    override var texture: TextureRegion = if(textureList.isNotEmpty()) textureList[0] else DefaultTextures[6][5]

    // Flammable values
    override val fireResistance: Float = 0f
    override val burningTime: Float = 1f

    override fun pickTexture(onMapPosition: OnMapPosition) {
        val randVal = RandomGenerator.nextInt(0, 100)
        if (!isBurnt) {
            when (randVal) {
                in 0 until 50 -> {
                    texture = getTexture("basicFloorGrass")
                } in 50 until 100 -> {
                    texture = getTexture("basicFloorGrass2")
                }
            }
        } else {
            when (randVal) {
                in 0 until 50 -> {
                    texture = getTexture("basicFloorGrassBurnt")
                } in 50 until 100 -> {
                    texture = getTexture("basicFloorGrassBurnt2")
                }
            }
        }
    }
}