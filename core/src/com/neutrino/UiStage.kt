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
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.*
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.StatsEnum
import com.neutrino.game.domain.model.items.EquipmentType
import com.neutrino.game.domain.model.items.Item
import com.neutrino.game.domain.model.items.items.Gold
import com.neutrino.game.domain.model.items.utility.EqActor
import com.neutrino.game.domain.model.items.utility.EqElement
import com.neutrino.game.domain.model.items.utility.Inventory
import com.neutrino.game.graphics.utility.ItemContextPopup
import com.neutrino.game.graphics.utility.ItemDetailsPopup
import ktx.scene2d.container
import ktx.scene2d.scene2d
import ktx.scene2d.table
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.ceil
import kotlin.math.roundToInt
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

    private lateinit var inventory: ScrollPane
    private val border = Image(uiElements["InventoryBorder"])
    private val mainTabsGroup = Group()
    private val openTabsGroup = Group()
    private val sortingTabsGroup = Group()

    private val itemContextPopup = ItemContextPopup(usedItemList) {
        showInventory = false
        nullifyAllValues()
    }

    /** ======================================================================================================================================================
                                                                    Equipment related variables
    */

    private val equipment: Group = Group()
    private val stats: Table = Table()
    private lateinit var equipmentTable: Table
    private var equipmentMap: EnumMap<EquipmentType, Container<Actor>> = EnumMap(EquipmentType::class.java)

    /** ======================================================================================================================================================
                                                                    Other pages
    */

    private val skills = Group()
    private val quests = Group()
    private val map = Group()


    /** ======================================================================================================================================================
                                                                    Initializations
     */

    fun initialize() {
        addInventory()
        addEquipment()
        addSkills()
        addScreensTemp()
        equipment.isVisible = false
        addActor(border)
        border.name = "border"

        addTabs()
        mainTabsGroup.zIndex = 0
        sortingTabsGroup.zIndex = 1
        scrollFocus = inventory
        currentScreen = inventory

        mainTabsGroup.name = "mainTabsGroup"
        openTabsGroup.name = "openTabsGroup"
        sortingTabsGroup.name = "sortingTabsGroup"

        GlobalData.registerObserver(object: GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.PLAYERINVENTORYSIZE
            override fun update(data: Any?): Boolean {
                val zIndex = inventory.zIndex
                actors.removeValue(inventory, true)
                addInventory()
                inventory.zIndex = zIndex
                updateSize(width.toInt(), height.toInt())
                currentScreen = inventory
                return true
            }
        })
    }

    fun showInventory() {
        hoveredTab = mainTabsGroup.findActor("InventoryClosed")
        hoveredTab!!.moveTab(true)
        activateTab()
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

    private fun addEquipment() {
        equipment.name = "equipment"
        equipment.addActor(Image(uiElements["EquipmentScreen"]))

        val namesList: List<Pair<String, EquipmentType>> = listOf(
            Pair("hands", EquipmentType.HANDS), Pair("head", EquipmentType.HEAD), Pair("amulet", EquipmentType.AMULET),
            Pair("lHand", EquipmentType.LHAND), Pair("torso", EquipmentType.TORSO), Pair("rHand", EquipmentType.RHAND),
            Pair("lRing", EquipmentType.LRING), Pair("legs", EquipmentType.LEGS), Pair("rRing", EquipmentType.RRING),
            Pair("money", EquipmentType.MONEY), Pair("feet", EquipmentType.FEET), Pair("bag", EquipmentType.BAG)
        )

        equipmentTable = scene2d.table {
            for (x in 0 until 4) {
                for (y in 0 until 3) {
                    add(container {
                        val list = namesList[x * 3 + y]
                        name = list.first
                        align(Align.bottomLeft)
                        equipmentMap[list.second] = this
                        setEquipmentDrawable(list.second)
                    }).size(96f, 96f).pad(8f)
                }
                row().pad(0f).space(0f)
            }
        }
        equipmentTable.pack()
        equipmentTable.name = "equipmentTable"
        equipmentTable.layout()
        equipment.addActor(equipmentTable)

        equipmentTable.width = 336f
        equipmentTable.height = 448f
        equipmentTable.setPosition(border.width - equipmentTable.width - 12 - 8, 38f)

        // Initialize Gold actor
        equipmentMap[EquipmentType.MONEY]!!.actor = EqActor(Gold())
        (equipmentMap[EquipmentType.MONEY]!!.actor as EqActor).item.amount = 0
        (equipmentMap[EquipmentType.MONEY]!!.actor as EqActor).refreshAmount()

        addStatsTable()
        stats.name = "stats"
        equipment.addActor(stats)
        stats.setPosition(24f, 24f)

        addActor(equipment)
    }

    private fun setEquipmentDrawable(type: EquipmentType) {
        val item: Item? = Player.equipment.getEquipped(type)

        if (item != null)
            equipmentMap[type]!!.background = TextureRegionDrawable(uiElements["equipmentDefault"])
        else
            equipmentMap[type]!!.background = TextureRegionDrawable(uiElements["equipmentDefault"])
    }

    private fun addStatsTable() {
        val width = border.width * 0.55f
        val title = scene2d.table {
            val name = TextraLabel(Player.name, Fonts.EQUIPMENT, Color.BLACK)
            name.align = Align.left
            add(name).padLeft(24f)

            val lvl = TextraLabel("lvl ${Player.level}", Fonts.EQUIPMENT, Color.BLACK)
            lvl.name = "level"
            lvl.align = Align.center
            add(lvl).fillX().expandX().center()

            val expValues = scene2d.table {
                val expValue = TextraLabel("${Player.experience}", Fonts.MATCHUP, Color.BLACK)
                expValue.name = "exp"
                expValue.align = Align.center
                add(expValue).fillX().expandX().uniform()
                add(TextraLabel("/", Fonts.MATCHUP, Color.BLACK))
                val expMax = TextraLabel("2137", Fonts.MATCHUP, Color.BLACK)
                expMax.name = "expMax"
                expMax.align = Align.center
                add(expMax).fillX().expandX().uniform()
            }
            expValues.pack()
            add(expValues).top().right()
        }
        stats.add(title).fillX().center().padBottom(12f)
        stats.row()

        val stats1 = scene2d.table {
            val hpLabel = TextraLabel("Hp", Fonts.EQUIPMENT, Color.BLACK)
            add(hpLabel).width(width / 3).fillX().colspan(2).uniform()
            hpLabel.align = Align.left
            val hpValues = scene2d.table {
                val hpValue = TextraLabel("${Player.hp.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
                hpValue.name = "hp"
                hpValue.align = Align.center
                add(hpValue).fillX().expandX().uniform()
                add(TextraLabel("/", Fonts.EQUIPMENT, Color.BLACK))
                val hpMax = TextraLabel("${Player.hpMax.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
                hpMax.name = "hpMax"
                hpMax.align = Align.center
                add(hpMax).fillX().expandX().uniform()
            }
            add(hpValues).width(width / 3).colspan(2).uniform().fillX()
            row()

            val mpLabel = TextraLabel("Mp", Fonts.EQUIPMENT, Color.BLACK)
            mpLabel.align = Align.left
            add(mpLabel).fillX().width(width / 3).colspan(2).uniform()
            val mpValues = scene2d.table {
                val mpValue = TextraLabel("${Player.mp.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
                mpValue.name = "mp"
                mpValue.align = Align.center
                add(mpValue).fillX().expandX().uniform()
                add(TextraLabel("/", Fonts.EQUIPMENT, Color.BLACK))
                val mpMax = TextraLabel("${Player.mpMax.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
                mpMax.name = "mpMax"
                mpMax.align = Align.center
                add(mpMax).fillX().expandX().uniform()
            }
            add(mpValues).colspan(2).uniform().fillX()
            row()

            val strengthLabel = TextraLabel("Strength", Fonts.EQUIPMENT, Color.BLACK)
            strengthLabel.alignment = Align.left
            add(strengthLabel).fillX().width(width / 3).colspan(2).uniform()
            strengthLabel.align = Align.left
            val strength = TextraLabel("${Player.strength.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            strength.name = "strength"
            strength.align = Align.center
            add(strength).fillX().colspan(2).uniform()
            row()

            val dexterityLabel = TextraLabel("Dexterity", Fonts.EQUIPMENT, Color.BLACK)
            dexterityLabel.align = Align.left
            add(dexterityLabel).fillX().width(width / 3).colspan(2).uniform()
            val dexterity = TextraLabel("${Player.dexterity.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            dexterity.name = "dexterity"
            dexterity.align = Align.center
            add(dexterity).fillX().colspan(2).uniform()
            row()

            val intelligenceLabel = TextraLabel("Intelligence", Fonts.EQUIPMENT, Color.BLACK)
            intelligenceLabel.align = Align.left
            add(intelligenceLabel).fillX().width(width / 3).colspan(2).uniform()
            val intelligence = TextraLabel("${Player.intelligence.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            intelligence.name = "intelligence"
            intelligence.align = Align.center
            add(intelligence).fillX().colspan(2).uniform()
            row()

            val luckLabel = TextraLabel("Luck", Fonts.EQUIPMENT, Color.BLACK)
            luckLabel.align = Align.left
            add(luckLabel).fillX().width(width / 3).colspan(2).uniform()
            val luck = TextraLabel("${Player.luck.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            luck.name = "luck"
            luck.align = Align.center
            add(luck).fillX().colspan(2).uniform()
            row()

            val damageLabel = TextraLabel("Damage", Fonts.EQUIPMENT, Color.BLACK)
            damageLabel.align = Align.left
            damageLabel.name = "damageLabel"
            add(damageLabel).fillX().width(width / 3).colspan(2).uniform()
            val damageValues = scene2d.table {
                val damageValue = TextraLabel("${(Player.damage - Player.damageVariation).roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
                damageValue.name = "damage"
                damageValue.align = Align.center
                add(damageValue).fillX().expandX().uniform()
                add(TextraLabel("-", Fonts.EQUIPMENT, Color.BLACK))
                val damageMax = TextraLabel("${(Player.damage + Player.damageVariation).roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
                damageMax.name = "damageMax"
                damageMax.align = Align.center
                add(damageMax).fillX().expandX().uniform()
            }
            add(damageValues).colspan(2).uniform().fillX()
            row()

            val defenceLabel = TextraLabel("Defence", Fonts.EQUIPMENT, Color.BLACK)
            defenceLabel.align = Align.left
            add(defenceLabel).fillX().width(width / 3).colspan(2).uniform()
            val defence = TextraLabel("${Player.defence.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            defence.name = "defence"
            defence.align = Align.center
            add(defence).fillX().colspan(2).uniform()
            row()
        }
        stats.add(stats1).padBottom(12f)
        stats.row()

        val stats2 = scene2d.table {
            val evasionLabel = TextraLabel("Evasion", Fonts.EQUIPMENT, Color.BLACK)
            evasionLabel.align = Align.left
            add(evasionLabel).fillX().width(width / 3).colspan(2).uniform()
            val evasion = TextraLabel("${(Player.evasion * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            evasion.name = "evasion"
            evasion.align = Align.center
            add(evasion).width(width / 6).colspan(1).uniform()

            val accuracyLabel = TextraLabel("Accuracy", Fonts.EQUIPMENT, Color.BLACK)
            accuracyLabel.align = Align.left
            add(accuracyLabel).fillX().width(width / 3).colspan(2).uniform()
            val accuracy = TextraLabel("${(Player.accuracy * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            accuracy.name = "accuracy"
            accuracy.align = Align.center
            add(accuracy).width(width / 6).colspan(1).uniform()
            row()

            val critChanceLabel = TextraLabel("Crit chance", Fonts.EQUIPMENT, Color.BLACK)
            critChanceLabel.align = Align.left
            add(critChanceLabel).fillX().colspan(2).uniform()
            val critChance = TextraLabel("${(Player.criticalChance * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            critChance.name = "critChance"
            critChance.align = Align.center
            add(critChance).fillX().colspan(1).uniform()

            val critDamageLabel = TextraLabel("Crit damage", Fonts.EQUIPMENT, Color.BLACK)
            critDamageLabel.align = Align.left
            add(critDamageLabel).fillX().colspan(2).uniform()
            val critDamage = TextraLabel("${(Player.criticalDamage * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            critDamage.name = "critDamage"
            critDamage.align = Align.center
            add(critDamage).fillX().colspan(1).uniform()
            row()

            val movementSpeedLabel = TextraLabel("Movement", Fonts.EQUIPMENT, Color.BLACK)
            movementSpeedLabel.align = Align.left
            add(movementSpeedLabel).fillX().colspan(2).uniform()
            movementSpeedLabel.setBounds(0f, 0f, border.width / 3f, 100f)
            val movementSpeed = TextraLabel("${Player.movementSpeed.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            movementSpeed.name = "movementSpeed"
            movementSpeed.align = Align.center
            add(movementSpeed).fillX().colspan(1).uniform()

            val attackSpeedLabel = TextraLabel("Attack spd", Fonts.EQUIPMENT, Color.BLACK)
            attackSpeedLabel.align = Align.left
            add(attackSpeedLabel).fillX().colspan(2).uniform()
            val attackSpeed = TextraLabel("${Player.attackSpeed.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            attackSpeed.name = "attackSpeed"
            attackSpeed.align = Align.center
            add(attackSpeed).fillX().colspan(1).uniform()
            row().padTop(12f)

            val fireDamageLabel = TextraLabel("Fire dmg", Fonts.EQUIPMENT, Color.BLACK)
            fireDamageLabel.align = Align.left
            fireDamageLabel.name = "fireDamageLabel"
            add(fireDamageLabel).fillX().colspan(2)
            val fireDamage = TextraLabel("${Player.fireDamage.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            fireDamage.name = "fireDamage"
            fireDamage.align = Align.center
            add(fireDamage).fillX().colspan(1)

            val fireDefenceLabel = TextraLabel("Fire def", Fonts.EQUIPMENT, Color.BLACK)
            fireDefenceLabel.align = Align.left
            add(fireDefenceLabel).fillX().colspan(2)
            val fireDefence = TextraLabel("${(Player.fireDefence * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            fireDefence.name = "fireDefence"
            fireDefence.align = Align.center
            add(fireDefence).fillX().colspan(1)
            row()

            val waterDamageLabel = TextraLabel("Water dmg", Fonts.EQUIPMENT, Color.BLACK)
            waterDamageLabel.align = Align.left
            waterDamageLabel.name = "waterDamageLabel"
            add(waterDamageLabel).fillX().colspan(2)
            val waterDamage = TextraLabel("${Player.waterDamage.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            waterDamage.name = "waterDamage"
            waterDamage.align = Align.center
            add(waterDamage).fillX().colspan(1)

            val waterDefenceLabel = TextraLabel("Water def", Fonts.EQUIPMENT, Color.BLACK)
            waterDefenceLabel.align = Align.left
            add(waterDefenceLabel).fillX().colspan(2)
            val waterDefence = TextraLabel("${(Player.waterDefence * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            waterDefence.name = "waterDefence"
            waterDefence.align = Align.center
            add(waterDefence).fillX().colspan(1)
            row()

            val earthDamageLabel = TextraLabel("Earth dmg", Fonts.EQUIPMENT, Color.BLACK)
            earthDamageLabel.align = Align.left
            earthDamageLabel.name = "earthDamageLabel"
            add(earthDamageLabel).fillX().colspan(2)
            val earthDamage = TextraLabel("${Player.earthDamage.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            earthDamage.name = "earthDamage"
            earthDamage.align = Align.center
            add(earthDamage).fillX().colspan(1)

            val earthDefenceLabel = TextraLabel("Earth def", Fonts.EQUIPMENT, Color.BLACK)
            earthDefenceLabel.align = Align.left
            add(earthDefenceLabel).fillX().colspan(2)
            val earthDefence = TextraLabel("${(Player.earthDefence * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            earthDefence.name = "earthDefence"
            earthDefence.align = Align.center
            add(earthDefence).fillX().colspan(1)
            row()

            val airDamageLabel = TextraLabel("Air dmg", Fonts.EQUIPMENT, Color.BLACK)
            airDamageLabel.align = Align.left
            airDamageLabel.name = "airDamageLabel"
            add(airDamageLabel).fillX().colspan(2)
            val airDamage = TextraLabel("${Player.airDamage.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            airDamage.name = "airDamage"
            airDamage.align = Align.center
            add(airDamage).fillX().colspan(1)

            val airDefenceLabel = TextraLabel("Air def", Fonts.EQUIPMENT, Color.BLACK)
            airDefenceLabel.align = Align.left
            add(airDefenceLabel).fillX().colspan(2)
            val airDefence = TextraLabel("${(Player.airDefence * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            airDefence.name = "airDefence"
            airDefence.align = Align.center
            add(airDefence).fillX().colspan(1)
            row()

            val poisonDamageLabel = TextraLabel("Poison dmg", Fonts.EQUIPMENT, Color.BLACK)
            poisonDamageLabel.align = Align.left
            poisonDamageLabel.name = "poisonDamageLabel"
            add(poisonDamageLabel).fillX().colspan(2)
            val poisonDamage = TextraLabel("${Player.poisonDamage.roundOneDecimal()}", Fonts.EQUIPMENT, Color.BLACK)
            poisonDamage.name = "poisonDamage"
            poisonDamage.align = Align.center
            add(poisonDamage).fillX().colspan(1)

            val poisonDefenceLabel = TextraLabel("Poison def", Fonts.EQUIPMENT, Color.BLACK)
            poisonDefenceLabel.align = Align.left
            add(poisonDefenceLabel).fillX().colspan(2)
            val poisonDefence = TextraLabel("${(Player.poisonDefence * 100).roundToInt()}%", Fonts.EQUIPMENT, Color.BLACK)
            poisonDefence.name = "poisonDefence"
            poisonDefence.align = Align.center
            add(poisonDefence).fillX().colspan(1)
            row()
        }

        stats.add(stats2)
        stats.row()
        stats.pack()
        refreshDamageLabelText()
    }

    private fun refreshStats() {
        val uniqueStats: MutableSet<StatsEnum> = mutableSetOf()
        val otherData: MutableSet<String> = mutableSetOf()

        for (data in GlobalData.getData(GlobalDataType.PLAYERSTAT)) {
            when (data) {
                is StatsEnum -> uniqueStats.add(data)
                is String -> otherData.add(data)
            }
        }

        for (stat in uniqueStats)
            setStatLabelText(stat)

        for (other in otherData) {
            when (other) {
                "level" -> {
                    stats.findActor<TextraLabel>("level").setText("lvl ${Player.level}")
                    stats.findActor<TextraLabel>("expMax").setText("1237")
                }
            }
        }

        val expData = GlobalData.getData(GlobalDataType.PLAYEREXP)
        if (expData.isNotEmpty()) {
            stats.findActor<TextraLabel>("exp").setText("${Player.experience.roundOneDecimal()}")
            expData.clear()
        }
        val hpData = GlobalData.getData(GlobalDataType.PLAYERHP)
        if (hpData.isNotEmpty()) {
            stats.findActor<TextraLabel>("hp").setTextSameWidth("${Player.hp.roundOneDecimal()}")
            hpData.clear()
        }
        val mpData = GlobalData.getData(GlobalDataType.PLAYERMANA)
        if (mpData.isNotEmpty()) {
            stats.findActor<TextraLabel>("mp").setTextSameWidth("${Player.mp.roundOneDecimal()}")
            mpData.clear()
        }
    }

    private fun refreshDamageLabelText() {
        val biggestDamageType = maxOf(Player.damage, Player.fireDamage, Player.waterDamage, Player.earthDamage, Player.airDamage, Player.poisonDamage)
        val damageLabel = stats.findActor<TextraLabel>("damageLabel")
        when (biggestDamageType.roundToInt()) {
            Player.damage.roundToInt() -> {
                if (damageLabel.storedText != "Damage") {
                    val previousDamageType = damageLabel.storedText.substringBefore(' ')
                    val previousDamageLabel = previousDamageType.lowercase() + if (previousDamageType != "Damage") "Damage" else ""
                    val previousValue = stats.findActor<TextraLabel>("damageMax").storedText.toFloat() - Player.damageVariation
                    stats.findActor<TextraLabel>(previousDamageLabel + "Label").setTextSameWidth(previousDamageType + if (previousDamageType != "Damage") " dmg" else "")
                    stats.findActor<TextraLabel>(previousDamageLabel)
                        .setTextSameWidth(previousValue.toString())

                    damageLabel.setTextSameWidth("Damage")
                }
                stats.findActor<TextraLabel>("damage").setTextSameWidth("${(Player.damage - Player.damageVariation).roundOneDecimal()}")
                stats.findActor<TextraLabel>("damageMax").setTextSameWidth("${(Player.damage + Player.damageVariation).roundOneDecimal()}")
            }
            Player.fireDamage.roundToInt() -> {
                if (damageLabel.storedText != "Fire dmg") {
                    val previousDamageType = damageLabel.storedText.substringBefore(' ')
                    val previousDamageLabel = previousDamageType.lowercase() + if (previousDamageType != "Damage") "Damage" else ""
                    val previousValue = stats.findActor<TextraLabel>("damageMax").storedText.toFloat() - Player.damageVariation
                    stats.findActor<TextraLabel>(previousDamageLabel + "Label").setTextSameWidth(previousDamageType + if (previousDamageType != "Damage") " dmg" else "")
                    stats.findActor<TextraLabel>(previousDamageLabel)
                        .setTextSameWidth(previousValue.toString())

                    damageLabel.setTextSameWidth("Fire dmg")
                    stats.findActor<TextraLabel>("fireDamageLabel").setTextSameWidth("Damage")
                    stats.findActor<TextraLabel>("fireDamage").setTextSameWidth("${Player.damage}")
                }
                stats.findActor<TextraLabel>("damage").setTextSameWidth("${(Player.fireDamage - Player.damageVariation).roundOneDecimal()}")
                stats.findActor<TextraLabel>("damageMax").setTextSameWidth("${(Player.fireDamage + Player.damageVariation).roundOneDecimal()}")
            }
            Player.waterDamage.roundToInt() -> {
                if (damageLabel.storedText != "Water dmg") {
                    val previousDamageType = damageLabel.storedText.substringBefore(' ')
                    val previousDamageLabel = previousDamageType.lowercase() + if (previousDamageType != "Damage") "Damage" else ""
                    val previousValue = stats.findActor<TextraLabel>("damageMax").storedText.toFloat() - Player.damageVariation
                    stats.findActor<TextraLabel>(previousDamageLabel + "Label").setTextSameWidth(previousDamageType + if (previousDamageType != "Damage") " dmg" else "")
                    stats.findActor<TextraLabel>(previousDamageLabel)
                        .setTextSameWidth(previousValue.toString())

                    damageLabel.setTextSameWidth("Water dmg")
                    stats.findActor<TextraLabel>("poisonDamageLabel").setTextSameWidth("Damage")
                    stats.findActor<TextraLabel>("poisonDamage").setTextSameWidth("${Player.damage}")
                }
                stats.findActor<TextraLabel>("damage").setTextSameWidth("${(Player.waterDamage - Player.damageVariation).roundOneDecimal()}")
                stats.findActor<TextraLabel>("damageMax").setTextSameWidth("${(Player.waterDamage + Player.damageVariation).roundOneDecimal()}")
            }
            Player.earthDamage.roundToInt() -> {
                if (damageLabel.storedText != "Earth dmg") {
                    val previousDamageType = damageLabel.storedText.substringBefore(' ')
                    val previousDamageLabel = previousDamageType.lowercase() + if (previousDamageType != "Damage") "Damage" else ""
                    val previousValue = stats.findActor<TextraLabel>("damageMax").storedText.toFloat() - Player.damageVariation
                    stats.findActor<TextraLabel>(previousDamageLabel + "Label").setTextSameWidth(previousDamageType + if (previousDamageType != "Damage") " dmg" else "")
                    stats.findActor<TextraLabel>(previousDamageLabel)
                        .setTextSameWidth(previousValue.toString())

                    damageLabel.setTextSameWidth("Earth dmg")
                    stats.findActor<TextraLabel>("poisonDamageLabel").setTextSameWidth("Damage")
                    stats.findActor<TextraLabel>("poisonDamage").setTextSameWidth("${Player.damage}")
                }
                stats.findActor<TextraLabel>("damage").setTextSameWidth("${(Player.earthDamage - Player.damageVariation).roundOneDecimal()}")
                stats.findActor<TextraLabel>("damageMax").setTextSameWidth("${(Player.earthDamage + Player.damageVariation).roundOneDecimal()}")
            }
            Player.airDamage.roundToInt() -> {
                if (damageLabel.storedText != "Air dmg") {
                    val previousDamageType = damageLabel.storedText.substringBefore(' ')
                    val previousDamageLabel = previousDamageType.lowercase() + if (previousDamageType != "Damage") "Damage" else ""
                    val previousValue = stats.findActor<TextraLabel>("damageMax").storedText.toFloat() - Player.damageVariation
                    stats.findActor<TextraLabel>(previousDamageLabel + "Label").setTextSameWidth(previousDamageType + if (previousDamageType != "Damage") " dmg" else "")
                    stats.findActor<TextraLabel>(previousDamageLabel)
                        .setTextSameWidth(previousValue.toString())

                    damageLabel.setTextSameWidth("Air dmg")
                    stats.findActor<TextraLabel>("poisonDamageLabel").setTextSameWidth("Damage")
                    stats.findActor<TextraLabel>("poisonDamage").setTextSameWidth("${Player.damage}")
                }
                stats.findActor<TextraLabel>("damage").setTextSameWidth("${(Player.airDamage - Player.damageVariation).roundOneDecimal()}")
                stats.findActor<TextraLabel>("damageMax").setTextSameWidth("${(Player.airDamage + Player.damageVariation).roundOneDecimal()}")
            }
            Player.poisonDamage.roundToInt() -> {
                if (damageLabel.storedText != "Poison dmg") {
                    val previousDamageType = damageLabel.storedText.substringBefore(' ')
                    val previousDamageLabel = previousDamageType.lowercase() + if (previousDamageType != "Damage") "Damage" else ""
                    val previousValue = stats.findActor<TextraLabel>("damageMax").storedText.toFloat() - Player.damageVariation
                    stats.findActor<TextraLabel>(previousDamageLabel + "Label").setTextSameWidth(previousDamageType + if (previousDamageType != "Damage") " dmg" else "")
                    stats.findActor<TextraLabel>(previousDamageLabel)
                        .setTextSameWidth(previousValue.toString())

                    damageLabel.setTextSameWidth("Poison dmg")
                    stats.findActor<TextraLabel>("poisonDamageLabel").setTextSameWidth("Damage")
                    stats.findActor<TextraLabel>("poisonDamage").setTextSameWidth("${Player.damage}")
                }
                stats.findActor<TextraLabel>("damage").setTextSameWidth("${(Player.poisonDamage - Player.damageVariation).roundOneDecimal()}")
                stats.findActor<TextraLabel>("damageMax").setTextSameWidth("${(Player.poisonDamage + Player.damageVariation).roundOneDecimal()}")
            }
        }
    }

    private fun setStatLabelText(stat: StatsEnum) {
        when (stat) {
            StatsEnum.HPMAX -> stats.findActor<TextraLabel>("hpMax").setTextSameWidth("${Player.hpMax.roundOneDecimal()}")
            StatsEnum.MPMAX -> stats.findActor<TextraLabel>("mpMax").setTextSameWidth("${Player.mpMax.roundOneDecimal()}")
            StatsEnum.STRENGTH -> stats.findActor<TextraLabel>("strength").setTextSameWidth("${Player.strength.roundOneDecimal()}")
            StatsEnum.DEXTERITY -> stats.findActor<TextraLabel>("dexterity").setTextSameWidth("${Player.dexterity.roundOneDecimal()}")
            StatsEnum.INTELLIGENCE -> stats.findActor<TextraLabel>("intelligence").setTextSameWidth("${Player.intelligence.roundOneDecimal()}")
            StatsEnum.LUCK -> stats.findActor<TextraLabel>("luck").setTextSameWidth("${Player.luck.roundOneDecimal()}")
            StatsEnum.DAMAGE, StatsEnum.DAMAGEVARIATION -> refreshDamageLabelText()
            StatsEnum.DEFENCE -> stats.findActor<TextraLabel>("defence").setTextSameWidth("${Player.defence.roundOneDecimal()}")
            StatsEnum.EVASION -> stats.findActor<TextraLabel>("evasion").setTextSameWidth("${(Player.evasion * 100).roundToInt()}%")
            StatsEnum.ACCURACY -> stats.findActor<TextraLabel>("accuracy").setTextSameWidth("${(Player.accuracy * 100).roundToInt()}%")
            StatsEnum.CRITICALCHANCE -> stats.findActor<TextraLabel>("critChance").setTextSameWidth("${(Player.criticalChance * 100).roundToInt()}%")
            StatsEnum.CRITICALDAMAGE -> stats.findActor<TextraLabel>("critDamage").setTextSameWidth("${(Player.criticalDamage * 100).roundToInt()}%")
            StatsEnum.ATTACKSPEED -> stats.findActor<TextraLabel>("movementSpeed").setTextSameWidth("${Player.movementSpeed.roundOneDecimal()}")
            StatsEnum.MOVEMENTSPEED -> stats.findActor<TextraLabel>("attackSpeed").setTextSameWidth("${Player.attackSpeed.roundOneDecimal()}")

            StatsEnum.FIREDAMAGE -> refreshDamageLabelText()
            StatsEnum.WATERDAMAGE -> refreshDamageLabelText()
            StatsEnum.EARTHDAMAGE -> refreshDamageLabelText()
            StatsEnum.AIRDAMAGE -> refreshDamageLabelText()
            StatsEnum.POISONDAMAGE -> refreshDamageLabelText()

            StatsEnum.FIREDEFENCE -> stats.findActor<TextraLabel>("fireDefence").setTextSameWidth("${(Player.fireDefence * 100).roundToInt()}%")
            StatsEnum.WATERDEFENCE -> stats.findActor<TextraLabel>("waterDefence").setTextSameWidth("${(Player.waterDefence * 100).roundToInt()}%")
            StatsEnum.EARTHDEFENCE -> stats.findActor<TextraLabel>("earthDefence").setTextSameWidth("${(Player.earthDefence * 100).roundToInt()}%")
            StatsEnum.AIRDEFENCE -> stats.findActor<TextraLabel>("airDefence").setTextSameWidth("${(Player.airDefence * 100).roundToInt()}%")
            StatsEnum.POISONDEFENCE -> stats.findActor<TextraLabel>("poisonDefence").setTextSameWidth("${(Player.poisonDefence * 100).roundToInt()}%")

            StatsEnum.RANGE -> {}
            StatsEnum.RANGETYPE -> {}
            StatsEnum.STEALTH -> {}
        }
    }

    private fun getCellDrawable(cellNumber: Int, rows: Int): Drawable {
        if (cellNumber >= Player.inventory.size)
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

    private fun getSkillsCellDrawable(cellNumber: Int, rows: Int): Drawable {
        if (cellNumber == 0)
            return TextureRegionDrawable(uiElements["cellTopLeft"])
        if (cellNumber < 5)
            return TextureRegionDrawable(uiElements["cellTop"])
        val bottomRowNumber = cellNumber - (rows - 1) * 5
        if (bottomRowNumber == 0 )
            return TextureRegionDrawable(uiElements["cellBottomLeft"])
        if (bottomRowNumber in 1..4 )
            return TextureRegionDrawable(uiElements["cellBottom"])
        if (cellNumber % 5 == 0)
            return TextureRegionDrawable(uiElements["cellLeft"])
        return TextureRegionDrawable(uiElements["cellMiddle"])
    }

    private fun addInventory() {
        val borderWidth: Int = 12
        val borderHeight: Int = 12

        var rows = Player.inventory.size / 10 + if (Player.inventory.size % 10 != 0) 1 else 0
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
        inventory = ScrollPane(table)
        inventory.name = "inventory"
        // without this line, scrollPane generously adds idiotic and undeletable empty space for each column with children in it
        inventory.setScrollingDisabled(true, false)
        inventory.setOverscroll(false, false)
        inventory.setScrollbarsVisible(false)
        inventory.layout()

        addActor(inventory)
        inventory.width = border.width - 2 * (borderWidth - 2)
        inventory.height = border.height - 2 * borderHeight + 4
        inventory.setPosition(inventory.x + 2, inventory.y + 2)
    }

    /** Adds tabs of the UI */
    private fun addTabs() {
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
        (inventory.actor as Table).children.forEach {
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
        mainTabsGroup.setPosition(border.x, border.y + border.heightScaled())
        openTabsGroup.setPosition(border.x, border.y + border.heightScaled())
        sortingTabsGroup.setPosition(border.x, border.y + border.heightScaled())

        border.roundPosition()
        inventory.roundPosition()
        equipment.roundPosition()
        skills.roundPosition()
        quests.roundPosition()
        map.roundPosition()
        mainTabsGroup.roundPosition()
        openTabsGroup.roundPosition()
        sortingTabsGroup.roundPosition()
    }

    /** ======================================================================================================================================================
                                                                    Equipment
    */

    fun refreshGoldInEquipment() {
        val goldActor = equipmentMap[EquipmentType.MONEY]!!.actor as EqActor
        val prevAmount = goldActor.item.amount!!
        var goldAmount = 0
        Player.inventory.itemList.forEach { if (it.item is Gold) goldAmount += it.item.amount!! }
        goldActor.item.amount = goldAmount
        if (prevAmount != goldAmount)
            goldActor.refreshAmount()
    }

    fun refreshEquipment(type: EquipmentType) {
        val item: Item? = Player.equipment.getEquipped(type)

        if (item != null)
            equipmentMap[type]!!.actor = EqActor(item)
        else if (equipmentMap[type]!!.hasChildren())
            equipmentMap[type]!!.removeActorAt(0, false)
    }

    /** ======================================================================================================================================================
                                                                    Skills
    */

    private fun addSkills() {
        skills.name = "skills"
        skills.addActor(Image(uiElements["Background"]))

        var rows = Player.skills.size / 10 + if (Player.skills.size % 10 != 0) 1 else 0
        rows = if (rows < 6) 6 else rows

        val table = scene2d.table {
            this.setFillParent(false)
            clip(true)
            for (n in 0 until rows) {
                for (i in 0 until 5) {
                    add(container {
                        val cellNumber = n * 5 + i
                        name = (cellNumber).toString()
                        background = getSkillsCellDrawable(cellNumber, cellNumber)
                        align(Align.bottomLeft)
                    }).size(84f, 84f).space(0f)
                }
                row().space(0f)
            }
        }
        table.pack()

        val skillsPane = ScrollPane(table)
        skillsPane.name = "skillsPane"
        // without this line, scrollPane generously adds idiotic and undeletable empty space for each column with children in it
        skillsPane.setScrollingDisabled(true, false)
        skillsPane.setOverscroll(false, false)
        skillsPane.setScrollbarsVisible(false)
        skillsPane.layout()

        skills.addActor(skillsPane)
        skillsPane.width = border.width / 2 - 10
        skillsPane.height = border.height - 2 * 12 + 4
        skillsPane.setPosition(skills.x + 12, skills.y + 12)

        addActor(skills)
        skills.width = border.width
        skills.height = border.height
        skills.isVisible = false
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

    private var currentScreen: Group = Group()

    private fun activateTab() {
        // Don't change the screen if sorting was clicked
        if (!hoveredTab!!.name.startsWith("Sorting")) {
            when (activeTab.name) {
                "InventoryOpen" -> inventory.isVisible = false
                "EquipmentOpen" -> equipment.isVisible = false
                "SkillsOpen" -> skills.isVisible = false
                "QuestsOpen" -> quests.isVisible = false
                "MapOpen" -> map.isVisible = false
            }

            when (hoveredTab!!.name) {
                "InventoryClosed" -> {
                    inventory.isVisible = true
                    currentScreen = inventory
                }
                "EquipmentClosed" -> {
                    equipment.isVisible = true
                    currentScreen = equipment
                    refreshGoldInEquipment()
                    refreshStats()
                }
                "SkillsClosed" -> {
                    skills.isVisible = true
                    currentScreen = skills
                }
                "QuestsClosed" -> {
                    quests.isVisible = true
                    currentScreen = quests
                }
                "MapClosed" -> {
                    map.isVisible = true
                    currentScreen = map
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
                activeTab = openTabsGroup.children.find { it.name == currentScreen.name.replaceFirstChar { it.uppercaseChar() }.plus("Open") }!!
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
            if (forceRefreshInventory) {
                refreshInventory()
                forceRefreshInventory = false
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
        }

        // New tab was clicked
        if (openTabsGroup.children.find { it.name == "SortingOpen" }?.isInUnscaled(coord.x - openTabsGroup.x, coord.y - openTabsGroup.y) == true)
            hoveredTab = mainTabsGroup.children.find { it.name == "SortingClosed" }
        if (hoveredTab != activeTab && hoveredTab != null && button == Input.Buttons.LEFT) {
            activateTab()
        }

        if (forceRefreshInventory) {
            refreshInventory()
            forceRefreshInventory = false
        }

        return super.touchUp(screenX, screenY, pointer, button)
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        val coord: Vector2 = screenToStageCoordinates(
            Vector2(screenX.toFloat(), screenY.toFloat())
        )

        // move tabs
        var tab = getTabByPosition(screenX.toFloat(), screenY.toFloat())
        if (border.isIn(screenX.toFloat(), screenY.toFloat()))
            tab = null

        if (tab != null)
            tab!!.moveTab(true)
        if (hoveredTab != null)
            hoveredTab!!.moveTab(false)

        hoveredTab = tab

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
}