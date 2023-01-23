package com.neutrino.game.UI.UIelements

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.neutrino.game.UI.UiStage
import com.neutrino.game.equalsDelta
import com.neutrino.game.isInUnscaled
import ktx.actors.setScrollFocus

class Tabs(private val uiStage: UiStage, private val uiElements: Map<String, TextureAtlas.AtlasRegion>) {

    val mainTabsGroup: Group = Group()
    val openTabsGroup: Group = Group()
    val sortingTabsGroup: Group = Group()

    var hoveredTab: Actor? = null
    lateinit var activeTab: Actor
    lateinit var currentSorting: Actor
    var isSortAscending: Boolean = false

    fun activateTab() {
        // Don't change the screen if sorting was clicked
        if (!hoveredTab!!.name.startsWith("Sorting")) {
            when (activeTab.name) {
                "InventoryOpen" -> uiStage.inventory.isVisible = false
                "EquipmentOpen" -> uiStage.equipment.isVisible = false
                "SkillsOpen" -> uiStage.skills.isVisible = false
                "QuestsOpen" -> uiStage.quests.isVisible = false
                "MapOpen" -> uiStage.map.isVisible = false
            }

            when (hoveredTab!!.name) {
                "InventoryClosed" -> {
                    uiStage.inventory.isVisible = true
                    uiStage.currentScreen = uiStage.inventory
                    uiStage.inventory.setScrollFocus(true)
                }
                "EquipmentClosed" -> {
                    uiStage.equipment.isVisible = true
                    uiStage.currentScreen = uiStage.equipment
                    uiStage.equipment.refreshGoldInEquipment()
                    uiStage.equipment.stats.refreshStats()
                }
                "SkillsClosed" -> {
                    uiStage.skills.isVisible = true
                    uiStage.currentScreen = uiStage.skills
                }
                "QuestsClosed" -> {
                    uiStage.quests.isVisible = true
                    uiStage.currentScreen = uiStage.quests
                }
                "MapClosed" -> {
                    uiStage.map.isVisible = true
                    uiStage.currentScreen = uiStage.map
                }
            }
        }

        /** ======================================================================================================================================================
        Sorting and tabs
         */

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
                activeTab = openTabsGroup.children.find { it.name == uiStage.currentScreen.name.replaceFirstChar { it.uppercaseChar() }.plus("Open") }!!
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
                    sortingTab.moveBy(0f, -14f * uiStage.currentScale)
                    sortingTab.moveTab(true)
                    sortingTab.isVisible = true
                    return
                }
                sortingTab.moveBy(0f, 14f * uiStage.currentScale)
                sortingTab.moveTab(false)
                sortingTab.isVisible = true
                return
            }

            sortingTabsGroup.children.find { it.name == currentSorting.name.replace("Open", "") }!!.isVisible = true
            currentSorting.isVisible = false
            currentSorting = sortingTabsGroup.children.find { it.name == hoveredTab!!.name + "Open" }!!
            // move tab without delay
            currentSorting.moveBy(0f, 14f * uiStage.currentScale)
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

    fun showInventory() {
        hoveredTab = mainTabsGroup.findActor("InventoryClosed")
        hoveredTab!!.moveTab(true)
        activateTab()
    }

    /** Returns tab at coord position
     * @param stageCoord stage coordinates
     * */
    fun getTabByPosition(stageCoord: Vector2): Actor? {
        return try {
            if (activeTab.name == "SortingOpen")
                sortingTabsGroup.children.first { it.isInUnscaled(stageCoord.x - sortingTabsGroup.x, stageCoord.y - sortingTabsGroup.y, uiStage.currentScale) }
            else
                mainTabsGroup.children.first { it.isInUnscaled(stageCoord.x - mainTabsGroup.x, stageCoord.y - mainTabsGroup.y, uiStage.currentScale) }
        } catch (e: NoSuchElementException) {
            null
        }
    }

    /** Moves an actor by 14 pixels */
    private fun Actor.moveTab(up: Boolean) { if (up) this.addAction(Actions.moveBy(0f, 14f * uiStage.currentScale, 0.15f))
        else this.addAction(Actions.moveBy(0f, -14f * uiStage.currentScale, 0.15f)) }

    fun initialize()  {
        var xPos = 0f
        val yPos = - 14f

        // Main tabs
        val equipmentClosed = Image(uiElements["EquipmentClosed"])
        equipmentClosed.name = "EquipmentClosed"
        equipmentClosed.setPosition(xPos, yPos)
        val equipmentOpen = Image(uiElements["EquipmentOpen"])
        equipmentOpen.name = "EquipmentOpen"
        equipmentOpen.setPosition(xPos, yPos + 10)
        equipmentOpen.isVisible = false
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
        mainTabsGroup.addActor(equipmentClosed)

        // Adding open tabs
        openTabsGroup.addActor(sortingOpen)
        openTabsGroup.addActor(mapOpen)
        openTabsGroup.addActor(questsOpen)
        openTabsGroup.addActor(skillsOpen)
        openTabsGroup.addActor(inventoryOpen)
        openTabsGroup.addActor(equipmentOpen)

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

        mainTabsGroup.name = "mainTabsGroup"
        openTabsGroup.name = "openTabsGroup"
        sortingTabsGroup.name = "sortingTabsGroup"

        activeTab = inventoryOpen
        activeTab.isVisible = true
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
}