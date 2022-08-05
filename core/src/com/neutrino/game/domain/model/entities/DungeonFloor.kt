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
        val randVal = RandomGenerator.nextFloat() * 100

        // new
        val textureName: String =
            getTextureFromEqualRange(randVal, until = 90f, textures = textureNames.subList(0, 2)) ?:
            if(randVal < 94f) "crossRoadFloor" else
            getTextureFromEqualRange(randVal, 94f, until = 100f, textures = textureNames.subList(2, 4)) ?:
            textureNames[0]

        texture = getTexture(textureName)
    }
}