package com.neutrino.game.domain.model.items.scrolls

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.event.CausesCooldown
import com.neutrino.game.domain.model.event.types.CooldownType
import com.neutrino.game.domain.model.event.types.EventModifyStat
import com.neutrino.game.domain.model.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.event.wrappers.TimedEvent
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.ItemType

class ScrollOfDefence: Item(), ItemType.USABLE, CausesCooldown {
    override val name: String = "Scroll of defence"
    override val description: String = "Makes your skin harder and more resistant to physical attacks"
    override var amount: Int? = 1
    override val causesCooldown: Int = 1
    override val itemTier: Int = 2

    override val textureNames: List<String> = listOf("scrollOfDefence")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    private val power: Float = 15f
    private val timeout: Double = 20.0

    override val eventWrappers: List<EventWrapper> = List(1) {
        TimedEvent(0.0, timeout, 1, EventModifyStat(StatsEnum.DEFENCE, power))
    }

    override val cooldownLength: Double = timeout
    override val cooldownType: CooldownType = CooldownType.ITEM(name)

    override var goldValueOg: Int = 30
    override var goldValue: Int = goldValueOg
    override var realValue: Int = goldValue
}