package com.neutrino.game.domain.model.characters.utility

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.equalsDelta
import com.neutrino.game.lessThanDelta
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.math.sign

class TurnBar(
    private var currentTurn: Double,
    private var playerTurn: Double,
    private var movement: Double
): Actor() {
    private val textureRegion: TextureRegion = TextureRegion(Texture("whitePixel.png"), 0, 0, 1, 1)
    private var drawer: ShapeDrawer? = null
    private var baseColor: Color =
        when (sign(movement.compareTo(Player.movementSpeed).toDouble())) {
            -1.0 -> Color.BLACK
            0.0 -> Color.CLEAR
            1.0 -> Color.WHITE
            else -> {Color.CYAN}
        }

    private var size = 60f

    init {
        name = "turnBar"
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (drawer == null) {
            drawer = ShapeDrawer(batch, textureRegion)
            drawer!!.setColor(pickColor())
        }
        // required for fading
        if (parentAlpha.lessThanDelta(1.0f)) {
            drawer!!.setColor(pickColor(parentAlpha))
        }

        drawer!!.filledRectangle(2f, -26f, size, 2f)
    }

    fun update(currentTurn: Double, playerTurn: Double, movement: Double, forceBaseColorChange: Boolean = false) {
        this.currentTurn = currentTurn
        this.playerTurn = playerTurn
        if (!this.movement.equalsDelta(movement) || forceBaseColorChange) {
            baseColor =
                when (sign(movement.compareTo(Player.movementSpeed).toDouble())) {
                    -1.0 -> Color.BLACK
                    0.0 -> Color.CLEAR
                    1.0 -> Color.WHITE
                    else -> {
                        Color.CYAN
                    }
                }
        }
        drawer?.setColor(pickColor())
    }

    private fun pickColor(a: Float = 1f): Color {
        if (currentTurn + movement <= playerTurn + Player.movementSpeed)
            return Color(240f, 113f, 120f, a)
        if (currentTurn > playerTurn + Player.movementSpeed)
            return Color.SKY
        baseColor.a = a
        return baseColor
    }

    override fun setSize(width: Float, height: Float) {
        super.setSize(width, height)
        size = width
    }

    override fun remove(): Boolean {
        textureRegion.texture.dispose()
        return super.remove()
    }
}