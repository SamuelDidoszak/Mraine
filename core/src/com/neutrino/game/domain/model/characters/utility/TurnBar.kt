package com.neutrino.game.domain.model.characters.utility

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.neutrino.game.domain.model.characters.Player
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.math.sign

class TurnBar(
    var currentTurn: Double,
    var playerTurn: Double,
    var movement: Double
): Actor() {
    val textureRegion: TextureRegion = TextureRegion(Texture("whitePixel.png"), 0, 0, 1, 1)
    val baseColor: Color =
        when (sign(movement.compareTo(Player.movementSpeed).toDouble())) {
            -1.0 -> Color.WHITE
            0.0 -> Color.CLEAR
            1.0 -> Color.LIGHT_GRAY
            else -> {Color.CYAN}
        }

    var size = 60f

    init {
        name = "turnBar"
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        // required for fading
        val color = pickColor()
        color.a = parentAlpha
        val drawer = ShapeDrawer(batch, textureRegion)
        drawer.filledRectangle(2f, -26f, size, 2f, color)
    }

    fun pickColor(): Color {
        if (currentTurn + movement <= playerTurn + Player.movementSpeed)
            return Color(240f, 113f, 120f, 1f)
        if (currentTurn > playerTurn + Player.movementSpeed)
            return Color.SKY
        return baseColor
    }

    override fun setSize(width: Float, height: Float) {
        super.setSize(width, height)
        size = width
    }
}