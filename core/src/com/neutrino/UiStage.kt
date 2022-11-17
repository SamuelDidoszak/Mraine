package com.neutrino

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.TimeUtils
import com.badlogic.gdx.utils.viewport.Viewport
import com.neutrino.game.*
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.utility.EqActor
import com.neutrino.game.domain.model.items.utility.EqElement
import com.neutrino.game.domain.model.items.utility.Inventory
import com.neutrino.game.domain.model.turn.Turn
import com.neutrino.game.graphics.utility.ItemContextPopup
import com.neutrino.game.graphics.utility.ItemDetailsPopup
import ktx.actors.centerPosition
import ktx.scene2d.container
import ktx.scene2d.scene2d
import ktx.scene2d.table
import kotlin.math.ceil
import kotlin.math.sign


class UiStage(viewport: Viewport, private val hudStage: HudStage): Stage(viewport) {
    /** FIFO of dropped items */
    val itemDropList: ArrayDeque<Item> = ArrayDeque()

    /** FIFO of used item actions */
    val usedItemList: ArrayDeque<Item> = ArrayDeque()

    var showInventory: Boolean = true

    // UI elements
    private val uiAtlas = TextureAtlas("UI/ui.atlas")
    private val uiElements: Map<String, TextureAtlas.AtlasRegion> = mapOf(
        "BottomBar" to uiAtlas.findRegion("BottomBar"),
        "InventoryBorder" to uiAtlas.findRegion("InventoryBorder"),
        "EqClosed" to uiAtlas.findRegion("EqClosed"),
        "EqOpen" to uiAtlas.findRegion("EqOpen"),
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
    )

    /** ======================================================================================================================================================
                                                                    Inventory related variables
     */

    private lateinit var invScreen: ScrollPane
    private val inventoryBorder = Image(uiElements["InventoryBorder"])
    private val mainTabsGroup = Group()
    private val openTabsGroup = Group()
    private val sortingTabsGroup = Group()

    private val itemContextPopup = ItemContextPopup(usedItemList) {
        showInventory = false
        nullifyAllValues()
    }

    /** ======================================================================================================================================================
                                                                    Initializations
     */

    fun initialize() {
        addInventory()
        addActor(inventoryBorder)
        inventoryBorder.name = "border"

        addTabs()
        mainTabsGroup.zIndex = 0
        sortingTabsGroup.zIndex = 1
        scrollFocus = invScreen

        mainTabsGroup.name = "mainTabsGroup"
        openTabsGroup.name = "openTabsGroup"
        sortingTabsGroup.name = "sortingTabsGroup"
    }

    private fun addInventoryActorOld() {
        val borderWidth: Int = 12
        val borderHeight: Int = 12

        val color = Color(238f, 195f, 154f, 1f)

        val rows = Player.inventorySize / 10 + if (Player.inventorySize % 10 != 0) 1 else 0
        val table = scene2d.table {
                pad(0f)
                this.setFillParent(false)
                clip(true)
                for (n in 0 until rows) {
                    for (i in 0 until 10)
                        add(container {
                            val cellNumber = n * 9 + i
                            name = (cellNumber).toString()
                            align(Align.bottomLeft)
                            if (cellNumber < Player.inventory.itemList.size)
                                actor = EqActor(Player.inventory.itemList[cellNumber].item)
                        }).size(84f, 84f).left().bottom().padRight(if (i != 8) 4f else 0f).space(0f)
                    row().padTop(if (n != 12) 4f else 0f).space(0f)
                }
        }
        table.pack()
        invScreen = ScrollPane(table)
        invScreen.name = "playerEq"
        // without this line, scrollPane generously adds idiotic and undeletable empty space for each column with children in it
        invScreen.setScrollingDisabled(true, false)
        invScreen.setOverscroll(false, false)
        invScreen.setScrollbarsVisible(false)
        invScreen.layout()

        addActor(invScreen)
        invScreen.setSize(invScreen.width, invScreen.height)
        invScreen.centerPosition()
    }

