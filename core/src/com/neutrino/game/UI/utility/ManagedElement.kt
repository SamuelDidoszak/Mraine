package com.neutrino.game.UI.utility

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane

data class ManagedElement(
    val pane: ScrollPane,
    val boundsActor: Actor,
    val type: ManagerType
)

enum class ManagerType {
    INVENTORY,
    EQUIPMENT,
    SKILLS
}