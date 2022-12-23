package com.neutrino.game.domain.model.entities.lightSources

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.EntityChecker
import com.neutrino.game.domain.model.entities.utility.OnMapPosition

class Torch: Entity() {
    override val name: String = "Torch"
    override var allowOnTop: Boolean = true
    override var allowCharacterOnTop: Boolean = true


    override val textureNames: List<String> = listOf(
        "torchFront", "torchSide"
    )
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override fun pickTexture(onMapPosition: OnMapPosition) {
        val textureName =
            if (EntityChecker(onMapPosition, "DungeonWall").checkAllTiles(listOf(7, 8, 9), tileSkipList = listOf(4, 6)))
                "torchFront"
            else if (EntityChecker(onMapPosition, "DungeonWall").checkAllTiles(listOf(1, 4, 7), tileSkipList = listOf(2, 8)))
                "torchSide"
            else if (EntityChecker(onMapPosition, "DungeonWall").checkAllTiles(listOf(3, 6, 9), tileSkipList = listOf(2, 8))) {
                mirrored = true
                "torchSide"
            }
            else
                textureNames[0]
        texture = getTexture(textureName)
    }
}