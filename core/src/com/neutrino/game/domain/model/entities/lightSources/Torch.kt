package com.neutrino.game.domain.model.entities.lightSources

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.characters.utility.Animated
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.EntityChecker
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import com.neutrino.game.domain.model.entities.utility.TextureHaver

class Torch: Entity(), Animated {
    override val name: String = "Torch"
    override var allowOnTop: Boolean = true
    override var allowCharacterOnTop: Boolean = true

    override var animation: Animation<TextureRegion>? = null
    override lateinit var defaultAnimation: Animation<TextureRegion>
    override lateinit var defaultAnimationName: String
    override val textureHaver: TextureHaver = this

    override val textureNames: List<String> = listOf(
        "torchFront#1", "torchFront#2", "torchFront#3",
        "torchSide#1", "torchSide#2", "torchSide#3"
    )
    override var texture: TextureAtlas.AtlasRegion = setTexture()
    override var textureList: List<TextureAtlas.AtlasRegion> = setTextureList(Constants.DefaultEntityTexture)

    override fun pickTexture(onMapPosition: OnMapPosition) {
        val textureName =
            if (EntityChecker(onMapPosition, "DungeonWall").checkAllTiles(listOf(7, 8, 9), tileSkipList = listOf(4, 6)))
                "torchFront#1"
            else if (EntityChecker(onMapPosition, "DungeonWall").checkAllTiles(listOf(1, 4, 7), tileSkipList = listOf(2, 8)))
                "torchSide#1"
            else if (EntityChecker(onMapPosition, "DungeonWall").checkAllTiles(listOf(3, 6, 9), tileSkipList = listOf(2, 8))) {
                mirrored = true
                "torchSide#1"
            }
            else
                textureNames[0]
        texture = getTexture(textureName)
        defaultAnimationName = textureName.substringBefore('#')
    }
}