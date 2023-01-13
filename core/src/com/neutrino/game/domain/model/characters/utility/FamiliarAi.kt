package com.neutrino.game.domain.model.characters.utility

import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.turn.Action
import com.neutrino.game.domain.model.turn.Turn
import kotlin.math.abs

class FamiliarAi(private val character: Character, private val master: Character): EnemyAi(character) {

    /**
     * Sets the alignment
     */
    init {
        if (master.characterAlignment == CharacterAlignment.PLAYER)
            character.characterAlignment = CharacterAlignment.FAMILIAR
        else
            character.characterAlignment = master.characterAlignment
    }

    override fun decide() {
        if (!isInMasterBounds()) {
            currentBehavior = AiBehavior.GOTO_CHARACTER
        }
        when (currentBehavior) {
            AiBehavior.GOTO_CHARACTER -> {
                if (abs(character.xPos - master.xPos) <= 3 && abs(character.yPos - master.yPos) <= 3) {
                    currentBehavior = AiBehavior.SENSE_ENEMIES
                    return decide()
                }

                moveTo(master.xPos, master.yPos,
                    Turn.dijkstraMap, Turn.mapImpassableList.plus(Turn.charactersUseCases.getImpassable()))
            }
            AiBehavior.SENSE_ENEMIES -> {
                searchTarget(Turn.characterMap)
                if (targettedEnemy != null) {
                    currentBehavior = AiBehavior.TARGET_ENEMY
                    ActorVisuals.showAiIntention(character, AiIntentionIcons.ENEMY_DETECTED())
                    return decide()
                }

                if (abs(character.xPos - master.xPos) <= 2 && abs(character.yPos - master.yPos) <= 2) {
                    character.ai.action = Action.WAIT
                    return
                }

                moveTo(master.xPos, master.yPos,
                    Turn.dijkstraMap, Turn.mapImpassableList.plus(Turn.charactersUseCases.getImpassable()))
            }

            else -> {
                super.decide()
            }
        }
        energy = 15
    }

    private fun isInMasterBounds(): Boolean {
        return (abs(character.xPos - master.xPos) <= 10 && abs(character.yPos - master.yPos) <= 10)
    }
}