package com.neutrino.game.entities

import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

open class Entity() {
    constructor(attributes: List<Attribute>): this() {
        for (attribute in attributes)
            addAttribute(attribute)
    }

    private var idSet = false
    protected var nameSet = false

    var id: Int = 0
        set(value) { if (!idSet) {
            idSet = true
            field = value
        }}

    open var name: String = ""
        get() {return if (nameSet) field else Entities.getName(id)}
        set(value) {
            nameSet = true
            field = value
        }


    private val attributes: HashMap<KClass<out Attribute>, Attribute> = HashMap()

    private var callables: ArrayList<Callable>? = null


    infix fun addAttribute(attribute: Attribute): Entity {
        attributes.put(attribute::class, attribute)
        attribute.entity = this
        attribute.onEntityAttached()
        return this
    }

    infix fun <T: Attribute> get(attributeClass: KClass<T>): T? {
        return attributes[attributeClass] as? T?
    }

    infix fun <T: Attribute> getSuper(attributeClass: KClass<T>): T? {
        if (attributes[attributeClass] != null)
            return attributes[attributeClass] as? T?

        attributes.keys.forEach { attribute ->
            attribute.superclasses.forEach {
                if (it == attributeClass)
                    return attributes[attribute] as? T?
            }
        }

        return null
    }

    infix fun has(attributeClass: KClass<out Attribute>): Boolean {
        return attributes[attributeClass] != null
    }

    infix fun hasNot(attributeClass: KClass<out Attribute>): Boolean {
        return attributes[attributeClass] == null
    }


    infix fun call(callableClass: KClass<out Callable>) {
        if (callables == null)
            return
        for (callable in callables!!) {
            if (callableClass.java.isAssignableFrom(callable::class.java))
                callable.call(this)
        }
    }

    fun call(callableClass: KClass<out Callable>, vararg data: Any?) {
        if (callables == null)
            return
        for (callable in callables!!) {
            if (callableClass.java.isAssignableFrom(callable::class.java))
                callable.call(this, *data)
        }
    }

    infix fun attach(callable: Callable): Entity {
        if (callables == null)
            callables = ArrayList()
        callables!!.add(callable)
        return this
    }

    infix fun detach(callable: Callable): Entity {
        callables!!.remove(callable)
        return this
    }
}