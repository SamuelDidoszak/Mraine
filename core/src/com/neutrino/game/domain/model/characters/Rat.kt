package com.neutrino.game.domain.model.characters

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.characters.utility.Randomization
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.edible.Meat
import kotlin.reflect.KClass

class Rat(xPos: Int, yPos: Int, turn: Double) : Character(xPos, yPos, turn), Randomization {
    override var hpMax: Float = 10f
    override var mpMax: Float = 10f
    override var strength: Float = 2f
    override var dexterity: Float = 2f
    override var intelligence: Float = 0f
    override var luck: Float = 2f
    override var damage: Float = 3f
    override var damageVariation: Float = 1f
    override var defence: Float = 1f
    override var criticalChance: Float = 0.05f
    override var experience: Float = 5f

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
        hpMax = hpMax.randomizeByPercent(0.2f).roundToDecimalPlaces(10)
    }

}