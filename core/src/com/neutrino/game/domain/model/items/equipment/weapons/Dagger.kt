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
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.event.types.EventModifyStat
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent

import kotlin.math.roundToInt

class Dagger: EquipmentItem(), ItemType.EQUIPMENT.RHAND {
    @Transient
    override val handedItemType: HandedItemType = HandedItemType.DAGGER
    @Transient
    override val name: String = "Dagger"
    @Transient
    override val description: String = "A dagger"

    @Transient
    override val textureNames: List<String> = listOf("dagger")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    @Transient
    override var goldValueOg: Int = 25

    @Transient
    override var requirements: RequirementPrintable = RequirementPrintable(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.DAMAGE, 3f)),
        // example
//        TimedEvent(0.0, 5.0, Int.MAX_VALUE, EventHeal(1f))
    )

    @Transient
    override var attack: Attack = BasicAttack(getDamageTypesFromModifiers(modifierList))

    init {
        requirements
            .add(RequirementPrintable.PrintableReq("Dexterity", 1f)
            { requirements.get("character", Character::class)!!.dexterity })
            { requirements.get("character", Character::class)!!.dexterity.compareDelta(1f) >= 0 }
//        statRandomization(1f)

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}