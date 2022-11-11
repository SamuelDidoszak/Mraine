package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants.RandomGenerator
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.Flammable
import com.neutrino.game.domain.model.entities.utility.OnMapPosition

class DungeonGrass(
    override val isBurnt: Boolean = false
) : Entity(), Flammable {
    override var allowOnTop = true
    override var allowCharacterOnTop = true
    override val name = "Dungeon grass"
    override val description = "A short grass finding it's way to grow on the pavement"

    // Textures
    override val textureNames: List<String> = listOf("basicFloorGrass", "basicFloorGrass2", "basicFloorGrassBurnt", "basicFloorGrassBurnt2")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

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