package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.util.InventoryElement
import com.neutrino.game.entities.items.attributes.Amount
import com.neutrino.game.entities.items.attributes.GoldValue
import com.neutrino.game.util.lessThanDelta

class Inventory(
    var maxSize: Int = 30,
    items: List<Entity>? = null
): Attribute() {
    private val items: ArrayList<InventoryElement> = ArrayList()
    val size: Int
        get() = items.size
    init {
        if (items != null) {
            for (item in items) {
                this.items.add(InventoryElement(item, Turn.turn, this.items.size))
            }
        }
    }

    fun printAll() {
        items.forEach { println("${it.item.id}: ${it.item.name}, ${it.item.get(Amount::class)!!.amount}") }
    }

    var sortType: SortType = SortType.SORT_DATE
    var isSortAscending: Boolean = false

    fun get(i: Int): Entity {
        return items[i].item
    }

    fun getElement(entity: Entity): InventoryElement? {
        return items.find { it.item == entity }
    }

    fun get(element: InventoryElement): InventoryElement? {
        return items.find { it == element }
    }

    fun remove(element: InventoryElement): Boolean {
        return items.removeAll { it == element }
    }

    fun getItem(id: Int): Entity? {
        return items.find { it.item.id == id }?.item
    }

    fun getItem(item: Entity): Entity? {
        return items.find { it.item == item }?.item
    }

    fun removeItem(id: Int): Boolean {
        return items.removeAll { it.item.id == id }
    }

    fun removeItem(item: Entity): Boolean {
        return items.removeAll { it.item == item }
    }

    fun add(inventoryElement: InventoryElement): Boolean {
        if (items.size == maxSize)
            return false
        items.add(inventoryElement)
        return true
    }

    fun add(item: Entity): Boolean {
        if (addToStack(item))
            return true
        if (items.size == maxSize)
            return false

        var customItemPosition = if (items.isEmpty()) 0 else items.last().customPosition + 1
        if (customItemPosition == maxSize) {
            for (i in (0 .. items.size - 2).reversed()) {
                customItemPosition = items[i].customPosition + 1
                if (items[i + 1].customPosition != customItemPosition)
                    break
            }
        }

        val inventoryElement = InventoryElement(item, Turn.turn, customItemPosition)

        if (sortType == SortType.SORT_DATE) {
            var i = 0
            while (i < items.size && !Turn.turn.lessThanDelta(items[i].dateAdded)) {
                i++
            }
            items.add(i, inventoryElement)
        } else if (sortType == SortType.SORT_VALUE) {
            var i = 0
            val itemValue = item.get(GoldValue::class)!!.value
            while (i < items.size && items[i].item.get(GoldValue::class)!!.value > itemValue) {
                i++
            }
            items.add(i, inventoryElement)
        }
        return true
    }

    /** @return true if item was consumed. Falsed if item should be added as a separate entity */
    private fun addToStack(item: Entity): Boolean {
        val amount = item.get(Amount::class) ?: return false
        for (stackableItem in items) {
            // TODO ECS ITEMS Compare if all of entity attributes are equal
            if (stackableItem.item.id != item.id)
                continue
            val stackableItemAmount = stackableItem.item.get(Amount::class)!!
            val stackRemaining = stackableItemAmount.stackRemaining()
            if (stackRemaining > 0) {
                if (amount.amount > stackRemaining) {
                    stackableItemAmount.amount += stackRemaining
                    amount.amount -= stackRemaining
                } else {
                    stackableItemAmount.amount += amount.amount
                    return true
                }
            }
        }
//        val stackableItem = items.find { it.item.id == item.id } ?: return false
        return false
    }

    enum class SortType {
        SORT_DATE,
        SORT_VALUE,
        SORT_CUSTOM,
        SORT_TYPE
    }

}