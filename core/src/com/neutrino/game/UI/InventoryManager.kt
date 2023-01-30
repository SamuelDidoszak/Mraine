package com.neutrino.game.UI

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.TimeUtils
import com.neutrino.game.UI.popups.EquipmentComparisonPopup
import com.neutrino.game.UI.popups.ItemContextPopup
import com.neutrino.game.UI.popups.ItemDetailsPopup
import com.neutrino.game.UI.popups.SkillContextPopup
import com.neutrino.game.UI.utility.*
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.items.EquipmentItem
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.utility.EqElement
import com.neutrino.game.domain.model.items.utility.Inventory
import com.neutrino.game.isIn
import kotlin.math.ceil

class InventoryManager(private val uiStage: UiStage) {

    val elements: ArrayList<ManagedElement> = ArrayList(2)
    private var currentElement: ManagedElement? = null

    fun setElement(group: Group) {
        if (group == uiStage.inventory)
            currentElement = elements[0]
        if (group == uiStage.skills)
            currentElement = elements[1]
    }
    
    private var originalContainer: Container<*>? = null
    private var originalInventory: Inventory? = null
    // possibly change to EqActor
    var clickedItem: Actor? = null
    private var dragItem: Boolean? = null
    private var draggingStacking: Boolean = false
    /** Original item split into a stack. Null signifies that no stack was taken */
    var originalStackItem: EqActor? = null
    /** Value decreases when dragging upwards and decreases when dragging downwards */
    private var previousDragPosition: Int = 2137
    // required for drag and drop
    private var timeClicked: Long = 0

    private var displayedItem: Item? = null
    private var detailsPopup: Actor? = null
    private var contextPopup: Actor? = null
    private val itemContextPopup = ItemContextPopup(uiStage.usedItemList) {
        uiStage.showInventory = false
        nullifyAllValues()
        uiStage.nullifyAllValues()
    }

    /**
     * @return 0 if false, 1 if true, -1 if passed the whole function
     */
    fun touchDown(coord: Vector2, pointer: Int, button: Int, downSuper: () -> Boolean): Int {
        // Sets the click behavior for the item drop
        if (clickedItem != null) {
            timeClicked = TimeUtils.millis()
            return if (downSuper.invoke()) 1 else 0
        }
        
        // gets the eq ui and sets the originalEq
        currentElement = getInvClicked(coord.x, coord.y) ?: currentElement
        when (currentElement?.pane) {
            null -> return -1
            uiStage.inventory -> {
                originalInventory = Player.inventory
            }
            uiStage.skills.skillTable -> {

            }
        }
        // Sets the clicked item for drag handling
        clickedItem = getInventoryCell(coord.x, coord.y, currentElement!!.pane)?.actor
        if (clickedItem != null)
            timeClicked = TimeUtils.millis()
        return -1
    }

    /**
     * @return 0 if false, 1 if true, -1 if passed the whole function
     */
    fun touchDragged(screenX: Int, screenY: Int, coord: Vector2, pointer: Int, draggedSuper: () -> Boolean): Int {
        if (clickedItem == null)
            return 0

        if (dragItem == null && originalContainer == null) {
            dragItem = TimeUtils.millis() - timeClicked >= 450
            if (dragItem!!) {
                pickUpItem()

                if (detailsPopup != null)
                    uiStage.actors.removeValue(detailsPopup, true)
            }
        }

        if (dragItem == true) {
            clickedItem!!.setPosition(coord.x - (clickedItem!! as PickupActor).ogWidth / 2, coord.y - (clickedItem!! as PickupActor).ogHeight / 2)
        }

        // Adjust the stack of an item
        if (dragItem != true) {
            // Initialize the value
            if (!draggingStacking) {
                previousDragPosition = screenY
                draggingStacking = true
            }

            if (clickedItem !is EqActor)
                return -1

            if ((clickedItem as EqActor).item.amount != null) {
                var dragStrength = (previousDragPosition - screenY)

                // threshold
                if (dragStrength in -30 .. 30 && originalStackItem == null)
                    return -1

                if (originalStackItem == null) {
                    if (originalContainer == null) {
                        if (detailsPopup != null)
                            uiStage.actors.removeValue(detailsPopup, true)

                        originalContainer = clickedItem!!.parent as Container<*>
                        createStack()
                        originalContainer!!.actor = null
                        clickedItem!!.setPosition(coord.x - (clickedItem!! as PickupActor).ogWidth * (uiStage.currentScale * 1.25f) / 2 - 6 * uiStage.currentScale,
                            coord.y - (clickedItem!! as PickupActor).ogHeight * (uiStage.currentScale * 1.25f) / 2 - 9 * uiStage.currentScale)

                    }
                    else {
                        createStack()
                        originalContainer!!.actor = originalStackItem
                        originalStackItem!!.setScale(1f, 1f)
                    }
                    dragStrength = 0
                }

                val amount = originalStackItem!!.item.amount!! + (clickedItem as EqActor).item.amount!!

                var stackAmount = ((dragStrength / -125f) * amount).toInt()
                if (stackAmount < 1)
                    stackAmount = 1
                if (stackAmount >= amount - 1)
                    stackAmount = amount - 1

                setStackAmount(amount - stackAmount)
            }
            return -1
        }
        return -1
    }

