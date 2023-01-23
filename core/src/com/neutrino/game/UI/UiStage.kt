package com.neutrino.game.UI

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.utils.viewport.Viewport
import com.neutrino.GlobalData
import com.neutrino.GlobalDataObserver
import com.neutrino.GlobalDataType
import com.neutrino.HudStage
import com.neutrino.game.*
import com.neutrino.game.UI.UIelements.Equipment
import com.neutrino.game.UI.UIelements.Skills
import com.neutrino.game.UI.UIelements.Tabs
import com.neutrino.game.UI.popups.ItemContextPopup
import com.neutrino.game.UI.popups.ItemDetailsPopup
import com.neutrino.game.UI.popups.SkillContextPopup
import com.neutrino.game.UI.utility.EqActor
import com.neutrino.game.UI.utility.SkillActor
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.utility.EqElement
import com.neutrino.game.domain.model.items.utility.Inventory
import com.neutrino.game.domain.model.systems.skills.Skill
import kotlin.math.ceil
import kotlin.math.sign


class UiStage(viewport: Viewport, private val hudStage: HudStage): Stage(viewport) {

    /** FIFO of dropped items */
    val itemDropList: ArrayDeque<Item> = ArrayDeque()

    /** FIFO of used item actions */
    val usedItemList: ArrayDeque<Item> = ArrayDeque()

    var showInventory: Boolean = true

    /**
     * Skill used from UI
     */
    var usedSkill: Skill? = null

    // UI elements
    private val uiAtlas = Constants.DefaultUITexture
    private val uiElements: Map<String, TextureAtlas.AtlasRegion> = mapOf(
        "BottomBar" to uiAtlas.findRegion("BottomBar"),
        "InventoryBorder" to uiAtlas.findRegion("InventoryBorder"),
        "EquipmentScreen" to uiAtlas.findRegion("EquipmentScreen"),
        "Background" to uiAtlas.findRegion("Background"),
        "EquipmentClosed" to uiAtlas.findRegion("EquipmentClosed"),
        "EquipmentOpen" to uiAtlas.findRegion("EquipmentOpen"),
        "InventoryClosed" to uiAtlas.findRegion("InventoryClosed"),
        "InventoryOpen" to uiAtlas.findRegion("InventoryOpen"),
        "SkillsClosed" to uiAtlas.findRegion("SkillsClosed"),
        "SkillsOpen" to uiAtlas.findRegion("SkillsOpen"),
        "QuestsClosed" to uiAtlas.findRegion("QuestsClosed"),
        "QuestsOpen" to uiAtlas.findRegion("QuestsOpen"),
        "MapClosed" to uiAtlas.findRegion("MapClosed"),
        "MapOpen" to uiAtlas.findRegion("MapOpen"),
            "SortingClosed" to uiAtlas.findRegion("SortingClosed"),
            "SortingClosedCentered" to uiAtlas.findRegion("SortingClosedCentered"),
        "SortingOpen" to uiAtlas.findRegion("SortingOpen"),
        "SortingCustom" to uiAtlas.findRegion("SortingCustom"),
        "SortingCustomOpen" to uiAtlas.findRegion("SortingCustomOpen"),
        "SortingType" to uiAtlas.findRegion("SortingType"),
        "SortingTypeOpen" to uiAtlas.findRegion("SortingTypeOpen"),
        "SortingValue" to uiAtlas.findRegion("SortingValue"),
        "SortingValueOpen" to uiAtlas.findRegion("SortingValueOpen"),
        "SortingDate" to uiAtlas.findRegion("SortingDate"),
        "SortingDateOpen" to uiAtlas.findRegion("SortingDateOpen"),
        "SortingAsc" to uiAtlas.findRegion("SortingAsc"),
        "SortingDesc" to uiAtlas.findRegion("SortingDesc"),
        "cellTopLeft" to uiAtlas.findRegion("cellTopLeft"),
        "cellTop" to uiAtlas.findRegion("cellTop"),
        "cellTopRight" to uiAtlas.findRegion("cellTopRight"),
        "cellLeft" to uiAtlas.findRegion("cellLeft"),
        "cellMiddle" to uiAtlas.findRegion("cellMiddle"),
        "cellRight" to uiAtlas.findRegion("cellRight"),
        "cellBottomLeft" to uiAtlas.findRegion("cellBottomLeft"),
        "cellBottom" to uiAtlas.findRegion("cellBottom"),
        "cellBottomRight" to uiAtlas.findRegion("cellBottomRight"),
        "cellUnavailable" to uiAtlas.findRegion("cellUnavailable"),
        "equipmentDefault" to uiAtlas.findRegion("equipmentDefault"),
    )

