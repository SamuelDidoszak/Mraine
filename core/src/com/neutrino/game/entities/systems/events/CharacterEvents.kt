package com.neutrino.game.entities.systems.events

import com.neutrino.game.entities.Characters
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Character
import com.neutrino.game.entities.characters.attributes.DefensiveStats
import com.neutrino.game.entities.characters.attributes.OffensiveStats
import com.neutrino.game.entities.characters.attributes.util.Status
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.map.attributes.Turn
import com.neutrino.game.util.EntityName
import com.neutrino.game.util.x
import com.neutrino.game.util.y

abstract class CharacterEvents: Event {

    class Heal(val entity: Entity, val power: Float): CharacterEvents(), Status {
        override fun apply() {
            val stats = entity.get(DefensiveStats::class) ?: return

            if (stats.hp + power > stats.hpMax)
                stats.hp = stats.hpMax
            else
                stats.hp += power
//            entity.findActor<HpBar>("hpBar").update(character.hp)
        }

        override val name: String = "Heal"
        override val data: ArrayList<Pair<String, *>> = ArrayList()
    }

    class ManaRegen(val entity: Entity, val regen: Float): CharacterEvents() {
        override fun apply() {
            val stats = entity.get(DefensiveStats::class) ?: return

            if (stats.mp + regen > stats.mpMax)
                stats.mp = stats.mpMax
            else
                stats.mp += regen
        }
    }

    class Burn(val entity: Entity, fireDamageMin: Float, fireDamageMax: Float): CharacterEvents() {
        constructor(entity: Entity, fireDamage: Float): this(entity, fireDamage, fireDamage)
        private val fakeEntity = Entity()
            .addAttribute(OffensiveStats(
                fireDamageMin = fireDamageMin,
                fireDamageMax = fireDamageMax,
                accuracy = 1000f))

        override fun apply() {
            entity.get(DefensiveStats::class)?.getDamage(fakeEntity.get(OffensiveStats::class)!!)
        }
    }

    class Bleed(val entity: Entity, damageMin: Float, damageMax: Float): CharacterEvents() {
        constructor(entity: Entity, damage: Float): this(entity, damage, damage)
        private val fakeEntity = Entity()
            .addAttribute(OffensiveStats(
                damageMin = damageMin,
                damageMax = damageMax,
                accuracy = 1000f))

        override fun apply() {
            entity.get(DefensiveStats::class)?.getDamage(fakeEntity.get(OffensiveStats::class)!!)
        }
    }

    class Teleport(val entity: Character, val position: Position): CharacterEvents() {
        override fun apply() {
            entity.get(Position::class)!!.chunk.characterMap[entity.y][entity.x] = null
            position.chunk.characterMap[position.y][position.x] = entity
            entity.addAttribute(position)
        }
    }

    class Spawn(val entity: EntityName, val position: Position,
                val apply: (entity: Entity) -> Unit): EntityEvents() {
        override fun apply() {
            val addedEntity = Characters.new(entity)
            apply.invoke(addedEntity)
            addedEntity.addAttribute(position)
            addedEntity.addAttribute(Turn())
            position.chunk.characterArray.add(addedEntity)
            position.chunk.characterMap[position.y][position.x] = addedEntity
        }
    }
}














