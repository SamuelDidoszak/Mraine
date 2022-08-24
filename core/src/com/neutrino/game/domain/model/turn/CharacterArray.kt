package com.neutrino.game.domain.model.turn
import com.neutrino.game.domain.model.characters.Character
import kotlin.math.abs

class CharacterArray() : ArrayList<Character>() {
    constructor(character: Character): this() {
        this.add(character)
    }
    private fun Double.equalsDelta(other: Double) = abs(this - other) < 0.001
    private fun Double.lessThanDelta(other: Double) = (this - other) < -0.001

    override fun add(element: Character): Boolean {
        for (i in 0 until this.size) {
            if (element.turn.lessThanDelta(this[i].turn)) {
                this.add(i, element)
                return true
            }
        }
        this.add(this.size, element)
        return true
    }

    fun move(character: Character): Boolean {
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
     * Adds a number of turns to the first character
     */
    fun addTurns(turns: Double): Boolean {
        // array always has an element at 0
        this.elementAt(0).turn += turns
        return this.move(0)
    }

    /**
     * Get element by turn
     * @return null if it's not character's turn
     */
    fun get(turn: Double): Character? {
        return if (this.elementAt(0).turn.equalsDelta(turn))
            this.elementAt(0)
        else
            null
    }

    /**
     * Get element by position
     * @return null if not found
     */
    fun get(x: Int, y: Int): Character? {
        return this.find { character -> character.xPos == x && character.yPos == y }
    }

}