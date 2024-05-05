package com.neutrino.game.graphics.drawing.layers

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.DrawerAttribute
import com.neutrino.game.graphics.drawing.actions.Action
import com.neutrino.game.graphics.drawing.actions.Actions
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.map.chunk.ChunkManager
import com.neutrino.game.util.Constants
import com.neutrino.game.utility.Optimize
import space.earlygrey.shapedrawer.ShapeDrawer

abstract class LayeredDraw(
    var xOffset: Float = 0f,
    var yOffset: Float = 0f,
    var z: Int = 1
): Attribute() {

    open var width: Int = 0
    open var height: Int = 0
    var alpha: Float = 1f
    var debug = false
    private var isAttached = false

    protected companion object Defaults {
        val drawPosition = DrawPosition()
        @JvmStatic
        protected val textureRegion: TextureRegion = TextureRegion(Constants.WhitePixel, 0, 0, 1, 1)
        @JvmStatic
        protected var drawer: ShapeDrawer? = null
    }

    protected var drawPosition: DrawPosition = Defaults.drawPosition

    abstract fun draw(batch: Batch, x: Float, y: Float, parentAlpha: Float)

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

    fun setPosition(xOffset: Float, yOffset: Float): LayeredDraw {
        this.xOffset = xOffset
        this.yOffset = yOffset
        return this
    }

    open fun attach() {
        if (isAttached)
            return
        entity.get(DrawPosition::class)?.let { drawPosition = it }
        val drawer = entity.get(DrawerAttribute::class)?.drawer ?: entity.get(Position::class)?.chunk?.let { ChunkManager.getDrawer(it) }
        drawer?.addLayeredDraw(this)?.also { isAttached = true }
    }

    open fun detach() {
        drawPosition = Defaults.drawPosition
        val drawer = entity.get(DrawerAttribute::class)?.drawer ?: entity.get(Position::class)?.chunk?.let { ChunkManager.getDrawer(it) }
        drawer?.removeLayeredDraw(this)
        isAttached = false
    }

    fun addToGroup() {
        detach()
        isAttached = true
    }

    fun initialize(newEntity: Entity) {
        entity = newEntity
        onEntityAttached()
        attach()
    }

    @Optimize
    open fun drawDebug(batch: Batch, x: Float, y: Float, parentAlpha: Float) {
        draw(batch, x, y, parentAlpha)
        if (debug) {
            if (drawer?.batch != batch) {
                drawer = ShapeDrawer(batch, textureRegion)
                drawer!!.setColor(0.1f, 0.85f, 0.15f, 1f)
            }
            @Optimize
            drawer!!.rectangle(x + getX(), y + getY(), width.toFloat(), height.toFloat())
        }
    }

    protected fun setDrawer(batch: Batch) {
        drawer = ShapeDrawer(batch, textureRegion)
        drawer!!.setColor(0.1f, 0.85f, 0.15f, 1f)
    }

    fun addAction(action: Action, timeout: Float = 0f) {
        if (action is Action.UsesLayeredDraw)
            action.layeredDraw = this
        if (action is Action.Sequence)
            action.actions.forEach {
                if (it is Action.UsesLayeredDraw)
                    it.layeredDraw = this
            }

        if (timeout != 0f) {
            Actions.addAction(
                Action.Sequence(
                    Action.Delay(timeout),
                    action
                ))
            return
        }
        Actions.addAction(action)
    }

    protected fun Batch.setAlpha(alpha: Float) {
        this.setColor(this.color.r, this.color.g, this.color.b, alpha)
    }
}