    private fun getCellDrawable(cellNumber: Int, rows: Int): Drawable {
        if (cellNumber >= Player.inventorySize)
            return TextureRegionDrawable(uiElements["cellUnavailable"])
        if (cellNumber == 0)
            return TextureRegionDrawable(uiElements["cellTopLeft"])
        if (cellNumber < 9)
            return TextureRegionDrawable(uiElements["cellTop"])
        if (cellNumber == 9)
            return TextureRegionDrawable(uiElements["cellTopRight"])
        val bottomRowNumber = cellNumber - (rows - 1) * 10
        if (bottomRowNumber == 0 )
            return TextureRegionDrawable(uiElements["cellBottomLeft"])
        if (bottomRowNumber in 1..8 )
            return TextureRegionDrawable(uiElements["cellBottom"])
        if (bottomRowNumber == 9 )
            return TextureRegionDrawable(uiElements["cellBottomRight"])
        if (cellNumber % 10 == 0)
            return TextureRegionDrawable(uiElements["cellLeft"])
        if (cellNumber % 10 == 9)
            return TextureRegionDrawable(uiElements["cellRight"])
        return TextureRegionDrawable(uiElements["cellMiddle"])
    }

    private fun addInventory() {
        val borderWidth: Int = 12
        val borderHeight: Int = 12

        var rows = Player.inventorySize / 10 + if (Player.inventorySize % 10 != 0) 1 else 0
        rows = if (rows < 6) 6 else rows
        val table = scene2d.table {
                this.setFillParent(false)
                clip(true)
                for (n in 0 until rows) {
                    for (i in 0 until 10) {
                        add(container {
                            val cellNumber = n * 10 + i
                            name = (cellNumber).toString()
                            background = getCellDrawable(cellNumber, rows)
                            align(Align.bottomLeft)
                        }).size(84f, 84f).space(0f)
                    }
                    row().space(0f)
                }
        }
        table.pack()
        invScreen = ScrollPane(table)
        invScreen.name = "playerEq"
        // without this line, scrollPane generously adds idiotic and undeletable empty space for each column with children in it
        invScreen.setScrollingDisabled(true, false)
        invScreen.setOverscroll(false, false)
        invScreen.setScrollbarsVisible(false)
        invScreen.layout()

        addActor(invScreen)
        invScreen.width = inventoryBorder.width - 2 * (borderWidth - 2)
        invScreen.height = inventoryBorder.height - 2 * borderHeight + 4
        invScreen.setPosition(invScreen.x + 2, invScreen.y + 2)
    }

