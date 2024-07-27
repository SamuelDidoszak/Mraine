package com.neutrino.game.UI.popups

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.systems.skills.Skill
import com.neutrino.game.entities.systems.skills.SkillType
import com.neutrino.game.graphics.utility.ColorUtils
import com.neutrino.game.util.Fonts
import ktx.scene2d.Scene2DSkin

class SkillDetailsPopup(val skill: Skill): Table() {
    init {
        val skillName = TextraLabel(skill.name, Fonts.EQUIPMENT, ColorUtils.getSkillTypeColor(skill.skillType))
        skillName.wrap = true
        skillName.alignment = Align.center
        val description = TextraLabel("[%75]" + skill.description, Fonts.MATCHUP, Color.BLACK)
        description.wrap = true
        description.alignment = Align.left

        add(skillName).growX().center().colspan(10)
        row().space(12f)
        add(description).growX().colspan(10)

        row().padTop(12f)
        row().space(8f).padBottom(0f)

        printSKillData(skill)

        background = Scene2DSkin.defaultSkin.getDrawable("squareBackground")

        width = 256f
        layout()
        height = minHeight
        layout()
    }

    private fun printSKillData(skill: Skill) {
        val printableList = skill.getPrintableInfo(null)
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
            add(value).growX().colspan(if (twoColumns) 1 else 10).spaceBottom(8f)
            if (!twoColumns) {
                row()
                continue
            }

            val valueLabel = TextraLabel("[%75]" + (minMaxPrintable ?: printable.second.toString()), Fonts.MATCHUP, extractSkillColor(printable.first))
            valueLabel.alignment = Align.center
            add(valueLabel).center().spaceBottom(8f)
            row()
        }

        if (skill.requirements?.map { it.check(Player) }?.any { it == false } != true)
            return

        val requirements: ArrayList<Pair<String, String>> = ArrayList()
        skill.requirements?.forEach { it.print(Player).forEach { requirements.add(it) } }

        add(TextraLabel("Requirements", Fonts.EQUIPMENT, Color.BLACK)).expandX().center().colspan(10)
        row().padTop(8f)
        row().space(8f).padBottom(0f)

        for (requirement in requirements) {
            val dataLabel = TextraLabel("[%75]" + requirement.first, Fonts.MATCHUP, Color.BLACK)
            dataLabel.wrap = true
            dataLabel.alignment = Align.left
            val valueLabel = TextraLabel("[%75]" + requirement.second, Fonts.MATCHUP, Color.BLACK)
            add(dataLabel).growX().spaceBottom(8f)
            add(valueLabel).right().spaceBottom(8f)
            row()
        }
    }

    private fun extractSkillColor(first: String): Color {
        val colorString = first.substringAfter('[').substringBefore(']')
        if (colorString.first() != '#')
            return Color.BLACK
        else
            return Color.valueOf(colorString)
    }
}