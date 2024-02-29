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
import com.neutrino.game.UI.popups.ItemContextPopup
import com.neutrino.game.UI.popups.SkillContextPopup
import com.neutrino.game.UI.utility.*
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.Items
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.characters.attributes.Inventory
import com.neutrino.game.entities.characters.attributes.util.InventoryElement
import com.neutrino.game.entities.items.attributes.Amount
import com.neutrino.game.entities.shared.attributes.DrawerAttribute
import com.neutrino.game.util.isIn
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
    private var originalInventory: com.neutrino.game.entities.characters.attributes.Inventory? = null
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

    private var displayedItem: Entity? = null
    private var detailsPopup: Actor? = null
    private var contextPopup: Actor? = null
    private val itemContextPopup = ItemContextPopup(uiStage.usedItemList, { item: Item -> uiStage.useItemOn = item})  {
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
                originalInventory = Player.get(com.neutrino.game.entities.characters.attributes.Inventory::class)
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

            if ((clickedItem as EqActor).maxStack != 1) {
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

                val amount = originalStackItem!!.amount + (clickedItem as EqActor).amount

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
                    if (originalStackItem?.amount == 0)
                        originalContainer?.actor = null
                    else
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
                            ManagerType.INVENTORY, ManagerType.EQUIPMENT -> {}
                                // TODO ECS ITEMS Create popups
//                                contextPopup = itemContextPopup.createContextMenu((hoveredActor as EqActor).entity, coord.x, coord.y)
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
                    if (hoveredItem != null && (hoveredItem as EqActor).entity != displayedItem) {
                        if (detailsPopup != null)
                            uiStage.actors.removeValue(detailsPopup, true)

                        // TODO ECS ITEMS Create popups
//                        val group = Group()
//                        val popup =
//                            if (hoveredItem.entity has com.neutrino.game.entities.items.attributes.EquipmentItem::class)
//                                EquipmentComparisonPopup(hoveredItem.entity)
//                            else
//                                ItemDetailsPopup(hoveredItem.entity)
//                        group.setSize(popup.width, popup.height)
//                        group.setScale(uiStage.currentScale)
//                        group.addActor(popup)
//                        detailsPopup = group
//                        detailsPopup!!.setPosition(coord.x, coord.y)
//                        displayedItem = hoveredItem.entity
//                        uiStage.addActor(detailsPopup)
//                        detailsPopup!!.setPosition(coord.x, coord.y)
                    }
                }
                ManagerType.SKILLS -> {}
            }
        }

        when (currentElement?.type) {
            ManagerType.INVENTORY -> {
                // delete or move the popup
                // TODO ECS ITEMS POPUP
//                if ((hoveredInv == null || hoveredItem == null)) {
//                    displayedItem = null
//                    uiStage.actors.removeValue(detailsPopup, true)
//                    detailsPopup = null
//                } else {
//                    detailsPopup!!.setPosition(coord.x, coord.y)
//                }
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
        // TODO ECS ITEMS Clone item for a new stack
//        val item = (clickedItem as EqActor).entity.clone() as Item
        val item = Items.new((clickedItem as EqActor).entity.id)
        originalStackItem = clickedItem as EqActor
        item.get(Amount::class)!!.amount = 0
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

        val totalAmount = (clickedItem as EqActor).amount + originalStackItem!!.amount
        (clickedItem as EqActor).amount = value
        originalStackItem!!.amount = totalAmount - value
        (clickedItem as EqActor).refreshAmount()
        originalStackItem!!.refreshAmount()
        if (originalStackItem!!.amount == 0)
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
        (clickedItem as EqActor).amount = (clickedItem as EqActor).amount.plus(value)
        originalStackItem!!.amount = originalStackItem!!.amount.minus(value)
        (clickedItem as EqActor).refreshAmount()
        originalStackItem!!.refreshAmount()
        if (originalStackItem!!.amount == 0)
            originalContainer!!.actor = null
    }

    private fun pickUpItem() {
        // Create a new stack of the item
        if (dragItem == true && clickedItem is EqActor && (clickedItem as EqActor).maxStack != 1) {
            originalContainer = clickedItem!!.parent as Container<*>
            createStack()
            setStackAmount(ceil(originalStackItem!!.amount / 2f).toInt())
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
                uiStage.itemDropList.add((clickedItem as EqActor).entity)
                // TODO change the remove implementation to this after adding the sorting and user defined positions
//                originalEq!!.itemList.removeAt(originalContainer!!.name.toInt())
                originalInventory!!.removeItem(originalInventory!!.getItem((clickedItem as EqActor).entity)!!)

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
        if (container == null || container.name?.toInt()!! >= Player.get(Inventory::class)!!.maxSize) {
            if (originalStackItem != null) {
                originalStackItem!!.amount = originalStackItem!!.amount.plus((clickedItem as EqActor).amount)
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
            // TODO ECS ITEMS Stack check if entity attributes are equal
            // OLD
//            if ((container.actor as EqActor).maxStack != 1 &&
//                (clickedItem as EqActor).entity.equalsIdentical((container.actor as EqActor).entity)) {
            if ((container.actor as EqActor).maxStack != 1 && (clickedItem as EqActor).entity.id == (container.actor as EqActor).entity.id) {
                (container.actor as EqActor).amount =
                    (container.actor as EqActor).amount.plus((clickedItem as EqActor).amount)
                originalInventory!!.getItem((clickedItem as EqActor).entity)?.let { originalInventory!!.removeItem(it) }

                // Remove empty stack
                if (originalStackItem != null) {
                    val stackedItem = originalInventory!!.getItem(originalStackItem!!.entity)
                    if (stackedItem?.get(Amount::class)!!.amount == 0) {
                        originalInventory!!.removeItem(stackedItem)
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
                originalStackItem!!.amount = originalStackItem!!.amount + (clickedItem as EqActor).amount
                originalStackItem!!.refreshAmount()
                uiStage.actors.removeValue(clickedItem, true)
                uiStage.refreshHotBar()
                return true
            } else
                originalContainer!!.actor = container.actor as EqActor
        }
        // Creates a new item in inventory out of the stack
        if (originalStackItem != null) {
            val stackedItem = originalInventory!!.getElement(originalStackItem!!.entity)
            // Create a new item or move the existing one while preserving references
            if (stackedItem!!.item.get(Amount::class)!!.amount != 0) {
//                originalInventory!!.add((clickedItem as EqActor).entity, stackedItem.dateAdded)
                originalInventory!!.add(InventoryElement((clickedItem as EqActor).entity, stackedItem.dateAdded, container.name.toInt()))
                container.actor = clickedItem
            }
            else {
                originalStackItem!!.amount = originalStackItem!!.amount + (clickedItem as EqActor).amount
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
        if (clickedChild is Table || clickedChild == null)
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
            changeStackAmount((clickedItem as EqActor).amount * -1)
            uiStage.actors.removeValue(clickedItem, true)
            clickedItem = originalStackItem
            originalStackItem = null
        }
        if (clickedItem != null && originalContainer != null)
            originalContainer!!.actor = clickedItem
        else
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
            changeStackAmount((clickedItem as EqActor).amount * -1)
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