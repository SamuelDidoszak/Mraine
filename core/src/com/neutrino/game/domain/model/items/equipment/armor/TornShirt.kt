package com.neutrino.game.domain.model.items.equipment.armor

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.compareDelta
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

class TornShirt: EquipmentItem(), ItemType.EQUIPMENT.TORSO {
    override val name: String = "Torn shirt"
    override val description: String = "You may look like a hobo, but it's better than appearing like an exhibitionist"

    override val textureNames: List<String> = listOf("tornShirt")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override var goldValueOg: Int = 30

    override var requirements: Requirement = Requirement(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.DEFENCE, 2.0f)),
        // example
//        TimedEvent(0.0, 5.0, Int.MAX_VALUE, EventHeal(1f))
    )
    init {
        requirements.add { requirements.get("character", Character::class)!!.strength.compareDelta(0f) >= 0  }
//        statRandomization(1f)

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}