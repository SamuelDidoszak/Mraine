package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.Items
import com.neutrino.game.entities.characters.attributes.util.LootElement
import com.neutrino.game.entities.systems.attack.callables.EntityDiedCallable
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.map.chunk.ChunkManager
import kotlin.random.Random

class Loot(
    private val possibleLoot: List<LootElement>
): Attribute() {
    constructor(possibleLoot: LootElement): this(listOf(possibleLoot))
    constructor(vararg possibleLoot: Pair<String, Float>): this(possibleLoot.map { LootElement(it.first, it.second) })

    private val loot = ArrayList<Entity>()

    fun generateLoot(rng: Random) {
        possibleLoot.forEach {
            if (rng.nextDouble() < it.probability)
                loot.add(Items.new(it.item).apply { it.afterGeneration.invoke(this, rng) })
        }
    }

    override fun onEntityAttached() {
        entity.attach(object : EntityDiedCallable() {
            override fun call(entity: Entity, vararg data: Any?) {
                ChunkManager.addEntityAt(
                    entity.get(Position::class)!!,
                    loot
                )
            }
        })
    }
}