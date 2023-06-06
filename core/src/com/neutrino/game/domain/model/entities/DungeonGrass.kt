package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.Flammable
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import kotlin.random.Random

class DungeonGrass: Entity(), Flammable {
    @Transient
    override var allowOnTop = true
    @Transient
    override var allowCharacterOnTop = true
    @Transient
    override val name = "Dungeon grass"
    @Transient
    override val description = "A short grass finding it's way to grow on the pavement"

    // Textures
    @Transient
    override val textureNames: List<String> = listOf("basicFloorGrass", "basicFloorGrass2", "basicFloorGrassBurnt", "basicFloorGrassBurnt2")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    // Flammable values
    @Transient
    override val fireResistance: Float = 0f
    @Transient
    override val burningTime: Float = 1f
    override val isBurnt: Boolean = false

    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
        val randVal = randomGenerator.nextFloat() * 100
        val textureName = if (!isBurnt) {
            getTextureFromEqualRange(randVal, until = 100f, textures = textureNames.subList(0, 2))
        } else {
            getTextureFromEqualRange(randVal, until = 100f, textures = textureNames.subList(2, 4))
        } ?: textureNames[0]
        texture = getTexture(textureName)
    }
}