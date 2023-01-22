package com.neutrino.game.UI.popups

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.entities.utility.Entity
import com.neutrino.game.graphics.utility.BackgroundColor

class EntityLookupPopup(entityList: MutableList<Entity>, character: Character?): Table() {
    init {
        pad(8f)
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
        pack()
        this.align(Align.bottomLeft)
    }

    /** Made as a separate function in order to create the texture in the right place. Otherwise, it shows up for a split second at 0, 0 screen coordinates */
    fun assignBg(x: Float, y: Float) {
        val bgColor: BackgroundColor = BackgroundColor("UI/whiteColorTexture.png", x, y, width, height)
        bgColor.setColor(0, 0, 0, 160)
        this.background = bgColor
    }
}