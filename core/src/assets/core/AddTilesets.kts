import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.generation.Tilesets

Tilesets.add("Dungeon", listOf(
    Identity.Wall() to "DungeonWall",
    Identity.Floor() to "DungeonFloorClean",
    Identity.Floor() to "DungeonFloor",
    Identity.StairsUp() to "DungeonStairsUp",
    Identity.StairsDown() to "DungeonStairsDown",
    Identity.Door() to "WoodenDoor"
))