    /** ======================================================================================================================================================
                                                                    Inventory related variables
     */

    val inventory: com.neutrino.game.UI.UIelements.Inventory = com.neutrino.game.UI.UIelements.Inventory(uiElements)
    private val border = Image(uiElements["InventoryBorder"])
    val tabs: Tabs = Tabs(this, uiElements)

    private val itemContextPopup = ItemContextPopup(usedItemList) {
        showInventory = false
        nullifyAllValues()
    }

    val equipment: Equipment = Equipment(uiElements)

    /** ======================================================================================================================================================
                                                                    Other pages
    */

    lateinit var skills: Skills
    val quests = Group()
    val map = Group()

    /** ======================================================================================================================================================
                                                                    Initializations
     */

    fun initialize() {
        inventory.initialize()
        addActor(inventory)
        inventory.width = border.width - 2 * (inventory.borderSize - 2)
        inventory.height = border.height - 2 * inventory.borderSize + 4
        inventory.setPosition(inventory.x + 2, inventory.y + 2)

        equipment.initialize(border)
        addActor(equipment)
        equipment.isVisible = false

        skills = Skills(uiElements)
        skills.initialize(border)
        addActor(skills)
        skills.isVisible = false

        addScreensTemp()
        addActor(border)
        border.name = "border"

        tabs.initialize()
        addActor(tabs.mainTabsGroup)
        addActor(tabs.openTabsGroup)
        addActor(tabs.sortingTabsGroup)

        tabs.mainTabsGroup.zIndex = 0
        tabs.sortingTabsGroup.zIndex = 1
        scrollFocus = inventory
        currentScreen = inventory

        GlobalData.registerObserver(object : GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.PLAYERINVENTORYSIZE
            override fun update(data: Any?): Boolean {
                val zIndex = inventory.zIndex
                actors.removeValue(inventory, true)
                inventory.initialize()
                inventory.zIndex = zIndex
                updateSize(width.toInt(), height.toInt())
                currentScreen = inventory
                return true
            }
        })
    }

    private fun addScreensTemp() {
        quests.name = "quests"
        quests.addActor(Image(uiElements["Background"]))
        map.name = "map"
        map.addActor(Image(uiElements["Background"]))

        addActor(quests)
        quests.isVisible = false
        addActor(map)
        map.isVisible = false
    }

    var currentScale: Float = 1f

    fun updateSize(width: Int, height: Int) {
        // Update scale
        while(true) {
            if (width < 2 * (border.width - 8) * currentScale) {
                currentScale -= 0.25f
            } else if (width > 2 * (border.width - 8) * (currentScale + 0.25f)) {
                currentScale += 0.25f
            } else break
        }

        actors.forEach { it.setScale(currentScale) }
        border.setPosition((width - border.widthScaled()) / 2f, (height - border.heightScaled()) / 2f)
        inventory.setPosition((this.width - inventory.widthScaled()) / 2f, (this.height - inventory.heightScaled()) / 2f)
        inventory.setPosition(inventory.x + 2 * currentScale, inventory.y + 2 * currentScale)
        equipment.setPosition(border.x, border.y)
        skills.setPosition(border.x, border.y)
        quests.setPosition(border.x, border.y)
        map.setPosition(border.x, border.y)
        tabs.mainTabsGroup.setPosition(border.x, border.y + border.heightScaled())
        tabs.openTabsGroup.setPosition(border.x, border.y + border.heightScaled())
        tabs.sortingTabsGroup.setPosition(border.x, border.y + border.heightScaled())

        border.roundPosition()
        inventory.roundPosition()
        equipment.roundPosition()
        skills.roundPosition()
        quests.roundPosition()
        map.roundPosition()
        tabs.mainTabsGroup.roundPosition()
        tabs.openTabsGroup.roundPosition()
        tabs.sortingTabsGroup.roundPosition()
    }

    /** ======================================================================================================================================================
                                                                    Item related variables
     */

    private var detailsPopup: Table? = null
    private var displayedItem: Item? = null
    private var contextPopup: Table? = null

    // input processor
    private var timeClicked: Long = 0
    private var originalContainer: Container<*>? = null
    private var originalInventory: Inventory? = null
    // possibly change to EqActor
    var clickedItem: Actor? = null
    private var dragItem: Boolean? = null

    var currentScreen: Group = Group()

    /** ======================================================================================================================================================
                                                                    Input processor
     */

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!(button != Input.Buttons.LEFT || button != Input.Buttons.RIGHT) || pointer > 0) return false
        if (button == Input.Buttons.RIGHT)
            return super.touchDown(screenX, screenY, pointer, button)

        when (currentScreen) {
            inventory -> {
                // Sets the click behavior for the item drop
                if (clickedItem != null) {
                    timeClicked = TimeUtils.millis()
                    return super.touchDown(screenX, screenY, pointer, button)
                }

                val coord: Vector2 = screenToStageCoordinates(
                    Vector2(screenX.toFloat(), screenY.toFloat())
                )
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
            }
            skills -> {
                val coord: Vector2 = screenToStageCoordinates(
                    Vector2(screenX.toFloat(), screenY.toFloat())
                )
                clickedItem = getSkillCell(coord.x, coord.y)
            }
        }

        return super.touchDown(screenX, screenY, pointer, button)
    }

    /** Value decreases when dragging upwards and decreases when dragging downwards */
    var previousDragPosition: Int = -2137
    /** Original item split into a stack. Null signifies that no stack was taken */
    var originalStackItem: EqActor? = null

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        when (currentScreen) {
            inventory -> {
                if (clickedItem == null)
                    return false

                if (dragItem == null && originalContainer == null) {
                    dragItem = TimeUtils.millis() - timeClicked >= 650
                    if (dragItem!!)
                        pickUpItem()
                }

                if (dragItem == true) {
                    if (detailsPopup != null)
                        this.actors.removeValue(detailsPopup, true)

                    val coord: Vector2 = screenToStageCoordinates(
                        Vector2(screenX.toFloat(), screenY.toFloat())
                    )
                    clickedItem!!.setPosition(coord.x - (clickedItem!! as EqActor).item.texture.regionWidth * 2, coord.y - (clickedItem!! as EqActor).item.texture.regionHeight * 2)
                    return true
                }

                // TODO change this behavior to manage amount sizes
                //      dragItem false means that click-hold was quick. Null means that an item was previously picked up

                // An item was previously picked up. Adjust its stack size
                if (dragItem == null) {
                    // Initialize the value
                    if (previousDragPosition == -2137) {
                        previousDragPosition = screenY
                        return super.touchDragged(screenX, screenY, pointer)
                    }
                    // Negative values mean upwards drag, positive downwards
                    val dragStrength = previousDragPosition - screenY

                    if ((clickedItem as EqActor).item.amount != null) {
                        println((clickedItem as EqActor).item.amount!! - dragStrength.sign)
                    }
                }


                if (dragItem != true) {
                    if (originalContainer != null) {
                        clickedItem!!.setScale(1f, 1f)
                        originalContainer!!.actor = clickedItem
                        itemPassedToHud()
                    }
                }
            }
        }

        return super.touchDragged(screenX, screenY, pointer)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val coord: Vector2 = screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )
        // Stop contextMenu click from propagating
        if (contextPopup != null && button == Input.Buttons.LEFT) {
            val clickedInMenu = super.touchUp(screenX, screenY, pointer, button)

            if (clickedItem != null) {
                clickedItem = null
                originalInventory = null
            }
            if (inventory.forceRefreshInventory) {
                inventory.refreshInventory()
                inventory.forceRefreshInventory = false
            }
            this.actors.removeValue(contextPopup, true)
            contextPopup = null
            return clickedInMenu
        }

        when (currentScreen) {
            inventory -> {
                if (button == Input.Buttons.LEFT) {
                    if (detailsPopup != null)
                        this.actors.removeValue(detailsPopup, true)

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
                        clickedItem!!.setPosition(coord.x - (clickedItem!! as EqActor).item.texture.regionWidth * (4f * currentScale * 1.25f) / 2 - 6 * currentScale,
                            coord.y - (clickedItem!! as EqActor).item.texture.regionHeight * (4f * currentScale * 1.25f) / 2 - 9 * currentScale)
                        return super.touchUp(screenX, screenY, pointer, button)
                    }
                }
                else if (button == Input.Buttons.RIGHT && clickedItem == null) {
                    if (contextPopup != null) {
                        this.actors.removeValue(contextPopup, true)
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
                                        this.actors.removeValue(detailsPopup, true)
                                    addActor(contextPopup)
                                    contextPopup?.setPosition(coord.x, coord.y)
                                }
                            }
                        }
                    }
                }
            }
            skills -> {
                if (skills.currentTab.name != "skills") {
                    skills.parseClick(coord.x, coord.y)
                }
                else if (button == Input.Buttons.RIGHT && clickedItem == null) {
                    if (contextPopup != null) {
                        this.actors.removeValue(contextPopup, true)
                        contextPopup = null
                    }
                    else {
                        clickedItem = getSkillCell(coord.x, coord.y)
                        if (clickedItem != null && (clickedItem as Container<*>).actor != null) {
                            val skill = ((clickedItem as Container<*>).actor as SkillActor).skill
                            contextPopup = SkillContextPopup(skill, coord.x, coord.y) {
                                usedSkill = skill
                                showInventory = false
                                clickedItem = null
                                nullifyAllValues()
                            }
                            if (contextPopup != null) {
                                if (detailsPopup != null)
                                    this.actors.removeValue(detailsPopup, true)
                                addActor(contextPopup)
                                contextPopup?.setPosition(coord.x, coord.y)
                            }
                        }
                    }
                }
                clickedItem = null
            }
        }

        // New tab was clicked
        if (tabs.openTabsGroup.children.find { it.name == "SortingOpen" }?.isInUnscaled(coord.x - tabs.openTabsGroup.x, coord.y - tabs.openTabsGroup.y, currentScale) == true)
            tabs.hoveredTab = tabs.mainTabsGroup.children.find { it.name == "SortingClosed" }
        if (tabs.hoveredTab != tabs.activeTab && tabs.hoveredTab != null && button == Input.Buttons.LEFT) {
            tabs.activateTab()
        }

        if (inventory.forceRefreshInventory) {
            inventory.refreshInventory()
            inventory.forceRefreshInventory = false
        }

        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val coord: Vector2 = screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )

        // move tabs
        var tab = tabs.getTabByPosition(coord)
        if (border.isIn(screenX.toFloat(), screenY.toFloat()))
            tab = null

        if (tab != null)
            tab!!.moveTab(true)
        if (tabs.hoveredTab != null)
            tabs.hoveredTab!!.moveTab(false)

        tabs.hoveredTab = tab

        when (currentScreen) {
            inventory -> {
                // create new popup
                val hoveredInv = getInvClicked(coord.x, coord.y)
                var hoveredItem: Actor? = null
                if (hoveredInv != null && contextPopup == null) {
                    hoveredItem = getInventoryCell(coord.x, coord.y, hoveredInv)?.actor
                    if (hoveredItem != null && (hoveredItem as EqActor).item != displayedItem) {
                        if (detailsPopup != null)
                            this.actors.removeValue(detailsPopup, true)
                        detailsPopup = ItemDetailsPopup(hoveredItem.item, true)
                        detailsPopup!!.setPosition(coord.x, coord.y)
                        displayedItem = hoveredItem.item
                        addActor(detailsPopup)
                        detailsPopup!!.setPosition(coord.x, coord.y)
                        (detailsPopup!! as ItemDetailsPopup).assignBg(coord.x, coord.y)
                    }
                }

                // delete or move the popup
                if (hoveredInv == null || hoveredItem == null) {
                    displayedItem = null
                    this.actors.removeValue(detailsPopup, true)
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
                    clickedItem!!.setPosition(coord.x - (clickedItem!! as EqActor).item.texture.regionWidth * (4f * currentScale * 1.25f) / 2 - 6 * currentScale,
                        coord.y - (clickedItem!! as EqActor).item.texture.regionHeight * (4f * currentScale * 1.25f) / 2 - 9 * currentScale)
            }
            skills -> {
                if (skills.currentTab.name != "skills") {
                    skills.scrollFocus(coord.x, coord.y)
                    skills.onHover(coord.x, coord.y)
                }
            }
        }

        return super.mouseMoved(screenX, screenY)
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.TAB -> {
                showInventory = false
                nullifyAllValues()
            }
        }
        return true
    }

    /** Moves an actor by 14 pixels */
    private fun Actor.moveTab(up: Boolean) { if (up) this.addAction(Actions.moveBy(0f, 14f * currentScale, 0.15f))
        else this.addAction(Actions.moveBy(0f, -14f * currentScale, 0.15f)) }

    /** Returns tab at coord position */
    private fun actorAt(x: Float, y: Float): Actor? {
        for (actor in Array.ArrayIterator(actors).reversed()) {
            if (actor is Group) {
                for (child in actor.children) {
                    if (child.isIn(x, y))
                        return child
                }
            } else if (actor.isIn(x, y))
                return actor
        }
        return null
    }

    /** ======================================================================================================================================================
                                                                    Item related methods
    */

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

    private fun pickUpItem() {
        // Create a new stack of the item
        if (dragItem == true && (clickedItem as EqActor).item.amount != null) {
            originalContainer = clickedItem!!.parent as Container<*>
            val item = (clickedItem as EqActor).item.clone() as Item
            originalStackItem = clickedItem as EqActor
            item.amount = 0
            clickedItem = EqActor(item)
            changeStackAmount(ceil(originalStackItem!!.item.amount!! / 2f).toInt())
            addActor(clickedItem)
            clickedItem!!.setScale(currentScale * 1.25f, currentScale * 1.25f)
            return
        }

        originalContainer = clickedItem!!.parent as Container<*>
        originalContainer!!.removeActor(clickedItem)
        addActor(clickedItem)
        clickedItem!!.setScale(currentScale * 1.25f, currentScale * 1.25f)
    }

    /** Returns the eq ui and sets the originalEq */
    private fun getInvClicked(x: Float, y: Float): ScrollPane? {
        val inPlayerInventory = (x in inventory.x .. inventory.x + inventory.width * currentScale - 1 &&
                y in inventory.y .. inventory.y + inventory.height * currentScale - 1)
        if (inPlayerInventory) {
            return inventory
        }
        else
            return null
    }

    private fun getSkillCell(x: Float, y: Float): Container<*>? {
        val coord: Vector2 = skills.stageToLocalCoordinates(
            Vector2(x, y)
        )

        var clickedChild = skills.hit(coord.x, coord.y, false)

        // space between cells was hit
        if (clickedChild is Table)
            return null

        if (clickedChild !is Container<*>)
            return null

        return clickedChild
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
                itemDropList.add((clickedItem as EqActor).item)
                // TODO change the remove implementation to this after adding the sorting and user defined positions
//                originalEq!!.itemList.removeAt(originalContainer!!.name.toInt())
                originalInventory!!.itemList.remove(
                    originalInventory!!.itemList.find { it.item == (clickedItem as EqActor).item }
                )

                clickedItem!!.addAction(Actions.scaleTo(0f, 0f, 0.35f))
                clickedItem!!.addAction(Actions.moveBy(32f * currentScale, 32f * currentScale, 0.35f))
                clickedItem!!.addAction(Actions.sequence(
                    Actions.fadeOut(0.35f),
                    Actions.removeActor()
                ))
                // Refresh hotBar after dropping the item
                hudStage.refreshHotBar()
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
                this.actors.removeValue(clickedItem, true)
                return
            }
            originalContainer!!.actor = clickedItem
            return
        }

        this.actors.removeValue(clickedItem, true)
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
                hudStage.refreshHotBar()
                return
            // Item was dropped onto another item, position is reset
            } else if (originalStackItem != null) {
                // If original stack is 0 and container's actor was removed, create a new one
                if (originalContainer?.actor == null)
                    originalContainer?.actor = originalStackItem
                originalStackItem!!.item.amount = originalStackItem!!.item.amount?.plus((clickedItem as EqActor).item.amount!!)
                originalStackItem!!.refreshAmount()
                this.actors.removeValue(clickedItem, true)
                hudStage.refreshHotBar()
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

            hudStage.refreshHotBar()
            return
        }
        container.actor = clickedItem
    }

    fun itemPassedToHud() {
        if (originalStackItem != null) {
            changeStackAmount((clickedItem as EqActor).item.amount!! * -1)
            actors.removeValue(clickedItem, true)
            clickedItem = originalStackItem
            originalStackItem = null
        }
        actors.removeValue(clickedItem, true)
        originalContainer?.actor = clickedItem
        clickedItem = null
        originalContainer = null
        originalInventory = null
        dragItem = null
        timeClicked = 0 // TODO delete after making touchDragged working as intended
    }

    /** Sets all values to null */
    private fun nullifyAllValues() {
        if (originalStackItem != null) {
            changeStackAmount((clickedItem as EqActor).item.amount!! * -1)
            actors.removeValue(clickedItem, true)
            clickedItem = originalStackItem
            originalStackItem = null
        }
        clickedItem?.addAction(Actions.removeActor())
        clickedItem = null
        originalContainer = null
        originalInventory = null
        dragItem = null
        originalStackItem = null
        if (tabs.hoveredTab != null) {
            tabs.hoveredTab!!.moveBy(0f, -14f)
            tabs.hoveredTab = null
        }
        if (detailsPopup != null) {
            this.actors.removeValue(detailsPopup, true)
            detailsPopup = null
        }

        displayedItem = null
        if (contextPopup != null) {
            this.actors.removeValue(contextPopup, true)
            contextPopup = null
        }
    }
}