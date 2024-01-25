package com.neutrino.game.graphics.drawing

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group
import com.neutrino.game.util.Constants
import com.neutrino.game.util.Constants.SCALE
import com.neutrino.game.util.Constants.SCALE_INT
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.shared.attributes.StitchedSprite
import com.neutrino.game.entities.shared.attributes.Texture
import com.neutrino.game.graphics.shaders.Shaders
import com.neutrino.game.graphics.textures.Light
import com.neutrino.game.graphics.textures.TextureSprite
import com.neutrino.game.map.attributes.OnMapPosition
import com.neutrino.game.map.attributes.Position
import java.util.*
import kotlin.random.Random

open class LevelDrawer: EntityDrawer, Group() {

    override val animations: Animations = Animations()
    override val lights: ArrayList<Pair<Entity, Light>> = ArrayList()
    private val textureLayers: SortedMap<Int, LayeredTextureList> = sortedMapOf()

    fun clearAll() {
        animations.clear()
        lights.clear()
        textureLayers.forEach { t, u -> u.clear() }
        textureLayers.clear()
    }

    override fun addTexture(entity: Entity, texture: TextureSprite) {
        if (textureLayers[texture.z] == null) {
            textureLayers[texture.z] = LayeredTextureList()
        }
        if (entity has StitchedSprite::class)
            textureLayers[texture.z]!!.add(LayeredTextureUnsorted(entity, texture))
        else
            textureLayers[texture.z]!!.add(LayeredTexture(entity, texture))
    }

    override fun removeTexture(entity: Entity, texture: TextureSprite) {
        textureLayers[texture.z]!!.removeIf { it.entity == entity && it.texture == texture }
    }

    override var map: List<List<MutableList<Entity>>> = initializeMap()

    init {
        width = map[0].size * 48f
        height = map.size * 48f
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        val gameCamera = parent.stage.camera as OrthographicCamera

        var yTop = MathUtils.floor((height - (gameCamera.position.y + gameCamera.viewportHeight * gameCamera.zoom / 2f)) / 48) + 1
        var yBottom = MathUtils.ceil((height - (gameCamera.position.y - gameCamera.viewportHeight * gameCamera.zoom / 2f)) / 48) + 2
        var xLeft: Int = MathUtils.floor((gameCamera.position.x - gameCamera.viewportWidth * gameCamera.zoom / 2f) / 48)
        var xRight = MathUtils.ceil((gameCamera.position.x + gameCamera.viewportWidth * gameCamera.zoom / 2f) / 48)

        // Make sure that values are in range
        yTop = if (yTop <= 0) 0 else if (yTop > map.size) map.size else yTop
        yBottom = if (yBottom <= 0) 0 else if (yBottom > map.size) map.size else yBottom
        xLeft = if (xLeft <= 0) 0 else if (xLeft > map[0].size) map[0].size else xLeft
        xRight = if (xRight <= 0) 0 else if (xRight > map[0].size) map[0].size else xRight

        var screenX = xLeft * 48f
        var screenY = height - (yTop * 48f)

        for (y in yTop until yBottom) {
            for (x in xLeft until xRight) {
                for (entity in map[y][x]) {
                    val textures = entity.get(Texture::class)!!.textures
                    for (texture in textures) {
                        if (texture.z == 0)
                            batch!!.draw(
                                texture.texture, if (!texture.mirrorX) screenX else screenX + texture.texture.regionWidth * SCALE,
                                screenY,
                                texture.texture.regionWidth * if (!texture.mirrorX) SCALE else -1 * SCALE,
                                texture.texture.regionHeight * SCALE)
                    }
                }
                screenX += 48
            }
            screenY -= 48
            screenX = xLeft * 48f
        }

        yTop = Math.round(gameCamera.position.y + gameCamera.viewportHeight * gameCamera.zoom / 2f)
        yBottom = Math.round(gameCamera.position.y - gameCamera.viewportHeight * gameCamera.zoom / 2f)
        xLeft *= 16 * SCALE_INT
        xRight *= 16 * SCALE_INT

        var textureX = 0f
        var textureY = 0f
        var textureWidth = 0
        var texture: TextureSprite
        textureLayers.forEach { key, layer ->
            layer.sort()
            for (layeredTexture in layer) {
                textureX = layeredTexture.getX()
                textureY = layeredTexture.getY()
                textureWidth = layeredTexture.getWidth()
                if (textureY + layeredTexture.getHeight() >= yBottom && textureY <= yTop &&
                    textureX + textureWidth >= xLeft && textureX <= xRight) {
                    texture = layeredTexture.texture
                    batch!!.draw(texture.texture,
                        if (!texture.mirrorX) x + textureX else x + textureX + textureWidth,
                        y + textureY,
                        textureWidth * if (!texture.mirrorX) 1f else -1f,
                        layeredTexture.getHeight() * 1f)
                }
            }
        }

        drawLights(batch)
    }

    private fun drawLights(batch: Batch?) {
        batch?.shader = Shaders.lightShader
        batch?.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
        for (light in lights) {
            val positionAttribute = light.first.get(Position::class)!!
            val radius = light.second.radius
            batch?.color = light.second.color
            // TODO add texture position
            batch?.draw(
                Constants.WhitePixel,
                x + positionAttribute.x + light.second.x * SCALE_INT - radius + SCALE / 2,
                y + positionAttribute.y + light.second.y * SCALE_INT - radius + SCALE / 2,
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
                    entity addAttribute Position()
                    entity addAttribute OnMapPosition(x, y, this)
                    entity.get(Texture::class)?.setTextures(null, rng)
                }
            }
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