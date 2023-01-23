package com.neutrino.game.UI

import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.TimeUtils
import com.neutrino.game.UI.popups.ItemContextPopup
import com.neutrino.game.UI.popups.ItemDetailsPopup
import com.neutrino.game.UI.utility.EqActor
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.utility.EqElement
import com.neutrino.game.domain.model.items.utility.Inventory
import kotlin.math.ceil
import kotlin.math.sign

class InventoryManager(private val uiStage: UiStage) {

    val inventories: ArrayList<ScrollPane> = ArrayList(2)
    
    private var originalContainer: Container<*>? = null
    private var originalInventory: Inventory? = null
    // possibly change to EqActor
    var clickedItem: Actor? = null
    private var dragItem: Boolean? = null
    /** Original item split into a stack. Null signifies that no stack was taken */
    var originalStackItem: EqActor? = null
    /** Value decreases when dragging upwards and decreases when dragging downwards */
    var previousDragPosition: Int = -2137
    // required for drag and drop
    var timeClicked: Long = 0

    private var displayedItem: Item? = null
    private var detailsPopup: Table? = null
    private var contextPopup: Table? = null
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
        val clickedInv = getInvClicked(coord.x, coord.y)
        if (clickedInv != null) {
            if (clickedInv.name == "inventory")
                originalInventory = Player.inventory
            // Sets the clicked item for drag handling
            clickedItem = getInventoryCell(coord.x, coord.y, clickedInv)?.actor
            if (clickedItem != null)
                timeClicked = TimeUtils.millis()
        }
        return -1
    }

    /**
     * @return 0 if false, 1 if true, -1 if passed the whole function
     */
    fun touchDragged(screenX: Int, screenY: Int, coord: Vector2, pointer: Int, draggedSuper: () -> Boolean): Int {
        if (clickedItem == null)
            return 0

        if (dragItem == null && originalContainer == null) {
            dragItem = TimeUtils.millis() - timeClicked >= 650
            if (dragItem!!)
                pickUpItem()
        }

        if (dragItem == true) {
            if (detailsPopup != null)
                uiStage.actors.removeValue(detailsPopup, true)

            clickedItem!!.setPosition(coord.x - (clickedItem!! as EqActor).item.texture.regionWidth * 2, coord.y - (clickedItem!! as EqActor).item.texture.regionHeight * 2)
            return 1
        }

        // TODO change this behavior to manage amount sizes
        //      dragItem false means that click-hold was quick. Null means that an item was previously picked up

        // An item was previously picked up. Adjust its stack size
        if (dragItem == null) {
            // Initialize the value
            if (previousDragPosition == -2137) {
                previousDragPosition = screenY
                return if (draggedSuper.invoke()) 1 else 0
            }
            // Negative values mean upwards drag, positive downwards
            val dragStrength = previousDragPosition - screenY

            if ((clickedItem as EqActor).item.amount != null) {
                println((clickedItem as EqActor).item.amount!! - dragStrength.sign)
            }
            return -1
        }

        if (dragItem != true) {
            if (originalContainer != null) {
                clickedItem!!.setScale(1f, 1f)
                originalContainer!!.actor = clickedItem
                itemPassedToHud()
            }
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

            if (dragItem == true) {
                parseItemDrop(coord.x, coord.y)
                clickedItem = null
                originalContainer = null
                dragItem = null
                originalStackItem = null
            }
            // TODO potential bugs i guess
            else if (dragItem == false) {
                clickedItem = null
                dragItem = null
            }

            // Dropping the clicked item
            if (originalContainer != null && clickedItem != null && TimeUtils.millis() - timeClicked <= 200) {
                parseItemDrop(coord.x, coord.y)
                clickedItem = null
                originalContainer = null
            }

            // The item was clicked
            if (clickedItem != null && TimeUtils.millis() - timeClicked <= 200) {
                pickUpItem()
                clickedItem!!.setPosition(coord.x - (clickedItem!! as EqActor).item.texture.regionWidth * (4f * uiStage.currentScale * 1.25f) / 2 - 6 * uiStage.currentScale,
                    coord.y - (clickedItem!! as EqActor).item.texture.regionHeight * (4f * uiStage.currentScale * 1.25f) / 2 - 9 * uiStage.currentScale)
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
                    val hoveredItem: Actor? = getInventoryCell(coord.x, coord.y, hoveredInv)?.actor
                    if (hoveredItem != null) {
                        contextPopup = itemContextPopup.createContextMenu((hoveredItem as EqActor).item, coord.x, coord.y)
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
        return -1
    }

    fun mouseMoved(coord: Vector2) {
        // create new popup
        val hoveredInv = getInvClicked(coord.x, coord.y)
        var hoveredItem: Actor? = null
        if (hoveredInv != null && contextPopup == null) {
            hoveredItem = getInventoryCell(coord.x, coord.y, hoveredInv)?.actor
            if (hoveredItem != null && (hoveredItem as EqActor).item != displayedItem) {
                if (detailsPopup != null)
                    uiStage.actors.removeValue(detailsPopup, true)
                detailsPopup = ItemDetailsPopup(hoveredItem.item, true)
                detailsPopup!!.setPosition(coord.x, coord.y)
                displayedItem = hoveredItem.item
                uiStage.addActor(detailsPopup)
                detailsPopup!!.setPosition(coord.x, coord.y)
                (detailsPopup!! as ItemDetailsPopup).assignBg(coord.x, coord.y)
            }
        }

        // delete or move the popup
        if (hoveredInv == null || hoveredItem == null) {
            displayedItem = null
            uiStage.actors.removeValue(detailsPopup, true)
            detailsPopup = null
        } else {
            detailsPopup!!.setPosition(coord.x, coord.y)
        }

        // TODO pass item from hud
//              // check if there is an item in hud
//              if (hudStage.clickedItem != null) {
//                  clickedItem = hudStage.clickedItem
//                  actors.add(clickedItem)
//                  hudStage.passedItemToUi()
//              }

        if (clickedItem != null)
            clickedItem!!.setPosition(coord.x - (clickedItem!! as EqActor).item.texture.regionWidth * (4f * uiStage.currentScale * 1.25f) / 2 - 6 * uiStage.currentScale,
                coord.y - (clickedItem!! as EqActor).item.texture.regionHeight * (4f * uiStage.currentScale * 1.25f) / 2 - 9 * uiStage.currentScale)
    }

    /** ======================================================================================================================================================
                                                                    Item handling
    */

    private fun pickUpItem() {
        // Create a new stack of the item
        if (dragItem == true && (clickedItem as EqActor).item.amount != null) {
            originalContainer = clickedItem!!.parent as Container<*>
            val item = (clickedItem as EqActor).item.clone() as Item
            originalStackItem = clickedItem as EqActor
            item.amount = 0
            clickedItem = EqActor(item)
            changeStackAmount(ceil(originalStackItem!!.item.amount!! / 2f).toInt())
            uiStage.addActor(clickedItem)
            clickedItem!!.setScale(uiStage.currentScale * 1.25f, uiStage.currentScale * 1.25f)
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
    private fun parseItemDrop(x: Float, y: Float) {
        if (clickedItem == null)
            return

        val clickedInv = getInvClicked(x, y)
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
            return
        }
        clickedItem!!.setScale(1f, 1f)
        // if the area between cells was clicked, reset the item position
        val container = getInventoryCell(x, y, clickedInv)
        // TODO checking the Player inventorySize here can cause bugs when other inventories will be displayed
        if (container == null || container.name?.toInt()!! >= Player.inventory.size) {
            if (originalStackItem != null) {
                originalStackItem!!.item.amount = originalStackItem!!.item.amount?.plus((clickedItem as EqActor).item.amount!!)
                originalStackItem!!.refreshAmount()
                uiStage.actors.removeValue(clickedItem, true)
                return
            }
            originalContainer!!.actor = clickedItem
            return
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
                return
                // Item was dropped onto another item, position is reset
            } else if (originalStackItem != null) {
                // If original stack is 0 and container's actor was removed, create a new one
                if (originalContainer?.actor == null)
                    originalContainer?.actor = originalStackItem
                originalStackItem!!.item.amount = originalStackItem!!.item.amount?.plus((clickedItem as EqActor).item.amount!!)
                originalStackItem!!.refreshAmount()
                uiStage.actors.removeValue(clickedItem, true)
                uiStage.refreshHotBar()
                return
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
            return
        }
        container.actor = clickedItem
    }

    /** Returns the eq ui and sets the originalEq */
    private fun getInvClicked(x: Float, y: Float): ScrollPane? {
        val inPlayerInventory = (x in inventories[0].x .. inventories[0].x + inventories[0].width * uiStage.currentScale - 1 &&
                y in inventories[0].y .. inventories[0].y + inventories[0].height * uiStage.currentScale - 1)
        if (inPlayerInventory) {
            return inventories[0]
        }
        else
            return null
    }

    private fun getInventoryCell(x: Float, y: Float, clickedEq: ScrollPane): Container<*>? {
        val coord: Vector2 = clickedEq.stageToLocalCoordinates(
            Vector2(x, y)
        )

        var clickedChild = clickedEq.hit(coord.x, coord.y, false)

        // space between cells was hit
        if (clickedChild is Table)
            return null

        while (clickedChild !is Container<*>) {
            clickedChild = clickedChild.parent
        }
        return clickedChild
    }

    /**
     * clickedItem becomes a new stack with passed value
     * Subtracts stack amount from the originalStackItem
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

    /** ======================================================================================================================================================
                                                                    Cleanup and pass to HUD
    */

    /** Sets all values to null */
    fun nullifyAllValues() {
        if (originalStackItem != null) {
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
        timeClicked = 0 // TODO delete after making touchDragged working as intended
    }
}