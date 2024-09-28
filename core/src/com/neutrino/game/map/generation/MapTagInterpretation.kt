package com.neutrino.game.map.generation

import com.neutrino.game.entities.Items
import com.neutrino.game.map.generation.util.ItemPool
import com.neutrino.game.util.EntityName
import com.neutrino.game.utility.Probability
import kotlin.math.ceil
import kotlin.random.Random

class MapTagInterpretation(tagList: List<MapTag>, val rng: Random) {
    lateinit var tilesets: ArrayList<Tileset>
    lateinit var mapGenerators: ArrayList<Generator>
    lateinit var characterList: ArrayList<EntityName>
    lateinit var itemPool: ItemPool
    val tagParams: TagParams = tagList.firstOrNull()?.tagParams?.copy() ?: TagParams(0f)

    init {
        if (tagList.isEmpty()) {
            tilesets = ArrayList()
            mapGenerators = ArrayList()
            characterList = ArrayList()
            itemPool = ItemPool(mutableMapOf(0 to ArrayList()))
        } else if (tagList.size == 1) {
            tilesets = ArrayList<Tileset>().apply { addAll(tagList[0].tilesets) }
            mapGenerators = ArrayList<Generator>().apply { addAll(tagList[0].mapGenerators) }
            characterList = ArrayList<EntityName>().apply { addAll(tagList[0].characterList) }
            itemPool = generateItemPool(tagList)
        } else {
            val tilesets: ArrayList<Tileset> = ArrayList()
            val mapGenerators: ArrayList<Generator> = ArrayList()
            val characterList: ArrayList<EntityName> = ArrayList()
            val itemList: ArrayList<Probability<EntityName>> = ArrayList()
            for (i in 1 until tagList.size) {
                if (tagList[i].isModifier)
                    tagParams.mergeParamModifiers(tagList[i].tagParams)
                else
                    tagParams.mergeParams(tagList[i].tagParams)
            }
            for (tag in tagList) {
                for (tileset in tag.tilesets) {
                    var canAdd = true
                    for (addedTileset in tilesets) {
                        if (tileset == addedTileset) {
                            canAdd = false
                            break
                        }
                    }
                    if (canAdd)
                        tilesets.add(tileset)
                }
                // Add generators
                for (generator in tag.mapGenerators) {
                    var canAdd = true
                    for (addedGenerator in mapGenerators) {
                        if (generator == addedGenerator) {
                            canAdd = false
                            break
                        }
                    }
                    if (canAdd)
                        mapGenerators.add(generator)
                }
                // Add characters
                for (character in tag.characterList) {
                    var canAdd = true
                    for (addedCharacter in characterList) {
                        if (character == addedCharacter) {
                            canAdd = false
                            break
                        }
                    }
                    if (canAdd)
                        characterList.add(character)
                }
            }
            this.tilesets = tilesets
            this.characterList = characterList
            itemPool = generateItemPool(tagList)
        }
    }

    private fun generateItemPool(tagList: List<MapTag>): ItemPool {
        val allItemList = ArrayList<Probability<EntityName>>()
        if (tagList.size == 1)
            allItemList.addAll(tagList[0].itemList)
        else {
            // CURRENTLY, EACH ITEM CAN BE ADDED ONLY ONCE
            // PROBABILITY WILL BE ONLY OF THE FIRST ITEM
            for (tag in tagList) {
                for (item in tag.itemList) {
                    var canAdd = true
                    for (addedItem in allItemList) {
                        if (item.value == addedItem.value) {
                            canAdd = false
                            break
                        }
                    }
                    if (canAdd)
                        allItemList.add(item)
                }
            }
        }
        val difficultyTiers = mutableMapOf(
            (tagParams.difficulty - 1).toInt() to mutableMapOf(1 to 0, 2 to 0, 3 to 0),
            (tagParams.difficulty).toInt() to mutableMapOf(1 to 0, 2 to 0, 3 to 0),
            (tagParams.difficulty + 1).toInt() to mutableMapOf(1 to 0, 2 to 0, 3 to 0),
            (tagParams.difficulty + 2).toInt() to mutableMapOf(1 to 0, 2 to 0, 3 to 0)
        )

        for (item in allItemList) {
            val tier = Items.getTier(item.value)
            if (tier.difficulty.toFloat() !in tagParams.difficulty - 1 .. tagParams.difficulty + 2)
                continue
            difficultyTiers[tier.difficulty]!![tier.tier] = difficultyTiers[tier.difficulty]!![tier.tier]!! + 1
        }

        val difficulties = mutableMapOf(
            (tagParams.difficulty - 1).toInt() to 0.1f - 0.1f * getDifficultyDecimal(),
            (tagParams.difficulty).toInt() to 0.8f - 0.8f * getDifficultyDecimal() + 0.1f * getDifficultyDecimal(),
            (tagParams.difficulty + 1).toInt() to 0.1f + 0.8f * getDifficultyDecimal() - 0.1f * getDifficultyDecimal(),
            (tagParams.difficulty + 2).toInt() to 0.1f * getDifficultyDecimal(),
        )

        for (key in difficulties.keys) {
            for (value in difficultyTiers[key]!!.keys) {
                difficultyTiers[key]!![value] = ceil(difficultyTiers[key]!![value]!! * difficulties[key]!!).toInt()
            }
        }

        val itemList: MutableMap<Int, ArrayList<Probability<EntityName>>> = mutableMapOf()

        for (item in allItemList.shuffled(rng)) {
            val itemTier = Items.getTier(item.value)
            if (difficultyTiers.containsKey(itemTier.difficulty)) {
                if (difficultyTiers[itemTier.difficulty]!![itemTier.tier]!! > 0) {
                    difficultyTiers[itemTier.difficulty]!![itemTier.tier] = difficultyTiers[itemTier.difficulty]!![itemTier.tier]!! - 1

                    if (itemList[itemTier.tier] == null)
                        itemList[itemTier.tier] = ArrayList()
                    itemList[itemTier.tier]?.add(item)
                }
            }
        }

        for (tier in itemList) {
            itemList[tier.key] = ArrayList(tier.value.sortedWith(compareBy {it.probability}))
        }

        printItems(itemList)
        return ItemPool(itemList)
    }

    private fun printItems(itemList: MutableMap<Int, ArrayList<Probability<EntityName>>>) {
        println("ITEMS")
        println("NAME\tTIER\tDIFFICULTY")
        for (key in itemList.keys) {
            println(key)
            itemList[key]?.forEach {
                val tier = Items.getTier(it.value)
                println("\t${it.value}\t${tier.tier}\t${tier.difficulty}")
            }
        }
    }

    private fun getDifficultyDecimal(): Float {
        return tagParams.difficulty - tagParams.difficulty.toInt()
    }
}
