package com.neutrino.game.domain.model.systems.event

class Data<T> {
    constructor()
    constructor(data: T) {
        setData(data)
    }
    var data: T? = null
    fun setData(data: T): Data<T> {
        this.data = data
        return this
    }
}