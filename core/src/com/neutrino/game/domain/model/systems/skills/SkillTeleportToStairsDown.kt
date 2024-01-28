package com.neutrino.game.domain.model.systems.skills

import com.neutrino.EventDispatcher
import com.neutrino.LevelArrays
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.event.types.CooldownType
import com.neutrino.game.domain.model.systems.event.types.EventTeleport
import com.neutrino.game.domain.model.systems.event.wrappers.CharacterEvent
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.util.hasIdentity
import squidpony.squidmath.Coord

class SkillTeleportToStairsDown(override val character: Character): Skill.ActiveSkill {
    override val skillType: SkillType = SkillType.INTELLIGENCE
    override val name: String = "Teleport"
    override val description: String = "Teleports you away"
    override val requirement: RequirementPrintable = RequirementPrintable()
        .add(RequirementPrintable.PrintableReq("Intelligence", 5f) { Player.intelligence })
        { character.intelligence >= 5 }
    override val textureName: String = "exclamationMark"

    override val cooldownLength: Double = 2.0
    override val cooldownType: CooldownType = CooldownType.SKILL(this)

    override val manaCost: Float? = null

    override val printableData: List<Pair<String, () -> Any>> = listOf(
        Pair("Stairss") { "ya" }
    )

    override fun use() {
        var stairsDown: Coord? = null
        var stairsUp: Coord? = null
        for (y in 0 until LevelArrays.getLevel().sizeY) {
            for (x in 0 until LevelArrays.getLevel().sizeX) {
                for (z in 0 until LevelArrays.getLevel().map[y][x].size) {
                    if (LevelArrays.getLevel().map[y][x][z] hasIdentity Identity.StairsDown::class)
                        stairsDown = Coord.get(x, y)
                    if (LevelArrays.getLevel().map[y][x][z] hasIdentity Identity.StairsUp::class)
                        stairsUp = Coord.get(x, y)
                }
            }
            if (stairsUp != null && stairsDown != null)
                break
        }

        val event = CharacterEvent(character, OnOffEvent(EventTeleport(character, stairsDown!!)), Turn.turn)
        EventDispatcher.dispatchEvent(event)
        causeCooldown()
    }
}