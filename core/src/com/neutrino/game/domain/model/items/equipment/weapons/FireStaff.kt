package com.neutrino.game.domain.model.items.equipment.weapons

import com.badlogic.gdx.graphics.g2d.TextureAtlas
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
import kotlin.math.roundToInt

class FireStaff: EquipmentItemRanged(), ItemType.EQUIPMENT.RHAND {
    override val handedItemType: HandedItemType = HandedItemType.WAND
    override val name: String = "Fire staff"
    override val description: String = "Proper magical staff"

    override val textureNames: List<String> = listOf("fireStaff")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override var goldValueOg: Int = 65

    override var range: Int = 5
    override var rangeType: RangeType = RangeType.SQUARE
    override val projectileType: Projectile.ProjectileType = Projectile.ProjectileType.FIREPROJECTILE

    override var requirements: RequirementPrintable = RequirementPrintable(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.FIRE_DAMAGE, 7f)),
        OnOffEvent(EventModifyStat(StatsEnum.DAMAGE_VARIATION, 2f)),
        OnOffEvent(EventModifyStat(StatsEnum.RANGE, range))
    )

    override var attack: Attack = ProjectileAttack(this, getDamageTypesFromModifiers(modifierList))

    init {
        requirements
            .add(RequirementPrintable.PrintableReq("Intelligence", 4f)
            { requirements.get("character", Character::class)!!.intelligence })
            {requirements.get("character", Character::class)!!.intelligence.compareDelta(4f) >= 0  }

        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}