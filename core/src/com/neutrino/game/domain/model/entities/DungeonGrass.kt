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
        val randVal = RandomGenerator.nextFloat() * 100
        val textureName = if (!isBurnt) {
            getTextureFromEqualRange(randVal, until = 100f, textures = textureNames.subList(0, 2))
        } else {
            getTextureFromEqualRange(randVal, until = 100f, textures = textureNames.subList(2, 4))
        } ?: textureNames[0]
        texture = getTexture(textureName)
    }
}