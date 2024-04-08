package com.neutrino.game.graphics.drawing.layers

import com.badlogic.gdx.graphics.g2d.Batch
import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.DrawerAttribute
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.map.chunk.ChunkManager

abstract class LayeredDraw(
    var xOffset: Float = 0f,
    var yOffset: Float = 0f,
    var z: Int = 0
): Attribute() {

    open var width: Int = 0
    open var height: Int = 0

    private companion object Defaults {
        val drawPosition = DrawPosition()
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
}