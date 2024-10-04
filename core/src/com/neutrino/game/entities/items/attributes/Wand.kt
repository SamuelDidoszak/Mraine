package com.neutrino.game.entities.items.attributes

import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Character
import com.neutrino.game.entities.characters.callables.OnItemEquipped
import com.neutrino.game.entities.characters.callables.OnItemUnequipped
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.attack.attributes.Projectile
import com.neutrino.game.entities.systems.attack.callables.StatsChangedCallable
import com.neutrino.game.entities.systems.attack.util.StatsEnum

class Wand(val elementalType: StatsEnum, private val projectile: Projectile.ProjectileType): Attribute() {

    private var resetDamageCallable: StatsChangedCallable? = null
    private var addedDamage = 0f

    override fun onEntityAttached() {
        entity.addAttribute(Projectile(projectile))
        entity.attach(AddElementalDamageCallable())
        entity.attach(RemoveElementalDamageCallable())
    }

    private inner class AddElementalDamageCallable: OnItemEquipped() {
        override fun call(entity: Entity, vararg data: Any?) {
            val character = data[0] as Character
            setElementalDamage(character, true)
            resetDamageCallable = object : StatsChangedCallable() {
                override fun call(entity: Entity, vararg data: Any?) {
                    setElementalDamage(character, false)
                    setElementalDamage(character, true)
                }
            }
            character.attach(resetDamageCallable!!)
        }
    }

    private inner class RemoveElementalDamageCallable: OnItemUnequipped() {
        override fun call(entity: Entity, vararg data: Any?) {
            val character = data[0] as Character
            setElementalDamage(character, false)
            character.detach(resetDamageCallable!!)
        }
    }

    private fun setElementalDamage(character: Entity, add: Boolean) {
        val offensiveStats = character.get(OffensiveStats::class)!!
        val intelligence = offensiveStats.intelligence
        if (add)
            addedDamage = intelligence * 0.5f
        else
            addedDamage *= -1

        when (elementalType) {
            StatsEnum.FIRE_DAMAGE -> {
                offensiveStats.fireDamageMin += addedDamage
                offensiveStats.fireDamageMax += addedDamage
            }
            StatsEnum.WATER_DAMAGE -> {
                offensiveStats.waterDamageMin += addedDamage
                offensiveStats.waterDamageMax += addedDamage
            }
            StatsEnum.AIR_DAMAGE -> {
                offensiveStats.airDamageMin += addedDamage
                offensiveStats.airDamageMax += addedDamage
            }
            StatsEnum.POISON_DAMAGE -> {
                offensiveStats.poisonDamageMin += addedDamage
                offensiveStats.poisonDamageMax += addedDamage
            }
            else -> throw (Exception("Wrong elementalType on wand ${entity.name}"))
        }

        if (!add)
            addedDamage = 0f
    }
}