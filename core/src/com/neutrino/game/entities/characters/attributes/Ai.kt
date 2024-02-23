package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.domain.model.turn.Action
import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.map.attributes.Turn
import com.neutrino.game.entities.shared.util.InteractionType
import com.neutrino.game.map.chunk.ChunkManager
import com.neutrino.game.util.Constants
import com.neutrino.game.util.x
import com.neutrino.game.util.y
import squidpony.squidmath.Coord

open class Ai(var viewDistance: Int = 10): Attribute() {

    var targetCoords: Pair<Int, Int>? = null

    /**
     * List of coordinates in view
     */
    val fov: Array<BooleanArray> = Array(Constants.LevelChunkSize) { BooleanArray(Constants.LevelChunkSize) {false} }

    /**
     * FIFO list with planned moves
     */
    var moveList: ArrayDeque<Coord> = ArrayDeque()

    var action: Action = Action.NOTHING

    fun updateFov() {
        ChunkManager.characterMethods.updateFov(entity)
    }

    /**
     * Return action and set it to NOTHING
     * Adds turns to the character
     */
    fun useAction(): Action {
        val thisAction = action
        action = Action.NOTHING
        entity.get(Turn::class)!!.turn +=
            when (thisAction) {
                is Action.MOVE -> entity.get(DefensiveStats::class)!!.movementSpeed
                is Action.ATTACK -> entity.get(OffensiveStats::class)!!.attackSpeed
                is Action.SKILL -> 1.0
                is Action.INTERACTION -> {
                    if (thisAction.interaction is InteractionType.DESTROY)
                        entity.get(OffensiveStats::class)!!.attackSpeed
                    else
                        thisAction.interaction.turnCost
                }
                is Action.WAIT ->  {
                    if (entity.get(DefensiveStats::class)!!.movementSpeed < 1.0)
                        entity.get(DefensiveStats::class)!!.movementSpeed
                    else
                        1.0
                }
                is Action.NOTHING -> 0.0
                is Action.ITEM -> 1.0
                is Action.EVENT -> 0.0
            }
        return thisAction
    }

    open fun decide() {}

    fun target(xPos: Int, yPos: Int) {
        if (canAttack(xPos, yPos)) {
            action = Action.ATTACK(xPos, yPos)
            return
        }
        moveTo(xPos, yPos)
    }

    fun moveTo(xPos: Int, yPos: Int) {
        setMoveList(xPos, yPos)
        val coord = getMove()
        if (coord.getX() == entity.x && coord.getY() == entity.y) {
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
            moveList.add(Coord.get(entity.get(Position::class)!!.x, entity.get(Position::class)!!.y))
        return moveList.removeFirst()
    }

    /**
     * Finds the path to target if it isn't already set
     */
    fun setMoveList(xPos: Int, yPos: Int, forceUpdate: Boolean = false) {
        if (xPos == moveList.lastOrNull()?.x && yPos == moveList.lastOrNull()?.y && !forceUpdate) {
            return
        }
        moveList = ArrayDeque()
        moveList.addAll(ChunkManager.characterMethods
            .getPath(entity, Position(xPos, yPos, entity.get(Position::class)!!.chunk)))
    }

    fun canAttack(xTarget: Int, yTarget: Int): Boolean {
        return entity.get(OffensiveStats::class)!!.isInRange(
            entity.get(Position::class)!!,
            Position(xTarget, yTarget, com.neutrino.game.domain.model.turn.Turn.currentChunk))
    }
}