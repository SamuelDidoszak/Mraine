package com.neutrino.game.entities.shared.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.AttributeOperations
import kotlin.reflect.KClass

class PreviousAttributes: Attribute(), AttributeOperations<PreviousAttributes> {

    val attributeList = ArrayList<Attribute>()

    fun <T: Attribute> get(attribute: KClass<T>): T? {
        return attributeList.first { it::class == attribute } as T?
    }

    fun add(attribute: Attribute) {
        attributeList.add(attribute)
    }

    fun <T: Attribute> remove(attribute: T, attrClass: KClass<T>): T? {
        for (attr in attributeList) {
            if (attr::class == attrClass && (attr as AttributeOperations<T>) isEqual attribute) {
                attributeList.remove(attr)
                removeIfEmpty()
                return attr as T
            }
        }
        return null
    }

    fun <T: Attribute> remove(attributeClass: KClass<T>): T? {
        for (attr in attributeList.asReversed()) {
            if (attr::class == attributeClass) {
                attributeList.remove(attr)
                removeIfEmpty()
                return attr as T?
            }
        }
        return null
    }

    fun removeIfEmpty() {
        if (attributeList.size == 0)
            entity.removeAttribute(PreviousAttributes::class)
    }


    override fun plus(other: PreviousAttributes): PreviousAttributes {
        val newAttrs = PreviousAttributes()
        for (attr in attributeList)
            newAttrs.add(attr)
        for (attr in other.attributeList)
            newAttrs.add(attr)
        return newAttrs
    }
    override fun minus(other: PreviousAttributes): PreviousAttributes {
        val newAttrs = PreviousAttributes()
        for (attr in attributeList)
            newAttrs.add(attr)
        for (attr in other.attributeList)
            newAttrs.remove(attr::class)
        return newAttrs
    }
    override fun shouldRemove(): Boolean = attributeList.size == 0
    override fun clone(): PreviousAttributes {
        val newAttrs = PreviousAttributes()
        for (attr in attributeList)
            newAttrs.add(attr)
        return newAttrs
    }
    override fun isEqual(other: PreviousAttributes): Boolean = attributeList == other.attributeList
}