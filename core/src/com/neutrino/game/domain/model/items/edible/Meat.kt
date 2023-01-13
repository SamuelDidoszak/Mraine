package com.neutrino.game.domain.model.items.edible

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.systems.event.types.CooldownType
import com.neutrino.game.domain.model.systems.event.types.EventHeal
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.event.wrappers.TimedEvent
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.ItemType

class Meat: Item(), ItemType.EDIBLE {
    override var name: String = "Meat"
    override val description: String = "A piece of raw meat. What's the worst that could happen?"
    override var amount: Int? = 1

    override val textureNames: List<String> = listOf("meat")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override val itemTier: Int = 1

    override val isFood: Boolean = true
    override val powerOg: Float = 0.2f
    override val timeoutOg: Double = 0.5
    override val executionsOg: Int = 60
    override var power: Float = powerOg
    override val timeout: Double = timeoutOg
    override val executions: Int = executionsOg
    override var goldValueOg: Int = 5

    override val eventWrappers: List<EventWrapper> = List(1) {
        TimedEvent(0.0, timeout, executions, EventHeal(power))
    }

    override val cooldownType: CooldownType = CooldownType.FOOD
    override val cooldownLength: Double = (eventWrappers[0] as TimedEvent).getEventLength()

    init {
        goldValue = goldValueOg
        val randomizedValue = Constants.RandomGenerator.nextDouble()
        randomizedValue.let {
            when {
                it < 0.2 -> {
                    power = 0.15f
                    name = "Rotten meat"
                    goldValue -= 2
                }
                it < 0.4 -> {
                    power = 0.3f
                    name = "Tasty meat"
                    goldValue += 2
                }
                else -> {
                    power = powerOg
                }
            }
        }
        realValue = goldValue + 2
    }
}