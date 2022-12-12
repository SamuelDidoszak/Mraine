package com.neutrino.game.domain.model.items.utility

import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.lessThanDelta

data class Inventory(
    val itemList: MutableList<EqElement> = ArrayList(),
    val name: String? = null,
    var size: Int = 137,
    var sortType: SortType = SortType.SORT_DATE,
    var isSortAscending: Boolean = false
) {
    fun sort() {
        when (sortType) {
            SortType.SORT_DATE -> {
//                itemList.sortWith(Comparator { item, item2 -> item.dateAdded < item2.dateAdded })
            }
            else -> {}
        }
    }

    public fun get(name: String): Item? {
        return itemList.find { it.item.name == name }?.item
    }

    fun add(item: Item) {
        // add to stack
        if (item.amount != null) {
            val stackableItem = itemList.find { it.item.name == item.name }
            if (stackableItem != null) {
                stackableItem.item.amount = stackableItem.item.amount!!.plus(item.amount!!)
                return
            }
        }
        if (itemList.size == size) {
            println("$name inventory is full")
            return
        }
        // decide the item position (for custom)
        var itemPosition: Int = itemList.last().customPosition!! + 1
//        while (itemPosition < maxSize && )


//        var itemPosition: Int = itemList.last().customPosition!! + 1
        if (itemPosition == size) {
            for (i in itemList.size - 2 .. 0) {
                itemPosition = itemList[i].customPosition!! + 1
                if (itemList[i + 1].customPosition != itemPosition)
                    break
            }
        }
        // add as a new item
        val eqElement = EqElement(item, Turn.turn, itemPosition)

        // sort the item into the list
        if (sortType == SortType.SORT_DATE) {
            var i = 0
            while (i < itemList.size && !Turn.turn.lessThanDelta(itemList[i].dateAdded)) {
                i++
            }
            itemList.add(i, eqElement)
        } else if (sortType == SortType.SORT_VALUE) {
            var i = 0
//            while (i < itemList.size && item.goldValue) {
//                i++
//            }
            itemList.add(i, eqElement)
        }


    }
}


enum class SortType {
    SORT_DATE,
    SORT_VALUE,
    SORT_CUSTOM,
    SORT_TYPE
}