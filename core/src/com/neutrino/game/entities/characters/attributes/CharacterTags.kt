package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.domain.model.systems.CharacterTag
import com.neutrino.game.entities.Attribute
import kotlin.reflect.KClass
import kotlin.reflect.cast

class CharacterTags: Attribute() {
    private val tags: HashMap<KClass<out CharacterTag>, CharacterTag> = HashMap()

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