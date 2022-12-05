package com.neutrino.game.domain.model.event

abstract class Event: DataMap {
    /** Variable to check for data correctness only once instead of in each method call */
    var dataAttached: Boolean = false

    open fun start() {

    }

    open fun stop() {

    }

    fun checkData(): Boolean {
        if (!dataAttached)
            dataAttached = isDataSet()
        return dataAttached
    }

}