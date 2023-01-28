package com.neutrino.game.domain.model.map

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Rat
import com.neutrino.game.domain.model.entities.DungeonFloor
import com.neutrino.game.domain.model.entities.DungeonWall
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.books.*
import com.neutrino.game.domain.model.items.edible.SmallHealingPotion
import com.neutrino.game.domain.model.items.equipment.armor.*
import com.neutrino.game.domain.model.items.equipment.misc.SmallBackpack
import com.neutrino.game.domain.model.items.equipment.misc.SmallBag
import com.neutrino.game.domain.model.items.equipment.weapons.*
import com.neutrino.game.domain.model.items.items.Gold
import com.neutrino.game.domain.model.items.scrolls.ScrollOfDefence
import com.neutrino.game.domain.use_case.map.utility.EntityParams
import com.neutrino.game.domain.use_case.map.utility.GenerationParams
import kotlin.reflect.KClass

enum class MapTags(val entityParams: EntityParams, val characterList: List<KClass<out Character>>, val itemList: List<Pair<KClass<out Item>, Float>>,
                   val generationParams: GenerationParams, val isModifier: Boolean) {
    STARTING_AREA(
        entityParams = EntityParams(
            wall = DungeonWall::class as KClass<Entity>,
            floor = DungeonFloor::class as KClass<Entity>
        ),
        characterList = listOf(
            Rat::class
        ),
        itemList = listOf(
            Pair(Gold::class, 40f),
            Pair(SmallHealingPotion::class, 5f),
            Pair(ScrollOfDefence::class, 0.3f),

            Pair(PocketKnife::class, 5f),
            Pair(BrokenSword::class, 5f),

            Pair(SmallBow::class, 5f),
            Pair(CurvedBow::class, 5f),
            Pair(BigBow::class, 5f),

            Pair(ExtinguishedFireWand::class, 5f),
            Pair(BasicFireWand::class, 5f),
            Pair(BasicPoisonWand::class, 5f),
            Pair(FireStaff::class, 5f),
            Pair(RockWand::class, 5f),

            Pair(Dagger::class, 5f),
            Pair(Sword::class, 5f),

            Pair(SmallBag::class, 5f),
            Pair(SmallBackpack::class, 5f),

            Pair(TornShirt::class, 5f),
            Pair(RippedPants::class, 5f),
            Pair(WoodenFlipFlops::class, 5f),

            Pair(LeatherCap::class, 5f),
            Pair(LinenShirt::class, 5f),
            Pair(LeatherSocks::class, 5f),

            Pair(ReinforcedLeatherCap::class, 5f),
            Pair(LeatherJacket::class, 5f),
            Pair(CuffedLinenPants::class, 5f),
            Pair(LeatherBoots::class, 5f),

            Pair(ReinforcedLeatherBoots::class, 5f),
            
            Pair(BookSkillBleed::class, 5f),
            Pair(BookSkillManaDrain::class, 5f),
            Pair(BookSkillMeteorite::class, 5f),
            Pair(BookSkillShieldBash::class, 5f),
            Pair(BookSkillTwoshot::class, 5f),
            Pair(BookSkillCripplingSpin::class, 5f),
            Pair(BookTeleportBackstab::class, 5f),
        ),
        generationParams = GenerationParams(
            difficulty = 100f
        ),
        isModifier = false
    )
}