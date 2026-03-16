package com.neutrino.game.map.generation.worldgen.generators

import com.neutrino.game.entities.Entities
import com.neutrino.game.entities.Entity
import com.neutrino.game.map.generation.worldgen.GenerationContext
import com.neutrino.game.map.generation.worldgen.generators.util.GenerationArea
import com.neutrino.game.util.EntityName
import com.neutrino.game.util.SeedUtil
import com.neutrino.game.utility.ProbabilityList
import kotlin.random.Random

class FloorGenerator: TileGenerator {

    override fun generate(context: GenerationContext, area: GenerationArea, method: (x: Int, y: Int) -> Unit) {
        area.forEachTile { x, y -> method(x, y) }
    }

    private fun generateFloor(context: GenerationContext, entity: Entity, x: Int, y: Int) {
        if (context.chunk.map[x][y].isEmpty())
            context.chunk.map[x][y].add(entity)
        else
            context.chunk.map[x][y][0] = entity
    }

    override fun place(context: GenerationContext, entity: EntityName) {
        generate(context) { x, y ->
            generateFloor(context, Entities.new(entity), x, y)
        }
    }

    override fun place(context: GenerationContext, area: GenerationArea, entity: EntityName) {
        generate(context, area) { x, y ->
            generateFloor(context, Entities.new(entity), x, y)
        }
    }

    override fun place(context: GenerationContext, entities: ProbabilityList<EntityName>, seedBranch: String) {
        val rng = Random(SeedUtil.branch(context.world.worldSeed, seedBranch))

        generate(context) { x, y ->
            entities.resolve(rng)?.let { generateFloor(context, Entities.new(it), x, y) }
        }
    }

    override fun place(
        context: GenerationContext,
        area: GenerationArea,
        entities: ProbabilityList<EntityName>,
        seedBranch: String
    ) {
        val rng = Random(SeedUtil.branch(context.world.worldSeed, seedBranch))

        generate(context, area) { x, y ->
            entities.resolve(rng)?.let { generateFloor(context, Entities.new(it), x, y) }
        }
    }
}