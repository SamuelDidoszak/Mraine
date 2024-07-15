package com.neutrino.game.UI.popups

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.characters.attributes.Equipment
import com.neutrino.game.entities.items.Item
import com.neutrino.game.entities.items.attributes.EquipmentItem
import com.neutrino.game.entities.items.attributes.GoldValue
import com.neutrino.game.entities.items.attributes.usable.EquipEvents
import com.neutrino.game.entities.items.attributes.usable.UseOnEntity
import com.neutrino.game.entities.shared.attributes.Description
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.requirements.PrintableInfo
import com.neutrino.game.entities.systems.requirements.Requirements
import com.neutrino.game.graphics.textures.Textures
import com.neutrino.game.graphics.utility.ColorUtils.toTextraColor
import com.neutrino.game.util.Fonts
import com.neutrino.game.util.add
import ktx.scene2d.Scene2DSkin
import java.lang.Float.max

class EquipmentComparisonPopup(val item: Item): Table() {

    init {
        val equippedItem = Player.get(Equipment::class)!!.getEquipped(item.get(EquipmentItem::class)!!.getEquipmentType()) as Item?
        if (equippedItem != null)
            add(addItemScreen(equippedItem, item)).width(256f).top()
        add(addItemScreen(item, equippedItem)).width(256f).top()
        layout()
        var maxHeight = 0f
        children.forEach { maxHeight = max(maxHeight, (it as Table).minHeight) }
        height = maxHeight
    }


    private fun addItemScreen(item: Item, itemToCompare: Item?): Table {
        val table = Table()
        val itemName = TextraLabel("[BLACK]" + item.name, Fonts.EQUIPMENT)
        itemName.wrap = true
        itemName.alignment = Align.center

        table.add(itemName).growX().center().colspan(10).spaceBottom(12f)
        table.row()

        if (item has Description::class) {
            val description = TextraLabel("[%75]" + item.get(Description::class)!!.description, Fonts.MATCHUP, Color.BLACK)
            description.wrap = true
            description.alignment = Align.left
            table.add(description).growX().colspan(10).spaceBottom(12f)
            table.row()
        }

        for (attribute in item.getItemAttributes()) {
            if (attribute !is PrintableInfo<*> || attribute is UseOnEntity || attribute is EquipEvents)
                continue

            val alternateAttributes = Entity().addAttribute(OffensiveStats(accuracy = 1f, attackSpeed = 1.0))

            val printableList = (attribute as PrintableInfo<Attribute>).getPrintableInfo(itemToCompare?.get(attribute::class) ?: alternateAttributes.get(attribute::class))
            val skippedMinMax: ArrayList<String> = ArrayList()
            for (printable in printableList) {
                if (skippedMinMax.find { it == printable.first } != null)
                    continue

                var minMaxPrintable: String? = null
                if (printable.first.endsWith("Min")) {
                    minMaxPrintable = printable.second.toString() + " - "
                    val maxString = printable.first.replace("Min", "Max")
                    val maxVal = printableList.find { it.first ==  maxString}
                    if (maxVal != null) {
                        minMaxPrintable += maxVal.second.toString()
                        skippedMinMax.add(maxVal.first)
                    } else
                        minMaxPrintable = null
                } else if (printable.first.endsWith("Max")) {
                    val minString = printable.first.replace("Max", "Min")
                    val minVal = printableList.find { it.first ==  minString}
                    if (minVal == null)
                        minMaxPrintable = null
                    else {
                        minMaxPrintable += minVal.second.toString() + " - " + printable.second.toString()
                        skippedMinMax.add(minVal.first)
                    }
                }

                val twoColumns = printable.second != null

                val value = TextraLabel("[%75]" +
                        if (minMaxPrintable != null)
                            printable.first.substring(0, printable.first.length - 3)
                        else printable.first,
                    Fonts.MATCHUP, Color.BLACK)
                value.wrap = true
                value.alignment = Align.left
                table.add(value).growX().colspan(if (twoColumns) 1 else 10).spaceBottom(8f)
                if (!twoColumns) {
                    table.row()
                    continue
                }

                val valueLabel = TextraLabel("[%75]" + (minMaxPrintable ?: printable.second.toString()), Fonts.MATCHUP)
                valueLabel.alignment = Align.center
                table.add(valueLabel).center().spaceBottom(8f)
                table.row()
            }
        }

        table.row()

        val eventAttributes = ArrayList<Attribute>()
        eventAttributes.add(item.get(UseOnEntity::class))
        eventAttributes.add(item.get(EquipEvents::class))
        for (attribute in eventAttributes) {
            val printableList = (attribute as PrintableInfo<Attribute>).getPrintableInfo(itemToCompare?.get(attribute::class))
            for (printable in printableList) {
                val value = TextraLabel("[%75]" + printable.first, Fonts.MATCHUP, Color.BLACK)
                value.wrap = true
                value.alignment = Align.center
                table.add(value).center().growX().colspan(10).spaceBottom(8f)
                table.row()

                val valueLabel = TextraLabel("[%75]" + printable.second.toString(), Fonts.MATCHUP)
                valueLabel.alignment = Align.left
                valueLabel.wrap = true
                table.add(valueLabel).left().growX().colspan(10).spaceBottom(8f)
                table.row()
            }
        }

        val requirements: ArrayList<Pair<String, String>> = ArrayList()
        item.get(Requirements.Stats::class)?.print(Player)?.forEach { requirements.add(it) }
        item.get(Requirements.Custom::class)?.print(Player)?.forEach { requirements.add(it) }
        if (requirements.isNotEmpty()) {
            table.add(TextraLabel("Requirements", Fonts.EQUIPMENT, Color.BLACK)).expandX().center().colspan(10).spaceTop(12f).spaceBottom(12f)
            table.row()
        }

        for (requirement in requirements) {
            val dataLabel = TextraLabel("[%75]" + requirement.first, Fonts.MATCHUP, Color.BLACK)
            dataLabel.wrap = true
            dataLabel.alignment = Align.left
            val valueLabel = TextraLabel("[%75]" + requirement.second, Fonts.MATCHUP, Color.BLACK)
            table.add(dataLabel).growX().spaceBottom(8f)
            table.add(valueLabel).right().spaceBottom(8f)
            table.row()
        }

        val goldImage = Image(Textures.get("gold1").texture)
        val goldValue =
            TextraLabel("[%75]" + Color.BLACK.toTextraColor() + item.get(GoldValue::class)!!.value, Fonts.EQUIPMENT)
        goldValue.alignment = Align.bottom
        val goldGroup = Table()
        goldGroup.add(goldImage).size(24f)
        goldGroup.add(goldValue).spaceLeft(8f).bottom()
        table.add(goldGroup).expandX().colspan(10).right().spaceTop(12f)

        table.background = Scene2DSkin.defaultSkin.getDrawable("stretchableCell")
        table.name = "itemDetails"
        table.width = 256f
        table.layout()
        table.height = minHeight
        table.layout()
        return table
    }
}