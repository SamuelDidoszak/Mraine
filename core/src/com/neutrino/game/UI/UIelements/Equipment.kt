package com.neutrino.game.UI.UIelements

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.neutrino.game.UI.utility.EqActor
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.Items
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.characters.attributes.Equipment
import com.neutrino.game.entities.characters.attributes.Inventory
import com.neutrino.game.entities.items.attributes.Amount
import com.neutrino.game.entities.shared.attributes.Shaders
import ktx.scene2d.container
import ktx.scene2d.scene2d
import ktx.scene2d.table
import java.util.*

class Equipment(private val uiElements: Map<String, TextureAtlas.AtlasRegion>): Group() {

    val stats: Stats = Stats()

    lateinit var equipmentTable: Table
    private var equipmentMap: EnumMap<Equipment.EquipmentType, Container<Actor>> = EnumMap(Equipment.EquipmentType::class.java)
    val equipmentUi = ScrollPane(Table())

    fun initialize(border: Image) {
        addEquipment(border)

        stats.initialize(border)
        addActor(stats)
        stats.setPosition(24f, 24f)
    }


    private fun addEquipment(border: Image) {
        name = "equipment"
        addActor(Image(uiElements["EquipmentScreen"]))

        val namesList: List<Pair<String, Equipment.EquipmentType>> = listOf(
            Pair("hands", Equipment.EquipmentType.HANDS), Pair("head", Equipment.EquipmentType.HEAD),
            Pair("amulet", Equipment.EquipmentType.AMULET), Pair("lHand", Equipment.EquipmentType.LHAND),
            Pair("torso", Equipment.EquipmentType.TORSO), Pair("rHand", Equipment.EquipmentType.RHAND),
            Pair("lRing", Equipment.EquipmentType.LRING), Pair("legs", Equipment.EquipmentType.LEGS),
            Pair("rRing", Equipment.EquipmentType.RRING), Pair("money", Equipment.EquipmentType.MONEY),
            Pair("feet", Equipment.EquipmentType.FEET), Pair("bag", Equipment.EquipmentType.BAG)
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
        equipmentUi.actor = equipmentTable
        addActor(equipmentUi)
        equipmentUi.width = 336f
        equipmentUi.height = 448f
        equipmentUi.setPosition(border.width - equipmentUi.width - 12 - 8, 38f)
        equipmentUi.name = "equipment"

        equipmentTable.width = 336f
        equipmentTable.height = 448f

        equipmentUi.setScrollingDisabled(true, false)
        equipmentUi.setOverscroll(false, false)
        equipmentUi.setScrollbarsVisible(false)
        equipmentUi.layout()

        // Initialize Gold actor
        equipmentMap[Equipment.EquipmentType.MONEY]!!.actor = EqActor(Items.new("Gold"))
        (equipmentMap[Equipment.EquipmentType.MONEY]!!.actor as EqActor).entity.removeAttribute(Shaders::class)
        (equipmentMap[Equipment.EquipmentType.MONEY]!!.actor as EqActor).setScale(1.25f, 1.25f)
        (equipmentMap[Equipment.EquipmentType.MONEY]!!.actor as EqActor).amount = 0
        (equipmentMap[Equipment.EquipmentType.MONEY]!!.actor as EqActor).refreshAmount()
    }

    private fun setEquipmentDrawable(type: Equipment.EquipmentType) {
        val item: Entity? = Player.get(Equipment::class)!!.getEquipped(type)

        if (item != null)
            equipmentMap[type]!!.background = TextureRegionDrawable(uiElements["equipmentDefault"])
        else
            equipmentMap[type]!!.background = TextureRegionDrawable(uiElements["equipmentDefault"])
    }

    fun refreshGoldInEquipment() {
        val goldActor = equipmentMap[Equipment.EquipmentType.MONEY]!!.actor as EqActor
        val prevAmount = goldActor.amount
        var goldAmount = 0
        val goldList = Player.get(Inventory::class)!!.getAll { item: Entity -> item.id == Items.getId("Gold") }
        goldList?.forEach { goldAmount += it.get(Amount::class)!!.amount }
        goldActor.amount = goldAmount
        if (prevAmount != goldAmount)
            goldActor.refreshAmount()
    }

    fun refreshEquipment(type: Equipment.EquipmentType) {
        val item: Entity? = Player.get(Equipment::class)!!.getEquipped(type)

        if (item != null) {
            val eqActor = EqActor(item)
            eqActor.setScale(1.25f, 1.25f)
            equipmentMap[type]!!.actor = eqActor
        }
        else if (equipmentMap[type]!!.hasChildren())
            equipmentMap[type]!!.removeActorAt(0, false)
    }
}