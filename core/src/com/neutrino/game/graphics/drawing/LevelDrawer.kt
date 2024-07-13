package com.neutrino.game.graphics.drawing

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group
import com.neutrino.GlobalData
import com.neutrino.GlobalDataObserver
import com.neutrino.GlobalDataType
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.Drawables
import com.neutrino.game.entities.shared.attributes.Shaders
import com.neutrino.game.entities.shared.attributes.StitchedSprite
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.entities.util.Cloneable
import com.neutrino.game.graphics.drawing.drawables.*
import com.neutrino.game.graphics.drawing.drawables.DrawableTextureUnsorted
import com.neutrino.game.graphics.drawing.drawables.LayeredDrawableList
import com.neutrino.game.graphics.shaders.ShaderParametered
import com.neutrino.game.graphics.shaders.ShaderPrograms
import com.neutrino.game.graphics.textures.AnimatedTextureSprite
import com.neutrino.game.graphics.textures.Light
import com.neutrino.game.graphics.textures.TextureSprite
import com.neutrino.game.map.attributes.DrawPosition
import com.neutrino.game.map.chunk.CharacterArray
import com.neutrino.game.map.chunk.Chunk
import com.neutrino.game.map.chunk.EntityList
import com.neutrino.game.util.Constants
import com.neutrino.game.util.Constants.SCALE
import com.neutrino.game.util.Constants.SCALE_INT
import com.neutrino.game.util.Constants.TILE_SIZE
import com.neutrino.game.util.Constants.TILE_SIZE_INT
import java.util.*
import kotlin.random.Random

open class LevelDrawer(chunk: Chunk): EntityDrawer, Group() {

    override val animations: Animations = Animations(this)
    override val lights: ArrayList<Pair<DrawableTexture, Light>> = ArrayList()
    private val drawableLayers: SortedMap<Int, LayeredDrawableList> = sortedMapOf()

    var chunk: Chunk = chunk
        set(value) {
            clearAll()
            field = value
            fogOfWar.chunk = chunk
            fogOfWar.initializeFogOfWar()
        }
    override val map: List<List<EntityList>>
        get() = chunk.map

    val fogOfWar = FogOfWar(chunk)

    fun clearAll() {
        animations.clear()
        lights.clear()
        drawableLayers.forEach { t, u -> u.clear() }
        drawableLayers.clear()
    }

    override fun addDrawable(drawable: Drawable) {
        if (drawableLayers[drawable.z] == null)
            drawableLayers[drawable.z] = LayeredDrawableList()

        if (drawable is DrawableTexture)
            addDrawableDetails(drawable)

        drawable.entity.get(Shaders::class)?.shaders?.forEach { drawable.addShader(it) }
        drawableLayers[drawable.z]!!.add(drawable)
        if (drawable.entity hasNot Drawables::class)
            drawable.entity.addAttribute(Drawables())
        drawable.entity.get(Drawables::class)!!.addDrawable(drawable)
    }

    override fun removeDrawable(drawable: Drawable) {
        drawable.entity.get(Drawables::class)?.removeDrawable(drawable)
        drawableLayers[drawable.z]!!.remove(drawable)
        if (drawable is DrawableTexture)
            removeDrawableDetails(drawable)
    }

