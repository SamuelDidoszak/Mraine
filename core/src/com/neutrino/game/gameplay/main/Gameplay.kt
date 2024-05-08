package com.neutrino.game.gameplay.main

import com.neutrino.GameStage
import com.neutrino.HudStage
import com.neutrino.game.UI.UiStage
import com.neutrino.game.gameplay.turn.Action
import com.neutrino.game.gameplay.turn.Turn
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.characters.attributes.ActionBlock
import com.neutrino.game.entities.characters.attributes.Ai
import com.neutrino.game.entities.characters.attributes.DefensiveStats
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.Interaction
import com.neutrino.game.map.chunk.ChunkManager
import com.neutrino.game.util.x
import com.neutrino.game.util.y
import com.neutrino.game.utility.Highlighting
import kotlin.math.abs

class Gameplay(
    val gameStage: GameStage,
    val hudStage: HudStage,
    val uiStage: UiStage
) {

    private val gameplaySkills = GameplaySkills(this, gameStage, hudStage, uiStage)
    private val gameplayItems = GameplayItems(this, gameStage, hudStage, uiStage)
    internal var waitForAdditionalClick: Boolean = false

    fun gameLoop() {
        if ((Player.has(ActionBlock::class) || gameStage.focusPlayer) && !gameStage.lookingAround) {
            gameStage.gameCamera.moveCameraToEntity(Player)
            gameStage.focusPlayer = !gameStage.gameCamera.isPlayerFocused()
        }

        // decide on the player action. They are executed in the Turn.makeTurn method along with ai actions
        if (Turn.playerAction) {
            gameStage.waitForPlayerInput = true

            gameplayItems.useItems()

            // use skill
            val usedSkill = hudStage.usedSkill ?: uiStage.usedSkill
            if (usedSkill != null) {
                val used = gameplaySkills.useSkill(usedSkill)
                if (!used)
                    return
            }

            // interact with an entity
             if (Player.getSuper(Ai::class)!!.action is Action.NOTHING
                 && Player.hasNot(ActionBlock::class)
                 && Player.getSuper(Ai::class)!!.targetCoords != null) {
                 entityInteraction()
             }

            // WASD movement
             if (Player.getSuper(Ai::class)!!.action is Action.NOTHING
                 && gameStage.moveDirection != null
                 && Player.hasNot(ActionBlock::class)) {
                 wasdMovement()
             }

            // move the Player if a tile was clicked previously, or stop if user clicked during the movement
            // Add the move action if the movement animation has ended
             if (Player.getSuper(Ai::class)!!.moveList.isNotEmpty()
                 && Player.hasNot(ActionBlock::class)
                 && gameStage.clickedCoordinates == null
                 && Player.getSuper(Ai::class)!!.action is Action.NOTHING) {
                 moveOrStop()
             }

            // Set the player action if there was no previous one
            if (Player.getSuper(Ai::class)!!.action is Action.NOTHING) {
                // calls this method until a tile is clicked
                if (gameStage.clickedCoordinates == null) return
                // player clicked during movement
                if (Player.getSuper(Ai::class)!!.moveList.isNotEmpty() || Player.has(ActionBlock::class)) {
                    Player.getSuper(Ai::class)!!.moveList = ArrayDeque()
                    Player.getSuper(Ai::class)!!.targetCoords = null
                    gameStage.clickedCoordinates = null
                    return
                }

                // Parses everything that player could have done
                parseAction()
            }

            // reset stage to wait for input
            gameStage.waitForPlayerInput = false
            gameStage.clickedCoordinates = null
            Turn.playerAction = false
        }
        while (!Turn.playerAction)
            Turn.makeTurn()
    }

    private fun entityInteraction() {
        val entityCoords = Player.getSuper(Ai::class)!!.targetCoords!!
        val entity = Turn.currentChunk.getEntityWithAction(entityCoords.first, entityCoords.second)?.get(Interaction::class)
        // Entity has disappeared in the meantime
        if (entity == null)
            Player.getSuper(Ai::class)!!.targetCoords = null
        else {
            val action = entity.getPrimaryInteraction()
            if (action != null) {
                // check the distance and act if close enough
                if ((entityCoords.first in Player.x - action.requiredDistance .. Player.x + action.requiredDistance) &&
                    (entityCoords.second in Player.y - action.requiredDistance .. Player.y + action.requiredDistance)) {
                    Player.getSuper(Ai::class)!!.action = Action.INTERACTION(entity.entity, action)
                    // Stop moving
                    Player.getSuper(Ai::class)!!.moveList = ArrayDeque()
                }
            }
        }
    }

    private fun wasdMovement() {
        val yChange = when (gameStage.moveDirection) {
            7, 8, 9 -> -1
            1, 2, 3 -> 1
            else -> 0
        }
        val xChange = when (gameStage.moveDirection) {
            1, 4, 7 -> -1
            3, 6, 9 -> 1
            else -> 0
        }

        val wasdCoord = Position(Player.x + xChange, Player.y + yChange, Player.get(Position::class)!!.chunk)
        if (!ChunkManager.allowsCharacter(wasdCoord) || ChunkManager.getCharacterAt(wasdCoord) != null)
            return

        Player.getSuper(Ai::class)!!.moveTo(wasdCoord.x, wasdCoord.y)
        gameStage.lookingAround = false
    }

    private fun moveOrStop() {
        if (Turn.updateBatch.firstOrNull() is Action.MOVE) // Some character has moved in the meantime, so the movement map should be updated
            Player.getSuper(Ai::class)!!.setMoveList(
                Player.getSuper(Ai::class)!!.moveList.last().x, Player.getSuper(Ai::class)!!.moveList.last().y, true)
        val tile = Player.getSuper(Ai::class)!!.getMove()
        Player.getSuper(Ai::class)!!.action = Action.MOVE(tile.x, tile.y)
        if (!gameStage.lookingAround)
            gameStage.focusPlayer = true
    }

    /** Parses every action that Player could have made */
    private fun parseAction() {
        // get coordinates
        val x = gameStage.clickedCoordinates!!.x
        val y = gameStage.clickedCoordinates!!.y

        val attackableEntity = Turn.characterArray.get(x, y) ?:
            ChunkManager.getEntitiesAt(Position(x, y, Turn.currentChunk)).firstOrNull { it has DefensiveStats::class }

        if(attackableEntity == Player) {
            gameStage.focusPlayer = true
            gameStage.lookingAround = false
            if (Turn.currentChunk.getTopItem(x, y) != null)
                Player.getSuper(Ai::class)!!.action = Action.NOTHING
            else {
                // TODO add defend action
                Player.getSuper(Ai::class)!!.action = Action.WAIT
            }
        }
        // Attack the enemy
        else if (attackableEntity != null && Player.getSuper(Ai::class)!!.canAttack(x, y))
            Player.getSuper(Ai::class)!!.action = Action.ATTACK(x, y)

        // Calculate move list
        if (Player.getSuper(Ai::class)!!.action is Action.NOTHING) {
            // Add the interactable entity as the target
            if (Turn.currentChunk.getEntityWithAction(x, y) != null)
                Player.getSuper(Ai::class)!!.targetCoords = Pair(x, y)
            else
                Player.getSuper(Ai::class)!!.targetCoords = null

            // Add player movement list
            if (!Turn.currentChunk.discoveredMap[y][x] || !ChunkManager.allowsCharacterChangesImpassable(Position(x, y, ChunkManager.middleChunk)))
                Player.getSuper(Ai::class)!!.action = Action.NOTHING
            else
                Player.getSuper(Ai::class)!!.setMoveList(x, y)

            // Focus player either if he's off screen or if he clicked near his current position
            if (!gameStage.gameCamera.isInCamera(Player.x, Player.y) ||
                abs(Player.x - x) <= 5 &&  abs(Player.y - y) <= 5) {
                gameStage.lookingAround = false
                gameStage.focusPlayer = true
            }
        }
    }

    internal fun cancelUsage() {
        gameStage.highlightRange = null
        gameStage.highlightMode = Highlighting.Companion.HighlightModes.NORMAL
        waitForAdditionalClick = false
        gameStage.highlighting.deHighlight()
        gameStage.clickedCoordinates = null
        uiStage.usedSkill = null
        hudStage.usedSkill = null
        uiStage.useItemOn = null
        hudStage.useItemOn = null

        gameStage.skillRange = null
    }
}