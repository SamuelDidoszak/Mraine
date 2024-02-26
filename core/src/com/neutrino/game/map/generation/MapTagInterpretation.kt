package com.neutrino.game.map.generation

import com.neutrino.game.util.EntityName
import com.neutrino.game.utility.Probability

class MapTagInterpretation(val tagList: List<MapTag>) {
    lateinit var tilesets: ArrayList<Tileset>
    lateinit var mapGenerators: ArrayList<Generator>
    lateinit var characterList: ArrayList<EntityName>
    lateinit var itemList: ArrayList<Probability<EntityName>>
    val tagParams: TagParams = TagParams(10f)

    init {
        if (tagList.isEmpty()) {
            tilesets = ArrayList()
            mapGenerators = ArrayList()
            characterList = ArrayList()
            itemList = ArrayList()
        } else if (tagList.size == 1) {
            tilesets = ArrayList<Tileset>().apply { addAll(tagList[0].tilesets) }
            mapGenerators = ArrayList<Generator>().apply { addAll(tagList[0].mapGenerators) }
            characterList = ArrayList<EntityName>().apply { addAll(tagList[0].characterList) }
            itemList = ArrayList<Probability<EntityName>>().apply { addAll(tagList[0].itemList) }
        } else {
            val tilesets: ArrayList<Tileset> = ArrayList()
            val mapGenerators: ArrayList<Generator> = ArrayList()
            val characterList: ArrayList<EntityName> = ArrayList()
            val itemList: ArrayList<Probability<EntityName>> = ArrayList()
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
                // Add items
                for (item in tag.itemList) {
                    var canAdd = true
                    for (addedItem in itemList) {
// TODO
//                        if (item.value == addedItem.value) {
//                            canAdd = true
//                            break
//                        }
                    }
                    if (canAdd)
                        itemList.add(item)
                }
                // Generate params
                if (tag.isModifier)
                    tagParams.mergeParamModifiers(tag.tagParams)
                else
                    tagParams.mergeParams(tag.tagParams)
            }
            this.tilesets = tilesets
            this.characterList = characterList
            this.itemList = itemList
        }
    }
}
