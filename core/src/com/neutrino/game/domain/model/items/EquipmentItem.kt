package com.neutrino.game.domain.model.items

import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.event.Event
import com.neutrino.game.domain.model.utility.RandomizationTypes

abstract class EquipmentItem: Item(), ItemType.EQUIPMENT {
    override var amount: Int? = null
    override val causesCooldown: Int = -1
    /** Parses only ModifyStat, ModifyStatPercent and Event */
    abstract val modifierList: ArrayList<Event<*>>
    override val itemTier: Int = 3

    fun statRandomization(energy: Float, variationSkew: Float = 0.5f, energyCanIncrease: Boolean = false, randomizationType: RandomizationTypes? = null) {
//        modifierList.sortBy { (it as ModifyStat).value as Float }
//        var energy = energy
//        while (energy.compareDelta(0f) == 1) {
//            val statToModify: ModifyStat = getWeighedStat(Constants.RandomGenerator.nextFloat()) // TODO randomize getting the stat with weight in an efficient way
//            // TODO Modify coerce percentage to be a custom percentage for each stat and to follow a certain curve that changes with game progression
//            // variation here is 0.2 -> 1 - 0.2 == 0.8 && 0.2 * 2 = 0.4
//            val variation = 0.2f
//            val valueModified = (statToModify.value as Float) * (1 - variation + Constants.RandomGenerator.nextFloat() * variation * 2)
//            var energyCost: Float = (valueModified - statToModify.value as Float) * statToModify.stat.statCost
//            energyCost = 1f // TODO implement thought out energy costs
//            if (!energyCanIncrease)
//                energyCost = abs(energyCost)
//            energy -= energyCost
//            (modifierList.find { (it as ModifyStat) == statToModify } as ModifyStat).value = valueModified
//        }
    }

    private fun getWeighedStat(value: Float): StatsEnum {
        // TODO weighing implementation
        return modifierList[value.coerceAtMost(modifierList.size.toFloat()).toInt()] as StatsEnum
    }
}