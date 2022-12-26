package com.neutrino.game.domain.model.entities.utility

interface Interactable {
    val interactionList: List<Interaction>

    fun getPrimaryInteraction(): Interaction? {
        for (action in interactionList)
            if (action.isPrimary)
                return action
        return null
    }
}