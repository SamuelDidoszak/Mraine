import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.map.generation.Tilesets

Tilesets.add("Dungeon", listOf(
    Identity.Wall() to "DungeonWall",
    Identity.Floor() to "DungeonFloorClean",
    Identity.Floor() to "DungeonFloor",
    Identity.StairsUp() to "DungeonStairsUp",
    Identity.StairsDown() to "DungeonStairsDown",
    Identity.Door() to "WoodenDoor"
))
