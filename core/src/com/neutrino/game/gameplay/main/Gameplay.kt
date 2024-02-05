package com.neutrino.game.gameplay.main

import com.neutrino.GameStage
import com.neutrino.HudStage
import com.neutrino.LevelArrays
import com.neutrino.game.UI.UiStage
import com.neutrino.game.domain.model.turn.Action
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.characters.attributes.Ai
import com.neutrino.game.entities.shared.attributes.Interaction
import com.neutrino.game.util.x
import com.neutrino.game.util.y
import com.neutrino.game.utility.Highlighting
import squidpony.squidmath.Coord
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
        // TODO Actions
        // Before there was if ((Player.hasActions() || gameStage.focusPlayer) && ...
        if ((gameStage.focusPlayer) && !gameStage.lookingAround) {
            gameStage.setCameraToPlayer()
            gameStage.focusPlayer = !gameStage.isPlayerFocused()
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
            // TODO Actions
            // Before it was:
            // if (Player.get(Ai::class)!!.action is Action.NOTHING && !Player.hasActions() && Player.get(Ai::class)!!.targetCoords != null) {
            if (Player.get(Ai::class)!!.action is Action.NOTHING && Player.get(Ai::class)!!.targetCoords != null) {
                val entityCoords = Player.get(Ai::class)!!.targetCoords!!
                val entity = Turn.currentLevel.getEntityWithAction(entityCoords.first, entityCoords.second)?.get(Interaction::class)
                // Entity has disappeared in the meantime
                if (entity == null)
                    Player.get(Ai::class)!!.targetCoords = null
                else {
                    val action = entity.getPrimaryInteraction()
                    if (action != null) {
                        // TODO ECS Attack
//                        if (action is InteractionType.DESTROY)
//                            action.requiredDistance = if ((entity as Destructable).destroyed) -1 else Player.range
                        // check the distance and act if close enough
                        if ((entityCoords.first in Player.x - action.requiredDistance .. Player.x + action.requiredDistance) &&
                            (entityCoords.second in Player.y - action.requiredDistance .. Player.y + action.requiredDistance)) {
                            Player.get(Ai::class)!!.action = Action.INTERACTION(entity.entity, action)
                            // Stop moving
                            Player.get(Ai::class)!!.moveList = ArrayDeque()
                        }
                    }
                }
            }

            // WASD movement
            // TODO Actions
            // Before it was:
            // if (Player.ai.action is Action.NOTHING && gameStage.moveDirection != null && !Player.hasActions()) {
            if (Player.get(Ai::class)!!.action is Action.NOTHING && gameStage.moveDirection != null) {
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

                val wasdCoord = Coord.get(Player.x + xChange, Player.y + yChange)
                if (!Turn.currentLevel.allowsCharacter(wasdCoord.x, wasdCoord.y) || LevelArrays.getCharacterAt(wasdCoord) != null)
                    return

                Player.get(Ai::class)!!.moveTo(wasdCoord.x, wasdCoord.y, Turn.dijkstraMap, LevelArrays.getImpassableList())
            }

            // move the Player if a tile was clicked previously, or stop if user clicked during the movement
            // Add the move action if the movement animation has ended
            // TODO Actions
            // Before it was:
            // if (Player.ai.moveList.isNotEmpty() && !Player.hasActions() && gameStage.clickedCoordinates == null && Player.ai.action is Action.NOTHING) {
            if (Player.get(Ai::class)!!.moveList.isNotEmpty() && gameStage.clickedCoordinates == null && Player.get(Ai::class)!!.action is Action.NOTHING) {
                if (Turn.updateBatch.firstOrNull() is Action.MOVE) // Some character has moved in the meantime, so the movement map should be updated
                    Player.get(Ai::class)!!.setMoveList(
                        Player.get(Ai::class)!!.moveList.last().x, Player.get(Ai::class)!!.moveList.last().y, Turn.dijkstraMap, Turn.mapImpassableList.plus(
                            Turn.characterArray.getImpassable()), true)
                val tile = Player.get(Ai::class)!!.getMove()
                Player.get(Ai::class)!!.action = Action.MOVE(tile.x, tile.y)
                if (!gameStage.lookingAround)
                    gameStage.focusPlayer = true
            }

            // Set the player action if there was no previous one
            if (Player.get(Ai::class)!!.action is Action.NOTHING) {
                // calls this method until a tile is clicked
                if (gameStage.clickedCoordinates == null) return
                // player clicked during movement
                // TODO Actions
                // Before it was:
                // if (Player.ai.moveList.isNotEmpty() || Player.hasActions()) {
                if (Player.get(Ai::class)!!.moveList.isNotEmpty()) {
                    Player.get(Ai::class)!!.moveList = ArrayDeque()
                    Player.get(Ai::class)!!.targetCoords = null
                    gameStage.clickedCoordinates = null
                    return
                }

                // get coordinates
                val x = gameStage.clickedCoordinates!!.x
                val y = gameStage.clickedCoordinates!!.y

                val clickedCharacter = Turn.characterArray.get(x, y)

                if(clickedCharacter == Player) {
                    gameStage.focusPlayer = true
                    gameStage.lookingAround = false
                    if (Turn.currentLevel.getTopItem(x, y) != null)
                        Player.get(Ai::class)!!.action = Action.NOTHING
                    else {
                        // TODO add defend action
                        Player.get(Ai::class)!!.action = Action.WAIT
                    }
                }
                // Attack the enemy
                else if (clickedCharacter != null && Player.get(Ai::class)!!.canAttack(x, y))
                    Player.get(Ai::class)!!.action = Action.ATTACK(x, y) // can pass a character

                // Calculate move list
                if (Player.get(Ai::class)!!.action is Action.NOTHING) {
                    // Add the interactable entity as the target
                    if (Turn.currentLevel.getEntityWithAction(x, y) != null)
                        Player.get(Ai::class)!!.targetCoords = Pair(x, y)
                    else
                        Player.get(Ai::class)!!.targetCoords = null

                    // Add player movement list
                    if (!Turn.currentLevel.discoveredMap[y][x] || !Turn.currentLevel.allowsCharacterChangesImpassable(x, y))
                        Player.get(Ai::class)!!.action = Action.NOTHING
                    else
                        Player.get(Ai::class)!!.setMoveList(x, y, Turn.dijkstraMap, Turn.mapImpassableList.plus(Turn.characterArray.getImpassable()))

                    // Focus player either if he's off screen or if he clicked near his current position
                    if (!gameStage.isInCamera(Player.x, Player.y) ||
                        abs(Player.x - x) <= 5 &&  abs(Player.y - y) <= 5) {
                        gameStage.lookingAround = false
                        gameStage.focusPlayer = true
                    }
                }
            }

            // reset stage to wait for input
            gameStage.waitForPlayerInput = false
            gameStage.clickedCoordinates = null
            Turn.playerAction = false
        }
        while (!Turn.playerAction)
            Turn.makeTurn()
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