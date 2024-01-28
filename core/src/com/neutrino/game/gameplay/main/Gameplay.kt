package com.neutrino.game.gameplay.main

import com.neutrino.GameStage
import com.neutrino.HudStage
import com.neutrino.LevelArrays
import com.neutrino.game.UI.UiStage
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.turn.Action
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.entities.shared.attributes.Interaction
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
        if ((Player.hasActions() || gameStage.focusPlayer) && !gameStage.lookingAround) {
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
            if (Player.ai.action is Action.NOTHING && !Player.hasActions() && Player.ai.entityTargetCoords != null) {
                val entityCoords = Player.ai.entityTargetCoords!!
                val entity = Turn.currentLevel.getEntityWithAction(entityCoords.first, entityCoords.second)?.get(Interaction::class)
                // Entity has disappeared in the meantime
                if (entity == null)
                    Player.ai.entityTargetCoords = null
                else {
                    val action = entity.getPrimaryInteraction()
                    if (action != null) {
                        // TODO ECS Attack
//                        if (action is InteractionType.DESTROY)
//                            action.requiredDistance = if ((entity as Destructable).destroyed) -1 else Player.range
                        // check the distance and act if close enough
                        if ((entityCoords.first in Player.xPos - action.requiredDistance .. Player.xPos + action.requiredDistance) &&
                            (entityCoords.second in Player.yPos - action.requiredDistance .. Player.yPos + action.requiredDistance)) {
                            Player.ai.action = Action.INTERACTION(entity.entity, action)
                            // Stop moving
                            Player.ai.moveList = ArrayDeque()
                        }
                    }
                }
            }

            // WASD movement
            if (Player.ai.action is Action.NOTHING && gameStage.moveDirection != null && !Player.hasActions()) {
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

                val wasdCoord = Coord.get(Player.xPos + xChange, Player.yPos + yChange)
                if (!Turn.currentLevel.allowsCharacter(wasdCoord.x, wasdCoord.y) || LevelArrays.getCharacterAt(wasdCoord) != null)
                    return

                Player.ai.moveTo(wasdCoord.x, wasdCoord.y, Turn.dijkstraMap, LevelArrays.getImpassableList())
            }

            // move the Player if a tile was clicked previously, or stop if user clicked during the movement
            // Add the move action if the movement animation has ended
            if (Player.ai.moveList.isNotEmpty() && !Player.hasActions() && gameStage.clickedCoordinates == null && Player.ai.action is Action.NOTHING) {
                if (Turn.updateBatch.firstOrNull() is Action.MOVE) // Some character has moved in the meantime, so the movement map should be updated
                    Player.ai.setMoveList(
                        Player.ai.moveList.last().x, Player.ai.moveList.last().y, Turn.dijkstraMap, Turn.mapImpassableList.plus(
                            Turn.charactersUseCases.getImpassable()), true)
                val tile = Player.ai.getMove()
                Player.ai.action = Action.MOVE(tile.x, tile.y)
                if (!gameStage.lookingAround)
                    gameStage.focusPlayer = true
            }

            // Set the player action if there was no previous one
            if (Player.ai.action is Action.NOTHING) {
                // calls this method until a tile is clicked
                if (gameStage.clickedCoordinates == null) return
                // player clicked during movement
                if (Player.ai.moveList.isNotEmpty() || Player.hasActions()) {
                    Player.ai.moveList = ArrayDeque()
                    Player.ai.entityTargetCoords = null
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
                        Player.ai.action = Action.NOTHING
                    else {
                        // TODO add defend action
                        Player.ai.action = Action.WAIT
                    }
                }
                // Attack the enemy
                else if (clickedCharacter != null && Player.ai.canAttack(x, y))
                    Player.ai.action = Action.ATTACK(x, y) // can pass a character

                // Calculate move list
                if (Player.ai.action is Action.NOTHING) {
                    // Add the interactable entity as the target
                    if (Turn.currentLevel.getEntityWithAction(x, y) != null)
                        Player.ai.entityTargetCoords = Pair(x, y)
                    else
                        Player.ai.entityTargetCoords = null

                    // Add player movement list
                    if (!Turn.currentLevel.discoveredMap[y][x] || !Turn.currentLevel.allowsCharacterChangesImpassable(x, y))
                        Player.ai.action = Action.NOTHING
                    else
                        Player.ai.setMoveList(x, y, Turn.dijkstraMap, Turn.mapImpassableList.plus(Turn.charactersUseCases.getImpassable()))

                    // Focus player either if he's off screen or if he clicked near his current position
                    if (!gameStage.isInCamera(Player.xPos, Player.yPos) ||
                        abs(Player.xPos - x) <= 5 &&  abs(Player.yPos - y) <= 5) {
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