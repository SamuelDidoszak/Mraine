package com.neutrino.game.domain.use_case.characters

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.Font
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TypingLabel
import com.neutrino.game.domain.model.characters.Character
import ktx.actors.centerPosition

class GetNameLabels(
    private val characterMap: MutableList<Character>,
    private val font: Font
) {
    operator fun invoke(): List<TypingLabel> {
        val nameLabels: List<TypingLabel> = characterMap.map { TypingLabel("[@Cozette][GREEN][_]" + it.name + "[@title] cock", KnownFonts.getStandardFamily()) }
        for (i in 0 until characterMap.size) {
            nameLabels[i].alignment = Align.center
            nameLabels[i].setPosition(characterMap[i].x - nameLabels[i].width / 2, characterMap[i].y + 32)
//            println(nameLabels[i].width)
        }
        return nameLabels
    }
}