    /**
     * @return -1 if method passed to the finish, 0 if false should be returned, 1 if true should be returned
     */
    fun touchUp(coord: Vector2, button: Int, touchSuper: () -> Boolean): Int {
        // Stop contextMenu click from propagating
        if (contextPopup != null && button == Input.Buttons.LEFT) {
            val clickedInMenu = touchSuper.invoke()

            if (clickedItem != null) {
                clickedItem = null
                originalInventory = null
            }
            uiStage.actors.removeValue(contextPopup, true)
            contextPopup = null
            return if (clickedInMenu) 1 else 0
        }

        if (button == Input.Buttons.LEFT) {
            if (detailsPopup != null)
                uiStage.actors.removeValue(detailsPopup, true)

            if (draggingStacking) {
                if (originalContainer?.actor == null && clickedItem is EqActor) {
                    originalContainer?.actor = originalStackItem
                    originalStackItem?.setScale(1f, 1f)
                }
                if (originalContainer == null && !itemFromHud)
                    pickUpItem()

                clickedItem!!.setPosition(coord.x - (clickedItem!! as PickupActor).ogWidth * (uiStage.currentScale * 1.25f) / 2 - 6 * uiStage.currentScale,
                    coord.y - (clickedItem!! as PickupActor).ogHeight * (uiStage.currentScale * 1.25f) / 2 - 9 * uiStage.currentScale)
                draggingStacking = false
            }

            if (dragItem == true) {
                parseItemDrop(coord.x, coord.y)
                clickedItem = null
                originalContainer = null
                dragItem = null
                originalStackItem = null
            }

            // Dropping the clicked item
            if (originalContainer != null && clickedItem != null && TimeUtils.millis() - timeClicked <= 200) {
                if (!parseItemDrop(coord.x, coord.y))
                    return -1
                clickedItem = null
                originalContainer = null
                originalStackItem = null
                dragItem = null
            }

            // The item was clicked
            if (!itemFromHud && clickedItem != null && TimeUtils.millis() - timeClicked <= 200) {
                pickUpItem()

                clickedItem!!.setPosition(coord.x - (clickedItem!! as PickupActor).ogWidth * (uiStage.currentScale * 1.25f) / 2 - 6 * uiStage.currentScale,
                    coord.y - (clickedItem!! as PickupActor).ogHeight * (uiStage.currentScale * 1.25f) / 2 - 9 * uiStage.currentScale)
                return if (touchSuper.invoke()) 1 else 0
            }
        }
        else if (button == Input.Buttons.RIGHT && clickedItem == null) {
            if (contextPopup != null) {
                uiStage.actors.removeValue(contextPopup, true)
                contextPopup = null
            }
            else {
                val hoveredInv = getInvClicked(coord.x, coord.y)
                if (hoveredInv != null) {
                    val hoveredActor: Actor? = getInventoryCell(coord.x, coord.y, hoveredInv.pane)?.actor
                    if (hoveredActor != null) {
                        when (hoveredInv.type) {
                            ManagerType.INVENTORY, ManagerType.EQUIPMENT ->
                                contextPopup = itemContextPopup.createContextMenu((hoveredActor as EqActor).item, coord.x, coord.y)
                            ManagerType.SKILLS -> {
                                val skill = (hoveredActor as SkillActor).skill
                                contextPopup = SkillContextPopup(skill, coord.x, coord.y) {
                                    uiStage.usedSkill = skill
                                    uiStage.showInventory = false
                                    clickedItem = null
                                    nullifyAllValues()
                                    uiStage.inventoryManager.nullifyAllValues()
                                }
                            }
                        }
                        if (contextPopup != null) {
                            if (detailsPopup != null)
                                uiStage.actors.removeValue(detailsPopup, true)
                            uiStage.addActor(contextPopup)
                            contextPopup?.setPosition(coord.x, coord.y)
                        }
                    }
                }
            }
        }
        if (itemFromHud)
            itemPassedToHud()

        return -1
    }

