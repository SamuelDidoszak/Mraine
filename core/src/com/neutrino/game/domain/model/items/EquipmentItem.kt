package com.neutrino.game.domain.model.items

abstract class EquipmentItem: Item(), ItemType.EQUIPMENT {
    override var amount: Int? = null
    override val causesCooldown: Int = -1
    /** Parses only ModifyStat, ModifyStatPercent and Event */
    abstract val modifierList: ArrayList<Any>
}