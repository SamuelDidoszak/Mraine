package com.neutrino.game.domain.model.entities

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.EntityChecker
import com.neutrino.game.domain.model.entities.utility.OnMapPosition

class StonePillar: Entity() {
    override val name: String = "Stone pillar"
    override var allowOnTop: Boolean = false
    override var allowCharacterOnTop: Boolean = false


    override val textureNames: List<String> = listOf(
        "stonePillar", "stonePillarCracked", "stonePillarTop"
    )
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition) {
        val randVal = Constants.RandomGenerator.nextFloat() * 100

        val textureName =
            if (EntityChecker(onMapPosition, "DungeonWall").checkAllTiles(listOf(1, 2, 3), tileSkipList = listOf(4, 6, 7, 8, 9)))
                "stonePillarTop"
            else
                getTextureFromEqualRange(randVal, 0f, 80f, listOf("stonePillar")) ?:
                getTextureFromEqualRange(randVal, 80f, textures = listOf("stonePillarCracked")) ?:
                textureNames[0]
        texture = getTexture(textureName)
        mirror(20f)
    }
}