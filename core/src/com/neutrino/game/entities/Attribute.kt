package com.neutrino.game.entities

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

//    fun <T: attributes.Attribute> getThisAttribute(attributeClass: KClass<T>): T {
//        return Entities[entity.id]!!.getAttribute(this::class)!! as T
//    }
}