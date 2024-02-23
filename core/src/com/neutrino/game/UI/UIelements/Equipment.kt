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
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.items.EquipmentType
import com.neutrino.game.domain.model.items.Item
import ktx.scene2d.container
import ktx.scene2d.scene2d
import ktx.scene2d.table
import java.util.*

class Equipment(private val uiElements: Map<String, TextureAtlas.AtlasRegion>): Group() {

    val stats: Stats = Stats()

    private lateinit var equipmentTable: Table
    private var equipmentMap: EnumMap<EquipmentType, Container<Actor>> = EnumMap(EquipmentType::class.java)

    fun initialize(border: Image) {
        addEquipment(border)

        stats.initialize(border)
        addActor(stats)
        stats.setPosition(24f, 24f)
    }


    private fun addEquipment(border: Image) {
        name = "equipment"
        addActor(Image(uiElements["EquipmentScreen"]))

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
        addActor(equipmentTable)

        equipmentTable.width = 336f
        equipmentTable.height = 448f
        equipmentTable.setPosition(border.width - equipmentTable.width - 12 - 8, 38f)

        // Initialize Gold actor
        // TODO ECS Items
//        equipmentMap[EquipmentType.MONEY]!!.actor = EqActor(Gold())
//        (equipmentMap[EquipmentType.MONEY]!!.actor as EqActor).item.amount = 0
//        (equipmentMap[EquipmentType.MONEY]!!.actor as EqActor).refreshAmount()
    }

    private fun setEquipmentDrawable(type: EquipmentType) {
        val item: Item? = Player.equipment.getEquipped(type)

        if (item != null)
            equipmentMap[type]!!.background = TextureRegionDrawable(uiElements["equipmentDefault"])
        else
            equipmentMap[type]!!.background = TextureRegionDrawable(uiElements["equipmentDefault"])
    }

    fun refreshGoldInEquipment() {
//        val goldActor = equipmentMap[EquipmentType.MONEY]!!.actor as EqActor
//        val prevAmount = goldActor.item.amount!!
//        var goldAmount = 0
        // TODO ECS Items
//        Player.inventory.itemList.forEach { if (it.item is Gold) goldAmount += it.item.amount!! }
//        goldActor.item.amount = goldAmount
//        if (prevAmount != goldAmount)
//            goldActor.refreshAmount()
    }

    fun refreshEquipment(type: EquipmentType) {
        val item: Item? = Player.equipment.getEquipped(type)

        if (item != null)
            equipmentMap[type]!!.actor = EqActor(item)
        else if (equipmentMap[type]!!.hasChildren())
            equipmentMap[type]!!.removeActorAt(0, false)
    }
}