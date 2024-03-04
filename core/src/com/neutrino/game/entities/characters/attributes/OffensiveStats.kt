package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.AttributeOperations
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.util.HasRange
import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.util.add
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class OffensiveStats(
    var strength: Float = 0f,
    var dexterity: Float = 0f,
    var intelligence: Float = 0f,
    var luck: Float = 0f,
    var damageMin: Float = 0f,
    var damageMax: Float = damageMin,
    /** Range is 0 - 2 which tells the probability of hitting the enemy */
    var accuracy: Float = 1f,
    var criticalChance: Float = 0f,
    /** Damage multiplier applied on critical hit */
    var criticalDamage: Float = 1.2f,
    var attackSpeed: Double = 1.0,
    override var range: Int = 1,
    override var rangeType: RangeType = RangeType.SQUARE,
    // elemental
    var fireDamageMin: Float = 0f,
    var fireDamageMax: Float = fireDamageMin,
    var waterDamageMin: Float = 0f,
    var waterDamageMax: Float = waterDamageMin,
    var airDamageMin: Float = 0f,
    var airDamageMax: Float = airDamageMin,
    var poisonDamageMin: Float = 0f,
    var poisonDamageMax: Float = poisonDamageMin
): Attribute(), HasRange, AttributeOperations<OffensiveStats> {

    fun attack(target: Position) {
        if (entity has AreaAttack::class)
            return areaAttack(target, entity.get(AreaAttack::class)!!)
        if (entity has AroundAttack::class)
            return aroundAttack(target)
        basicAttack(target)
    }

    private fun basicAttack(target: Position) {
        getTopAttackable(target)?.get(DefensiveStats::class)?.getDamage(this)
        val projectile = entity get Projectile::class
        projectile?.shoot(target)
    }

    private fun areaAttack(target: Position, hasRange: HasRange) {
        val projectile = entity get Projectile::class
        for (tile in hasRange.getTilesInRange(target)) {
            val attackables = getAllAttackables(tile) ?: continue
            for (entity in attackables) {
                entity.get(DefensiveStats::class)!!.getDamage(this)
                projectile?.shoot(entity)
            }
        }
    }

    private fun aroundAttack(target: Position) {
        val projectile = entity get Projectile::class
        for (tile in getTilesInRange(target, true)) {
            val attackables = getAllAttackables(tile) ?: continue
            for (entity in attackables) {
                entity.get(DefensiveStats::class)!!.getDamage(this)
                projectile?.shoot(entity)
            }
        }
    }

    private fun getTopAttackable(target: Position): Entity? {
        return target.chunk.characterMap[target.y][target.x] ?:
            target.chunk.map[target.y][target.x].asReversed().firstOrNull { it has DefensiveStats::class }
    }

    private fun getAllAttackables(target: Position): List<Entity>? {
        val list = ArrayList<Entity>()
        list.add(target.chunk.characterMap[target.y][target.x])
        target.chunk.map[target.y][target.x].forEach {
            if (it has DefensiveStats::class)
                list.add(it)
        }
        return if (list.isEmpty()) null else list
    }

    fun getDamage(): Float {
        return damageMin + (damageMax - damageMin) * Random.nextFloat()
    }

    fun getFireDamage(): Float {
        return fireDamageMin + (fireDamageMax - fireDamageMin) * Random.nextFloat()
    }

    fun getWaterDamage(): Float {
        return waterDamageMin + (waterDamageMax - waterDamageMin) * Random.nextFloat()
    }

    fun getAirDamage(): Float {
        return airDamageMin + (airDamageMax - airDamageMin) * Random.nextFloat()
    }

    fun getPoisonDamage(): Float {
        return poisonDamageMin + (poisonDamageMax - poisonDamageMin) * Random.nextFloat()
    }

    /**
     * Returns new joined offensiveStats.
     * New range is max range
     * New entity is leftSide entity
     */
    override fun plus(other: OffensiveStats): OffensiveStats {
        val newStats = OffensiveStats()
        newStats.entity = entity
        newStats.strength = strength + other.strength
        newStats.dexterity = dexterity + other.dexterity
        newStats.intelligence = intelligence + other.intelligence
        newStats.luck = luck + other.luck
        newStats.damageMin = damageMin + other.damageMin
        newStats.damageMax = damageMax + other.damageMax
        newStats.accuracy = min(accuracy, other.accuracy) + abs(accuracy - other.accuracy) / 2f
        newStats.criticalChance = criticalChance + other.criticalChance
        newStats.criticalDamage = criticalDamage + other.criticalDamage
        newStats.attackSpeed = min(attackSpeed, other.attackSpeed) + abs(attackSpeed - other.attackSpeed) / 2f
        val isOtherRangeBigger = other.range > range
        newStats.range = max(range, other.range)
        newStats.rangeType = if (isOtherRangeBigger) other.rangeType else rangeType
        newStats.fireDamageMin = fireDamageMin + other.fireDamageMin
        newStats.fireDamageMax = fireDamageMax + other.fireDamageMax
        newStats.waterDamageMin = waterDamageMin + other.waterDamageMin
        newStats.waterDamageMax = waterDamageMax + other.waterDamageMax
        newStats.airDamageMin = airDamageMin + other.airDamageMin
        newStats.airDamageMax = airDamageMax + other.airDamageMax
        newStats.poisonDamageMin = poisonDamageMin + other.poisonDamageMin
        newStats.poisonDamageMax = poisonDamageMax + other.poisonDamageMax

        return newStats
    }
    override fun minus(other: OffensiveStats): OffensiveStats {
        val newStats = OffensiveStats()
        newStats.entity = entity
        newStats.strength = strength - other.strength
        newStats.dexterity = dexterity - other.dexterity
        newStats.intelligence = intelligence - other.intelligence
        newStats.luck = luck - other.luck
        newStats.damageMin = damageMin - other.damageMin
        newStats.damageMax = damageMax - other.damageMax
        newStats.accuracy = min(accuracy, other.accuracy) - abs(accuracy - other.accuracy) / 2f
        newStats.criticalChance = criticalChance - other.criticalChance
        newStats.criticalDamage = criticalDamage - other.criticalDamage
        newStats.attackSpeed = min(attackSpeed, other.attackSpeed) - abs(attackSpeed - other.attackSpeed) / 2f
        val isOtherRangeBigger = other.range > range
        newStats.range = min(range, other.range)
        newStats.rangeType = if (isOtherRangeBigger) rangeType else other.rangeType
        newStats.fireDamageMin = fireDamageMin - other.fireDamageMin
        newStats.fireDamageMax = fireDamageMax - other.fireDamageMax
        newStats.waterDamageMin = waterDamageMin - other.waterDamageMin
        newStats.waterDamageMax = waterDamageMax - other.waterDamageMax
        newStats.airDamageMin = airDamageMin - other.airDamageMin
        newStats.airDamageMax = airDamageMax - other.airDamageMax
        newStats.poisonDamageMin = poisonDamageMin - other.poisonDamageMin
        newStats.poisonDamageMax = poisonDamageMax - other.poisonDamageMax

        return newStats
    }
    override fun clone(): OffensiveStats {
        return OffensiveStats(
            strength, dexterity, intelligence, luck,
            damageMin, damageMax, accuracy,
            criticalChance, criticalDamage, attackSpeed,
            range, rangeType, fireDamageMin, fireDamageMax,
            waterDamageMin, waterDamageMax, airDamageMin,
            airDamageMax, poisonDamageMin, poisonDamageMax
        )
    }
    override fun isEqual(other: OffensiveStats): Boolean {
        return strength == other.strength &&
            dexterity == other.dexterity &&
            intelligence == other.intelligence &&
            luck == other.luck &&
            damageMin == other.damageMin &&
            damageMax == other.damageMax &&
            accuracy == other.accuracy &&
            criticalChance == other.criticalChance &&
            criticalDamage == other.criticalDamage &&
            attackSpeed == other.attackSpeed &&
            range == other.range &&
            rangeType == other.rangeType &&
            fireDamageMin == other.fireDamageMin &&
            fireDamageMax == other.fireDamageMax &&
            waterDamageMin == other.waterDamageMin &&
            waterDamageMax == other.waterDamageMax &&
            airDamageMin == other.airDamageMin &&
            airDamageMax == other.airDamageMax &&
            poisonDamageMin == other.poisonDamageMin &&
            poisonDamageMax == other.poisonDamageMax
    }
}