package com.neutrino.game.domain.model.items.equipment.weapons

import com.neutrino.game.compareDelta
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.RangeType
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.items.EquipmentItemRanged
import com.neutrino.game.domain.model.items.HandedItemType
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.items.utility.Projectile
import com.neutrino.game.domain.model.systems.attack.Attack
import com.neutrino.game.domain.model.systems.attack.ProjectileAttack
import com.neutrino.game.domain.model.systems.event.Data
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.event.types.EventModifyStat
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import com.neutrino.game.utility.serialization.AtlasRegion
import kotlin.math.roundToInt

class BigBow: EquipmentItemRanged(), ItemType.EQUIPMENT.RHAND {
    override val handedItemType: HandedItemType = HandedItemType.BOW
    override val name: String = "Big bow"
    override val description: String = "Almost the same size as you"

    override val textureNames: List<String> = listOf("bigBow")
    override var texture: AtlasRegion = setTexture()

    override var goldValueOg: Int = 65

    override var range: Int = 7
    override var rangeType: RangeType = RangeType.SQUARE
    override val projectileType: Projectile.ProjectileType = Projectile.ProjectileType.WOODENARROW

    override var requirements: RequirementPrintable = RequirementPrintable(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.DAMAGE, 5f)),
        OnOffEvent(EventModifyStat(StatsEnum.DAMAGE_VARIATION, 2f)),
        OnOffEvent(EventModifyStat(StatsEnum.RANGE, range))
    )

    override var attack: Attack = ProjectileAttack(this, getDamageTypesFromModifiers(modifierList))

    init {
        requirements
            .add(RequirementPrintable.PrintableReq("Dexterity", 4f)
            { requirements.get("character", Character::class)!!.dexterity })
            { requirements.get("character", Character::class)!!.dexterity.compareDelta(4f) >= 0 }

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}