package com.neutrino.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.ArrayMap
import com.github.tommyettinger.textra.Font
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.domain.model.entities.Entity
import com.neutrino.game.graphics.utility.PixelData
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

fun Double.equalsDelta(other: Double) = abs(this - other) <= 0.005
fun Double.lessThanDelta(other: Double) = (this - other) < -0.0000001
fun Float.equalsDelta(other: Float) = abs(this - other) <= 0.005f
fun Float.lessThanDelta(other: Float) = (this - other) < -0.0000001

/** Cuts off the decimal value of a floating point number. Mostly used to fix the floating point precision issue with rendering */
fun Float.round() = this.roundToInt().toFloat()
/** Rounds the number to one decimal place */
fun Float.roundOneDecimal() = (this * 10).roundToInt() / 10f
/** Cuts off the decimal value of a floating point number. Mostly used to fix the floating point precision issue with rendering */
fun Double.round() = this.roundToInt().toFloat()
/** Rounds the number to one decimal place */
fun Double.roundOneDecimal() = (this * 10).roundToInt() / 10.0

/** Cuts off the decimal value of the actor's position */
fun Actor.roundPosition() = this.setPosition(this.x.round(), this.y.round())
fun Actor.widthScaled() = this.width * this.scaleX
fun Actor.heightScaled() = this.height * this.scaleY


fun Actor.isIn(x: Float, y: Float) = (x.compareDelta(this.x) >= 0 && x.compareDelta(this.x + this.width) <= 0 &&
        y.compareDelta(this.y) >= 0 && y.compareDelta(this.y + this.height) <= 0)

fun Actor.isInSized(x: Float, y: Float) = (x.compareDelta(this.x) >= 0 && x.compareDelta(this.x + this.widthScaled()) <= 0 &&
        y.compareDelta(this.y) >= 0 && y.compareDelta(this.y + this.heightScaled()) <= 0)

fun Actor.isInUnscaled(x: Float, y: Float, scale: Float) = (x.compareDelta(this.x * scale) >= 0 && x.compareDelta(this.x * scale + this.width * scale) <= 0 &&
        y.compareDelta(this.y * scale) >= 0 && y.compareDelta(this.y * scale + this.height * scale) <= 0)

fun TextraLabel.setTextSameWidth(markupText: String) = run {
    this.storedText = markupText
    this.layout.targetWidth = width
    this.font.markup(markupText, layout.clear())
}

/** Returns 0 if the values are the same. Returns -1 if the value is smaller than other and 1 if it's bigger */
fun Float.compareDelta(other: Float) = if (this.equalsDelta(other)) 0
    else if (this.lessThanDelta(other)) -1 else 1
/** Returns 0 if the values are the same. Returns -1 if the value is smaller than other and 1 if it's bigger */
fun Double.compareDelta(other: Double) = if (this.equalsDelta(other)) 0
    else if (this.lessThanDelta(other)) -1 else 1

infix fun MutableList<Entity>.has(other: KClass<out Entity>): Boolean = this.any { it::class == other }
infix fun MutableList<Entity>.hasSuper(other: KClass<out Entity>): Boolean = this.any { it::class == other || it isSuper other }

infix fun Entity.isSuper(other: KClass<out Entity>): Boolean = this::class.superclasses.any { it == other }

object Constants {
    const val AnimationSpeed: Float = 0.1666666666666666f
    const val MoveSpeed: Float = 0.275f

    //  Global textures for items and entities

    /** Stores hashcodes of every texture */
    val textureArrayMap: ArrayMap<Texture, TextureAtlas> = ArrayMap<Texture, TextureAtlas>()

    val DefaultItemTexture: TextureAtlas = TextureAtlas("textures/items.atlas")
    val DefaultEntityTexture: TextureAtlas = TextureAtlas("textures/entities.atlas")
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