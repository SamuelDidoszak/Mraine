package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.map.attributes.Turn
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.gameplay.turn.Action
import com.neutrino.game.map.chunk.ChunkManager
import com.neutrino.game.util.Constants
import com.neutrino.game.util.x
import com.neutrino.game.util.y
import com.neutrino.game.utility.Change

open class Ai(var viewDistance: Int = 10): Attribute() {

    var targetCoords: Position? = null

    /**
     * List of coordinates in view
     */
    val fov: Array<BooleanArray> = Array(Constants.ChunkSize) { BooleanArray(Constants.ChunkSize) {false} }

    /**
     * FIFO list with planned moves
     */
    var moveList: ArrayDeque<Position> = ArrayDeque()

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
                is Action.INTERACTION -> thisAction.interaction.turnCost
                is Action.WAIT ->  {
                    if (entity.get(DefensiveStats::class)!!.movementSpeed < 1.0)
                        entity.get(DefensiveStats::class)!!.movementSpeed
                    else
                        1.0
                }
                is Action.WAITSKILL -> 1.0
                is Action.NOTHING -> 0.0
                is Action.ITEM -> 1.0
                is Action.EVENT -> 0.0
            }
        return thisAction
    }

    open fun decide() {}

    fun target(position: Position) {
        if (canAttack(position)) {
            action = Action.ATTACK(position.x, position.y)
            return
        }
        moveTo(position)
    }

    fun moveTo(position: Position) {
        setMoveList(position)
        val position = getMove()
        if (position.x == entity.x && position.y == entity.y)
            action = Action.WAIT
        else
            action = Action.MOVE(position)
    }

    /**
     * Returns the first coord from move FIFO list and deletes it
     */
    fun getMove(): Position {
        if(moveList.isEmpty())
            moveList.add(entity.get(Position::class)!!.clone())
        return moveList.removeFirst()
    }

    /**
     * Finds the path to target if it isn't already set
     */
    @Change
    fun setMoveList(position: Position, forceUpdate: Boolean = false) {
        if (position == moveList.lastOrNull() && !forceUpdate) {
            return
        }
        moveList = ArrayDeque()
        moveList.addAll(ChunkManager.characterMethods.dijkstra
            .getPath(entity, position))
    }

    @Change
    fun canAttack(position: Position): Boolean {
        return entity.get(OffensiveStats::class)!!.isInRange(
            entity.get(Position::class)!!,
            position)
    }
}