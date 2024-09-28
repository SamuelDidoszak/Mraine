package com.neutrino.game.map.generation

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.Inventory
import com.neutrino.game.entities.map.attributes.MapParams
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.map.chunk.Chunk
import com.neutrino.game.map.chunk.ChunkManager
import com.neutrino.game.map.chunk.EntityList
import com.neutrino.game.map.generation.util.GenerationParams
import com.neutrino.game.map.generation.util.ItemAdder

class ItemGenerator(
    private val chunk: Chunk,
    private val generationParams: GenerationParams
) {
    /**
     * Generates items
     */
    private val map: List<List<EntityList>>
        get() = chunk.map

    private val blockedTilesPercentage: Float = getBlockedTilesPercentage()
    private val itemAdders = getItemAdders()

    fun generate() {
        itemAdders.forEach {
            it.generate(generationParams)
            if (it.entity has Inventory::class)
                it.entity.get(Inventory::class)!!.add(it.items)
            else
                ChunkManager.addEntityAt(it.entity.get(Position::class)!!, it.items)
        }
    }

    /**
     * Returns the list of containers divided into lists having a particular item tier
     */
    private fun getItemAdders(): List<ItemAdder> {
        val itemAdder = ArrayList<ItemAdder>()
        for (y in map.indices) {
            for (x in map[y].indices) {
                for (entity in map[y][x]) {
                    if (entity has ItemAdder::class)
                        itemAdder.add(entity.get(ItemAdder::class)!!)
                }
            }
        }

        return itemAdder
    }


    /**
     * Returns the percentage of tiles with .allowOnTop set to false
     * Useful for making sure, that a certain amount of items will appear in chunk
     */
    private fun getBlockedTilesPercentage(): Float {
        var blockedAmount = 0
        for (y in 0 until map.size) {
            for (x in 0 until map[y].size) {
                for (mapEntity in map[y][x]) {
                    if (!mapEntity.allowOnTop()) {
                        blockedAmount++
                        break
                    }
                }
            }
        }
        return blockedAmount / (map.size * map[0].size).toFloat()
    }

    private fun Entity.allowOnTop(): Boolean = this.get(MapParams::class)?.allowOnTop == true
}