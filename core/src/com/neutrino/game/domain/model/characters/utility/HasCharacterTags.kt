package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.domain.model.systems.CharacterTag
import kotlin.reflect.KClass
import kotlin.reflect.cast

interface HasCharacterTags {
    val tags: HashMap<KClass<out CharacterTag>, CharacterTag>

    fun addTag(tag: CharacterTag) {
        tags.put(tag::class, tag)
    }

    fun <K: CharacterTag> getTag(tag: KClass<K>): K? {
        if (tags[tag] == null)
            return null

        return tag.cast(tags[tag])
    }

    fun <K: CharacterTag> removeTag(tag: KClass<K>) {
        tags.remove(tag)
    }
}