package com.neutrino.game.domain.model.map

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Rat
import com.neutrino.game.domain.model.entities.DungeonFloorClean
import com.neutrino.game.domain.model.entities.DungeonWall
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.books.*
import com.neutrino.game.domain.model.items.edible.SmallHealingPotion
import com.neutrino.game.domain.model.items.equipment.armor.*
import com.neutrino.game.domain.model.items.equipment.misc.SmallBackpack
import com.neutrino.game.domain.model.items.equipment.misc.SmallBag
import com.neutrino.game.domain.model.items.equipment.weapons.*
import com.neutrino.game.domain.model.items.items.Gold
import com.neutrino.game.domain.model.items.scrolls.ScrollOfDefence
import com.neutrino.game.domain.use_case.map.generation_types.*
import com.neutrino.game.domain.use_case.map.utility.EntityParams
import com.neutrino.game.domain.use_case.map.utility.GenerationParams
import com.neutrino.game.domain.use_case.map.utility.MapGenerator
import com.neutrino.game.utility.Probability
import kotlin.reflect.KClass

enum class MapTags(val entityParams: EntityParams,
                   val mapGenerators: List<Probability<KClass<out MapGenerator>>>,
                   val characterList: List<KClass<out Character>>,
                   val itemList: List<Probability<KClass<out Item>>>,
                   val generationParams: GenerationParams,
                   val isModifier: Boolean) {
    STARTING_AREA(
        entityParams = EntityParams(
            wall = DungeonWall::class,
            floor = DungeonFloorClean::class
        ),
        mapGenerators = listOf(
            Probability(DefaultDungeon::class, 5f),
            Probability(CornerCaves::class, 5f),
            Probability(CavesLimitConnectivity::class, 5f),
            Probability(MazeA::class, 2f),
            Probability(MazeB::class, 2f),
            Probability(RoomsAndCorridorsB::class, 5f),
            Probability(SimpleCaves::class, 5f),
        ),
        characterList = listOf(
            Rat::class
        ),
        itemList = listOf(
            Probability(Gold::class, 40f),
            Probability(SmallHealingPotion::class, 5f),
            Probability(ScrollOfDefence::class, 0.3f),

            Probability(PocketKnife::class, 5f),
            Probability(BrokenSword::class, 50f),

            Probability(SmallBow::class, 5f),
            Probability(CurvedBow::class, 5f),
            Probability(BigBow::class, 5f),

            Probability(ExtinguishedFireWand::class, 5f),
            Probability(BasicFireWand::class, 5f),
            Probability(BasicPoisonWand::class, 5f),
            Probability(FireStaff::class, 5f),
            Probability(RockWand::class, 5f),

            Probability(Dagger::class, 5f),
            Probability(Sword::class, 5f),

            Probability(SmallBag::class, 5f),
            Probability(SmallBackpack::class, 5f),

            Probability(TornShirt::class, 5f),
            Probability(RippedPants::class, 5f),
            Probability(WoodenFlipFlops::class, 5f),

            Probability(LeatherCap::class, 5f),
            Probability(LinenShirt::class, 5f),
            Probability(LeatherSocks::class, 5f),

            Probability(ReinforcedLeatherCap::class, 5f),
            Probability(LeatherJacket::class, 5f),
            Probability(CuffedLinenPants::class, 5f),
            Probability(LeatherBoots::class, 5f),

            Probability(ReinforcedLeatherBoots::class, 5f),
            
            Probability(BookSkillBleed::class, 5f),
            Probability(BookSkillManaDrain::class, 5f),
            Probability(BookSkillMeteorite::class, 5f),
            Probability(BookSkillShieldBash::class, 5f),
            Probability(BookSkillTwoshot::class, 5f),
            Probability(BookSkillCripplingSpin::class, 5f),
            Probability(BookTeleportBackstab::class, 5f),
        ),
        generationParams = GenerationParams(
            difficulty = 100f
        ),
        isModifier = false
    )
}