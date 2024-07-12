package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.graphics.drawing.drawables.DrawableText

class Name: DrawableText(width = 64) {

    override fun onEntityAttached() {
        text.setText("[@Cozette][WHITE][%175]${entity.name}")
    }
}