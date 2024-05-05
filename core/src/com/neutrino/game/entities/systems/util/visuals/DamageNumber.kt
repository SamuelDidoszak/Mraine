package com.neutrino.game.entities.systems.util.visuals

import com.github.tommyettinger.textra.KnownFonts
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.graphics.drawing.actions.Action
import com.neutrino.game.graphics.drawing.layers.LayeredText
import kotlin.math.round
import kotlin.random.Random

class DamageNumber: LayeredText("") {
    init {
        text.font = KnownFonts.getCozette()
        z = 2
    }

    fun init(color: String, damage: Float) {
        val damageDecimal = round((damage % 1) * 10).toInt()
        text.setText("[$color][%150][*]${damage.toInt()}" +
                if (damageDecimal != 0) ".$damageDecimal" else ""
        )
        xOffset = Random.nextFloat() * entity.get(Texture::class)!!.getWidthScaled() * 0.8f
        yOffset = Random.nextFloat() * entity.get(Texture::class)!!.getHeightScaled() / 3 + entity.get(Texture::class)!!.getHeightScaled() / 4
        addAction(Action.MoveBy(0f, 36f, 1f))
        addAction(Action.Sequence(
            Action.FadeOut(1.25f),
            Action.Delete()
        ))
    }
}