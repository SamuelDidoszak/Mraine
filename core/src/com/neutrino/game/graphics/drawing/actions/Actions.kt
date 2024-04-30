package com.neutrino.game.graphics.drawing.actions

object Actions {

    private val actions = ArrayList<Action>()

    fun addAction(action: Action) {
        actions.add(action)
    }

    fun update(delta: Float) {
        val iterator = actions.iterator()

        while (iterator.hasNext()) {
            if (iterator.next().update(delta))
                iterator.remove()
        }
    }
}