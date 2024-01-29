import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.map.generation.EntityPositionRequirement
import com.neutrino.game.map.generation.EntityPositionRequirementType
import com.neutrino.game.map.generation.Generator
import com.neutrino.game.map.generation.Generators
import com.neutrino.game.map.generation.algorithms.RoomFinderAlgorithm
import com.neutrino.game.map.generation.algorithms.SquidGenerationAlgorithm
import squidpony.squidgrid.mapping.styled.TilesetType

Generators.add("Test") {
    SquidGenerationAlgorithm(TilesetType.DEFAULT_DUNGEON, it).generateAll()
}
Generators.add("Rooms", Generator(true, 1) {
	SquidGenerationAlgorithm(TilesetType.ROOMS_AND_CORRIDORS_B, it)
		.generateAll()
	RoomFinderAlgorithm(it)
		.addInRooms("WoodenCrateBigger", true, "nearWall", 2f, false, false)
		.addInRooms("StandingMetalTorch", 1f, false, false)
		.addInCorridors("WoodenTorch", Identity.Torch(), 1f, false, false)
		.addInCorridors("Barrel", listOf(), 1f, false, false)
        .add("WoodenDoor", Identity.Door(), 0.5f, true)
        .add("WoodenDoorArched", Identity.Door(), 0.75f, true)
		.generateAll()
})
Generators.add("Stuff", Generator(true, 1) {
    RoomFinderAlgorithm(it)
        .addInRooms("WoodenChestMid", 1f, false, true)
        .addInRooms("WoodenCrateBigger", true, "nearWall", 2f, false, false)
        .addInRooms("StandingMetalTorch", 1f, false, false)
        .addInCorridors("WoodenTorch", Identity.Torch(), 1f, false, false)
        .addInCorridors("Barrel", listOf(), 1f, false, false)
        .add("WoodenDoor", Identity.Door(), 0.5f, true)
        .add("WoodenDoorArched", Identity.Door(), 0.85f, true)
        .generateAll()
})
Generators.add("ChestGenerator", Generator(true, 1) {
	RoomFinderAlgorithm(it)
		.add("WoodenChestMid", 1f, false, true)
		.generateAll()
})
Generators.add("ALotOfStuff", Generator(true, 1) {
	SquidGenerationAlgorithm(TilesetType.SIMPLE_CAVES, it)
		.generateAll()
})
Generators.add("Dungeon", Generator(true, 1) {
	SquidGenerationAlgorithm(TilesetType.CAVES_LIMIT_CONNECTIVITY, it)
		.add("WoodenDoor", Identity.Door(), 0.5f, true, false)
		.add("WoodenDoorArched", Identity.Door(), 1.0f, true, false)
        .add("StandingMetalTorch", 0.02f, true, false)
		.generateAll()
	RoomFinderAlgorithm(it)
		.addInRooms("WoodenChestMid", 0.025f, true, false)
		.addInCorridors("WoodenTorch", listOf(), 0.03f, true, false)
		.addInCorridors("ClayPotMultiple", true, "nearWall", 1f, false, false)
		.addInRooms("Barrel", true, "nearWall", 1f, false, false)
		.addInRooms("WoodenCrateSmall", true, "nearWall", 1f, false, false)
		.addInRooms("CandleWhiteMultiple", listOf(), 0.01f, true, false)
		.addInCorridors("ClayPot", true, "nearWall", 1f, false, false)
		.addInRooms("ClayPot", true, "nearWall", 1f, false, false)
		.addInRooms("CandleWhiteSingle", listOf(
			EntityPositionRequirement(EntityPositionRequirementType.OR, "CandleWhiteMultiple", listOf(1, 4, 7, 8, 9, 6, 3, 2)),
		), 1f, false, false)
		.generateAll()
})
