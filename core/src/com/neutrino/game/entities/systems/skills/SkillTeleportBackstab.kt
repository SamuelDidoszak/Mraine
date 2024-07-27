package com.neutrino.game.entities.systems.skills

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Character
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.events.CharacterEvents
import com.neutrino.game.entities.systems.events.Events
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.map.chunk.ChunkManager
import kotlin.math.roundToInt

class SkillTeleportBackstab(caster: Entity): Skill.ActiveSkillCharacter(
    "Teleportation backstab",
    "Teleports you behind an enemy and backstabs it instantly",
    SkillType.ROGUE,
    "skillTeleportBackstab",
    null,
    2.0,
    caster,
    7,
    RangeType.CIRCLE,
    Requirements.Stats(dexterity = 5f)
) {

    val damage = 5f
    val criticalChance = 1f

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf(
        ColorUtils.getStatColorTextra("Damage") + "Damage" to damage,
        ColorUtils.getStatColorTextra("CriticalChance") + "Crit %" to (criticalChance * 100).roundToInt(),
        ColorUtils.getStatColorTextra("Range") + "Range" to range
    )

    override fun use(target: Character) {
        val possibleTeleportPosition: List<Position>
        val mirrorCharacter: Boolean
        val targetPosition = target.get(Position::class)!!
        if (target.get(Texture::class)!!.textures.isMirrored()) {
            mirrorCharacter = false
            possibleTeleportPosition = listOf(
                ChunkManager.getCorrectPosition(targetPosition, 1, 0),
                ChunkManager.getCorrectPosition(targetPosition, 1, 1),
                ChunkManager.getCorrectPosition(targetPosition, 1, -1),
            )
        } else {
            mirrorCharacter = true
            possibleTeleportPosition = listOf(
                ChunkManager.getCorrectPosition(targetPosition, -1, 0),
                ChunkManager.getCorrectPosition(targetPosition, -1, 1),
                ChunkManager.getCorrectPosition(targetPosition, -1, -1),
            )
        }

        var teleportPosition: Position? = null
        for (i in 0 until 3) {
            if (!ChunkManager.characterMethods.isImpassable(possibleTeleportPosition[i])) {
                teleportPosition = possibleTeleportPosition[i]
                break
            }
        }

        if (teleportPosition == null)
            return

        Events.addEvent(caster, CharacterEvents.Teleport(teleportPosition).asTimedEvent())
        caster.get(Texture::class)!!.textures.mirror(mirrorCharacter)

        target.get(DefensiveStats::class)!!.getDamage(
            caster.get(OffensiveStats::class)!!.clone().also {
                it.damageMin += damage
                it.damageMax += damage
                it.criticalChance = criticalChance
                it.entity = caster
            }
        )

        causeCooldown()
    }
}