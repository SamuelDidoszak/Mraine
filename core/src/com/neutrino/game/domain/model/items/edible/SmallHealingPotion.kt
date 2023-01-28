package com.neutrino.game.domain.model.items.edible

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.systems.event.types.CooldownType
import com.neutrino.game.domain.model.systems.event.types.EventHeal
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.event.wrappers.TimedEvent

class SmallHealingPotion: Item(), ItemType.EDIBLE {
    override var name: String = "Small healing potion"
    override val description: String = "It's gonna heal you instantly"
    override var amount: Int? = 1

    override val textureNames: List<String> = listOf("smallHealingPotion")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override val itemTier: Int = 2

    override val powerOg: Float = 20f
    override val timeoutOg: Double = 0.0
    override val executionsOg: Int = 1
    override var power: Float = powerOg
    override val timeout: Double = timeoutOg
    override val executions: Int = executionsOg
    override var goldValueOg: Int = 15

    override val eventWrappers: List<EventWrapper> = List(1) {
        TimedEvent(0.0, timeout, executions, EventHeal(power))
    }

    override val cooldownType: CooldownType = CooldownType.NONE
    override val cooldownLength: Double = 0.0

    init {
        goldValue = goldValueOg
        val randomizedValue = Constants.RandomGenerator.nextDouble()
        randomizedValue.let {
            when {
                it < 0.6 -> {
                    power = 15f
                    name = "Diluted small healing potion"
                    goldValue -= 5
                }
                it < 0.7 -> {
                    power = 25f
                    name = "Concentrated small healing potion"
                    goldValue += 5
                }
            }
        }
        realValue = goldValue + 5
    }
}