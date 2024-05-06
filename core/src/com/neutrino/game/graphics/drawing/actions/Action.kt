package com.neutrino.game.graphics.drawing.actions

import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.DrawerAttribute
import com.neutrino.game.graphics.drawing.layers.LayeredDraw
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.map.chunk.ChunkManager
import com.neutrino.game.util.equalsDelta

sealed class Action(
    protected var length: Float = 0f
) {

    protected var totalTime = 0f

    /** @return true if action is finished */
    abstract fun update(delta: Float): Boolean
    protected fun addTime(delta: Float) { totalTime += delta }
    protected fun getActionFrame(delta: Float): Float = delta / length
    protected fun isActionFinished(): Boolean = (length - totalTime).equalsDelta(0f) || (length - totalTime) < 0f

    interface UsesLayeredDraw {
        var layeredDraw: LayeredDraw?
    }
    interface UsesEntity {
        var entity: Entity?
    }

    class Sequence(vararg actions: Action): Action() {
        val actions = ArrayList<Action>()
        init {
            this.actions.addAll(actions)
        }

        override fun update(delta: Float): Boolean {
            actions.first().update(delta)
            if (actions.first().isActionFinished())
                actions.removeFirst()
            return actions.isEmpty()
        }
    }

    class Delay(time: Float): Action(time) {

        override fun update(delta: Float): Boolean {
            totalTime += delta
            return isActionFinished()
        }
    }

    class MoveBy(val x: Float, val y: Float, length: Float = 0f):
        Action(length), UsesLayeredDraw, UsesEntity {

        override var layeredDraw: LayeredDraw? = null
        override var entity: Entity? = null

        private var targetX = x
        private var targetY = y

        override fun update(delta: Float): Boolean {
            if (totalTime == 0f) {
                if (entity != null) {
                    targetX = entity!!.get(DrawPosition::class)!!.x + x
                    targetY = entity!!.get(DrawPosition::class)!!.y + y
                } else if (layeredDraw != null) {
                    targetX = layeredDraw!!.xOffset + x
                    targetY = layeredDraw!!.yOffset + y
                }
            }

            if (layeredDraw != null) {
                layeredDraw!!.xOffset += x * getActionFrame(delta)
                layeredDraw!!.yOffset += y * getActionFrame(delta)
            }
            if (entity != null) {
                entity!!.get(DrawPosition::class)!!.x += x * getActionFrame(delta)
                entity!!.get(DrawPosition::class)!!.y += y * getActionFrame(delta)
            }

            totalTime += delta
            if (isActionFinished()) {
                layeredDraw?.xOffset = targetX
                layeredDraw?.yOffset = targetY
                entity?.get(DrawPosition::class)?.x = targetX
                entity?.get(DrawPosition::class)?.y = targetY
            }

            return isActionFinished()
        }
    }

    class FadeOut(length: Float): Action(length), UsesLayeredDraw, UsesEntity {

        override var layeredDraw: LayeredDraw? = null
        override var entity: Entity? = null

        // Entity always begints at 1f
        var initialAlpha: Float = 1f

        override fun update(delta: Float): Boolean {
            if (layeredDraw != null) {
                if (initialAlpha == 1f)
                    initialAlpha = layeredDraw!!.alpha
                layeredDraw!!.alpha -= initialAlpha * getActionFrame(delta)
            }
            if (entity != null) {
                val layeredDraws = (entity!!.get(DrawerAttribute::class)?.drawer ?:
                    ChunkManager.getDrawer(entity!!.get(Position::class)!!.chunk)).getTextures(entity!!)

                layeredDraws.forEach {
                    it.alpha -= initialAlpha * getActionFrame(delta)
                }
            }
            totalTime += delta
            return isActionFinished()
        }
    }

    class Delete: Action(), UsesLayeredDraw {

        override var layeredDraw: LayeredDraw? = null

        override fun update(delta: Float): Boolean {
            layeredDraw?.detach()
            return true
        }
    }

    class Custom(private val function: () -> Unit): Action() {
        override fun update(delta: Float): Boolean {
            function.invoke()
            return true
        }
    }


}