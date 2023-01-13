package com.neutrino.game.domain.model.systems.event

class Requirement(
    override val data: MutableMap<String, Data<*>> = mutableMapOf()
): DataMap {
    private val requirementList: ArrayList<() -> Boolean> = ArrayList()
    fun add(check: () -> Boolean): Requirement {
        requirementList.add(check)
        return this
    }
    fun checkAll(): Boolean {
        if (!isDataSet())
            return false

        for (requirement in requirementList) {
            if (!requirement())
                return false
        }
        return true
    }
}