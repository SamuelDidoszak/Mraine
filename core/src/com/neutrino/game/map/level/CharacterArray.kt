package com.neutrino.game.map.level

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.map.attributes.Turn
import com.neutrino.game.util.equalsDelta
import com.neutrino.game.util.lessThanDelta

class CharacterArray(): ArrayList<Entity>() {
    constructor(character: Entity): this() {
        this.add(character)
    }

    override fun add(element: Entity): Boolean {
        for (i in 0 until this.size) {
            if (element.get(Turn::class)!!.turn.lessThanDelta(this[i].get(Turn::class)!!.turn)) {
                this.add(i, element)
                return true
            }
        }
        this.add(this.size, element)
        return true
    }

    fun move(character: Entity): Boolean {
        if (this.size == 1) return true
        return if(this.remove(character))
            this.add(character)
        else
            false
    }

    fun move(index: Int): Boolean {
        if (this.size == 1) return true
        val character =
            try {
                this.elementAt(index)
            } catch (e: IndexOutOfBoundsException) {
                return false
            }
        return move(character)
    }

    /**
     * Adds a number of turns to the first character and move him in the list
     */
    fun addTurns(turns: Double): Boolean {
        // array always has an element at 0
        this.elementAt(0).get(Turn::class)!!.turn += turns
        return this.move(0)
    }

    /**
     * Get element by turn
     * @return null if it's not character's turn
     */
    fun get(turn: Double): Entity? {
        return if (this.elementAt(0).get(Turn::class)!!.turn.equalsDelta(turn))
            this.elementAt(0)
        else
            null
    }

    /**
     * Get element by position
     * @return null if not found
     */
    fun get(x: Int, y: Int): Entity? {
        return this.find { character -> character.get(Position::class)!!.x == x && character.get(Position::class)!!.y == y }
    }

}