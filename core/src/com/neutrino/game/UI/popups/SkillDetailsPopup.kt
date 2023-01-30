package com.neutrino.game.UI.popups

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.Fonts
import com.neutrino.game.domain.model.systems.skills.Skill
import com.neutrino.game.domain.model.systems.skills.SkillType
import com.neutrino.game.graphics.utility.ColorUtils
import ktx.scene2d.Scene2DSkin

class SkillDetailsPopup(val skill: Skill): Table() {
    init {
        val skillName = TextraLabel(skill.name, Fonts.EQUIPMENT, getTreeColor(skill.skillType))
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

        for (data in skill.printableData) {
            val dataLabel = TextraLabel("[%75]" + data.first, Fonts.MATCHUP, Color.BLACK)
            dataLabel.alignment = Align.left
            dataLabel.wrap = true
            val valueLabel = TextraLabel("[%75]" + data.second.invoke().toString(), Fonts.MATCHUP, Color.BLACK)
            add(dataLabel).growX()
            add(valueLabel)
            row().space(8f)
        }

        background = Scene2DSkin.defaultSkin.getDrawable("stretchableCell")

        width = 256f
        layout()
        height = minHeight
        layout()
    }

    private fun getTreeColor(skillType: SkillType): Color {
        return when (skillType) {
            SkillType.STRENGTH -> ColorUtils.STRENGTH
            SkillType.DEXTERITY -> ColorUtils.DEXTERITY
            SkillType.INTELLIGENCE -> ColorUtils.INTELLIGENCE
            SkillType.SUMMONING -> ColorUtils.SUMMONING
        }
    }
}