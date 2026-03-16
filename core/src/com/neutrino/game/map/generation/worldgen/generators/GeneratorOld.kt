package com.neutrino.game.map.generation.worldgen.generators

import com.neutrino.game.entities.Entities
import com.neutrino.game.map.chunk.EntityList
import com.neutrino.game.map.generation.worldgen.GenerationContext
import com.neutrino.game.map.generation.worldgen.generators.util.GenerationArea
import com.neutrino.game.util.Constants
import com.neutrino.game.util.EntityName
import com.neutrino.game.util.SeedUtil
import com.neutrino.game.utility.ProbabilityList
import kotlin.random.Random

abstract class GeneratorOld(seedBranchName: String) {

    private val rng = Random(SeedUtil.branch(Constants.Seed, seedBranchName))

    abstract fun generate(context: GenerationContext, area: GenerationArea, method: (EntityList) -> Unit)



    fun generate(context: GenerationContext, method: (EntityList) -> Unit) {
        generate(
            context,
            GenerationArea(0, 0, Constants.ChunkSize, Constants.ChunkSize),
            method
        )
    }

    // Probability list

    fun generate(context: GenerationContext, area: GenerationArea, entities: ProbabilityList<EntityName>) {
        generate(context, area) { entityList ->
            entities.resolve(rng)?.also { entityList.add(Entities.new(it)) }
        }
    }

    fun generate(context: GenerationContext, entities: ProbabilityList<EntityName>) {
        generate(
            context,
            GenerationArea(0, 0, Constants.ChunkSize, Constants.ChunkSize),
            entities
        )
    }

    // Generators

    fun generate(context: GenerationContext, area: GenerationArea, generator: GeneratorOld, method: (EntityList) -> Unit) {
        generate(context, area) {
            generator.generate(context, area, method)
        }
    }

    fun generate(context: GenerationContext, generator: GeneratorOld, method: (EntityList) -> Unit) {
        generate(
            context,
            GenerationArea(0, 0, Constants.ChunkSize, Constants.ChunkSize),
            generator,
            method
        )
    }

    // Entity name

    fun generate(context: GenerationContext, area: GenerationArea, generator: GeneratorOld, entity: EntityName) {
        generate(
            context,
            area,
            generator
        ) { entityList -> entityList.add(Entities.new(entity)) }
    }

    fun generate(context: GenerationContext, generator: GeneratorOld, entity: EntityName) {
        generate(
            context,
            GenerationArea(0, 0, Constants.ChunkSize, Constants.ChunkSize),
            generator,
            entity
        )
    }

    fun generate(context: GenerationContext, area: GenerationArea, entity: EntityName) {
        generate(context, area) { it.add(Entities.new(entity)) }
    }

    fun generate(context: GenerationContext, entity: EntityName) {
        generate(context) { it.add(Entities.new(entity)) }
    }
}