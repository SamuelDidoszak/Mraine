package com.neutrino.game.domain.model.entities.lightSources

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.characters.utility.Animated
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.entities.utility.EntityChecker
import com.neutrino.game.domain.model.entities.utility.OnMapPosition
import com.neutrino.game.domain.model.entities.utility.TextureHaver

import kotlin.random.Random

class Torch: Entity(), Animated {
    @Transient
    override val name: String = "Torch"
    @Transient
    override var allowOnTop: Boolean = true
    @Transient
    override var allowCharacterOnTop: Boolean = true

    @Transient
    override var animation: Animation<TextureRegion>? = null
    @Transient
    override lateinit var defaultAnimation: Animation<TextureRegion>
    override lateinit var defaultAnimationName: String
    @Transient
    override val textureHaver: TextureHaver = this

    @Transient
    override val textureNames: List<String> = listOf(
        "torchFront#1", "torchFront#2", "torchFront#3",
        "torchSide#1", "torchSide#2", "torchSide#3"
    )
    override var texture: TextureAtlas.AtlasRegion = setTexture()
    @Transient
    override var textureList: List<TextureAtlas.AtlasRegion> = setTextureList(Constants.DefaultEntityTexture)

    override fun pickTexture(onMapPosition: OnMapPosition, randomGenerator: Random) {
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