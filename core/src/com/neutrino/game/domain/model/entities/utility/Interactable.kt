package com.neutrino.game.domain.model.entities.utility

import com.neutrino.game.entities.shared.util.InteractionType

interface Interactable {
    val interactionList: List<InteractionType>

    fun getPrimaryInteraction(): InteractionType? {
        for (action in interactionList)
            if (action.isPrimary)
                return action
        return null
    }
}