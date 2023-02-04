package com.neutrino.game.domain.use_case.map

import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.domain.model.map.Level
import com.neutrino.game.domain.model.map.TagInterpretation
import com.neutrino.game.domain.use_case.map.utility.MapGenerator
import com.neutrino.game.utility.Probability
import kotlin.reflect.KClass

/**
 * Class used for map generation
 */
class GenerateMap(
    val level: Level
) {

    /**
     * Generates the map
     */
    operator fun invoke(): List<List<MutableList<Entity>>> {
        val interpretedTags = TagInterpretation(level.tagList)

        // get map generator
        val mapGenerators = ArrayList<Probability<KClass<out MapGenerator>>>()
        var sum = 0f
        for (generator in interpretedTags.mapGenerators.sortedWith(compareBy {it.probability})) {
            sum += generator.probability
            mapGenerators.add(Probability(generator.value, sum))
        }
        var random = level.randomGenerator.nextFloat() * sum
        if (random >= sum)
            random = sum - 0.000001f
        var chosenGenerator: KClass<MapGenerator>? = null
        for (generator in mapGenerators) {
            if (random < generator.probability) {
                chosenGenerator = generator.value as KClass<MapGenerator>
                break
            }
        }

//        val map = chosenGenerator!!.primaryConstructor!!.call(level, interpretedTags).generate()
        val map = com.neutrino.game.domain.use_case.map.generation_types.CavesLimitConnectivity(level, interpretedTags).generate()

//        GenerateItems(level, map, interpretedTags.itemList, interpretedTags.generationParams)()

        return map
    }
}