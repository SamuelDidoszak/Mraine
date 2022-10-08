package com.neutrino.game.domain.model.characters.utility

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import kotlin.math.round
import kotlin.random.Random

class DamageNumber: TextraLabel("", KnownFonts.getStandardFamily()) {
    private val moveAction = Actions.moveBy(0f, 36f, 1f)
    private val disappearAction = Actions.sequence(
        Actions.fadeOut(1.25f),
        Actions.removeActor())

    init {
        name = "damage"
        font = KnownFonts.getCozette()
    }

    fun init(color: String, damage: Float) {
        val damageDecimal = round((damage % 1) * 10).toInt()
        setText("[$color][%150][*]${damage.toInt()}" +
                if (damageDecimal != 0) ".$damageDecimal" else ""
        )
        setPosition(Random.nextFloat() * parent.width * 0.8f, Random.nextFloat() * parent.height / 3 + parent.height / 4)
        addAction(moveAction)
        addAction(disappearAction)
    }
}