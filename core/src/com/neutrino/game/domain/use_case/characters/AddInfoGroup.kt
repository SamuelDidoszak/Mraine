package com.neutrino.game.domain.use_case.characters

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.Font
import com.github.tommyettinger.textra.TypingLabel
import com.neutrino.game.domain.model.characters.Character

class AddInfoGroup(
    private val characterMap: MutableList<Character>,
    private val font: Font
) {
    operator fun invoke(): List<Group> {
        val groupList: List<Group> = characterMap.map { Group() }
        val nameLabels: List<TypingLabel> = GetNameLabels(characterMap, font)()
        for (i in 0 until characterMap.size) {
            nameLabels[i].alignment = Align.center
            groupList[i].setPosition(characterMap[i].x - groupList[i].width / 2, characterMap[i].y + 32, Align.center)
            groupList[i].addActor(nameLabels[i])
        }
        return groupList
    }
}