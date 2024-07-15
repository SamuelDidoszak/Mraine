package com.neutrino.game.graphics.drawing.drawables

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.DrawerAttribute
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.entities.util.Cloneable
import com.neutrino.game.entities.util.Equality
import com.neutrino.game.graphics.drawing.actions.Action
import com.neutrino.game.graphics.drawing.actions.Actions
import com.neutrino.game.graphics.shaders.OutlineShader
import com.neutrino.game.graphics.shaders.ShaderParametered
import com.neutrino.game.graphics.textures.AnimatedTextureSprite
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.map.chunk.ChunkManager
import com.neutrino.game.util.Constants
import com.neutrino.game.utility.Optimize
import space.earlygrey.shapedrawer.ShapeDrawer

abstract class Drawable(
    var xOffset: Float = 0f,
    var yOffset: Float = 0f,
    var z: Int = 1
): Attribute() {

    open var width: Int = 0
    open var height: Int = 0
    var alpha: Float = 1f
    var debug = false
    private var isAttached = false
    private var group: DrawableGroup? = null

    protected var shaders: ArrayList<ShaderParametered>? = null

    protected companion object Defaults {
        val drawPosition = DrawPosition()
        @JvmStatic
        protected val textureRegion: TextureRegion = TextureRegion(Constants.WhitePixel, 0, 0, 1, 1)
        @JvmStatic
        protected var drawer: ShapeDrawer? = null
    }

    protected var drawPosition: DrawPosition = Defaults.drawPosition

    var centerOnEntity: Boolean = false
        set(value) {
            field = value
            if (!isAttached)
                return
            val texture = entity.get(Texture::class)
            val entityWidth = if (texture?.textures?.get(0) is AnimatedTextureSprite)
                (texture.textures[0] as AnimatedTextureSprite).animationWidth
            else
                entity.get(Texture::class)?.getWidthScaled() ?: 0
            val centeredWidth =
                if (width <= entityWidth)
                    (entityWidth - width) / 2f
                else
                    (-1f * width / 2f) + (entityWidth / 2f)

            if (centerOnEntity)
                xOffset += centeredWidth
            else
                xOffset -= centeredWidth
        }

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

    operator fun compareTo(value: Drawable): Int {
        return compareValues(getY(), value.getY())
    }

    fun setSize(width: Int, height: Int): Drawable {
        this.width = width
        this.height = height
        return this
    }

    fun setPosition(xOffset: Float, yOffset: Float): Drawable {
        this.xOffset = xOffset
        this.yOffset = yOffset
        return this
    }

    open fun attach() {
        if (isAttached)
            return
        entity.get(DrawPosition::class)?.let { drawPosition = it }
        val drawer = entity.get(DrawerAttribute::class)?.drawer ?: entity.get(Position::class)?.chunk?.let { ChunkManager.getDrawer(it) }
        drawer?.addDrawable(this)?.also { isAttached = true }
    }

    open fun detach() {
        drawPosition = Defaults.drawPosition
        val drawer = entity.get(DrawerAttribute::class)?.drawer ?: entity.get(Position::class)?.chunk?.let { ChunkManager.getDrawer(it) }
        drawer?.removeDrawable(this)
        isAttached = false
        group?.removeDrawable(this)
    }

    fun addToGroup(group: DrawableGroup) {
        this.group = null
        detach()
        isAttached = true
        this.group = group
    }

    fun initialize(newEntity: Entity) {
        entity = newEntity
        onEntityAttached()
        attach()

        val texture = entity.get(Texture::class)
        val entityWidth = if (texture?.textures?.get(0) is AnimatedTextureSprite)
                (texture.textures[0] as AnimatedTextureSprite).animationWidth
            else
                entity.get(Texture::class)?.getWidthScaled() ?: 0
        val centeredWidth =
            if (width <= entityWidth)
                (entityWidth - width) / 2f
            else
                (-1f * width / 2f) + (entityWidth / 2f)

        if (centerOnEntity)
            xOffset += centeredWidth
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
        if (action is Action.UsesDrawable)
            action.drawable = this
        if (action is Action.Sequence)
            action.actions.forEach {
                if (it is Action.UsesDrawable)
                    it.drawable = this
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

    fun addShader(shader: ShaderParametered) {
        if (shaders == null)
            shaders = ArrayList()
        shaders!!.add(shader)

        if (shader is OutlineShader && this is DrawableTexture)
            shader.setTexture(this)
    }

    fun getShaders(): List<ShaderParametered>? = shaders

    fun removeShader(shader: ShaderParametered) {
        shaders?.remove(shader)
        if (shader is Cloneable<*> && shader is Equality<*>)
            shaders?.removeAll { it::class == shader::class && (shader as Equality<ShaderParametered>).isEqual(it) }
    }

    fun drawShaders(batch: Batch, x: Float, y: Float, parentAlpha: Float) {
        shaders?.forEach {
            it.applyToBatch(batch)
            if (this is CustomShaderDraw)
                drawShader(batch, x, y, parentAlpha)
            else
                draw(batch, x, y, parentAlpha)
            it.cleanUp(batch)
        }
    }
}