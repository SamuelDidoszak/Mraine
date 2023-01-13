package com.neutrino.game.domain.model.items.equipment.weapons

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.compareDelta
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.items.EquipmentItem
import com.neutrino.game.domain.model.items.HandedItemType
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.systems.attack.Attack
import com.neutrino.game.domain.model.systems.attack.BasicAttack
import com.neutrino.game.domain.model.systems.event.Data
import com.neutrino.game.domain.model.systems.event.Requirement
import com.neutrino.game.domain.model.systems.event.types.EventModifyStat
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import kotlin.math.roundToInt

class PocketKnife: EquipmentItem(), ItemType.EQUIPMENT.RHAND {
    override val handedItemType: HandedItemType = HandedItemType.DAGGER
    override val name: String = "Pocket knife"
    override val description: String = "A small blade"

    override val textureNames: List<String> = listOf("pocketKnife")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override var goldValueOg: Int = 15

    override var requirements: Requirement = Requirement(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.DAMAGE, 1f)),
        // example
//        TimedEvent(0.0, 5.0, Int.MAX_VALUE, EventHeal(1f))
    )

    override var attack: Attack = BasicAttack(getDamageTypesFromModifiers(modifierList))

    init {
        requirements.add { requirements.get("character", Character::class)!!.strength.compareDelta(0f) >= 0  }
//        statRandomization(1f)

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}