package com.neutrino.game.UI

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.Viewport
import com.neutrino.GlobalData
import com.neutrino.GlobalDataObserver
import com.neutrino.GlobalDataType
import com.neutrino.HudStage
import com.neutrino.game.*
import com.neutrino.game.UI.UIelements.Equipment
import com.neutrino.game.UI.UIelements.Shop
import com.neutrino.game.UI.UIelements.Skills
import com.neutrino.game.UI.UIelements.Tabs
import com.neutrino.game.UI.utility.ManagedElement
import com.neutrino.game.UI.utility.ManagerType
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.systems.skills.Skill


class UiStage(viewport: Viewport, private val hudStage: HudStage): Stage(viewport) {

    /** FIFO of dropped items */
    val itemDropList: ArrayDeque<Item> = ArrayDeque()

    /** FIFO of used item actions */
    val usedItemList: ArrayDeque<Item> = ArrayDeque()
    /** Item to use on another character / tile */
    var useItemOn: Item? = null

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
            "OptionsSortingClosed" to uiAtlas.findRegion("OptionsSortingClosed"),
            "OptionsSortingOpen" to uiAtlas.findRegion("OptionsSortingOpen"),
            "OptionsSkillsClosed" to uiAtlas.findRegion("OptionsSkillsClosed"),
            "OptionsSkillsOpen" to uiAtlas.findRegion("OptionsSkillsOpen"),
            "SortingClosedCentered" to uiAtlas.findRegion("SortingClosedCentered"),
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
    private val border = Image(uiElements["InventoryBorder"])
    val tabs: Tabs = Tabs(this, uiElements)

    val equipment: Equipment = Equipment(uiElements)
    val inventory: com.neutrino.game.UI.UIelements.Inventory = com.neutrino.game.UI.UIelements.Inventory(uiElements)
    lateinit var skills: Skills
    val quests = Group()
    val map = Group()

    val inventoryManager = InventoryManager(this)

    var currentScale: Float = 1f

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

        inventoryManager.elements.add(ManagedElement(inventory, this.root, ManagerType.INVENTORY))
        inventoryManager.elements.add(ManagedElement(skills.skillTable, skills, ManagerType.SKILLS))
        inventoryManager.setElement(inventory)

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

    var currentScreen: Group = Group()

    /** ======================================================================================================================================================
                                                                    Input processor
     */

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (!(button != Input.Buttons.LEFT || button != Input.Buttons.RIGHT) || pointer > 0) return false
        if (button == Input.Buttons.RIGHT)
            return super.touchDown(screenX, screenY, pointer, button)

        val coord: Vector2 = screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )

        when (currentScreen) {
            inventory, skills -> {
                val callback =
                inventoryManager.touchDown(coord, pointer, button) {
                    super.touchDown(screenX, screenY, pointer, button)
                }

                if (callback != -1)
                    return callback == 1
            }
        }

        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        val coord: Vector2 = screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )

        when (currentScreen) {
            inventory, skills -> {
                val callback =
                inventoryManager.touchDragged(screenX, screenY, coord, pointer) {
                    super.touchDragged(screenX, screenY, pointer)
                }

                if (callback != -1)
                    return callback == 1
            }
        }

        return super.touchDragged(screenX, screenY, pointer)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        val coord: Vector2 = screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )

        when (currentScreen) {
            inventory -> {
                val callback =
                inventoryManager.touchUp(coord, button) {
                    super.touchUp(screenX, screenY, pointer, button)
                }

                if (callback != -1) {
                    if (inventory.forceRefreshInventory) {
                        inventory.refreshInventory()
                        inventory.forceRefreshInventory = false
                    }
                    return callback == 1
                }

                if (inventory.forceRefreshInventory) {
                    inventory.refreshInventory()
                    inventory.forceRefreshInventory = false
                }
            }
            skills -> {
                if (skills.currentTab.name != "skillTable") {
                    skills.parseClick(coord.x, coord.y)
                } else {
                    inventoryManager.touchUp(coord, button) {
                        super.touchUp(screenX, screenY, pointer, button)
                    }
                }
            }
        }

        tabs.touchUp(coord, pointer, button)

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
                inventoryManager.mouseMoved(coord)
            }
            skills -> {
                skills.scrollFocus(coord.x, coord.y)
                if (skills.currentTab.name != "skillTable") {
                    skills.onHover(coord.x, coord.y)
                } else
                    inventoryManager.mouseMoved(coord)
            }
        }

        return super.mouseMoved(screenX, screenY)
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.TAB -> {
                showInventory = false
                inventoryManager.nullifyAllValues()
                nullifyAllValues()
                hudStage.nullifyAllValues()
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
                                                                    Cleanup
    */

    fun refreshHotBar() {
        hudStage.refreshHotBar()
    }

    /** Sets all values to null */
    fun nullifyAllValues() {
        if (tabs.hoveredTab != null) {
            tabs.hoveredTab!!.moveBy(0f, -14f)
            tabs.hoveredTab = null
        }
    }
}