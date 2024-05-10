package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.characters.attributes.util.CharacterTag
import kotlin.reflect.KClass
import kotlin.reflect.cast

class CharacterTags(
    initialTags: List<CharacterTag>? = null
): Attribute() {
    constructor(initialTag: CharacterTag): this(listOf(initialTag))

    private val tags: HashMap<KClass<out CharacterTag>, CharacterTag> = HashMap()

    init {
        initialTags?.forEach {
            tags[it::class] = it
        }
    }

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