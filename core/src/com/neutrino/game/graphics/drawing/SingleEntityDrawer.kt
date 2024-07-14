package com.neutrino.game.graphics.drawing

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.*
import com.neutrino.game.graphics.drawing.drawables.Drawable
import com.neutrino.game.graphics.drawing.drawables.DrawableTexture
import com.neutrino.game.graphics.shaders.OutlineShader
import com.neutrino.game.graphics.shaders.ShaderPrograms
import com.neutrino.game.graphics.textures.TextureSprite
import com.neutrino.game.graphics.textures.Textures
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.map.chunk.Chunk
import com.neutrino.game.map.chunk.ChunkCoords
import com.neutrino.game.util.Constants
import com.neutrino.game.util.Constants.SCALE
import kotlin.math.min
import kotlin.random.Random

class SingleEntityDrawer(entity: Entity,
                         private val fillSpace: Boolean = true): EntityDrawer() {

    var centered = true
    private var scale = SCALE
    private var offsetX = 0f
    private var offsetY = 0f

    private companion object {
        val fakeChunk: Chunk = Chunk(ChunkCoords(Int.MAX_VALUE, Int.MAX_VALUE, Int.MAX_VALUE))
    }

    override var map: List<List<MutableList<Entity>>> = getEmptyEntityList()
    var entity: Entity = entity
        set(value) {
            if (drawableLayers.isNotEmpty())
                drawableLayers.clear()
            field = value
            map[1][1][0] = field
            field.addAttribute(DrawPosition())
            field.addAttribute(Position(1, 1, fakeChunk))
            field.addAttribute(DrawerAttribute(this))
            val textureAttribute = field.get(Texture::class) ?:
            field.addAttribute(Texture { _, _, _ ->}).get(Texture::class)!!
            textureAttribute.textures.clear()
            textureAttribute.setTextures(null, Random)
            if (textureAttribute.textures.isEmpty()) {
                System.err.println(entity.name + " has no texture set!")
                textureAttribute.textures.add(Textures.get("backgroundTexture"))
            }
            if (fillSpace)
                updateScale()

            field.get(Shaders::class)?.shaders?.forEach { shader ->
                if (shader is OutlineShader) {
                    entity.get(Drawables::class)?.getBaseTextures()?.forEach { it.removeShader(shader) }
                }
            }
        }

    init {
        this.entity = entity
    }

    override fun addTexture(entity: Entity, texture: TextureSprite) {
        super.addTexture(entity, texture)
        drawableLayers[texture.z]!!.sort()
    }

    override fun addDrawable(drawable: Drawable) {
        super.addDrawable(drawable)
        drawableLayers[drawable.z]!!.sort()
    }

    private fun getEmptyEntityList(): List<List<MutableList<Entity>>> {
        val list = arrayListOf<ArrayList<MutableList<Entity>>>()
        for (y in 0 until 9) {
            list.add(arrayListOf())
            for (x in 0 until 9) {
                list[y].add(mutableListOf())
                val entity = Entity()
                entity.id = -1
                entity.addAttribute(Identity.Any())
                list[y][x].add(entity)
            }
        }
        return list
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        val clipBegin = clipBegin()
        val textures = entity.get(Texture::class)!!.textures
        for (texture in textures) {
            if (texture.z == 0) {
                batch!!.draw(texture.texture,
                    if (!texture.mirrorX) x + texture.x * scale + offsetX
                    else x + texture.x * scale + offsetX + texture.texture.regionWidth * scale,
                    y + texture.y * scale + offsetY,
                    texture.texture.regionWidth * if (!texture.mirrorX) scale else -1 * scale,
                    texture.texture.regionHeight * scale)

                entity.get(Shaders::class)?.shaders?.forEach {
                    it.applyToBatch(batch)
                    batch.draw(texture.texture,
                        if (!texture.mirrorX) x + texture.x * scale + offsetX
                        else x + texture.x * scale + offsetX + texture.texture.regionWidth * scale,
                        y + texture.y * scale + offsetY,
                        texture.texture.regionWidth * if (!texture.mirrorX) scale else -1 * scale,
                        texture.texture.regionHeight * scale)
                    it.cleanUp(batch)
                }
            }
        }
        for (layer in drawableLayers) {
            for (layeredTexture in layer.value) {
//                layeredTexture.draw(batch!!, x, y, parentAlpha)
                val texture = (layeredTexture as DrawableTexture).texture
                batch!!.draw(texture.texture,
                    if (!texture.mirrorX) x + texture.x * scale + offsetX
                    else x + texture.x * scale + offsetX + layeredTexture.texture.width() * scale,
                    y + texture.y * scale + offsetY,
                    layeredTexture.texture.width() * if (!texture.mirrorX) scale else -1 * scale,
                    layeredTexture.texture.height() * scale)

                layeredTexture.getShaders()?.forEach {
                    it.applyToBatch(batch)
                    batch.draw(texture.texture,
                        if (!texture.mirrorX) x + texture.x * scale + offsetX
                        else x + texture.x * scale + offsetX + layeredTexture.texture.width() * scale,
                        y + texture.y * scale + offsetY,
                        layeredTexture.texture.width() * if (!texture.mirrorX) scale else -1 * scale,
                        layeredTexture.texture.height() * scale)
                    it.cleanUp(batch)
                }
            }
        }
        drawLights(batch)
        if (clipBegin)
            clipEnd()
    }

    private fun drawLights(batch: Batch?) {
        batch?.shader = ShaderPrograms.lightShader
        batch?.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
        for (light in lights) {
            val radius = light.second.radius / 4 * scale
            batch?.color = light.second.color
            batch?.draw(
                Constants.WhitePixel,
                x + offsetX + light.second.x * scale - radius + SCALE / 2,
                y + offsetY + light.second.y * scale - radius + SCALE / 2,
                2 * radius, 2 * radius
            )
        }

        batch?.shader = null
        batch?.color = Color(1f, 1f, 1f, 1f)
        batch?.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun setSize(width: Float, height: Float) {
        super.setSize(width, height)
        if (fillSpace)
            updateScale()
    }

    private fun updateScale() {
        val textures = entity.get(Texture::class)!!.textures
        // maybe add abs(it.x)
        val maxWidth = textures.maxOf { it.width() + it.x * if(centered) 2 else 1 }
        val maxHeight = textures.maxOf { it.height() + it.y }

        val wScale: Float = width / maxWidth
        val hScale: Float = height / maxHeight
        scale = min(wScale, hScale)

        if (!centered) {
            offsetX = 0f
            offsetY = 0f
            return
        }

        offsetX = (width - maxWidth * scale) / 2
        offsetY = (height - maxHeight * scale) / 2
    }
}