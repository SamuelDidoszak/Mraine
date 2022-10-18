package com.neutrino.game.domain.model.items.edible

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.ItemType

class SmallHealingPotion: Item(), ItemType.EDIBLE {
    override var name: String = "Small healing potion"
    override val description: String = "It's gonna heal you instantly"
    override var amount: Int? = 1

    override val textureNames: List<String> = listOf("smallHealingPotion")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override val isFood: Boolean = false
    override val powerOg: Float = 20f
    override val speedOg: Double = 1.0
    override val repeatsOg: Int = 1
    override var power: Float = powerOg
    override val speed: Double = speedOg
    override val repeats: Int = repeatsOg
    override var goldValueOg: Int = 25

    init {
        goldValue = goldValueOg
        val randomizedValue = Constants.RandomGenerator.nextDouble()
        randomizedValue.let {
            when {
                it < 0.6 -> {
                    power = 15f
                    name = "Diluted small healing potion"
                    goldValue -= 10
                }
                it < 0.7 -> {
                    power = 25f
                    name = "Concentrated small healing potion"
                    goldValue += 10
                }
            }
        }
    }
}