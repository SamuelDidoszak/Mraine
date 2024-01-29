import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.map.generation.EntityPositionRequirement
import com.neutrino.game.map.generation.EntityPositionRequirementType
import com.neutrino.game.map.generation.GenerationRequirements

GenerationRequirements.addOther("nearWall", listOf(
    EntityPositionRequirement(EntityPositionRequirementType.OR, Identity.Wall(), listOf(2, 4, 6, 8)),
    EntityPositionRequirement(EntityPositionRequirementType.NAND, Identity.Wall(), listOf(4, 6)),
    EntityPositionRequirement(EntityPositionRequirementType.NAND, Identity.Wall(), listOf(2, 8))
))
GenerationRequirements.addOther("nearWallBlockEntries", listOf(
    EntityPositionRequirement(EntityPositionRequirementType.OR, Identity.Wall(), listOf(2, 4, 6, 8))
))
GenerationRequirements.add(Identity.Door(), listOf(
	EntityPositionRequirement(EntityPositionRequirementType.AND),
	EntityPositionRequirement(EntityPositionRequirementType.AND, Identity.Wall(), listOf(8, 2)),
	EntityPositionRequirement(EntityPositionRequirementType.NOR, Identity.Wall(), listOf(4, 9, 3)),
	EntityPositionRequirement(EntityPositionRequirementType.AND),
	EntityPositionRequirement(EntityPositionRequirementType.AND, Identity.Wall(), listOf(8, 2)),
	EntityPositionRequirement(EntityPositionRequirementType.NOR, Identity.Wall(), listOf(6, 7, 1)),
	EntityPositionRequirement(EntityPositionRequirementType.AND),
	EntityPositionRequirement(EntityPositionRequirementType.AND, Identity.Wall(), listOf(4, 6)),
	EntityPositionRequirement(EntityPositionRequirementType.NOR, Identity.Wall(), listOf(8, 1, 3)),
	EntityPositionRequirement(EntityPositionRequirementType.AND),
	EntityPositionRequirement(EntityPositionRequirementType.AND, Identity.Wall(), listOf(4, 6)),
	EntityPositionRequirement(EntityPositionRequirementType.NOR, Identity.Wall(), listOf(2, 7, 9)),
))
GenerationRequirements.add(Identity.Torch(), listOf(
    EntityPositionRequirement(EntityPositionRequirementType.AND),
    EntityPositionRequirement(EntityPositionRequirementType.AND, Identity.Wall(), listOf(7, 8, 9)),
    EntityPositionRequirement(EntityPositionRequirementType.NOR, Identity.Wall(), listOf(1, 2, 3)),
    EntityPositionRequirement(EntityPositionRequirementType.AND),
    EntityPositionRequirement(EntityPositionRequirementType.AND, Identity.Wall(), listOf(1, 4, 7)),
    EntityPositionRequirement(EntityPositionRequirementType.NOR, Identity.Wall(), listOf(3, 6, 9)),
    EntityPositionRequirement(EntityPositionRequirementType.AND),
    EntityPositionRequirement(EntityPositionRequirementType.AND, Identity.Wall(), listOf(3, 6, 9)),
    EntityPositionRequirement(EntityPositionRequirementType.NOR, Identity.Wall(), listOf(1, 4, 7))
))
GenerationRequirements.add("StandingMetalTorch", listOf(
	EntityPositionRequirement(EntityPositionRequirementType.NOR, Identity.Wall(), listOf(7, 8, 9, 6, 3, 2, 1, 4)),
))
GenerationRequirements.add("WoodenChestMid", listOf(
	EntityPositionRequirement(EntityPositionRequirementType.AND),
	EntityPositionRequirement(EntityPositionRequirementType.AND, Identity.Wall(), listOf(7, 8, 9)),
	EntityPositionRequirement(EntityPositionRequirementType.NOR, Identity.Wall(), listOf(6, 3, 2, 1, 4)),
	EntityPositionRequirement(EntityPositionRequirementType.AND),
	EntityPositionRequirement(EntityPositionRequirementType.AND, Identity.Wall(), listOf(1, 2, 3)),
	EntityPositionRequirement(EntityPositionRequirementType.NOR, Identity.Wall(), listOf(4, 7, 8, 9, 6)),
	EntityPositionRequirement(EntityPositionRequirementType.AND),
	EntityPositionRequirement(EntityPositionRequirementType.AND, Identity.Wall(), listOf(7, 4, 1)),
	EntityPositionRequirement(EntityPositionRequirementType.NOR, Identity.Wall(), listOf(8, 9, 6, 3, 2)),
	EntityPositionRequirement(EntityPositionRequirementType.AND),
	EntityPositionRequirement(EntityPositionRequirementType.AND, Identity.Wall(), listOf(9, 6, 3)),
	EntityPositionRequirement(EntityPositionRequirementType.NOR, Identity.Wall(), listOf(2, 1, 4, 7, 8)),
))
GenerationRequirements.add(Identity.Container(), listOf(
	EntityPositionRequirement(EntityPositionRequirementType.AND),
	EntityPositionRequirement(EntityPositionRequirementType.OR, "DungeonWall", listOf(8, 6, 3, 9)),
	EntityPositionRequirement(EntityPositionRequirementType.NAND, Identity.Wall(), listOf(7, 4, 1, 2)),
	EntityPositionRequirement(EntityPositionRequirementType.OR),
	EntityPositionRequirement(EntityPositionRequirementType.AND, Identity.Wall(), listOf(7, 4, 1)),
	EntityPositionRequirement(EntityPositionRequirementType.AND, "DungeonWall", listOf(9, 6, 3, 4)),
	EntityPositionRequirement(EntityPositionRequirementType.AND, Identity.Wall(), listOf(7, 8, 9)),
	EntityPositionRequirement(EntityPositionRequirementType.AND, Identity.Wall(), listOf(1, 2, 3)),
	EntityPositionRequirement(EntityPositionRequirementType.AND, Identity.Wall(), listOf(2, 3, 6)),
))
GenerationRequirements.addOther("costam", listOf(
	EntityPositionRequirement(EntityPositionRequirementType.AND, "WoodenChestMid", listOf(6)),
	EntityPositionRequirement(EntityPositionRequirementType.AND, Identity.Wall(), listOf(8)),
))