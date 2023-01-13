package com.neutrino.game.domain.model.items.equipment.weapons

import com.badlogic.gdx.graphics.g2d.TextureAtlas
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

class ExtinguishedFireWand: EquipmentItem(), ItemType.EQUIPMENT.RHAND, HasProjectile {
    override val handedItemType: HandedItemType = HandedItemType.WAND
    override val name: String = "Extinguished fire wand"
    override val description: String = "A piece of red rock on a stick"

    override val textureNames: List<String> = listOf("extinguishedFireWand")
    override var texture: TextureAtlas.AtlasRegion = setTexture()

    override var goldValueOg: Int = 15

    override val projectileType: Projectile.ProjectileType = Projectile.ProjectileType.FIREPROJECTILE

    override var requirements: Requirement = Requirement(mutableMapOf(Pair("character", Data<Character>())))

    override val modifierList: ArrayList<EventWrapper> = arrayListOf(
        OnOffEvent(EventModifyStat(StatsEnum.FIREDAMAGE, 2f)),
        OnOffEvent(EventModifyStat(StatsEnum.DAMAGEVARIATION, 1f)),
        OnOffEvent(EventModifyStat(StatsEnum.RANGE, 5))
    )

    override var attack: Attack = ProjectileAttack(this, getDamageTypesFromModifiers(modifierList))

    init {
        goldValue = goldValueOg
        realValue = (goldValue * 1.2).roundToInt()
    }
}