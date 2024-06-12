package com.neutrino.game.entities.systems.skills

import com.neutrino.game.domain.use_case.map.LevelArrays
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.entities.systems.events.CharacterEvents
import com.neutrino.game.entities.systems.events.Events
import com.neutrino.game.gameplay.turn.Turn
import com.neutrino.game.util.hasIdentity
import squidpony.squidmath.Coord

class SkillTeleportToStairsDown(caster: Entity): Skill.ActiveSkill(
    "Teleport to stairs down",
    "Teleports you to stairs going downwards",
    SkillType.INTELLIGENCE,
    "book",
    null,
    2.0,
    caster
) {

    override fun getPrintableInfo(other: Skill?): List<Pair<String, Any?>> = listOf()

    override fun use() {
        var stairsDown: Coord? = null
        var stairsUp: Coord? = null
        for (y in 0 until Turn.currentChunk.sizeY) {
            for (x in 0 until Turn.currentChunk.sizeX) {
                for (z in 0 until Turn.currentChunk.map[y][x].size) {
                    if (LevelArrays.getLevel().map[y][x][z] hasIdentity Identity.StairsDown::class)
                        stairsDown = Coord.get(x, y)
                    if (LevelArrays.getLevel().map[y][x][z] hasIdentity Identity.StairsUp::class)
                        stairsUp = Coord.get(x, y)
                }
            }
            if (stairsUp != null && stairsDown != null)
                break
        }
        if (stairsDown == null)
            return

        Events.addEvent(caster, CharacterEvents.Teleport(Position(stairsDown, Turn.currentChunk)).asTimedEvent())
        causeCooldown()
    }
}