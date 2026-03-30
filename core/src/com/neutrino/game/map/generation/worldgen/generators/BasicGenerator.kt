package com.neutrino.game.map.generation.worldgen.generators

import com.neutrino.game.entities.Entities
import com.neutrino.game.map.generation.worldgen.GenerationContext
import com.neutrino.game.map.generation.worldgen.generators.util.GenerationArea
import com.neutrino.game.util.EntityName
import com.neutrino.game.util.SeedUtil
import com.neutrino.game.utility.ProbabilityList
import kotlin.random.Random

class BasicGenerator: TileGenerator {

    override fun generate(context: GenerationContext, area: GenerationArea, method: (x: Int, y: Int) -> Unit): BasicGenerator {
        area.forEachTile { x, y -> method(x, y) }
        return this
    }

    fun generateFloor(context: GenerationContext, area: GenerationArea, entity: EntityName): BasicGenerator {
        area.forEachTile { x, y ->
            if (context.chunk.map[x][y].isEmpty())
                context.chunk.map[x][y].add(Entities.new(entity))
            else
                context.chunk.map[x][y][0] = Entities.new(entity)
        }
        return this
    }

    fun generateFloor(context: GenerationContext, entity: EntityName): BasicGenerator {
        return generateFloor(context, GenerationArea.Default, entity)
    }

    fun generateFloor(context: GenerationContext, area: GenerationArea, entities: ProbabilityList<EntityName>, seedBranch: String): BasicGenerator {
        val rng = Random(SeedUtil.branch(context.world.worldSeed, seedBranch))
        entities.resolve(rng)?.let { generateFloor(context, area, it) }
        return this
    }



    override fun place(context: GenerationContext, entity: EntityName): BasicGenerator {
        return super.place(context, entity) as BasicGenerator
    }

    override fun place(
        context: GenerationContext,
        entities: ProbabilityList<EntityName>,
        seedBranch: String
    ): BasicGenerator {
        return super.place(context, entities, seedBranch) as BasicGenerator
    }

    override fun place(context: GenerationContext, area: GenerationArea, entity: EntityName): BasicGenerator {
        return super.place(context, area, entity) as BasicGenerator
    }

    override fun place(
        context: GenerationContext,
        area: GenerationArea,
        entities: ProbabilityList<EntityName>,
        seedBranch: String
    ): BasicGenerator {
        return super.place(context, area, entities, seedBranch) as BasicGenerator
    }

    override fun generate(context: GenerationContext, method: (x: Int, y: Int) -> Unit): BasicGenerator {
        return super.generate(context, method) as BasicGenerator
    }

    override fun connect(
        context: GenerationContext,
        generator: TileGenerator,
        generatorMethod: (x: Int, y: Int) -> Unit
    ): BasicGenerator {
        return super.connect(context, generator, generatorMethod) as BasicGenerator
    }

    override fun connect(
        context: GenerationContext,
        area: GenerationArea,
        generator: TileGenerator,
        generatorMethod: (x: Int, y: Int) -> Unit
    ): BasicGenerator {
        return super.connect(context, area, generator, generatorMethod) as BasicGenerator
    }
}