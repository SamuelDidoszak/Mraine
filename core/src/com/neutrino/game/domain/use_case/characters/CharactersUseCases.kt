package com.neutrino.game.domain.use_case.characters

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.github.tommyettinger.textra.Font
import com.neutrino.game.domain.model.characters.Character

data class CharactersUseCases(
    val characterMap: MutableList<Character>,
    val font: Font,
    val addCharacter: AddCharacter = AddCharacter(characterMap),
    val getNameLabels: GetNameLabels = GetNameLabels(characterMap, font),
    val addInfoGroup: AddInfoGroup = AddInfoGroup(characterMap, font)
)