    fun mouseMoved(coord: Vector2) {
        // create new popup
        val hoveredInv = getInvClicked(coord.x, coord.y)
        var hoveredItem: Actor? = null
        if (hoveredInv != null && contextPopup == null) {
            hoveredItem = getInventoryCell(coord.x, coord.y, hoveredInv.pane)?.actor
            when (hoveredInv.type) {
                ManagerType.INVENTORY, ManagerType.EQUIPMENT -> {
                    if (hoveredItem != null && (hoveredItem as EqActor).item != displayedItem) {
                        if (detailsPopup != null)
                            uiStage.actors.removeValue(detailsPopup, true)

                        val group = Group()
                        val popup =
                            if (hoveredItem.item is EquipmentItem)
                                EquipmentComparisonPopup(hoveredItem.item as EquipmentItem)
                            else
                                ItemDetailsPopup(hoveredItem.item)
                        group.setSize(popup.width, popup.height)
                        group.setScale(uiStage.currentScale)
                        group.addActor(popup)
                        detailsPopup = group
                        detailsPopup!!.setPosition(coord.x, coord.y)
                        displayedItem = hoveredItem.item
                        uiStage.addActor(detailsPopup)
                        detailsPopup!!.setPosition(coord.x, coord.y)
                    }
                }
                ManagerType.SKILLS -> {}
            }
        }

        when (currentElement?.type) {
            ManagerType.INVENTORY -> {
                // delete or move the popup
                if ((hoveredInv == null || hoveredItem == null)) {
                    displayedItem = null
                    uiStage.actors.removeValue(detailsPopup, true)
                    detailsPopup = null
                } else {
                    detailsPopup!!.setPosition(coord.x, coord.y)
                }
            }
            ManagerType.SKILLS -> {
                if (clickedItem == null)
                    uiStage.skills.showSkillDetails((hoveredItem as SkillActor?)?.skill)
            }
            else -> {}
        }

        // TODO pass item from hud
//              // check if there is an item in hud
//              if (hudStage.clickedItem != null) {
//                  clickedItem = hudStage.clickedItem
//                  actors.add(clickedItem)
//                  hudStage.passedItemToUi()
//              }

        if (clickedItem != null && (dragItem == true || originalContainer != null))
            clickedItem!!.setPosition(coord.x - (clickedItem!! as PickupActor).ogWidth * (uiStage.currentScale * 1.25f) / 2 - 6 * uiStage.currentScale,
                coord.y - (clickedItem!! as PickupActor).ogHeight * (uiStage.currentScale * 1.25f) / 2 - 9 * uiStage.currentScale)
    }

    /** ======================================================================================================================================================
                                                                    Item handling
    */

    private fun createStack() {
        val itemPosition = Vector2(clickedItem!!.x, clickedItem!!.y)
        val item = (clickedItem as EqActor).item.clone() as Item
        originalStackItem = clickedItem as EqActor
        item.amount = 0
        clickedItem = EqActor(item)
        uiStage.addActor(clickedItem)
        clickedItem!!.setPosition(itemPosition.x, itemPosition.y)
        clickedItem!!.setScale(uiStage.currentScale * 1.25f, uiStage.currentScale * 1.25f)
    }

    /**
     * Sets the clickedItem amount to the provided value
     * clickedItem becomes a new stack with passed value
     * If the original stack becomes 0, originalStackContainer actor is removed
     */
    private fun setStackAmount(value: Int) {
        if (originalStackItem == null)
            return

        val totalAmount = (clickedItem as EqActor).item.amount!! + originalStackItem!!.item.amount!!
        (clickedItem as EqActor).item.amount = value
        originalStackItem!!.item.amount = totalAmount - value
        (clickedItem as EqActor).refreshAmount()
        originalStackItem!!.refreshAmount()
        if (originalStackItem!!.item.amount == 0)
            originalContainer!!.actor = null
    }

    /**
     * Subtracts stack amount from the originalStackItem
     * clickedItem becomes a new stack with passed value
     * If the original stack becomes 0, originalStackContainer actor is removed
     */
    private fun changeStackAmount(value: Int) {
        if (originalStackItem == null)
            return
        (clickedItem as EqActor).item.amount = (clickedItem as EqActor).item.amount!!.plus(value)
        originalStackItem!!.item.amount = originalStackItem!!.item.amount!!.minus(value)
        (clickedItem as EqActor).refreshAmount()
        originalStackItem!!.refreshAmount()
        if (originalStackItem!!.item.amount == 0)
            originalContainer!!.actor = null
    }

