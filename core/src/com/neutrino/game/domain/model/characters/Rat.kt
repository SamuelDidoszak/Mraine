package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.Constants.DefaultTextures
import com.neutrino.game.domain.model.characters.utility.Animated
import com.neutrino.game.domain.model.characters.utility.Randomization
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.edible.Meat

class Rat(xPos: Int, yPos: Int, turn: Double) : Character(xPos, yPos, turn), Animated , Randomization {
    override var hp: Float = 10f
    override var currentHp: Float = hp
    override var mp: Float = 10f
    override var currentMp: Float = mp
    override var attack: Float = 0f
    override var strength: Float = 2f
    override var defence: Float = 1f
    override var agility: Float = 2f
    override var evasiveness: Float = 2f
    override var accuracy: Float = 2f
    override var criticalChance: Float = 0.05f
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
    override val randomizationProbability: Float = 1f

    override val itemDropList: List<Pair<Item, Double>> = listOf(
        Pair(Meat(), 0.15)
    )

    init {
        randomizeStats()
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

    private fun randomizeStats() {
        movementSpeed = movementSpeed.randomizeByValue(0.3).roundToDecimalPlaces(10)
        hp = hp.randomizeByPercent(0.2f).roundToDecimalPlaces(10)
    }

}