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

class Grass(
    override val isBurnt: Boolean = false
) : Entity(), Flammable {
    override val id: Int = 1
    override val allowOnTop = true
    override val allowCharacterOnTop = true
    override val name = "High grass"
    override val description = "High and long grass"

    // Textures
    override var textureSrc = "environment/flora.png"
    override val textureNames: List<String> = listOf("tallGrass", "tallGrass2", "tallGrassHigh", "tallGrassHigh2")
    override var texture: TextureRegion = if(textureList.isNotEmpty()) textureList[0] else DefaultTextures[6][5]

    // Flammable values
    override val fireResistance: Float = 0f
    override val burningTime: Float = 1f

    override fun pickTexture(onMapPosition: OnMapPosition) {
        val randVal = RandomGenerator.nextInt(0, 100)
        if (!isBurnt) {
            when (randVal) {
                in 0 until 44 -> {
                    texture = getTexture("tallGrass")
                }
                in 44 until 88 -> {
                    texture = getTexture("tallGrass2")
                }
                in 88 until 94 -> {
                    texture = getTexture("tallGrassHigh")
                }
                in 94 until 100 -> {
                    texture = getTexture("tallGrassHigh2")
                }
            }
        } else {
            println("TOO BAD!!")
        }
    }
}