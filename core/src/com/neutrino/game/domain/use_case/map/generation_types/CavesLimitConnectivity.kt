package com.neutrino.game.domain.use_case.map.generation_types

//import com.neutrino.game.domain.model.entities.*
//import com.neutrino.game.domain.model.entities.containers.*
//import com.neutrino.game.domain.model.entities.lightSources.CandleSingle
//import com.neutrino.game.domain.model.entities.lightSources.CandlesMultiple
//import com.neutrino.game.domain.model.entities.lightSources.StandingTorch
//import com.neutrino.game.domain.model.entities.lightSources.Torch
//import com.neutrino.game.domain.model.map.Level
//import com.neutrino.game.domain.model.map.TagInterpretation
//import com.neutrino.game.domain.use_case.map.utility.*
//import squidpony.squidgrid.mapping.styled.TilesetType
//
//class CavesLimitConnectivity(level: Level, interpretedTags: TagInterpretation): MapGenerator(level, interpretedTags) {
//
//    override fun generate(): List<List<MutableList<Entity>>> {
//        val squidGeneration = SquidGeneration(level)
//
//        squidGeneration.generateDungeon(TilesetType.CAVES_LIMIT_CONNECTIVITY)
//        squidGeneration.setWalls(map, interpretedTags.entityParams.wall)
//        addEntities(DungeonFloorClean::class, listOf(), 1f)
//        squidGeneration.setEntrances(map, DungeonStairsDown::class, DungeonStairsUp::class)
//
//        addEntities(
//            StonePillar::class, listOf(
//                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(7, 8, 9)),
//                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(1, 2, 3, 4, 6))
//            ), 6f, assertAmount = true)
//        addEntities(
//            StonePillar::class, listOf(
//                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(1, 2, 3)),
//                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(4, 6, 7, 8, 9))
//            ), 2f, assertAmount = true)
//
//        addEntities(WoodenDoor::class, com.neutrino.game.map.generation.GenerationRequirements.getDefaults(WoodenDoor::class), 10f, assertAmount = true)
//        addEntities(WoodenDoorArched::class, com.neutrino.game.map.generation.GenerationRequirements.getDefaults(WoodenDoorArched::class), 8f, assertAmount = true)
//        addEntities(CrateDoor::class, com.neutrino.game.map.generation.GenerationRequirements.getDefaults(CrateDoor::class), 5f, assertAmount = true)
//
//        addEntities(Torch::class, com.neutrino.game.map.generation.GenerationRequirements.getDefaults(Torch::class), 20f, assertAmount = true)
//        addEntities(
//            StandingTorch::class, listOf(
//                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(1, 2, 3, 4, 6, 7, 8, 9))
//            ), 6f, assertAmount = true
//        )
//        addEntities(CandleSingle::class, com.neutrino.game.map.generation.GenerationRequirements.requirementNearWall(), 15f, assertAmount = true)
//        addEntities(
//            CandlesMultiple::class, listOf(
//                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(1, 2, 3, 4, 6, 7, 8, 9))
//            ), 3f, assertAmount = true
//        )
//        addEntities(
//            CandlesMultiple::class, listOf(
//                EntityPositionRequirement(EntityPositionRequirementType.OR, CandlesMultiple::class, listOf(1, 2, 3, 4, 6, 7, 8, 9)),
//                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(1, 2, 3, 4, 6, 7, 8, 9))
//            ), 0.15f
//        )
//        addEntities(
//            CandleSingle::class, listOf(
//                EntityPositionRequirement(EntityPositionRequirementType.OR, CandlesMultiple::class, listOf(1, 2, 3, 4, 6, 7, 8, 9)),
//                EntityPositionRequirement(EntityPositionRequirementType.NOR, Wall::class, listOf(1, 2, 3, 4, 6, 7, 8, 9))
//            ), 0.25f
//        )
//
//        addEntities(Barrel::class, com.neutrino.game.map.generation.GenerationRequirements.requirementNearWall(), 0.0085f)
//        addEntities(CrateSmall::class, com.neutrino.game.map.generation.GenerationRequirements.requirementNearWall(), 0.005f)
//        addEntities(
//            CrateBigger::class, listOf(
//                EntityPositionRequirement(EntityPositionRequirementType.AND, Wall::class, listOf(7, 8, 9)),
//                EntityPositionRequirement(EntityPositionRequirementType.NAND, Wall::class, listOf(4, 6)),
//                EntityPositionRequirement(EntityPositionRequirementType.NAND, CrateBigger::class, listOf(4)),
//            ), 0.01f)
//
//        addEntities(WoodenChest::class, com.neutrino.game.map.generation.GenerationRequirements.getDefaults(WoodenChest::class), 3f, assertAmount = true)
//
//        addEntities(ClayPot::class, com.neutrino.game.map.generation.GenerationRequirements.requirementNearWall(), 0.01f)
//        addEntities(ClayPotMultiple::class, com.neutrino.game.map.generation.GenerationRequirements.requirementNearWall(), 0.005f)
//
//        return map
//    }
//}