package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.Constants.DefaultTextures
import com.neutrino.game.domain.model.characters.utility.Animated
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.entities.utility.TextureHaver

object Player : Character(0, 0, 0.0), Animated {
    override var hp: Float = 10f
    override var currentHp: Float = hp
    override var mp: Float = 10f
    override var currentMp: Float = mp
    override var attack: Float = 0f
    override var strength: Float = 4f
    override var defence: Float = 2f
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

    // environmental stats
    override var fireDamage: Float = 0f
    override var waterDamage: Float = 0f
    override var earthDamage: Float = 0f
    override var airDamage: Float = 0f
    override var poisonDamage: Float = 0f
    override var fireDefence: Float = 0f
    override var waterDefence: Float = 0f
    override var earthDefence: Float = 0f
    override var airDefence: Float = 0f
    override var poisonDefence: Float = 0f

    init {
        setName("Player")
        attack = setAttack()
    }

    override val description: String
        get() = TODO("Not yet implemented")

    override var textureSrc: String = "characters/player.png"
    override val textureNames: List<String> = listOf(
        "buddy#1", "buddy#2", "buddy#3", "buddy#4", "buddy#5"
    )
    override var textureList: List<TextureAtlas.AtlasRegion> = listOf()
    override var texture: TextureAtlas.AtlasRegion = if(textureList.isNotEmpty()) textureList[0] else TextureAtlas.AtlasRegion(DefaultTextures[6][5])

    override val defaultAnimationName: String = "buddy"
    override lateinit var defaultAnimation: Animation<TextureRegion>
    override val textureHaver: TextureHaver = this

    override var animation: Animation<TextureRegion>? = null

}