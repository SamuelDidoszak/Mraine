package com.neutrino.game.domain.model.items.equipment.armor

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.systems.event.Data
import com.neutrino.game.domain.model.systems.event.Requirement
import com.neutrino.game.domain.model.systems.event.types.EventModifyStat
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import com.neutrino.game.domain.model.items.EquipmentItem
import com.neutrino.game.domain.model.items.ItemType
import kotlin.math.roundToInt

class WoodenFlipFlops: EquipmentItem(), ItemType.EQUIPMENT.FEET {
    override val name: String = "Wooden flip flops"
    override val description: String = "Not too comfortable, but they sound pretty funny. Klackklackklackklackklackklackklackklack"

    override val textureNames: List<String> = listOf("woodenFlipFlops")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override var goldValueOg: Int = 30

    override var requirements: Requirement = Requirement(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.DEFENCE, 1.0f)),
        OnOffEvent(EventModifyStat(StatsEnum.STEALTH, -10.0f)),
        // example
//        TimedEvent(0.0, 5.0, Int.MAX_VALUE, EventHeal(1f))
    )
    init {
//        statRandomization(1f)

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}