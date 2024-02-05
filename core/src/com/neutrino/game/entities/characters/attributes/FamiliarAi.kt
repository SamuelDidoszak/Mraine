package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.domain.model.turn.Action
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.util.FactionEnum
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.util.x
import com.neutrino.game.util.y
import kotlin.math.abs

class FamiliarAi(viewDistance: Int = 10, private val master: Entity): EnemyAi(viewDistance) {

    /**
     * Sets the alignment
     */
    init {
        if (master.get(Faction::class)!!.faction == FactionEnum.PLAYER)
            entity.get(Faction::class)!!.faction = FactionEnum.FAMILIAR
        else
            entity.get(Faction::class)!!.faction = master.get(Faction::class)!!.faction
    }

    override fun decide() {
        if (!isInMasterBounds()) {
            currentBehavior = AiBehavior.GOTO_CHARACTER
        }
        when (currentBehavior) {
            AiBehavior.GOTO_CHARACTER -> {
                if (abs(entity.get(Position::class)!!.x - master.get(Position::class)!!.x) <= 3 && abs(
                        entity.get(Position::class)!!.y - master.get(Position::class)!!.y) <= 3) {
                    currentBehavior = AiBehavior.SENSE_ENEMIES
                    return decide()
                }

                moveTo(master.get(Position::class)!!.x, master.get(Position::class)!!.y,
                    Turn.dijkstraMap, Turn.mapImpassableList.plus(Turn.characterArray.getImpassable()))
            }
            AiBehavior.SENSE_ENEMIES -> {
                searchTarget(Turn.characterMap)
                if (targettedEnemy != null) {
                    currentBehavior = AiBehavior.TARGET_ENEMY
                    // TODO ActorVisuals
//                    ActorVisuals.showAiIntention(entity, AiIntentionIcons.ENEMY_DETECTED())
                    return decide()
                }

                if (abs(entity.x - master.x) <= 2 && abs(entity.y - master.y) <= 2) {
                    entity.get(Ai::class)!!.action = Action.WAIT
                    return
                }

                moveTo(master.x, master.y,
                    Turn.dijkstraMap, Turn.mapImpassableList.plus(Turn.characterArray.getImpassable()))
            }

            else -> {
                super.decide()
            }
        }
        energy = 15
    }

    private fun isInMasterBounds(): Boolean {
        return (abs(entity.x - master.x) <= 10 && abs(entity.y - master.y) <= 10)
    }
}