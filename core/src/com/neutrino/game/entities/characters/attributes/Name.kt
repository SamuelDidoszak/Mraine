package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.graphics.drawing.layers.LayeredText

class Name: LayeredText(width = 64) {

    override fun onEntityAttached() {
        text.setText("[@Cozette][WHITE][%175]${entity.name}")
    }
}