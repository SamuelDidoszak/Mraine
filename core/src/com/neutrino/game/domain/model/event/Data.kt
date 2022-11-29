package com.neutrino.game.domain.model.event

class Data<T> {
    var data: T? = null
    fun setData(data: T): Data<T> {
        this.data = data
        return this
    }
}