    override fun addTexture(entity: Entity, texture: TextureSprite) {
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

    override fun removeTexture(entity: Entity, texture: TextureSprite) {
        val drawable = entity.get(Drawables::class)?.removeDrawable {
            it.entity == entity && it is DrawableTexture && it.texture == texture }
        drawableLayers[texture.z]!!.remove(drawable)
        removeDrawableDetails(drawable as DrawableTexture)
    }
    
    private fun addDrawableDetails(drawable: DrawableTexture) {
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

    private fun removeDrawableDetails(drawable: DrawableTexture) {
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

    init {
        width = map[0].size * TILE_SIZE
        height = map.size * TILE_SIZE

        GlobalData.registerObserver(object: GlobalDataObserver {
            override val dataType: GlobalDataType = GlobalDataType.PLAYERMOVED
            override fun update(data: Any?): Boolean {
                fogOfWar.updateVisibility()
                return true
            }
        })
    }

    val drawTimeMillis = ArrayList<Long>()

    override fun draw(batch: Batch?, parentAlpha: Float) {
        val gameCamera = parent.stage.camera as OrthographicCamera

        var yTop = MathUtils.floor((height - (gameCamera.position.y + gameCamera.viewportHeight * gameCamera.zoom / 2f)) / TILE_SIZE_INT) + 1
        var yBottom = MathUtils.ceil((height - (gameCamera.position.y - gameCamera.viewportHeight * gameCamera.zoom / 2f)) / TILE_SIZE_INT) + 2
        var xLeft: Int = MathUtils.floor((gameCamera.position.x - gameCamera.viewportWidth * gameCamera.zoom / 2f) / TILE_SIZE_INT)
        var xRight = MathUtils.ceil((gameCamera.position.x + gameCamera.viewportWidth * gameCamera.zoom / 2f) / TILE_SIZE_INT)

        // Make sure that values are in range
        yTop = if (yTop <= 0) 0 else if (yTop > map.size) map.size else yTop
        yBottom = if (yBottom <= 0) 0 else if (yBottom > map.size) map.size else yBottom
        xLeft = if (xLeft <= 0) 0 else if (xLeft > map[0].size) map[0].size else xLeft
        xRight = if (xRight <= 0) 0 else if (xRight > map[0].size) map[0].size else xRight

        var screenX = xLeft * TILE_SIZE
        var screenY = height - (yTop * TILE_SIZE)

        for (y in yTop until yBottom) {
            for (x in xLeft until xRight) {
                for (entity in map[y][x]) {
                    val textures = entity.get(Texture::class)!!.textures
                    for (texture in textures) {
                        if (texture.z == 0) {
                            batch!!.draw(
                                texture.texture, if (!texture.mirrorX) screenX else screenX + texture.texture.regionWidth * SCALE,
                                screenY,
                                texture.texture.regionWidth * if (!texture.mirrorX) SCALE else -1 * SCALE,
                                texture.texture.regionHeight * SCALE)

                            if (entity has Shaders::class)
                                entity.get(Shaders::class)?.shaders?.forEach {
                                    it.applyToBatch(batch)
                                    batch.draw(
                                        texture.texture, if (!texture.mirrorX) screenX else screenX + texture.texture.regionWidth * SCALE,
                                        screenY,
                                        texture.texture.regionWidth * if (!texture.mirrorX) SCALE else -1 * SCALE,
                                        texture.texture.regionHeight * SCALE)
                                    it.cleanUp(batch)
                                }
                        }
                    }
                }
                screenX += TILE_SIZE_INT
            }
            screenY -= TILE_SIZE_INT
            screenX = xLeft * TILE_SIZE
        }

        yTop = Math.round(gameCamera.position.y + gameCamera.viewportHeight * gameCamera.zoom / 2f)
        yBottom = Math.round(gameCamera.position.y - gameCamera.viewportHeight * gameCamera.zoom / 2f)
        xLeft *= 16 * SCALE_INT
        xRight *= 16 * SCALE_INT

        var textureX = 0f
        var textureY = 0f
        var textureWidth = 0
        var texture: TextureSprite
        drawableLayers.forEach { key, layer ->
            layer.sort()
            for (layeredTexture in layer) {
                textureX = layeredTexture.getX()
                textureY = layeredTexture.getY()
                textureWidth = layeredTexture.width
                if (textureY + layeredTexture.height >= yBottom && textureY <= yTop &&
                    textureX + textureWidth >= xLeft && textureX <= xRight) {
                    layeredTexture.drawDebug(batch!!, x, y, parentAlpha)
                    if (layeredTexture.getShaders() != null)
                        layeredTexture.drawShaders(batch, x, y, parentAlpha)
                }
            }
        }

        if (fogOfWar.drawFovFow % 3 in 0 .. 1) {
            batch?.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO)
            batch?.draw(fogOfWar.blurredFov.colorBufferTexture, 0f, 64f)
        }

        drawLights(batch)


        if (fogOfWar.drawFovFow % 3 == 0) {
            batch?.shader = ShaderPrograms.defaultShader
            batch?.draw(fogOfWar.blurredFogOfWar.colorBufferTexture, 0f, 64f)
            batch?.shader = null
        }
    }

    private fun drawLights(batch: Batch?) {
        batch?.shader = ShaderPrograms.lightShader
        batch?.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
        for (light in lights) {
            val drawPositionAttribute = light.first.entity.get(DrawPosition::class)!!
            val radius = light.second.radius
            batch?.color = light.second.color

            var x = x + drawPositionAttribute.x + light.first.xOffset - radius + SCALE / 2
            if (light.first.texture.mirrorX)
                x += (light.first.texture.x + light.first.texture.width() - light.second.x) * SCALE_INT
            else
                x += (light.first.texture.x + light.second.x) * SCALE_INT

            batch?.draw(
                Constants.WhitePixel,
                x,
                y + drawPositionAttribute.y + light.first.yOffset + (light.second.y + light.first.texture.y) * SCALE_INT - radius + SCALE / 2,
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

    fun initializeTextures(rng: Random = Random(Random.nextInt())) {
        for (y in map.indices) {
            for (x in map[0].indices) {
                for (entity in map[y][x]) {
                    entity addAttribute DrawPosition()
                    entity addAttribute Position(x, y, chunk)
                    entity.get(Texture::class)?.setTextures(null, rng)
                }
            }
        }
    }

    fun initializeCharacterTextures(characterArray: CharacterArray, rng: Random = Random(Random.nextInt())) {
        for (character in characterArray) {
            character.get(Texture::class)?.setTextures(null, rng)
            character.getDrawables()?.forEach { it.attach() }
        }
    }

    fun initializeMap(): List<List<MutableList<Entity>>> {
        return List(100) {
            List(100) {
                ArrayList<Entity>()
            }
        }
    }
}