package com.neutrino.game.map.generation

import com.neutrino.game.domain.model.entities.utility.Container
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.Items
import com.neutrino.game.entities.items.Item
import com.neutrino.game.entities.items.attributes.GoldValue
import com.neutrino.game.entities.items.attributes.ItemTier
import com.neutrino.game.entities.map.attributes.MapParams
import com.neutrino.game.entities.shared.attributes.Randomization
import com.neutrino.game.map.chunk.Chunk
import com.neutrino.game.map.chunk.EntityList
import com.neutrino.game.map.generation.util.GenerationParams
import com.neutrino.game.util.Constants
import com.neutrino.game.util.EntityName
import com.neutrino.game.util.id
import com.neutrino.game.utility.Probability
import kotlin.math.roundToInt

class ItemGenerator(
    private val chunk: Chunk,
    private val generationParams: GenerationParams
) {
    /**
     * Generates items
     */
    private val map: List<List<EntityList>>
        get() = chunk.map

    private val itemPool = List(Constants.maxItemTier){ArrayList<Entity>()}
    private val blockedTilesPercentage: Float = getBlockedTilesPercentage()
    private val containerList = getContainerList()

    fun generate() {
        val itemList = ArrayList<Probability<EntityName>>()
        var sum = 0f
        for (item in generationParams.interpretedTags.itemList.sortedWith(compareBy {it.probability})) {
            sum += item.probability
            itemList.add(Probability(item.value, sum))
        }

        // Generate items and add them to the pool
        var valuePool = generationParams.params.getTotalItemValue()
        while (valuePool > 0) {
            var random = chunk.randomGenerator.nextFloat() * sum / generationParams.params.itemRarityMultiplier
            if (random >= sum)
                random = sum - 0.000001f
            for (item in itemList) {
                if (random < item.probability) {
                    val generatedItem = Items.new(item.value)
                    generatedItem.get(Randomization::class)?.randomize(
                        chunk.randomGenerator,
                        generationParams.params.itemQuality,
                        generationParams.params.difficulty)
                    itemPool[generatedItem.get(ItemTier::class)?.tier ?: 1].add(generatedItem)
                    valuePool -= generatedItem.get(GoldValue::class)!!.value
                    break
                }
            }
        }

//        printGeneratedItems()

        // Add items to containers

        // Current approach is problematic for two reasons:
        // First, it fills containers from the upper left corner meaning, that they have more probability to be filled
        // Second, each container will be filled max once per item tier
        // There can be added capacity
        for (i in 0 until Constants.maxItemTier) {
            if (itemPool[i].isEmpty())
                continue
            for (container in containerList[i]) {
                val generationProbability = chunk.randomGenerator.nextFloat()
                if (generationProbability < container.itemTiers.find { it.first == i }!!.second) {
                    val index = chunk.randomGenerator.nextInt(itemPool[i].size)
                    // TODO ECS ITEMS Containers
//                    container.itemList.add(itemPool[i][index])
                    itemPool[i].removeAt(index)
                    if (itemPool[i].isEmpty())
                        break
                }
            }
        }

        // Add remaining items onto the floor
        if (generationParams.params.canGenerateOnTheFloor) {
            for (tier in itemPool) {
                for (item in tier) {
                    item.addAttribute(MapParams(true, true))
                    addItem(item)
                }
            }
        }
    }

    private fun printGeneratedItems() {
        var totalValue = 0
        var totalGold = 0
        for (tier in itemPool) {
            for (item in tier) {
                if (item.id == Items.getId("Gold"))
                    totalGold += item.get(GoldValue::class)!!.value
                else
                    println("${item.name} value: ${item.get(GoldValue::class)!!.value}")
                totalValue += item.get(GoldValue::class)!!.value
            }
        }
        println("Total value: $totalValue")
        println("Total gold: $totalGold")

    }

    /**
     * Returns the list of containers divided into lists having a particular item tier
     */
    private fun getContainerList(): List<List<Container>> {
        val containerList = List(Constants.maxItemTier) {ArrayList<Container>()}
        for (y in 0 until map.size) {
            for (x in 0 until map[y].size) {
                for (entity in map[y][x]) {
                    if (entity is Container) {
                        for (tier in entity.itemTiers)
                            containerList[tier.first].add(entity)
                    }
                }
            }
        }
        return containerList
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

    /** Returns the amount of certain items spawned */
    private fun checkSpawnedItemAmount(item: EntityName): Int {
        var amount = 0
        for (y in map.indices) {
            for (x in map[y].indices) {
                for (mapEntity in map[y][x]) {
                    if (mapEntity is Item && mapEntity.id == item.id()) {
                        amount++
                        break
                    }
                }
            }
        }
        return amount
    }

    private fun addItem(item: Entity) {
        var tries = 0
        while (true) {
            val x = (chunk.randomGenerator.nextFloat() * (map[0].size - 1)).roundToInt()
            val y = (chunk.randomGenerator.nextFloat() * (map.size - 1)).roundToInt()
            if (map[y][x].isNotEmpty() && map[y][x][map[y][x].size - 1].allowOnTop()) {
                map[y][x].add(item)
                break
            }
            if (tries++ == 50) {
                try {
                    throw Exception("Couldn't find more positions")
                } catch (e: Exception) {
                    e.printStackTrace()
                    break
                }
            }
        }
    }

    /**
     * Adds items to the map with a certain probability
     * @param amount how many items per 100x100 chunk
     * @param includeBlocked if the value is set, percentage increases to account for blocked tiles. Pass value calculated from getBlockedTilesPercentage()
     * TODO add a richness value to some tiles and generate items based on that
     */
    private fun addItems(item: EntityName, amount: Float, includeBlocked: Float? = null) {
        var probability = amount * 0.0001
        if (includeBlocked != null)
            probability += probability * includeBlocked

        for (y in 0 until map.size) {
            for (x in 0 until map[y].size) {
                var allowGeneration = true
                if (map[y][x].isNotEmpty() && !map[y][x][map[y][x].size - 1].allowOnTop())
                    allowGeneration = false

                if(allowGeneration && chunk.randomGenerator.nextFloat() < probability)
                    map[y][x].add(Items.new(item))
            }
        }
    }

    private fun addItemsToContainers(item: EntityName, amount: Float, includeBlocked: Float? = null) {
        var probability = amount * 0.0001
        if (includeBlocked != null)
            probability += probability * includeBlocked

        for (y in 0 until map.size) {
            for (x in 0 until map[y].size) {
                var allowGeneration = true
                if (map[y][x].isNotEmpty() && !map[y][x][map[y][x].size - 1].allowOnTop())
                    allowGeneration = false

                if(allowGeneration && chunk.randomGenerator.nextFloat() < probability)
                    map[y][x].add(Items.new(item))
            }
        }
    }

    private fun Entity.allowOnTop(): Boolean = this.get(MapParams::class)?.allowOnTop == true
}