    private fun pickUpItem() {
        // Create a new stack of the item
        if (dragItem == true && clickedItem is EqActor && (clickedItem as EqActor).item.amount != null) {
            originalContainer = clickedItem!!.parent as Container<*>
            createStack()
            setStackAmount(ceil(originalStackItem!!.item.amount!! / 2f).toInt())
            return
        }

        originalContainer = clickedItem!!.parent as Container<*>
        originalContainer!!.removeActor(clickedItem)
        uiStage.addActor(clickedItem)
        clickedItem!!.setScale(uiStage.currentScale * 1.25f, uiStage.currentScale * 1.25f)
    }

    /**
     * Interprets where the item was dropped.
     * Either drops it out of the inventory, adds to a different inventory, makes a new stack or combines stacks
     */
    private fun parseItemDrop(x: Float, y: Float): Boolean {
        if (clickedItem == null)
            return false

        val clickedInv = getInvClicked(x, y)

        if (currentElement?.type == ManagerType.SKILLS) {
            clickedItem!!.setScale(1f, 1f)
            if (clickedInv == null) {
                originalContainer!!.actor = clickedItem
                return true
            }
            val container = getInventoryCell(x, y, clickedInv!!.pane)
            if (container == null) {
                originalContainer!!.actor = clickedItem
                return true

            }

            uiStage.actors.removeValue(clickedItem, true)

            if (container.hasChildren())
                originalContainer!!.actor = container.actor as SkillActor

            container.actor = clickedItem
            return true
        }

        val isEqActor = currentElement?.type == ManagerType.INVENTORY || currentElement?.type == ManagerType.EQUIPMENT
        if (!isEqActor)
            return false

        if (clickedInv == null) {
            // Dropping the item
            if (clickedItem != null) {
                uiStage.itemDropList.add((clickedItem as EqActor).item)
                // TODO change the remove implementation to this after adding the sorting and user defined positions
//                originalEq!!.itemList.removeAt(originalContainer!!.name.toInt())
                originalInventory!!.itemList.remove(
                    originalInventory!!.itemList.find { it.item == (clickedItem as EqActor).item }
                )

                clickedItem!!.addAction(Actions.scaleTo(0f, 0f, 0.35f))
                clickedItem!!.addAction(Actions.moveBy(32f * uiStage.currentScale, 32f * uiStage.currentScale, 0.35f))
                clickedItem!!.addAction(
                    Actions.sequence(
                    Actions.fadeOut(0.35f),
                    Actions.removeActor()
                ))
                // Refresh hotBar after dropping the item
                uiStage.refreshHotBar()
            }
            return true
        }
        clickedItem!!.setScale(1f, 1f)
        // if the area between cells was clicked, reset the item position
        val container = getInventoryCell(x, y, clickedInv.pane)
        // TODO checking the Player inventorySize here can cause bugs when other inventories will be displayed
        if (container == null || container.name?.toInt()!! >= Player.inventory.size) {
            if (originalStackItem != null) {
                originalStackItem!!.item.amount = originalStackItem!!.item.amount?.plus((clickedItem as EqActor).item.amount!!)
                originalStackItem!!.refreshAmount()
                uiStage.actors.removeValue(clickedItem, true)
                return true
            }
            originalContainer!!.actor = clickedItem
            return true
        }

        uiStage.actors.removeValue(clickedItem, true)
        // check if the cell is ocupied and act accordingly
        if (container.hasChildren()) {
            // Item dropped onto identical stackable item
            // sum item amounts
            if ((container.actor as EqActor).item.amount != null &&
                (clickedItem as EqActor).item.equalsIdentical((container.actor as EqActor).item)) {
                (container.actor as EqActor).item.amount =
                    (container.actor as EqActor).item.amount?.plus((clickedItem as EqActor).item.amount!!)
                originalInventory!!.itemList.remove(
                    originalInventory!!.itemList.find { it.item == (clickedItem as EqActor).item })

                // Remove empty stack
                if (originalStackItem != null) {
                    val stackedItem = originalInventory!!.itemList.find { it.item == originalStackItem!!.item }
                    if (stackedItem?.item?.amount == 0) {
                        originalInventory!!.itemList.remove(stackedItem)
                    }
                }

                (container.actor as EqActor).refreshAmount()
                uiStage.refreshHotBar()
                return true
                // Item was dropped onto another item, position is reset
            } else if (originalStackItem != null) {
                // If original stack is 0 and container's actor was removed, create a new one
                if (originalContainer?.actor == null)
                    originalContainer?.actor = originalStackItem
                originalStackItem!!.item.amount = originalStackItem!!.item.amount?.plus((clickedItem as EqActor).item.amount!!)
                originalStackItem!!.refreshAmount()
                uiStage.actors.removeValue(clickedItem, true)
                uiStage.refreshHotBar()
                return true
            } else
                originalContainer!!.actor = container.actor as EqActor
        }
        // Creates a new item in inventory out of the stack
        if (originalStackItem != null) {
            val stackedItem = originalInventory!!.itemList.find { it.item == originalStackItem!!.item }
            // Create a new item or move the existing one while preserving references
            if (stackedItem!!.item.amount != 0) {
                originalInventory!!.itemList.add(EqElement((clickedItem as EqActor).item, stackedItem.dateAdded))
                container.actor = clickedItem
            }
            else {
                originalStackItem!!.item.amount = originalStackItem!!.item.amount?.plus((clickedItem as EqActor).item.amount!!)
                originalStackItem?.refreshAmount()
                container.actor = originalStackItem
            }

            uiStage.refreshHotBar()
            return true
        }
        container.actor = clickedItem
        return true
    }

