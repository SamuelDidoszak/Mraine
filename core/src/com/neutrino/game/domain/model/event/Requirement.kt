package com.neutrino.game.domain.model.event

class Requirement(
    val data: Map<String, Data<*>> = mutableMapOf()
) {
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

    private fun isDataSet(): Boolean {
        for (data in data) {
            if (data.value.data == null) {
                try {
                    throw Exception("Data \"${data.key}\" is not set")
                } catch (e: Exception) {
                    e.printStackTrace()
                    return false
                }
            }
        }
        return true
    }


}