    /** Adds tabs of the UI */
    private fun addTabs() {
        var xPos = 0f
        val yPos = - 14f

        // Main tabs
        val eqClosed = Image(uiElements["EqClosed"])
        eqClosed.name = "EqClosed"
        eqClosed.setPosition(xPos, yPos)
        val eqOpen = Image(uiElements["EqOpen"])
        eqOpen.name = "EqOpen"
        eqOpen.setPosition(xPos, yPos + 10)
        eqOpen.isVisible = false
        xPos += 82f
        val inventoryClosed = Image(uiElements["InventoryClosed"])
        inventoryClosed.name = "InventoryClosed"
        inventoryClosed.setPosition(xPos, yPos)
        val inventoryOpen = Image(uiElements["InventoryOpen"])
        inventoryOpen.name = "InventoryOpen"
        inventoryOpen.setPosition(xPos - 10, yPos + 10 - 8)
        inventoryOpen.isVisible = false
        xPos += 168f
        val skillsClosed = Image(uiElements["SkillsClosed"])
        skillsClosed.name = "SkillsClosed"
        skillsClosed.setPosition(xPos, yPos)
        val skillsOpen = Image(uiElements["SkillsOpen"])
        skillsOpen.name = "SkillsOpen"
        skillsOpen.setPosition(xPos - 10, yPos + 10)
        skillsOpen.isVisible = false
        xPos += 168f
        val questsClosed = Image(uiElements["QuestsClosed"])
        questsClosed.name = "QuestsClosed"
        questsClosed.setPosition(xPos, yPos)
        val questsOpen = Image(uiElements["QuestsOpen"])
        questsOpen.name = "QuestsOpen"
        questsOpen.setPosition(xPos - 10, yPos + 10)
        questsOpen.isVisible = false
        xPos += 168f
        val mapClosed = Image(uiElements["MapClosed"])
        mapClosed.name = "MapClosed"
        mapClosed.setPosition(xPos, yPos)
        val mapOpen = Image(uiElements["MapOpen"])
        mapOpen.name = "MapOpen"
        mapOpen.setPosition(xPos - 10, yPos + 10 - 8)
        mapOpen.isVisible = false
        xPos += 170f
        val sortingClosed = Image(uiElements["SortingClosed"])
        sortingClosed.name = "SortingClosed"
        sortingClosed.setPosition(xPos, yPos)
        val sortingOpen = Image(uiElements["SortingOpen"])
        sortingOpen.name = "SortingOpen"
        sortingOpen.setPosition(xPos - 12, yPos + 10)
        sortingOpen.isVisible = false

        // Sorting tabs
        xPos = 0f
        val sortingCustom = Image(uiElements["SortingCustom"])
        sortingCustom.name = "SortingCustom"
        sortingCustom.setPosition(xPos, yPos)
        val sortingCustomOpen = Image(uiElements["SortingCustomOpen"])
        sortingCustomOpen.name = "SortingCustomOpen"
        sortingCustomOpen.setPosition(xPos, yPos)
        sortingCustomOpen.isVisible = false
        xPos += 168f
        val sortingType = Image(uiElements["SortingType"])
        sortingType.name = "SortingType"
        sortingType.setPosition(xPos, yPos)
        val sortingTypeOpen = Image(uiElements["SortingTypeOpen"])
        sortingTypeOpen.name = "SortingTypeOpen"
        sortingTypeOpen.setPosition(xPos, yPos)
        sortingTypeOpen.isVisible = false
        xPos += 168f
        val sortingValue = Image(uiElements["SortingValue"])
        sortingValue.name = "SortingValue"
        sortingValue.setPosition(xPos, yPos)
        val sortingValueOpen = Image(uiElements["SortingValueOpen"])
        sortingValueOpen.name = "SortingValueOpen"
        sortingValueOpen.setPosition(xPos, yPos)
        sortingValueOpen.isVisible = false
        xPos += 168f
        val sortingDate = Image(uiElements["SortingDate"])
        sortingDate.name = "SortingDate"
        sortingDate.setPosition(xPos, yPos)
        val sortingDateOpen = Image(uiElements["SortingDateOpen"])
        sortingDateOpen.name = "SortingDateOpen"
        sortingDateOpen.setPosition(xPos, yPos)
        sortingDateOpen.isVisible = false
        xPos += 168f
        val sortingAsc = Image(uiElements["SortingAsc"])
        sortingAsc.name = "SortingAsc"
        sortingAsc.setPosition(xPos, yPos)
        val sortingDesc = Image(uiElements["SortingDesc"])
        sortingDesc.name = "SortingDesc"
        sortingDesc.setPosition(xPos, yPos)

        // Adding tabs
        mainTabsGroup.addActor(sortingClosed)
        mainTabsGroup.addActor(mapClosed)
        mainTabsGroup.addActor(questsClosed)
        mainTabsGroup.addActor(skillsClosed)
        mainTabsGroup.addActor(inventoryClosed)
        mainTabsGroup.addActor(eqClosed)

        // Adding open tabs
        openTabsGroup.addActor(sortingOpen)
        openTabsGroup.addActor(mapOpen)
        openTabsGroup.addActor(questsOpen)
        openTabsGroup.addActor(skillsOpen)
        openTabsGroup.addActor(inventoryOpen)
        openTabsGroup.addActor(eqOpen)

        // Adding sorting tabs
        sortingTabsGroup.addActor(sortingDesc)
        sortingTabsGroup.addActor(sortingAsc)
        sortingTabsGroup.addActor(sortingDate)
        sortingTabsGroup.addActor(sortingValue)
        sortingTabsGroup.addActor(sortingType)
        sortingTabsGroup.addActor(sortingCustom)
        sortingTabsGroup.addActor(sortingDateOpen)
        sortingTabsGroup.addActor(sortingValueOpen)
        sortingTabsGroup.addActor(sortingTypeOpen)
        sortingTabsGroup.addActor(sortingCustomOpen)

        addActor(mainTabsGroup)
        addActor(openTabsGroup)
        activeTab = inventoryOpen
        activeTab.isVisible = true
        addActor(sortingTabsGroup)
        currentSorting = sortingDateOpen
        currentSorting.isVisible = true
        sortingDate.isVisible = false
        sortingTabsGroup.isVisible = false
        if (isSortAscending) {
            sortingAsc.isVisible = true
            sortingDesc.isVisible = false
            sortingAsc.zIndex = 0
        } else {
            sortingAsc.isVisible = false
            sortingDesc.isVisible = true
            sortingDesc.zIndex = 0
        }
    }

