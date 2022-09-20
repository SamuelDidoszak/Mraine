package com.neutrino.game.presentation.utility

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.entities.utility.Entity

class EntityLookupPopup(entityList: MutableList<Entity>, character: Character?): Table() {
    init {
        if (character != null) {
            this.row()
            this.add(
                TextraLabel("[%75]" + character.name, KnownFonts.getStandardFamily())
            ).fillX().left()
        }
        for (entity in entityList.reversed()) {
            this.row()
            this.add(
                TextraLabel("[%75]" + entity.name, KnownFonts.getStandardFamily())
            ).fillX().left()
        }
        name = "entityPopup"
        this.align(Align.bottomLeft)
    }
}