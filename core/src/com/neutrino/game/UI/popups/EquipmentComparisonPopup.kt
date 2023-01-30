package com.neutrino.game.UI.popups

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.Constants
import com.neutrino.game.Fonts
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.items.EquipmentItem
import com.neutrino.game.domain.model.systems.event.EventToPrintableMapper
import com.neutrino.game.graphics.utility.ColorUtils.toTextraColor
import ktx.scene2d.Scene2DSkin
import java.lang.Float.max

class EquipmentComparisonPopup(val item: EquipmentItem): Table() {

    init {
        val equippedItem = Player.equipment.getEquipped(item.getEquipmentType())
        if (equippedItem != null)
            add(addItemScreen(equippedItem, item)).width(256f).top()
        add(addItemScreen(item, equippedItem)).width(256f).top()
        layout()
        println("Heights")
        var maxHeight = 0f
        children.forEach { maxHeight = max(maxHeight, (it as Table).minHeight) }
        height = maxHeight
    }


    private fun addItemScreen(item: EquipmentItem, itemToCompare: EquipmentItem?): Table {
        val table = Table()
        val itemName = TextraLabel("[BLACK]" + item.name, Fonts.EQUIPMENT)
        itemName.wrap = true
        itemName.alignment = Align.center

        val description = TextraLabel("[%75]" + item.description, Fonts.MATCHUP, Color.BLACK)
        description.wrap = true
        description.alignment = Align.left

        table.add(itemName).growX().center().colspan(10).spaceBottom(12f)
        table.row()
        table.add(description).growX().colspan(10).spaceBottom(12f)
        table.row()

        for (wrapper in item.modifierList) {
            val printable = EventToPrintableMapper.getPrintable(wrapper)
            val twoColumns = printable.second.toString().isNotEmpty()

            val secondItemEvent = itemToCompare?.modifierList?.find { it.event == wrapper.event }
            val color =
                if (secondItemEvent != null)
                    ValueComparison().compareStats(
                        wrapper.event.toString().substringAfterLast(' ').toFloatOrNull() ?: 0f,
                        secondItemEvent.event.toString().substringAfterLast(' ').toFloatOrNull() ?: 0f)
                else
                    Color.BLACK.toTextraColor()

            val value = TextraLabel("[%75]$color" + printable.first, Fonts.MATCHUP)
            value.wrap = true
            value.alignment = Align.left
            table.add(value).growX().colspan(if (twoColumns) 1 else 10).spaceBottom(8f)
            if (!twoColumns) {
                table.row()
                continue
            }

            val valueLabel = TextraLabel("[%75]$color" + printable.second.toString(), Fonts.MATCHUP, Color.BLACK)
            table.add(valueLabel).right().spaceBottom(8f)
            table.row()
        }

        table.row()

        val requirements = item.requirements
        requirements.set("character", Player)
        if (!requirements.checkAll()) {
            table.add(TextraLabel("Requirements", Fonts.EQUIPMENT, Color.BLACK)).expandX().center().colspan(10).spaceTop(12f).spaceBottom(12f)
            table.row()

            for (data in item.requirements.getPrintable(true)) {
                val dataLabel = TextraLabel("[%75]" + data.first, Fonts.MATCHUP, Color.BLACK)
                dataLabel.wrap = true
                dataLabel.alignment = Align.left
                val valueLabel = TextraLabel("[%75]" + data.second, Fonts.MATCHUP, Color.BLACK)
                table.add(dataLabel).growX().spaceBottom(8f)
                table.add(valueLabel).spaceBottom(8f)
                table.row()
            }
        }
        requirements.set("character", null)

        val goldImage = Image(Constants.DefaultIconTexture.findRegion("gold"))
        val goldValue =
            TextraLabel("[%75]" + Color.BLACK.toTextraColor() + item.goldValue, Fonts.EQUIPMENT)
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