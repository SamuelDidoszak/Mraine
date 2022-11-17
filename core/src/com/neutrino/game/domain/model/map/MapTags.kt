package com.neutrino.game.domain.model.map

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Rat
import com.neutrino.game.domain.model.entities.DungeonFloor
import com.neutrino.game.domain.model.entities.DungeonWall
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.edible.SmallHealingPotion
import com.neutrino.game.domain.model.items.equipment.Knife
import com.neutrino.game.domain.model.items.items.Gold
import com.neutrino.game.domain.model.items.scrolls.ScrollOfDefence
import com.neutrino.game.domain.use_case.map.utility.EntityParams
import com.neutrino.game.domain.use_case.map.utility.GenerationParams
import kotlin.reflect.KClass

enum class MapTags(val entityParams: EntityParams, val characterList: List<KClass<Character>>, val itemList: List<Pair<KClass<Item>, Float>>,
                   val generationParams: GenerationParams, val isModifier: Boolean) {
    STARTING_AREA(
        entityParams = EntityParams(
            wall = DungeonWall::class as KClass<Entity>,
            floor = DungeonFloor::class as KClass<Entity>
        ),
        characterList = listOf(
            Rat::class as KClass<Character>
        ),
        itemList = listOf(
            Pair(Gold::class as KClass<Item>, 40f),
            Pair(SmallHealingPotion::class as KClass<Item>, 5f),
            Pair(ScrollOfDefence::class as KClass<Item>, 0.3f),
            Pair(Knife::class as KClass<Item>, 5f)
        ),
        generationParams = GenerationParams(
            difficulty = 1f
        ),
        isModifier = false
    )
}