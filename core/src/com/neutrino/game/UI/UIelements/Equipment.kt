package com.neutrino.game.UI.UIelements

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
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
import ktx.scene2d.container
import ktx.scene2d.scene2d
import ktx.scene2d.table
import java.util.*

class Equipment(private val uiElements: Map<String, TextureAtlas.AtlasRegion>): Group() {

    val stats: Stats = Stats()

    private lateinit var equipmentTable: Table
    private var equipmentMap: EnumMap<Equipment.EquipmentType, Container<Actor>> = EnumMap(Equipment.EquipmentType::class.java)

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
        addActor(equipmentTable)

        equipmentTable.width = 336f
        equipmentTable.height = 448f
        equipmentTable.setPosition(border.width - equipmentTable.width - 12 - 8, 38f)

        // Initialize Gold actor
        equipmentMap[Equipment.EquipmentType.MONEY]!!.actor = EqActor(Items.new("Gold"))
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
        // TODO ECS Items
        val goldList = Player.get(Inventory::class)!!.getAll { item: Entity -> item.id == Items.getId("Gold") }
        goldList?.forEach { goldAmount += it.get(Amount::class)!!.amount }
        goldActor.amount = goldAmount
        if (prevAmount != goldAmount)
            goldActor.refreshAmount()
    }

    fun refreshEquipment(type: Equipment.EquipmentType) {
        val item: Entity? = Player.get(Equipment::class)!!.getEquipped(type)

        // TODO ECS ITEMS EQUIPMENT UI
        if (item != null)
            equipmentMap[type]!!.actor = EqActor(item)

        else if (equipmentMap[type]!!.hasChildren())
            equipmentMap[type]!!.removeActorAt(0, false)
    }
}