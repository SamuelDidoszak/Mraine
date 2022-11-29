package com.neutrino.game.domain.model.event

interface Event<T> {
    var data: T
    var dataAttached: Boolean

    fun attachData(data: T): Event<T> {
        this.data = data
        dataAttached = true
        return this
    }

    fun start() {

    }

    fun stop() {

    }

    fun checkData(): Boolean {
        if (!dataAttached) {
            try {
                throw (Exception("Data not attached"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }
        return true
    }

}