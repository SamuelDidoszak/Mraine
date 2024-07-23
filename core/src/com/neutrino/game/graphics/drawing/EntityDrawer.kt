package com.neutrino.game.graphics.drawing

import com.badlogic.gdx.scenes.scene2d.Group
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.shared.attributes.Drawables
import com.neutrino.game.entities.shared.attributes.Shaders
import com.neutrino.game.entities.shared.attributes.StitchedSprite
import com.neutrino.game.entities.util.Cloneable
import com.neutrino.game.graphics.drawing.drawables.*
import com.neutrino.game.graphics.shaders.ShaderParametered
import com.neutrino.game.graphics.textures.AnimatedTextureSprite
import com.neutrino.game.graphics.textures.Light
import com.neutrino.game.graphics.textures.TextureSprite
import java.util.*

abstract class EntityDrawer: Group() {

    protected val animations: Animations = Animations(this)
    val lights: ArrayList<Pair<DrawableTexture, Light>> = ArrayList()
    protected val drawableLayers: SortedMap<Int, LayeredDrawableList> = sortedMapOf()
    val actingDrawables: ArrayList<ActingDrawable> = ArrayList()

    abstract val map: List<List<MutableList<Entity>>>

    open fun addTexture(entity: Entity, texture: TextureSprite) {
        if (drawableLayers[texture.z] == null)
            drawableLayers[texture.z] = LayeredDrawableList()

        val drawableTexture =
            if (entity has StitchedSprite::class)
                DrawableTextureUnsorted(entity, texture)
            else
                DrawableTexture(entity, texture)

        addDrawableDetails(drawableTexture)

        entity.get(Shaders::class)?.shaders?.forEach {
            if (entity.get(Drawables::class)?.getBaseTextures()?.isNotEmpty() == true && it is Cloneable<*>)
                drawableTexture.addShader(it.clone() as ShaderParametered)
            else
                drawableTexture.addShader(it)
        }

        drawableLayers[texture.z]!!.add(drawableTexture)
        if (entity hasNot Drawables::class)
            entity.addAttribute(Drawables())
        entity.get(Drawables::class)!!.addDrawable(drawableTexture)
    }

    open fun addDrawable(drawable: Drawable)  {
        if (drawableLayers[drawable.z] == null)
            drawableLayers[drawable.z] = LayeredDrawableList()

        addDrawableDetails(drawable)

        drawable.entity.get(Shaders::class)?.shaders?.forEach { drawable.addShader(it) }
        drawableLayers[drawable.z]!!.add(drawable)
        if (drawable.entity hasNot Drawables::class)
            drawable.entity.addAttribute(Drawables())
        drawable.entity.get(Drawables::class)!!.addDrawable(drawable)
    }

    fun removeDrawable(drawable: Drawable) {
        drawable.entity.get(Drawables::class)?.removeDrawable(drawable)
        drawableLayers[drawable.z]?.remove(drawable)
        removeDrawableDetails(drawable)
    }

    fun removeTexture(entity: Entity, texture: TextureSprite) {
        val drawable = entity.get(Drawables::class)?.removeDrawable {
            it.entity == entity && it is DrawableTexture && it.texture == texture }
        drawableLayers[texture.z]?.remove(drawable)
        removeDrawableDetails(drawable!!)
    }

    fun addDrawableDetails(drawable: Drawable) {
        if (drawable is DrawableTexture)
            addDrawableTextureDetails(drawable)
        if (drawable is ActingDrawable)
            actingDrawables.add(drawable)
    }

    fun removeDrawableDetails(drawable: Drawable) {
        if (drawable is DrawableTexture)
            removeDrawableTextureDetails(drawable)
        if (drawable is ActingDrawable)
            actingDrawables.remove(drawable)
    }

    private fun addDrawableTextureDetails(drawable: DrawableTexture) {
        if (drawable.texture is AnimatedTextureSprite)
            animations.add(drawable)
        if (drawable.texture.lights == null)
            return

        val texture = drawable.texture
        if (texture.lights!!.isSingleLight)
            lights.add(Pair(drawable, texture.lights!!.getLight()))
        else {
            for (light in texture.lights!!.getLights()!!) {
                lights.add(Pair(drawable, light))
            }
        }
    }

    private fun removeDrawableTextureDetails(drawable: DrawableTexture) {
        if (drawable.texture is AnimatedTextureSprite)
            animations.remove(drawable)
        if (drawable.texture.lights == null)
            return

        val texture = drawable.texture
        if (texture.lights!!.isSingleLight) {
            val index = lights.indexOfFirst { it.first == drawable }
            lights.removeAt(index)
        } else
            lights.removeIf { it.first == drawable }
    }

    override fun act(delta: Float) {
        super.act(delta)
        animations.play(delta)
        actingDrawables.forEach { it.act(delta) }
    }
}