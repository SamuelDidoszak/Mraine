package com.neutrino.game.domain.use_case.map

import com.neutrino.game.Constants
import com.neutrino.game.domain.model.entities.utility.Container
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.domain.model.entities.utility.ItemEntity
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.items.Gold
import com.neutrino.game.domain.use_case.map.utility.GenerationParams
import kotlin.math.roundToInt
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class GenerateItems(
    private val map: List<List<MutableList<Entity>>>,
    private val itemList: List<Pair<KClass<Item>, Float>>,
    private val generationParams: GenerationParams
) {
    /**
     * Generates items
     */

    private val itemPool = ArrayList<Item>()
    private val blockedTilesPercentage: Float = getBlockedTilesPercentage()
    private val containerList = getContainerList()

    operator fun invoke() {
        this.itemList.sortedWith(compareBy {it.second})
        val itemList = ArrayList<Pair<KClass<Item>, Float>>()
        var sum = 0f
        for (item in this.itemList) {
            sum += item.second
            itemList.add(Pair(item.first, sum))
        }

        var valuePool = generationParams.getTotalItemValue()
        while (valuePool > 0) {
            var random = Constants.RandomGenerator.nextFloat() * sum / generationParams.itemRarityMultiplier
            if (random >= sum)
                random = sum - 0.000001f
            for (item in itemList) {
                if (random < item.second) {
                    val generatedItem = item.first.createInstance()
                    generatedItem.randomize(generationParams.itemQuality, generationParams.difficulty)
                    itemPool.add(generatedItem)
                    valuePool -= generatedItem.realValue
                    break
                }
            }
        }

        // variables for lookup
        var totalValue = 0
        var totalGold = 0

        for (item in itemPool) {
            addItem(item)
            if (item is Gold)
                totalGold += item.realValue
            else
                println("${item.name} value: ${item.realValue}")
            totalValue += item.realValue
        }
        println("Total value: $totalValue")
        println("Total gold: $totalGold")



//        addItems(Gold::class as KClass<Item>, 50f)
//        addItems(Knife::class as KClass<Item>, 5f)
//        addItems(SmallHealingPotion::class as KClass<Item>, 5f, blockedTilesPercentage)
//        addItems(ScrollOfDefence::class as KClass<Item>, 0.3f)
    }

    /**
     * Returns the position of containers on the map as a Triple list, where
     * first -> y
     * second -> x
     * third -> z
     */
    private fun getContainerList(): List<Triple<Int, Int, Int>> {
        val containerList = ArrayList<Triple<Int, Int, Int>>()
        for (y in 0 until map.size) {
            for (x in 0 until map[y].size) {
                for (z in 0 until map[y][x].size) {
                    if (map[y][x][z] is Container) {
                        containerList.add(Triple(y, x, z))
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
                    if (!mapEntity.allowOnTop) {
                        blockedAmount++
                        break
                    }
                }
            }
        }
        return blockedAmount / (map.size * map[0].size).toFloat()
    }

    /** Returns the amount of certain items spawned */
    private fun checkSpawnedItemAmount(item: KClass<Item>): Int {
        var amount = 0
        for (y in 0 until map.size) {
            for (x in 0 until map[y].size) {
                for (mapEntity in map[y][x]) {
                    if (mapEntity is ItemEntity && mapEntity.item::class == item) {
                        amount++
                        break
                    }
                }
            }
        }
        return amount
    }

    private fun addItem(item: Item) {
        while (true) {
            val x = (Constants.RandomGenerator.nextFloat() * (map[0].size - 1)).roundToInt()
            val y = (Constants.RandomGenerator.nextFloat() * (map.size - 1)).roundToInt()
            if (map[y][x].isNotEmpty() && map[y][x][map[y][x].size - 1].allowOnTop) {
                map[y][x].add(ItemEntity(item))
                break
            }
        }
    }

    /**
     * Adds items to the map with a certain probability
     * @param amount how many items per 100x100 chunk
     * @param includeBlocked if the value is set, percentage increases to account for blocked tiles. Pass value calculated from getBlockedTilesPercentage()
     * TODO add a richness value to some tiles and generate items based on that
     */
    private fun addItems(item: KClass<Item>, amount: Float, includeBlocked: Float? = null) {
        var probability = amount * 0.0001
        if (includeBlocked != null)
            probability += probability * includeBlocked

        for (y in 0 until map.size) {
            for (x in 0 until map[y].size) {
                var allowGeneration = true
                if (map[y][x].isNotEmpty() && !map[y][x][map[y][x].size - 1].allowOnTop)
                    allowGeneration = false

                if(allowGeneration && Constants.RandomGenerator.nextFloat() < probability)
                    map[y][x].add(ItemEntity(item.createInstance()) as Entity)
            }
        }
    }

    private fun addItemsToContainers(item: KClass<Item>, amount: Float, includeBlocked: Float? = null) {
        var probability = amount * 0.0001
        if (includeBlocked != null)
            probability += probability * includeBlocked

        for (y in 0 until map.size) {
            for (x in 0 until map[y].size) {
                var allowGeneration = true
                if (map[y][x].isNotEmpty() && !map[y][x][map[y][x].size - 1].allowOnTop)
                    allowGeneration = false

                if(allowGeneration && Constants.RandomGenerator.nextFloat() < probability)
                    map[y][x].add(ItemEntity(item.createInstance()) as Entity)
            }
        }
    }
}