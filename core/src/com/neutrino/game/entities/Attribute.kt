package com.neutrino.game.entities

import com.neutrino.game.entities.shared.attributes.PreviousAttributes

abstract class Attribute {

    private var _entity: Entity? = null
    private var entityAttached: Boolean = false

    var entity: Entity
        set(value) {
            if (entityAttached)
                return
            _entity = value
            entityAttached = true
        }
        get() = _entity!!

    open fun onEntityAttached() {}

    protected inline fun <reified T: Attribute> plusPrevious(other: T): T {
        val prevAttr = entity.get(PreviousAttributes::class) ?:
            entity.addAttribute(PreviousAttributes()).get(PreviousAttributes::class)!!
        prevAttr.add(this)
        val newInstance = (other as AttributeOperations<T>).clone()
        entity.addAttribute(newInstance)
        return this as T
    }

    protected inline fun <reified T: Attribute> minusPrevious(other: T): T {
        val prevAttr = entity.get(PreviousAttributes::class)
        if ((this as AttributeOperations<T>) isEqual other) {
            entity.removeAttribute(T::class)
            val attribute = prevAttr?.remove(T::class)
            if (attribute != null)
                entity.addAttribute(attribute)
        } else {
            val attribute = prevAttr?.remove(other, T::class)
            if (attribute != null)
                entity.addAttribute(attribute)
        }
        return this as T
    }
}