    /** Returns the eq ui and sets the originalEq */
    private fun getInvClicked(x: Float, y: Float): ManagedElement? {
        for (inventory in elements) {
            val coord = inventory.boundsActor.stageToLocalCoordinates(Vector2(x, y))
            val pane = inventory.pane
            if (!pane.isVisible || !inventory.boundsActor.isVisible)
                continue
            if (pane.name == "skillTable") {
                if (pane.isIn(coord.x, coord.y))
                    return inventory
            }

            if (coord.x in pane.x .. pane.x + pane.width * uiStage.currentScale - 1 &&
                coord.y in pane.y .. pane.y + pane.height * uiStage.currentScale - 1)
                return inventory
        }
        return null
    }

    private fun getInventoryCell(x: Float, y: Float, clickedEq: ScrollPane): Container<*>? {
        var coord: Vector2 = clickedEq.stageToLocalCoordinates(
            Vector2(x, y)
        )

        if (clickedEq.name == "skillTable")
            coord = uiStage.skills.stageToLocalCoordinates(Vector2(x, y))

        var clickedChild = clickedEq.hit(coord.x, coord.y, false)

        // space between cells was hit
        if (clickedChild is Table)
            return null

        // Skills require it for some reason
        if (clickedChild !is Container<*>)
            return null

        while (clickedChild !is Container<*>) {
            clickedChild = clickedChild.parent
        }
        return clickedChild
    }

    /** ======================================================================================================================================================
                                                                    Cleanup and pass to HUD
    */

    /** Sets all values to null */
    fun nullifyAllValues() {
        if (originalStackItem != null && clickedItem != null) {
            changeStackAmount((clickedItem as EqActor).item.amount!! * -1)
            uiStage.actors.removeValue(clickedItem, true)
            clickedItem = originalStackItem
            originalStackItem = null
        }
        clickedItem?.addAction(Actions.removeActor())
        clickedItem = null
        originalContainer = null
        originalInventory = null
        dragItem = null
        originalStackItem = null
        draggingStacking = false
        itemFromHud = false

        if (detailsPopup != null) {
            uiStage.actors.removeValue(detailsPopup, true)
            detailsPopup = null
        }

        displayedItem = null
        if (contextPopup != null) {
            uiStage.actors.removeValue(contextPopup, true)
            contextPopup = null
        }
    }

    fun itemPassedToHud() {
        if (originalStackItem != null) {
            changeStackAmount((clickedItem as EqActor).item.amount!! * -1)
            uiStage.actors.removeValue(clickedItem, true)
            clickedItem = originalStackItem
            originalStackItem = null
        }
        uiStage.actors.removeValue(clickedItem, true)
        originalContainer?.actor = clickedItem
        clickedItem = null
        originalContainer = null
        originalInventory = null
        dragItem = null
        draggingStacking = false
        itemFromHud = false
        timeClicked = 0 // TODO delete after making touchDragged working as intended
    }

    fun itemFromHud(actor: Actor) {
        clickedItem = actor
        itemFromHud = true
        dragItem = false
        uiStage.addActor(clickedItem)
        clickedItem!!.setScale(1.25f)
    }
    var itemFromHud: Boolean = false
}