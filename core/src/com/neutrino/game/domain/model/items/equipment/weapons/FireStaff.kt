package com.neutrino.game.domain.model.items.equipment.weapons

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.compareDelta
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.items.EquipmentItem
import com.neutrino.game.domain.model.items.HandedItemType
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.items.utility.HasProjectile
import com.neutrino.game.domain.model.items.utility.Projectile
import com.neutrino.game.domain.model.systems.attack.Attack
import com.neutrino.game.domain.model.systems.attack.ProjectileAttack
import com.neutrino.game.domain.model.systems.event.Data
import com.neutrino.game.domain.model.systems.event.Requirement
import com.neutrino.game.domain.model.systems.event.types.EventModifyStat
import com.neutrino.game.domain.model.systems.event.wrappers.EventWrapper
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import kotlin.math.roundToInt

class FireStaff: EquipmentItem(), ItemType.EQUIPMENT.RHAND, HasProjectile {
    override val handedItemType: HandedItemType = HandedItemType.WAND
    override val name: String = "Fire staff"
    override val description: String = "Proper magical staff"

    override val textureNames: List<String> = listOf("fireStaff")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override var goldValueOg: Int = 65

    override val projectileType: Projectile.ProjectileType = Projectile.ProjectileType.FIREPROJECTILE

    override var requirements: Requirement = Requirement(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.FIREDAMAGE, 7f)),
        OnOffEvent(EventModifyStat(StatsEnum.DAMAGEVARIATION, 2f)),
        OnOffEvent(EventModifyStat(StatsEnum.RANGE, 5))
    )

    override var attack: Attack = ProjectileAttack(this, getDamageTypesFromModifiers(modifierList))

    init {
        requirements.add { requirements.get("character", Character::class)!!.intelligence.compareDelta(4f) >= 0  }
        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}