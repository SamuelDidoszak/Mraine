package com.neutrino.game.domain.model.systems.skills

import com.neutrino.EventDispatcher
import com.neutrino.LevelArrays
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.systems.attack.BasicAttack
import com.neutrino.game.domain.model.systems.event.RequirementPrintable
import com.neutrino.game.domain.model.systems.event.types.CooldownType
import com.neutrino.game.domain.model.systems.event.types.EventTeleport
import com.neutrino.game.domain.model.systems.event.wrappers.CharacterEvent
import com.neutrino.game.domain.model.systems.event.wrappers.OnOffEvent
import com.neutrino.game.domain.model.turn.Turn
import squidpony.squidmath.Coord
import kotlin.math.roundToInt

class SkillTeleportBackstab(override val character: Character): Skill.ActiveSkillCharacter {
    override val skillType: SkillType = SkillType.DEXTERITY
    override val name: String = "Teleportation backstab"
    override val description: String = "Teleports you behind an enemy"
    override val requirement: RequirementPrintable = RequirementPrintable()
        .add(RequirementPrintable.PrintableReq("Dexterity", 5f) { Player.dexterity })
        { character.dexterity >= 5f }
    override val textureName: String = "skillTeleportBackstab"

    override val cooldownLength: Double = 2.0
    override val cooldownType: CooldownType = CooldownType.SKILL(this)

    override val manaCost: Float? = null
    val damage = 5f
    val criticalChance = 1f
    override var range: Int = 7
    override var rangeType: RangeType = RangeType.CIRCLE

    override val printableData: List<Pair<String, () -> Any>> = listOf(
        Pair("Range") { range },
        Pair("Damage") { damage },
        Pair("Crit chance") { (criticalChance * 100).roundToInt() }
    )

    override fun use(target: Character) {
        val possibleTeleportPosition: List<Coord>
        val mirrorCharacter: Boolean
        if (target.mirrored) {
            mirrorCharacter = false
            possibleTeleportPosition = listOf(
                Coord.get(target.xPos + 1, target.yPos),
                Coord.get(target.xPos + 1, target.yPos + 1),
                Coord.get(target.xPos + 1, target.yPos - 1)
            )
        }
        else {
            mirrorCharacter = true
            possibleTeleportPosition = listOf(
                Coord.get(target.xPos - 1, target.yPos),
                Coord.get(target.xPos - 1, target.yPos + 1),
                Coord.get(target.xPos - 1, target.yPos - 1)
            )
        }

        var teleportPosition: Coord? = null
        for (i in 0 until 3) {
            if (!LevelArrays.isImpassable(possibleTeleportPosition[i])) {
                teleportPosition = possibleTeleportPosition[i]
                break
            }
        }
        if (teleportPosition == null)
            return

        val event = CharacterEvent(character, OnOffEvent(EventTeleport(character, teleportPosition)), Turn.turn)
        EventDispatcher.dispatchEvent(event)

        character.mirrored = mirrorCharacter

        val attack = BasicAttack(mapOf(StatsEnum.DAMAGE to damage, StatsEnum.CRITICAL_CHANCE to 1f))
        attack.attack(character, target.getPosition())

        causeCooldown()
    }
}