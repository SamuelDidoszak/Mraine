package com.neutrino.game.domain.model.map

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.use_case.map.utility.EntityParams
import com.neutrino.game.domain.use_case.map.utility.GenerationParams
import kotlin.reflect.KClass

class TagInterpretation(val tagList: List<MapTags>) {
    lateinit var entityParams: EntityParams
    lateinit var  characterList: List<KClass<out Character>>
    lateinit var  itemList: List<Pair<KClass<out Item>, Float>>
    val generationParams: GenerationParams = tagList[0].generationParams

    init {
        if (tagList.size == 1) {
            entityParams = tagList[0].entityParams
            characterList = tagList[0].characterList
            itemList = tagList[0].itemList
        } else {
            val characterList: ArrayList<KClass<out Character>> = ArrayList()
            val itemList: ArrayList<Pair<KClass<out Item>, Float>> = ArrayList()
            for (tag in tagList) {
                // Add characters
                for (character in tag.characterList) {
                    var canAdd = true
                    for (addedCharacter in characterList) {
                        if (character == addedCharacter) {
                            canAdd = true
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
                        if (item.first == addedItem.first) {
                            canAdd = true
                            break
                        }
                    }
                    if (canAdd)
                        itemList.add(item)
                }
                // Generate params
                if (tag.isModifier)
                    generationParams.mergeParamModifiers(tag.generationParams)
                else
                    generationParams.mergeParams(tag.generationParams)
            }
            this.characterList = characterList
            this.itemList = itemList
        }
    }
}