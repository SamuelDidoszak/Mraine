package com.neutrino.game.utility.serialization

import com.neutrino.game.domain.model.entities.*
import com.neutrino.game.domain.model.entities.containers.*
import com.neutrino.game.domain.model.entities.lightSources.CandleSingle
import com.neutrino.game.domain.model.entities.lightSources.CandlesMultiple
import com.neutrino.game.domain.model.entities.lightSources.StandingTorch
import com.neutrino.game.domain.model.entities.lightSources.Torch
import com.neutrino.game.domain.model.entities.utility.ItemEntity
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object Serializers {
    val module = SerializersModule {
        polymorphic(Entity::class) {
            // door
            subclass(CrateDoor::class)
            subclass(WoodenDoor::class)
            subclass(WoodenDoorArched::class)

            // walls
            subclass(DungeonWall::class)

            // floors
            subclass(DungeonFloor::class)
            subclass(DungeonFloorClean::class)

            // stairs
            subclass(DungeonStairsUp::class)
            subclass(DungeonStairsDown::class)

            // misc
            subclass(StonePillar::class)

            // containers
            subclass(Barrel::class)
            subclass(ClayPot::class)
            subclass(ClayPotMultiple::class)
            subclass(CrateBigger::class)
            subclass(CrateSmall::class)
            subclass(WoodenChest::class)

            // light sources
            subclass(CandleSingle::class)
            subclass(CandlesMultiple::class)
            subclass(StandingTorch::class)
            subclass(Torch::class)

            // Abstracts
            subclass(ItemEntity::class)
        }

        /** ============================================================     Items     =============================================================================*/

    }


    val format = Json {serializersModule = module}
}