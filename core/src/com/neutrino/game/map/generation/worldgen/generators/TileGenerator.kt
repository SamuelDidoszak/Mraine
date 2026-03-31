package com.neutrino.game.map.generation.worldgen.generators

import com.neutrino.game.entities.Entities
import com.neutrino.game.map.generation.worldgen.GenerationContext
import com.neutrino.game.map.generation.worldgen.generators.util.GenerationArea
import com.neutrino.game.util.Constants
import com.neutrino.game.util.EntityName
import com.neutrino.game.util.SeedUtil
import com.neutrino.game.utility.ProbabilityList
import kotlin.random.Random

interface TileGenerator {

    fun generate(
        context: GenerationContext,
        area: GenerationArea,
        method: (x: Int, y: Int) -> Unit
    ): TileGenerator

    fun generate(
        context: GenerationContext,
        method: (x: Int, y: Int) -> Unit
    ): TileGenerator {
        return generate(
            context,
            GenerationArea(0, 0, Constants.ChunkSize, Constants.ChunkSize),
            method
        )
    }


    fun place(
        context: GenerationContext,
        area: GenerationArea,
        entity: EntityName
    ): TileGenerator {
        return generate(context, area) { x, y ->
            context.chunk.map[y][x].add(Entities.new(entity))
        }
    }

    fun place(
        context: GenerationContext,
        entity: EntityName
    ): TileGenerator {
        return generate(context) { x, y ->
            context.chunk.map[y][x].add(Entities.new(entity))
        }
    }


    fun place(
        context: GenerationContext,
        area: GenerationArea,
        entities: ProbabilityList<EntityName>,
        seedBranch: String
    ): TileGenerator {
        val rng = Random(SeedUtil.branch(context.world.worldSeed, seedBranch))

        return generate(context, area) { x, y ->
            entities.resolve(rng)?.let { context.chunk.map[y][x].add(Entities.new(it)) }
        }
    }

    fun place(
        context: GenerationContext,
        entities: ProbabilityList<EntityName>,
        seedBranch: String
    ): TileGenerator {
        val rng = Random(SeedUtil.branch(context.world.worldSeed, seedBranch))

        return generate(context) { x, y ->
            entities.resolve(rng)?.let { context.chunk.map[y][x].add(Entities.new(it)) }
        }
    }


    fun connect(
        context: GenerationContext,
        area: GenerationArea,
        generator: TileGenerator,
        generatorMethod: (x: Int, y: Int) -> Unit
    ): TileGenerator {
        return generate(context, area) { x, y ->
            generator.generate(
                context,
                GenerationArea(x, y, x + 1, y+ 1),
                generatorMethod
            )
        }
    }

    fun connect(
        context: GenerationContext,
        generator: TileGenerator,
        generatorMethod: (x: Int, y: Int) -> Unit
    ): TileGenerator {
        return generate(context) { x, y ->
            generator.generate(
                context,
                GenerationArea(x, y, x + 1, y+ 1),
                generatorMethod
            )
        }
    }
}