    var forceRefreshInventory: Boolean = false
    fun refreshInventory() {
        (invScreen.actor as Table).children.forEach {
            (it as Container<*>).actor = null
            val cellNumber = it.name.toInt()
            if (cellNumber < Player.inventory.itemList.size)
                it.actor = EqActor(Player.inventory.itemList[cellNumber].item)
        }
    }

    var currentScale: Float = 1f

    fun updateSize(width: Int, height: Int) {
        // Update scale
        while(true) {
            if (width < 2 * (inventoryBorder.width - 8) * currentScale) {
                currentScale -= 0.25f
            } else if (width > 2 * (inventoryBorder.width - 8) * (currentScale + 0.25f)) {
                currentScale += 0.25f
            } else break
        }

        actors.forEach { it.setScale(currentScale) }
        inventoryBorder.setPosition((width - inventoryBorder.widthScaled()) / 2f, (height - inventoryBorder.heightScaled()) / 2f)
        invScreen.setPosition((this.width - invScreen.widthScaled()) / 2f, (this.height - invScreen.heightScaled()) / 2f)
        invScreen.setPosition(invScreen.x + 2 * currentScale, invScreen.y + 2 * currentScale)
        mainTabsGroup.setPosition(inventoryBorder.x, inventoryBorder.y + inventoryBorder.heightScaled())
        openTabsGroup.setPosition(inventoryBorder.x, inventoryBorder.y + inventoryBorder.heightScaled())
        sortingTabsGroup.setPosition(inventoryBorder.x, inventoryBorder.y + inventoryBorder.heightScaled())

        inventoryBorder.roundPosition()
        invScreen.roundPosition()
        mainTabsGroup.roundPosition()
        openTabsGroup.roundPosition()
        sortingTabsGroup.roundPosition()
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
    // values for tab handling
    private var hoveredTab: Actor? = null
    private lateinit var activeTab: Actor
    private lateinit var currentSorting: Actor
    private var isSortAscending: Boolean = false

    private fun activateTab() {
        if (activeTab.name == "SortingOpen") {
            if (hoveredTab!!.name == "SortingClosed") {
                // force asc/desc tab to original position
                val sortingTabActive = sortingTabsGroup.children.find { it.name == "Sorting" + if (isSortAscending) "Asc" else "Desc" }!!
                val yPositionReference = sortingTabsGroup.children.find { it.name == "SortingCustom" }!!.y
                if (!sortingTabActive.y.equalsDelta(yPositionReference)) {
                    sortingTabActive.y = yPositionReference
                }

                mainTabsGroup.isVisible = true
                sortingTabsGroup.isVisible = false
                activeTab.isVisible = false
                activeTab = openTabsGroup.children.find { it.name == "InventoryOpen" }!!
                activeTab.isVisible = true
                return
            }
            if (hoveredTab!!.name == "SortingAsc" || hoveredTab!!.name == "SortingDesc") {
                val sortingTabPrevious = sortingTabsGroup.children.find { it.name == "Sorting" + if (isSortAscending) "Asc" else "Desc" }!!
                sortingTabPrevious.isVisible = false

                isSortAscending = !isSortAscending
                val sortingTab = sortingTabsGroup.children.find { it.name == "Sorting" + if (isSortAscending) "Asc" else "Desc" }!!
                sortingTab.zIndex = 0

                val yPositionReference = sortingTabsGroup.children.find { it.name == "SortingCustom" }!!.y
                if (sortingTabPrevious.y.equalsDelta(yPositionReference)) {
                    sortingTab.moveBy(0f, -14f * currentScale)
                    sortingTab.moveTab(true)
                    sortingTab.isVisible = true
                    return
                }
                sortingTab.moveBy(0f, 14f * currentScale)
                sortingTab.moveTab(false)
                sortingTab.isVisible = true
                return
            }

            sortingTabsGroup.children.find { it.name == currentSorting.name.replace("Open", "") }!!.isVisible = true
            currentSorting.isVisible = false
            currentSorting = sortingTabsGroup.children.find { it.name == hoveredTab!!.name + "Open" }!!
            // move tab without delay
            currentSorting.moveBy(0f, 14f * currentScale)
            currentSorting.isVisible = true
            sortingTabsGroup.children.find { it.name == hoveredTab!!.name }!!.isVisible = false
            currentSorting.moveTab(false)
            return
        }
        activeTab.isVisible = false
        activeTab = openTabsGroup.children.find { it.name == hoveredTab!!.name.replace("Closed", "Open") }!!
        activeTab.isVisible = true

        if (activeTab.name == "SortingOpen") {
            mainTabsGroup.isVisible = false
            sortingTabsGroup.isVisible = true
            hoveredTab = null
        }
    }

    /** ======================================================================================================================================================
                                                                    Input processor
     */

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!(button != Input.Buttons.LEFT || button != Input.Buttons.RIGHT) || pointer > 0) return false
        if (button == Input.Buttons.RIGHT)
            return super.touchDown(screenX, screenY, pointer, button)

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
            if (clickedInv.name == "playerEq")
                originalInventory = Player.inventory
            // Sets the clicked item for drag handling
            clickedItem = getInventoryCell(coord.x, coord.y, clickedInv)?.actor
            if (clickedItem != null)
                timeClicked = TimeUtils.millis()
        }
        return super.touchDown(screenX, screenY, pointer, button)
    }

    /** Value decreases when dragging upwards and decreases when dragging downwards */
    var previousDragPosition: Int = -2137
    /** Original item split into a stack. Null signifies that no stack was taken */
    var originalStackItem: EqActor? = null

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
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

        return super.touchDragged(screenX, screenY, pointer)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val coord: Vector2 = screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )
        // Stop contextMenu click from propagating
        if (contextPopup != null && button == Input.Buttons.LEFT) {
            val clickedInMenu = super.touchUp(screenX, screenY, pointer, button)
            if (!clickedInMenu) {
                this.actors.removeValue(contextPopup, true)
                contextPopup = null
            }
            return clickedInMenu
        }

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

        // New tab was clicked
        if (openTabsGroup.children.find { it.name == "SortingOpen" }?.isInUnscaled(coord.x - openTabsGroup.x, coord.y - openTabsGroup.y) == true)
            hoveredTab = mainTabsGroup.children.find { it.name == "SortingClosed" }
        if (hoveredTab != activeTab && hoveredTab != null && button == Input.Buttons.LEFT) {
            activateTab()
        }

        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val coord: Vector2 = screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )

        // move tabs
        var tab = getTabByPosition(screenX.toFloat(), screenY.toFloat())
        if (inventoryBorder.isIn(screenX.toFloat(), screenY.toFloat()))
            tab = null

        if (tab != null)
            tab!!.moveTab(true)
        if (hoveredTab != null)
            hoveredTab!!.moveTab(false)

        hoveredTab = tab

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
//        // check if there is an item in hud
//        if (hudStage.clickedItem != null) {
//            clickedItem = hudStage.clickedItem
//            actors.add(clickedItem)
//            hudStage.passedItemToUi()
//        }

        if (clickedItem != null)
            clickedItem!!.setPosition(coord.x - (clickedItem!! as EqActor).item.texture.regionWidth * (4f * currentScale * 1.25f) / 2 - 6 * currentScale,
                coord.y - (clickedItem!! as EqActor).item.texture.regionHeight * (4f * currentScale * 1.25f) / 2 - 9 * currentScale)
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

    /** ======================================================================================================================================================
                                                                    Actor related methods
     */

    /** Moves an actor by 14 pixels */
    private fun Actor.moveTab(up: Boolean) { if (up) this.addAction(Actions.moveBy(0f, 14f * currentScale, 0.15f))
    else this.addAction(Actions.moveBy(0f, -14f * currentScale, 0.15f)) }

    private fun Actor.isIn(x: Float, y: Float) = (x.compareDelta(this.x) >= 0 && x.compareDelta(this.x + this.width) <= 0 &&
            y.compareDelta(this.y) >= 0 && y.compareDelta(this.y + this.height) <= 0)

    private fun Actor.isInUnscaled(x: Float, y: Float) = (x.compareDelta(this.x * currentScale) >= 0 && x.compareDelta(this.x * currentScale + this.width * currentScale) <= 0 &&
            y.compareDelta(this.y * currentScale) >= 0 && y.compareDelta(this.y * currentScale + this.height * currentScale) <= 0)

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

    /** Returns tab at coord position */
    private fun getTabByPosition(x: Float, y: Float): Actor? {
        val coord: Vector2 = this.screenToStageCoordinates(
            Vector2(x, y)
        )

        return try {
            if (activeTab.name == "SortingOpen")
                sortingTabsGroup.children.first { it.isInUnscaled(coord.x - sortingTabsGroup.x, coord.y - sortingTabsGroup.y) }
            else
                mainTabsGroup.children.first { it.isInUnscaled(coord.x - mainTabsGroup.x, coord.y - mainTabsGroup.y) }
        } catch (e: NoSuchElementException) {
            null
        }
    }

    /** ======================================================================================================================================================
                                                                    Item related methods
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
        val inPlayerEq = (x in invScreen.x .. invScreen.x + invScreen.width * currentScale - 1 &&
                y in invScreen.y .. invScreen.y + invScreen.height * currentScale - 1)
        if (inPlayerEq) {
            return invScreen
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

    private fun parseItemDrop(x: Float, y: Float) {
        if (clickedItem == null)
            return

        var stackedItemDate: Double = Turn.turn
        if (originalStackItem != null) {
            val stackedItem = originalInventory!!.itemList.find { it.item == originalStackItem!!.item }
            stackedItemDate = stackedItem!!.dateAdded
            if (stackedItem.item.amount == 0)
                originalInventory!!.itemList.remove(stackedItem)
        }


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
            }
            return
        }
        clickedItem!!.setScale(1f, 1f)
        // if the area between cells was clicked, reset the item position
        val container = getInventoryCell(x, y, clickedInv)
        // TODO checking the Player inventorySize here can cause bugs when other inventories will be displayed
        if (container == null || container.name?.toInt()!! >= Player.inventorySize) {
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
            // sum item amounts
            if ((container.actor as EqActor).item.amount != null &&
                (clickedItem as EqActor).item.equalsIdentical((container.actor as EqActor).item)) {
                (container.actor as EqActor).item.amount =
                    (container.actor as EqActor).item.amount?.plus((clickedItem as EqActor).item.amount!!)
                originalInventory!!.itemList.remove(
                    originalInventory!!.itemList.find { it.item == (clickedItem as EqActor).item })
                (container.actor as EqActor).refreshAmount()
                return
            } else if (originalStackItem != null) {
                originalStackItem!!.item.amount = originalStackItem!!.item.amount?.plus((clickedItem as EqActor).item.amount!!)
                originalStackItem!!.refreshAmount()
                this.actors.removeValue(clickedItem, true)
                return
            } else
                originalContainer!!.actor = container.actor as EqActor
        }
        container.actor = (clickedItem)
        if (originalStackItem != null)
            originalInventory!!.itemList.add(EqElement((clickedItem as EqActor).item, stackedItemDate))
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
        if (hoveredTab != null) {
            hoveredTab!!.moveBy(0f, -14f)
            hoveredTab = null
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

    /** ======================================================================================================================================================
                                                                    Stage related methods
    */

    override fun draw() {
        if (Player.inventorySizeChanged) {
            Player.inventorySizeChanged = false
            actors.removeValue(invScreen, true)
            addInventory()
        }
        super.draw()
    }
}