package com.neutrino.game.gameplay.main

import com.neutrino.GameStage
import com.neutrino.HudStage
import com.neutrino.LevelArrays
import com.neutrino.game.UI.UiStage
import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.domain.model.turn.Action
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.characters.attributes.Ai
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.util.HasRange
import com.neutrino.game.entities.shared.util.RangeType
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
                Player.getSuper(Ai::class)!!.action = Action.SKILL(usedSkill)
                uiStage.usedSkill = null
                hudStage.usedSkill = null
            }
            is Skill.ActiveSkillCharacter -> {
                if (!gameplay.waitForAdditionalClick) {
                    gameplay.waitForAdditionalClick = true
                    gameStage.highlighting.highlightArea(usedSkill, Player.get(Position::class)!!.getPosition(), true, true)
                    gameStage.highlightRange = object: HasRange {
                        override var range: Int = 0
                        override var rangeType: RangeType = RangeType.SQUARE
                    }
                    gameStage.highlightMode = Highlighting.Companion.HighlightModes.ONLY_CHARACTERS
                    gameStage.skillRange = usedSkill
                }
                if (gameStage.clickedCoordinates == null)
                    return false

                if (!usedSkill.isInRange(usedSkill.character.getPosition(), gameStage.clickedCoordinates!!)) {
                    gameplay.cancelUsage()
                    return false
                }

                val clickedCharacter: Entity? = LevelArrays.getCharacterAt(gameStage.clickedCoordinates!!.x, gameStage.clickedCoordinates!!.y)
                if (clickedCharacter == null) {
                    gameStage.clickedCoordinates = null
                    return false
                }

                // TODO Ecs Skills
//                Player.getSuper(Ai::class)!!.action = Action.SKILL(usedSkill, clickedCharacter)
                gameplay.cancelUsage()
            }
            is Skill.ActiveSkillTile -> {
                if (!gameplay.waitForAdditionalClick) {
                    gameplay.waitForAdditionalClick = true
                    gameStage.highlighting.highlightArea(usedSkill, Player.get(Position::class)!!.getPosition(), true, true)
                    gameStage.highlightRange = object: HasRange {
                        override var range: Int = 0
                        override var rangeType: RangeType = RangeType.SQUARE
                    }
                    gameStage.highlightMode = Highlighting.Companion.HighlightModes.AREA
                    gameStage.skillRange = usedSkill
                }
                if (gameStage.clickedCoordinates == null)
                    return false

                if (!usedSkill.isInRange(usedSkill.character.getPosition(), gameStage.clickedCoordinates!!)) {
                    gameplay.cancelUsage()
                    return false
                }

                Player.getSuper(Ai::class)!!.action = Action.SKILL(usedSkill, tile = gameStage.clickedCoordinates!!)
                gameplay.cancelUsage()
            }
            is Skill.ActiveSkillArea -> {
                if (!gameplay.waitForAdditionalClick) {
                    gameplay.waitForAdditionalClick = true
                    gameStage.highlighting.highlightArea(usedSkill, Player.get(Position::class)!!.getPosition(), false, true)
                    gameStage.highlightRange = usedSkill.area
                    gameStage.highlightMode = Highlighting.Companion.HighlightModes.AREA
                    gameStage.skillRange = usedSkill
                }
                if (gameStage.clickedCoordinates == null)
                    return false

                if (!usedSkill.isInRange(usedSkill.character.getPosition(), gameStage.clickedCoordinates!!)) {
                    gameplay.cancelUsage()
                    return false
                }

                Player.getSuper(Ai::class)!!.action = Action.SKILL(usedSkill, tile = gameStage.clickedCoordinates!!)
                gameplay.cancelUsage()
            }

            is Skill.PassiveSkill -> {
                throw Exception("Cannot use passive skill!")
            }
        }
        return true
    }
}