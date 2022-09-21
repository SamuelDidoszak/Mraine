package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.domain.model.characters.Character
import squidpony.squidai.DijkstraMap
import squidpony.squidmath.Coord

class Ai (private val character: Character) {
    var xTarget: Int = -1
    var yTarget: Int = -1

    /**
     * FIFO list with planned moves
     */
    var moveList: ArrayDeque<Coord> = ArrayDeque()

    var action: Action = Action.NOTHING

    /**
     * Return action and set it to NOTHING
     * Adds turns to the character
     */
    fun useAction(): Action {
        val thisAction = action
        action = Action.NOTHING
        character.turn +=
            when (thisAction) {
                is Action.MOVE -> character.movementSpeed
                is Action.ATTACK -> character.attackSpeed
                is Action.SKILL -> 1.0
                is Action.PICKUP -> 1.0
                is Action.WAIT -> character.movementSpeed
                is Action.NOTHING -> 0.0
                is Action.ITEM -> 1.0
            }
        return thisAction
    }

    fun decide(xPos: Int, yPos: Int, dijkstraMap: DijkstraMap, impassable: Collection<Coord>) {
        if (canAttack(xPos, yPos)) {
            action = Action.ATTACK(xPos, yPos)
            return
        }
        setMoveList(xPos, yPos, dijkstraMap, impassable)
        dijkstraMap.clearGoals()
        val coord = getMove()
        if (coord.getX() == character.xPos && coord.getY() == character.yPos) {
            action = Action.WAIT
        }
        else
            action = Action.MOVE(coord.getX(), coord.getY())
    }

    /**
     * Returns the first coord from move FIFO list and deletes it
     */
    fun getMove(): Coord {
        if(moveList.isEmpty())
            moveList.add(Coord.get(character.xPos, character.yPos))
        return moveList.removeFirst()
    }

    /**
     * Finds the path to target if it isn't already set
     */
    fun setMoveList(xPos: Int, yPos: Int, dijkstraMap: DijkstraMap, impassable: Collection<Coord>, forceUpdate: Boolean = false) {
        if (xPos == xTarget && yPos == yTarget && !forceUpdate)
            return
        moveList = ArrayDeque()
        val map = dijkstraMap.findPath(30, 30,  impassable, null, Coord.get(character.xPos, character.yPos), Coord.get(xPos, yPos))
        moveList.addAll(map)
        xTarget = xPos
        yTarget = yPos
        dijkstraMap.reset()
    }

    fun canAttack(xTarget: Int, yTarget: Int): Boolean {
        when (character.rangeType) {
            RangeType.DIAGONAL -> {
                return (yTarget == character.yPos &&
                    xTarget in character.xPos - character.range .. character.xPos + character.range) ||
                    (xTarget == character.xPos &&
                            yTarget in character.yPos - character.range .. character.yPos + character.range)
            }
            RangeType.SQUARE -> {
                return (xTarget in character.xPos - character.range .. character.xPos + character.range) &&
                        (yTarget in character.yPos - character.range .. character.yPos + character.range)
            }
            RangeType.DIAMOND -> {
                TODO("implement DIAMOND RANGE TYPE")

            }
        }
    }
}