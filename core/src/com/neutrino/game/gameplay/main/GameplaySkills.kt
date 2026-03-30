package com.neutrino.game.gameplay.main

import com.neutrino.GameStage
import com.neutrino.HudStage
import com.neutrino.game.UI.UiStage
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.characters.attributes.Ai
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.util.HasRange
import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.skills.Skill
import com.neutrino.game.gameplay.turn.Action
import com.neutrino.game.gameplay.turn.Turn
import com.neutrino.game.map.chunk.ChunkManager
import com.neutrino.game.utility.Highlighting

class GameplaySkills(
    val gameplay: Gameplay,
    val gameStage: GameStage,
    val hudStage: HudStage,
    val uiStage: UiStage
) {

    /**
     * Tries to use provided skill
     * @return false if skill was cancelled
     */
    internal fun useSkill(usedSkill: Skill): Boolean {
        when (usedSkill) {
            is Skill.ActiveSkill -> {
                Player.getSuper(Ai::class)!!.action = Action.SKILL(usedSkill, null)
                uiStage.usedSkill = null
                hudStage.usedSkill = null
            }
            is Skill.ActiveSkillCharacter, is Skill.ActiveSkillEntity -> {
                if (!gameplay.waitForAdditionalClick) {
                    gameplay.waitForAdditionalClick = true
                    gameStage.highlighting.highlightArea(usedSkill as HasRange, Player.get(Position::class)!!, true, true)
                    gameStage.highlightRange = object: HasRange {
                        override var range: Int = 0
                        override var rangeType: RangeType = RangeType.SQUARE
                    }
                    if (usedSkill is Skill.ActiveSkillCharacter)
                        gameStage.highlightMode = Highlighting.Companion.HighlightModes.ONLY_CHARACTERS
                    else
                        gameStage.highlightMode = Highlighting.Companion.HighlightModes.AREA
                    gameStage.skillRange = usedSkill
                }
                if (gameStage.clickedCoordinates == null)
                    return false

                if (!(usedSkill as HasRange).isInRange(
                        usedSkill.caster.get(Position::class)!!,
                        Position(gameStage.clickedCoordinates!!, Turn.currentChunk.chunkCoords))) {
                    gameplay.cancelUsage()
                    return false
                }

                val clickedEntity: Entity?
                if (usedSkill is Skill.ActiveSkillCharacter)
                    clickedEntity = ChunkManager.getCharacterAt(gameStage.clickedCoordinates!!)
                else
                    clickedEntity = OffensiveStats.getTopAttackable(Position(gameStage.clickedCoordinates!!, Turn.currentChunk.chunkCoords))

                if (clickedEntity == null) {
                    gameStage.clickedCoordinates = null
                    return false
                }

                Player.getSuper(Ai::class)!!.action = Action.SKILL(usedSkill, clickedEntity)
                gameplay.cancelUsage()
            }
            is Skill.ActiveSkillPosition -> {
                if (!gameplay.waitForAdditionalClick) {
                    gameplay.waitForAdditionalClick = true
                    gameStage.highlighting.highlightArea(usedSkill, Player.get(Position::class)!!, true, true)
                    gameStage.highlightRange = object: HasRange {
                        override var range: Int = 0
                        override var rangeType: RangeType = RangeType.SQUARE
                    }
                    gameStage.highlightMode = Highlighting.Companion.HighlightModes.AREA
                    gameStage.skillRange = usedSkill
                }
                if (gameStage.clickedCoordinates == null)
                    return false

                if (!usedSkill.isInRange(usedSkill.caster.get(Position::class)!!, Position(gameStage.clickedCoordinates!!, Turn.currentChunk.chunkCoords))) {
                    gameplay.cancelUsage()
                    return false
                }

                Player.getSuper(Ai::class)!!.action = Action.SKILL(usedSkill, Position(gameStage.clickedCoordinates!!, Turn.currentChunk.chunkCoords))
                gameplay.cancelUsage()
            }
            is Skill.ActiveSkillArea -> {
                if (!gameplay.waitForAdditionalClick) {
                    gameplay.waitForAdditionalClick = true
                    gameStage.highlighting.highlightArea(usedSkill, Player.get(Position::class)!!, false, true)
                    gameStage.highlightRange = usedSkill.area
                    gameStage.highlightMode = Highlighting.Companion.HighlightModes.AREA
                    gameStage.skillRange = usedSkill
                }
                if (gameStage.clickedCoordinates == null)
                    return false

                if (!usedSkill.isInRange(usedSkill.caster.get(Position::class)!!, Position(gameStage.clickedCoordinates!!, Turn.currentChunk.chunkCoords))) {
                    gameplay.cancelUsage()
                    return false
                }

                Player.getSuper(Ai::class)!!.action = Action.SKILL(usedSkill, Position(gameStage.clickedCoordinates!!, Turn.currentChunk.chunkCoords))
                gameplay.cancelUsage()
            }

            is Skill.PassiveSkill -> {
                throw Exception("Cannot use passive skill!")
            }
        }
        return true
    }
}