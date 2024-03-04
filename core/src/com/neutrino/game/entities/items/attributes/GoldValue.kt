package com.neutrino.game.entities.items.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.AttributeOperations

data class GoldValue(
    var value: Int
): Attribute(), AttributeOperations<GoldValue> {
    private val originalValue = value

    /**
     * Compares value to the original value
     * @return 0 if values are the same. -1 if value is smaller and 1 if the value is bigger
     */
    fun compareToOriginal(): Int {
        return value.compareTo(originalValue)
    }

    override fun plus(other: GoldValue): GoldValue = GoldValue(value + other.value)
    override fun minus(other: GoldValue): GoldValue = GoldValue(value - other.value)
    override fun isEqual(other: GoldValue): Boolean = value == other.value
    override fun clone(): GoldValue = GoldValue(value)
}