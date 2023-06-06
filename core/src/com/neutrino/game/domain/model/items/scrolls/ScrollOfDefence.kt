package com.neutrino.game.domain.model.items.scrolls

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.characters.utility.HasRange
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.items.UseOn
import com.neutrino.game.domain.model.systems.event.CausesCooldown
import com.neutrino.game.domain.model.systems.event.types.CooldownType
import com.neutrino.game.domain.model.systems.event.types.EventModifyStat
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.event.wrappers.TimedEvent

class ScrollOfDefence: Item(), ItemType.USABLE, CausesCooldown {
    @Transient
    override val name: String = "Scroll of defence"
    @Transient
    override val description: String = "Makes your skin harder and more resistant to physical attacks"
    override var amount: Int? = 1
    @Transient
    override val itemTier: Int = 2

    @Transient
    override val useOn: UseOn = UseOn.SELF_AND_OTHERS
    @Transient
    override val hasRange: HasRange? = object: HasRange {
        override var range: Int = 5
        override var rangeType: RangeType = RangeType.SQUARE
    }

    @Transient
    override val textureNames: List<String> = listOf("scrollOfDefence")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    private val power: Float = 15f
    private val timeout: Double = 20.0

    override val eventWrappers: List<EventWrapper> = List(1) {
        TimedEvent(0.0, timeout, 1, EventModifyStat(StatsEnum.DEFENCE, power))
    }

    override val cooldownLength: Double = timeout
    @Transient
    override val cooldownType: CooldownType = CooldownType.ITEM(this::class)

    @Transient
    override var goldValueOg: Int = 30
    override var goldValue: Int = goldValueOg
    override var realValue: Int = goldValue
}