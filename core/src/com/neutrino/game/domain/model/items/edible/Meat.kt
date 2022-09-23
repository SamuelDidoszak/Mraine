package com.neutrino.game.domain.model.items.edible

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.ItemType

class Meat: Item(), ItemType.EDIBLE {
    override var name: String = "Meat"
    override val description: String = "A piece of raw meat. What's the worst that could happen?"
    override val stackable: Boolean = true
    override var amount: Int? = 1

    override val textureNames: List<String> = listOf("meat")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override val isFood: Boolean = true
    override val powerOg: Float = 2f
    override val speedOg: Double = 1.0
    override val repeatsOg: Int = 5
    override var power: Float = powerOg
    override val speed: Double = speedOg
    override val repeats: Int = repeatsOg

    init {
        val randomizedValue = Constants.RandomGenerator.nextDouble()
        randomizedValue.let {
            when {
                it < 0.2 -> {
                    power = 1f
                    name = "Rotten meat"
                }
                it < 0.4 -> {
                    power = 3f
                    name = "Tasty meat"
                }
                else -> {
                    power = 2f
                }
            }
        }
    }
}