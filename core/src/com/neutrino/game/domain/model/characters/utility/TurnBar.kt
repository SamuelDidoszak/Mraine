package com.neutrino.game.domain.model.characters.utility

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.neutrino.game.Constants
import com.neutrino.game.compareDelta
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.equalsDelta
import com.neutrino.game.lessThanDelta
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.math.abs
import kotlin.math.sign

class TurnBar(
    private var characterTurn: Double,
    private var movementSpeed: Double
): Actor() {
    private val textureRegion: TextureRegion = TextureRegion(Texture("whitePixel.png"), 0, 0, 1, 1)
    private var drawer: ShapeDrawer? = null
    private var baseColor: Color = Color.CLEAR

    private var slowerThanPlayer = Player.movementSpeed <= movementSpeed
    private var barCount: Float = if (slowerThanPlayer) (movementSpeed / Player.movementSpeed).toFloat() else (Player.movementSpeed / movementSpeed).toFloat()
    private var size = 0f

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

        drawer!!.filledRectangle(2f, -26f, size, 8f)
    }

    fun update(characterTurn: Double, movementSpeed: Double, playerSpeedChanged: Boolean = false) {
        this.characterTurn = characterTurn
        if (!this.movementSpeed.equalsDelta(movementSpeed) || playerSpeedChanged) {
            baseColor =
                when (sign(movementSpeed.compareTo(Player.movementSpeed).toDouble())) {
                    -1.0 -> Color.BLACK
                    0.0 -> Color.CLEAR
                    1.0 -> Color.WHITE
                    else -> {
                        Color.BROWN
                    }
                }
            this.movementSpeed = movementSpeed
            slowerThanPlayer = Player.movementSpeed <= movementSpeed
            barCount = if (slowerThanPlayer) (movementSpeed / Player.movementSpeed).toFloat() else (Player.movementSpeed / movementSpeed).toFloat()
        }
        drawer?.setColor(pickColor())

        size =
            if (slowerThanPlayer)
                ((characterTurn - Player.turn) / Player.movementSpeed).toFloat()
            else
                (abs(Player.turn - characterTurn) / movementSpeed).toFloat()
//                ((characterTurn - Player.turn) / movementSpeed).toFloat()
        println("Size%: $size")
        size *= 60f / barCount
        addAction(Actions.sizeTo(size, 8f, Constants.MoveSpeed))
    }

    private fun pickColor(a: Float = 1f): Color {
        if ((characterTurn + movementSpeed).compareDelta(Player.turn + Player.movementSpeed) == -1)
            return Color.FIREBRICK
        if (characterTurn.compareDelta(Player.turn + Player.movementSpeed) == 1)
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