package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.characters.utility.Randomization
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.edible.Meat
import kotlin.reflect.KClass

class Rat(xPos: Int, yPos: Int, turn: Double) : Character(xPos, yPos, turn), Randomization {
    override var hp: Float = 10f
    override var mp: Float = 10f
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

    override val randomizationProbability: Float = 1f

    override val possibleItemDropList: List<Pair<KClass<Item>, Double>> = listOf(
        Pair(Meat::class as KClass<Item>, 0.25)
    )

    init {
        randomizeStats()
        initialize("Rat")
    }

    override val description: String = "IT'S A RAAAAT"

    override var textureSrc: String = "characters/rat.png"
    override val textureNames: List<String> = listOf(
        "rat#1", "rat#2", "rat#3", "rat#4", "rat#5", "rat#6"
    )
    override var texture: TextureAtlas.AtlasRegion = Constants.DefaultItemTexture.findRegion("knife")

    override val textureHaver: TextureHaver = this

    override lateinit var defaultAnimation: Animation<TextureRegion>
    override val defaultAnimationName: String = "rat"

    override var animation: Animation<TextureRegion>? = null

    private fun randomizeStats() {
        movementSpeed = movementSpeed.randomizeByValue(0.3).roundToDecimalPlaces(10)
        hp = hp.randomizeByPercent(0.2f).roundToDecimalPlaces(10)
    }

}