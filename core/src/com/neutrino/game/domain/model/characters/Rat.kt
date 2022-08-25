package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.Constants.DefaultTextures
import com.neutrino.game.domain.model.characters.utility.Animated
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.entities.utility.TextureHaver

class Rat(xPos: Int, yPos: Int, turn: Double) : Character(xPos, yPos, turn), Animated {
    override var hp: Float = 10f
    override var currentHp: Float = hp
    override var mp: Float = 10f
    override var currentMp: Float = mp
    override var attack: Float = setAttack()
    override var strength: Float = 2f
    override var defence: Float = 1f
    override var agility: Float = 2f
    override var evasiveness: Float = 2f
    override var accuracy: Float = 2f
    override var criticalChance: Float = 0.3f
    override var luck: Float = 2f
    override var attackSpeed: Double = 1.0
    override var movementSpeed: Double = 1.0
    override var range: Int = 1
    override var rangeType: RangeType = RangeType.SQUARE
    override var experience: Float = 0f

    init {
        setName("Rat")
    }

    override val description: String = "IT'S A RAAAAT"

    override var textureSrc: String = "characters/rat.png"
    override val textureNames: List<String> = listOf(
        "rat#1", "rat#2", "rat#3", "rat#4", "rat#5", "rat#6"
    )
    override var textureList: List<TextureAtlas.AtlasRegion> = listOf()
    override var texture: TextureAtlas.AtlasRegion = if(textureList.isNotEmpty()) textureList[0] else TextureAtlas.AtlasRegion(
        DefaultTextures[6][5])

    override val textureHaver: TextureHaver = this

    override lateinit var defaultAnimation: Animation<TextureRegion>
    override val defaultAnimationName: String = "rat"

    override var animation: Animation<TextureRegion>? = null

}