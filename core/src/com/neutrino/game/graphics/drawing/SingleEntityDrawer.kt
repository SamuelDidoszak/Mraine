package com.neutrino.game.graphics.drawing

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.neutrino.game.util.Constants
import com.neutrino.game.util.Constants.SCALE
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.entities.shared.attributes.StitchedSprite
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.graphics.shaders.Shaders
import com.neutrino.game.graphics.textures.AnimatedTextureSprite
import com.neutrino.game.graphics.textures.Light
import com.neutrino.game.graphics.textures.TextureSprite
import com.neutrino.game.graphics.textures.Textures
import com.neutrino.game.map.attributes.OnMapPosition
import com.neutrino.game.map.attributes.Position
import java.util.*
import kotlin.math.min
import kotlin.random.Random

class SingleEntityDrawer(entity: Entity): Actor(), EntityDrawer {

    override val animations: Animations = Animations()
    override val lights: ArrayList<Pair<Entity, Light>> = ArrayList()
    // Optimize
    private val textureLayers: SortedMap<Int, LayeredTextureList> = sortedMapOf()

    var centered = true
    private var scale = SCALE
    private var offsetX = 0f
    private var offsetY = 0f

    override var map: List<List<MutableList<Entity>>> = getEmptyEntityList()
    private var entity: Entity = entity
        set(value) {
            if (textureLayers.isNotEmpty())
                textureLayers.clear()
            field = value
            map[1][1][0] = field
            field.addAttribute(Position())
            field.addAttribute(OnMapPosition(1, 1, this))
            val textureAttribute = field.get(Texture::class) ?:
            field.addAttribute(Texture { _, _, _ ->}).get(Texture::class)!!
            textureAttribute.setTextures(null, Random)
            if (textureAttribute.textures.isEmpty()) {
                System.err.println(entity.name + " has no texture set!")
                textureAttribute.textures.add(Textures.get("backgroundTexture"))
            }
            if (textureAttribute.textures.size == 1) {
                val texture = textureAttribute.textures.first()
                if (texture.lights != null) {
                    if (texture is AnimatedTextureSprite) {
                        for (i in 0 until texture.lights!!.getLightArraySize()) {
                            for (light in texture.lights!!.getLights(i)!!)
                                light.xyDiff(-1 * texture.x, -1 * texture.y)
                        }
                    } else {
                        for (light in texture.lights!!.getLights()!!) {
                            light.xyDiff(-1 * texture.x, -1 * texture.y)
                        }
                    }
                }
                texture.xy(0f, 0f)
            }
            updateScale()
        }

    init {
        this.entity = entity
    }

    override fun addTexture(entity: Entity, texture: TextureSprite) {
        if (textureLayers[texture.z] == null)
            textureLayers[texture.z] = LayeredTextureList()
        if (entity has StitchedSprite::class)
            textureLayers[texture.z]!!.add(LayeredTextureUnsorted(entity, texture))
        else
            textureLayers[texture.z]!!.add(LayeredTexture(entity, texture))
        textureLayers[texture.z]!!.sort()
    }

    override fun removeTexture(entity: Entity, texture: TextureSprite) {
        textureLayers[texture.z]!!.removeIf { it.entity == entity && it.texture == texture }
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
            if (texture.z == 0)
                batch!!.draw(texture.texture,
                    if (!texture.mirrorX) x + texture.x * scale + offsetX
                    else x + texture.x * scale + offsetX + texture.texture.regionWidth * scale,
                    y + texture.y * scale + offsetY,
                    texture.texture.regionWidth * if (!texture.mirrorX) scale else -1 * scale,
                    texture.texture.regionHeight * scale)
        }
        for (layer in textureLayers) {
            for (layeredTexture in layer.value) {
                val texture = layeredTexture.texture
                batch!!.draw(texture.texture,
                    if (!texture.mirrorX) x + texture.x * scale + offsetX
                    else x + texture.x * scale + offsetX + layeredTexture.texture.width() * scale,
                    y + texture.y * scale + offsetY,
                    layeredTexture.texture.width() * if (!texture.mirrorX) scale else -1 * scale,
                    layeredTexture.texture.height() * scale)
            }
        }
        drawLights(batch)
        if (clipBegin)
            clipEnd()
    }

    private fun drawLights(batch: Batch?) {
        batch?.shader = Shaders.lightShader
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

    override fun act(delta: Float) {
        super.act(delta)
        animations.play(delta)
    }

    override fun setSize(width: Float, height: Float) {
        super.setSize(width, height)
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