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
        val randVal = RandomGenerator.nextFloat() * 100
        val textureName = if (!isBurnt) {
            getTextureFromEqualRange(randVal, until = 88f, textures = textureNames.subList(0, 2)) ?:
            getTextureFromEqualRange(randVal, 88f, until = 100f, textures = textureNames.subList(2, 4))
        } else {
            // Not yet implemented, providing a default value
            textureNames[0]
        } ?: textureNames[0]
        texture = getTexture(textureName)
    }
}