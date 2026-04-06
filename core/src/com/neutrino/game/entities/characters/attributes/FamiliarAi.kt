package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.util.FactionEnum
import com.neutrino.game.gameplay.turn.Action
import com.neutrino.game.util.position
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
        val masterWorldPos = master.position.toWorldTilePos()
        val entityWorldPos = entity.position.toWorldTilePos()
        when (currentBehavior) {
            AiBehavior.GOTO_CHARACTER -> {
                if (abs(entityWorldPos.x - masterWorldPos.x) <= 3 && abs(
                        entityWorldPos.y - masterWorldPos.y) <= 3) {
                    currentBehavior = AiBehavior.SENSE_ENEMIES
                    return decide()
                }

                moveTo(master.position)
            }
            AiBehavior.SENSE_ENEMIES -> {
                searchTarget()
                if (targettedEnemy != null) {
                    currentBehavior = AiBehavior.TARGET_ENEMY
                    // TODO ActorVisuals
//                    ActorVisuals.showAiIntention(entity, AiIntentionIcons.ENEMY_DETECTED())
                    return decide()
                }

                if (abs(entityWorldPos.x - masterWorldPos.x) <= 2 && abs(entityWorldPos.y - masterWorldPos.y) <= 2) {
                    entity.getSuper(Ai::class)!!.action = Action.WAIT
                    return
                }

                moveTo(master.position)
            }

            else -> {
                super.decide()
            }
        }
        energy = 15
    }

    private fun isInMasterBounds(): Boolean {
        val masterWorldPos = master.position.toWorldTilePos()
        val entityWorldPos = entity.position.toWorldTilePos()
        return (abs(entityWorldPos.x - masterWorldPos.x) <= 10 && abs(entityWorldPos.y - masterWorldPos.y) <= 10)
    }
}