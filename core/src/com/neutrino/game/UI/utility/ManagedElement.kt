package com.neutrino.game.UI.utility

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.neutrino.game.entities.characters.attributes.Equipment
import com.neutrino.game.entities.characters.attributes.Inventory

data class ManagedElement(
    val pane: ScrollPane,
    val boundsActor: Actor,
    val type: ManagerType
)

sealed class ManagerType {
    class INVENTORY(val inventory: Inventory): ManagerType()
    class EQUIPMENT(val equipment: Equipment): ManagerType()
    object SKILLS: ManagerType()
}