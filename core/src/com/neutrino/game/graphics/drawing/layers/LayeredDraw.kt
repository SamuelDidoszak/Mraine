package com.neutrino.game.graphics.drawing.layers

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.DrawerAttribute
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.map.chunk.ChunkManager
import com.neutrino.game.util.Constants
import com.neutrino.game.utility.Optimize
import space.earlygrey.shapedrawer.ShapeDrawer

abstract class LayeredDraw(
    var xOffset: Float = 0f,
    var yOffset: Float = 0f,
    var z: Int = 0
): Attribute() {

    open var width: Int = 0
    open var height: Int = 0
    var debug = false

    private companion object Defaults {
        val drawPosition = DrawPosition()
        private val textureRegion: TextureRegion = TextureRegion(Constants.WhitePixel, 0, 0, 1, 1)
        private var drawer: ShapeDrawer? = null
    }

    protected var drawPosition: DrawPosition = Defaults.drawPosition

    abstract fun draw(batch: Batch, x: Float, y: Float, alpha: Float)

    /** Returns scaled x position including map placement */
    open fun getX(): Float {
        return drawPosition.x + xOffset
    }

    /** Returns scaled y position including map placement */
    open fun getY(): Float {
        return drawPosition.y + yOffset
    }

    open fun getYSort(): Float {
        return getY()
    }

    operator fun compareTo(value: LayeredDraw): Int {
        return compareValues(getY(), value.getY())
    }

    fun setSize(width: Int, height: Int): LayeredDraw {
        this.width = width
        this.height = height
        return this
    }

    fun attach() {
        drawPosition = entity.get(DrawPosition::class)!!
        val drawer = entity.get(DrawerAttribute::class)?.drawer ?: entity.get(Position::class)?.chunk?.let { ChunkManager.getDrawer(it) }
        drawer?.addLayeredDraw(this)
    }

    fun detach() {
        val drawer = entity.get(DrawerAttribute::class)?.drawer ?: entity.get(Position::class)?.chunk?.let { ChunkManager.getDrawer(it) }
        drawer?.removeLayeredDraw(this)
    }

    @Optimize
    open fun drawDebug(batch: Batch, x: Float, y: Float, alpha: Float) {
        draw(batch, x, y, alpha)
        if (debug) {
            if (drawer?.batch != batch) {
                drawer = ShapeDrawer(batch, textureRegion)
                drawer!!.setColor(0.1f, 0.85f, 0.15f, 1f)
            }
            @Optimize
            drawer!!.rectangle(x + getX(), y + getY(), width.toFloat(), height.toFloat())
        }
    }
}