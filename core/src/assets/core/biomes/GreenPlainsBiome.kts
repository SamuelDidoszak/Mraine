import com.neutrino.game.entities.items.attributes.tags.ItemTag
import com.neutrino.game.map.generation.worldgen.ItemQuery
import com.neutrino.game.map.generation.worldgen.LootTable
import com.neutrino.game.map.generation.worldgen.LootVal
import com.neutrino.game.map.generation.worldgen.biomes.Biome
import com.neutrino.game.map.generation.worldgen.biomes.BiomeFlags
import com.neutrino.game.map.generation.worldgen.biomes.Biomes
import com.neutrino.game.map.generation.worldgen.generators.FloorGenerator

Biomes.add(Biome(
    name = "Green plains",
    itemList = LootTable(
        LootVal.Type(ItemQuery(requiredTags = setOf(ItemTag.ALL)), 1f)
    ),
    enemyList = listOf("Mouse", "Slime"),
    biomeFlags = BiomeFlags(
        generateOnFloorProbability = 0.65f
    ),
    biomeGenerator = { context ->
        FloorGenerator()
            .place(context, "DungeonFloor")
    }
))