package com.neutrino.game.graphics.drawing.layers

import com.badlogic.gdx.graphics.g2d.Batch
import com.neutrino.game.utility.Optimize

open class DrawableGroup(
    val stackable: Boolean = false,
    val addAbove: Boolean = true,
    xOffset: Float = 0f,
    yOffset: Float = 0f,
    z: Int = 0
): Drawable(xOffset, yOffset, z) {

    private val children: ArrayList<Drawable> = ArrayList()

    fun add(drawable: Drawable, yMargin: Float = 0f): DrawableGroup {
        drawable.entity = entity
        drawable.onEntityAttached()
        drawable.addToGroup()
        if (stackable) {
            drawable.yOffset = yMargin
            children.add(drawable)
            return this
        }
        var yOffset = yMargin
        children.lastOrNull()?.let { yOffset += it.height + it.yOffset }
        if (!addAbove)
            yOffset *= -1
        drawable.yOffset = yOffset
        children.add(drawable)
        return this
    }

    override fun draw(batch: Batch, x: Float, y: Float, parentAlpha: Float) {
        val x = x + getX()
        val y = y + getY()
        children.forEach { it.draw(batch, x, y, parentAlpha * alpha) }
    }

    override var width: Int
        set(value) {}
        get() {
            var width = 0
            children.forEach {
                val newWidth = it.width
                if (newWidth > width)
                    width = newWidth
            }
            return width
        }

    override var height: Int
        set(value) {}
        get() {
            var height = 0
            if (stackable) {
                children.forEach {
                    val newHeight = it.yOffset.toInt() + it.height
                    if (newHeight > height)
                        height = newHeight
                }
            } else
                children.forEach { height += it.yOffset.toInt() + it.height }
            return height
        }

    override fun drawDebug(batch: Batch, x: Float, y: Float, parentAlpha: Float) {
        val xChild = x + getX()
        val yChild = y + getY()
        children.forEach { it.drawDebug(batch, xChild, yChild, parentAlpha * alpha) }
//        super.drawDebug(batch, x, y, alpha)
        if (debug) {
            if (Defaults.drawer?.batch != batch) {
                setDrawer(batch)
//                drawer = ShapeDrawer(batch, textureRegion)
//                drawer?.setColor(0.1f, 0.85f, 0.15f, 1f)
            }
            @Optimize
            drawer!!.rectangle(x + getX(), y + getY(), width.toFloat(), height.toFloat())
        }
    }

    override fun attach() {
        super.attach()
        children.forEach { it.addToGroup() }
    }

}