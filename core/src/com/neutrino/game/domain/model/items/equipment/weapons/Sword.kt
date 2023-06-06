package com.neutrino.game.domain.model.items.equipment.weapons

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.esotericsoftware.kryo.kryo5.Kryo
import com.esotericsoftware.kryo.kryo5.io.Input
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
import com.neutrino.game.utility.serialization.HeaderSerializable

import kotlin.math.roundToInt

class Sword: EquipmentItem(), ItemType.EQUIPMENT.RHAND, HeaderSerializable {
    @Transient
    override val handedItemType: HandedItemType = HandedItemType.SWORD
    @Transient
    override val name: String = "Sword"
    @Transient
    override val description: String = "Sword with a bit dull blade"

    @Transient
    override val textureNames: List<String> = listOf("sword")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override var goldValueOg: Int = 30

    @Transient
    override var requirements: RequirementPrintable = RequirementPrintable(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.DAMAGE, 3.25f)),
        // example
//        TimedEvent(0.0, 5.0, Int.MAX_VALUE, EventHeal(1f))
    )

    @Transient
    override var attack: Attack = BasicAttack(getDamageTypesFromModifiers(modifierList))

    override fun readAfter(kryo: Kryo?, input: Input?) {
        attack = BasicAttack(getDamageTypesFromModifiers(modifierList))
    }

    init {
        requirements
            .add(RequirementPrintable.PrintableReq("Strength", 1f)
            { requirements.get("character", Character::class)!!.strength })
            { requirements.get("character", Character::class)!!.strength.compareDelta(1f) >= 0 }
//        statRandomization(1f)

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}