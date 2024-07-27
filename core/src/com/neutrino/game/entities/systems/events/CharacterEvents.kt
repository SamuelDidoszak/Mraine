package com.neutrino.game.entities.systems.events

import com.neutrino.game.entities.Characters
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.attributes.Ai
import com.neutrino.game.entities.characters.attributes.util.Status
import com.neutrino.game.entities.characters.callables.VisionChangedCallable
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.map.attributes.Turn
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.requirements.PrintableInfo
import com.neutrino.game.util.EntityName
import com.neutrino.game.util.x
import com.neutrino.game.util.y

abstract class CharacterEvents: Event {

    private var _entity: Entity? = null
    var entity: Entity
        get() = _entity!!
        set(value) { _entity = value}

    fun setEntity(entity: Entity): CharacterEvents {
        this.entity = entity
        return this
    }

    fun asTimedEvent(): TimedEvent = TimedEvent(this, 0.0, 1)

    open val color: String = PrintableInfo.baseColor

    class Heal(var power: Float): CharacterEvents(), Status {
        override fun apply() {
            val stats = entity.get(DefensiveStats::class) ?: return

            if (stats.hp + power > stats.hpMax)
                stats.hp = stats.hpMax
            else
                stats.hp += power
//            entity.findActor<HpBar>("hpBar").update(character.hp)
        }

        override val color = "#e4265c"
        override val name: String = "Heal"
        override fun printable(other: Event?): String {
            return "[$color]Heals " + power + "[$color]hp"
        }
    }

    class ManaRegen(val regen: Float): CharacterEvents() {
        override fun apply() {
            val stats = entity.get(DefensiveStats::class) ?: return

            if (stats.mp + regen > stats.mpMax)
                stats.mp = stats.mpMax
            else
                stats.mp += regen
        }

        override val color = "#4929d6"
        override fun printable(other: Event?): String {
            return "[$color]Regenerates " + PrintableInfo.getColoredNumber(regen, (other as? ManaRegen)?.regen ?: regen) + "[$color]mp"
        }
    }

    class Burn(fireDamageMin: Float, fireDamageMax: Float): CharacterEvents() {
        constructor(fireDamage: Float): this(fireDamage, fireDamage)
        private val fakeEntity = Entity()
            .addAttribute(
                OffensiveStats(
                fireDamageMin = fireDamageMin,
                fireDamageMax = fireDamageMax,
                accuracy = 1000f)
            )
        private val offensiveStats: OffensiveStats
            get() = fakeEntity.get(OffensiveStats::class)!!

        override fun apply() {
            entity.get(DefensiveStats::class)?.getDamage(offensiveStats)
        }

        override fun printable(other: Event?): String {
            return "Applies " +
                    PrintableInfo.getColoredNumber(offensiveStats.fireDamageMin, (other as? Burn)?.offensiveStats?.fireDamageMin ?: offensiveStats.fireDamageMin) +
                    " - " +
                    PrintableInfo.getColoredNumber(offensiveStats.fireDamageMax, (other as? Burn)?.offensiveStats?.fireDamageMax ?: offensiveStats.fireDamageMax) +
                    " fire damage"
        }
    }

    class Bleed(damageMin: Float, damageMax: Float): CharacterEvents() {
        constructor(damage: Float): this(damage, damage)
        private val offensiveStats = OffensiveStats(
            damageMin = damageMin,
            damageMax = damageMax,
            accuracy = 1000f
        ).also {it.entity = Entity()}

        override fun apply() {
            entity.get(DefensiveStats::class)?.getDamage(offensiveStats)
        }

        override fun printable(other: Event?): String {
            return "Applies " +
                    PrintableInfo.getColoredNumber(offensiveStats.damageMin, (other as? Bleed)?.offensiveStats?.damageMin ?: offensiveStats.damageMin) +
                    " - " +
                    PrintableInfo.getColoredNumber(offensiveStats.damageMax, (other as? Bleed)?.offensiveStats?.damageMax ?: offensiveStats.damageMax) +
                    " damage"
        }
    }

    class SlowDown(val power: Double): CharacterEvents() {
        private var belowZero = 0.0
        override fun apply() {
            val stats = entity.get(DefensiveStats::class)!!
            stats.movementSpeed += power
            if (stats.movementSpeed < 0.0) {
                belowZero = stats.movementSpeed
                stats.movementSpeed = 0.0
            }
        }

        override fun stop() {
            entity.get(DefensiveStats::class)!!.movementSpeed -= power - belowZero
        }

        override fun printable(other: Event?): String = "Slows down by " +
                PrintableInfo.getColoredNumber(power, (other as SlowDown?)?.power)
    }

    class Teleport(val position: Position): CharacterEvents() {
        override fun apply() {
            entity.get(Position::class)!!.chunk.characterMap[entity.y][entity.x] = null
            position.chunk.characterMap[position.y][position.x] = entity
            entity.addAttribute(position)
            entity.getSuper(Ai::class)!!.updateFov()
            entity.call(VisionChangedCallable::class)
        }

        override fun printable(other: Event?): String = "Teleports character"
    }

    class Spawn(val entity: EntityName, val position: Position,
                val apply: (entity: Entity) -> Unit): Event {
        override fun apply() {
            val addedEntity = Characters.new(entity)
            apply.invoke(addedEntity)
            addedEntity.addAttribute(position)
            addedEntity.addAttribute(Turn())
            position.chunk.characterArray.add(addedEntity)
            position.chunk.characterMap[position.y][position.x] = addedEntity
        }

        override fun printable(other: Event?): String = "Spawns $entity"
    }

    class AddStats(val offensiveStats: OffensiveStats?, val defensiveStats: DefensiveStats?, val eventName: String): CharacterEvents(), Status {
        override fun apply() {
            if (offensiveStats != null)
                entity.get(OffensiveStats::class)?.plusEquals(offensiveStats)
            if (defensiveStats != null)
                entity.get(DefensiveStats::class)?.plusEquals(defensiveStats)
        }

        override fun stop() {
            if (offensiveStats != null)
                entity.get(OffensiveStats::class)?.minusEquals(offensiveStats)
            if (defensiveStats != null)
                entity.get(DefensiveStats::class)?.minusEquals(defensiveStats)
        }

        override val name: String = eventName
    }
}














