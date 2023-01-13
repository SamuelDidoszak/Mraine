package com.neutrino.game.domain.model.systems.event

import kotlin.reflect.KClass
import kotlin.reflect.cast

interface DataMap {
    /** Map of all data necessary to the implementing class */
    val data: MutableMap<String, Data<*>>

    fun <T> set(name: String, value: T) {
        try {
            (data[name] as Data<T>).setData(value)
        } catch (e: TypeCastException) {
            e.printStackTrace()
        }
    }

    fun <T : Any> get(name: String, type: KClass<T>): T? {
        if (data[name]?.data == null)
            return null
        try {
            return type.cast(data[name]?.data)
        } catch (e: TypeCastException) {
            e.printStackTrace()
        }
        return null
    }

    fun has(name: String): Boolean {
        return data.containsKey(name)
    }

    fun isDataSet(): Boolean {
        for (data in data) {
            if (data.value.data == null) {
                try {
                    throw Exception("Data \"${data.key}\" is not set")
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }
            }
        }
        return true
    }
}