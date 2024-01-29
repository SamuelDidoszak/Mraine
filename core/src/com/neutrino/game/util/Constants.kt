package com.neutrino.game.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.utils.ArrayMap
import com.github.tommyettinger.textra.Font
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.graphics.utility.PixelData
import ktx.script.KotlinScriptEngine
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

infix fun MutableList<Entity>.has(other: KClass<out Entity>): Boolean = this.any { it::class == other }
infix fun MutableList<Entity>.hasSuper(other: KClass<out Entity>): Boolean = this.any { it::class == other || it isSuper other }

infix fun Entity.isSuper(other: KClass<out Entity>): Boolean = this::class.superclasses.any { it == other }

object Constants {
    const val AnimationSpeed: Float = 0.1666666666666666f
    const val MoveSpeed: Float = 0.275f

    //  Global textures for items and entities

    val SCALE = 4f
    val SCALE_INT = SCALE.toInt()
    val TILE_SIZE = 16 * SCALE
    val TILE_SIZE_INT = 16 * SCALE_INT

    val scriptEngine = KotlinScriptEngine()

    /** Stores hashcodes of every texture */
    val textureArrayMap: ArrayMap<Texture, TextureAtlas> = ArrayMap<Texture, TextureAtlas>()

    val DefaultItemTexture: TextureAtlas = TextureAtlas("textures/items.atlas")
    val DefaultEntityTexture: TextureAtlas = TextureAtlas("textures/entitiesOld.atlas")
    val DefaultProjectileTexture: TextureAtlas = TextureAtlas("textures/projectiles.atlas")
    val DefaultIconTexture: TextureAtlas = TextureAtlas("textures/icons.atlas")
    val DefaultUITexture: TextureAtlas = TextureAtlas("UI/ui.atlas")
    val WhitePixel: Texture = Texture("whitePixel.png")
    val TransparentPixel: Texture = Texture("transparentPixel.png")
    val EntityPixelData = PixelData(DefaultEntityTexture)

    // Level constants
    val LevelChunkSize: Int = 100

    val maxItemTier: Int = 4

    const val IsSeeded: Boolean = true
    val Seed: Long = if (IsSeeded) 2137213721372137 else Random.Default.nextLong()
    val RandomGenerator: Random = Random(Seed)

    val fonts = ConstInits().getFontFamily()

    init {
        // Store hash codes
        DefaultItemTexture.textures.forEach { textureArrayMap.put(it, DefaultItemTexture) }
        DefaultEntityTexture.textures.forEach { textureArrayMap.put(it, DefaultEntityTexture) }
        DefaultProjectileTexture.textures.forEach { textureArrayMap.put(it, DefaultProjectileTexture) }
        DefaultIconTexture.textures.forEach { textureArrayMap.put(it, DefaultIconTexture) }
        DefaultUITexture.textures.forEach { textureArrayMap.put(it, DefaultUITexture) }
//        textureHashCodes[WhitePixel.hashCode()] = WhitePixel
    }
}

class ConstInits {
    fun getFontFamily(): Font.FontFamily {
        return Font.FontFamily(
            arrayOf(
                getFont("equipment").scale(2f, 2f),
                getFont("matchup").scale(2f, 2f),
                getFont("munro").scale(1.6f, 1.6f),
                getFont("schmal").scale(1.25f, 1.25f),
                getFont("outline"),
                getFont("gothic")
            )
        )
    }

    private fun getFont(name: String): Font {
        val font = Font(
            FreeTypeFontGenerator(Gdx.files.internal("fonts/$name.ttf"))
                .generateFont(FreeTypeFontGenerator.FreeTypeFontParameter())
        )
        font.name = name
        return font
    }
}

object Fonts {
    val EQUIPMENT = Constants.fonts.get("equipment")
    val MATCHUP = Constants.fonts.get("matchup")
    val MUNRO = Constants.fonts.get("munro")
    val SCHMAL = Constants.fonts.get("schmal")
    val OUTLINE = Constants.fonts.get("outline")
    val GOTHIC = Constants.fonts.get("gothic")
}