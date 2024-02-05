package com.neutrino.game.util

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Array
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.game.entities.Attribute
import com.neutrino.game.entities.Entities
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.attributes.Identity
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.reflect.KClass

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

/** Returns 0 if the values are the same. Returns -1 if the value is smaller than other and 1 if it's bigger */
fun Float.compareDelta(other: Float) = if (this.equalsDelta(other)) 0
else if (this.lessThanDelta(other)) -1 else 1
/** Returns 0 if the values are the same. Returns -1 if the value is smaller than other and 1 if it's bigger */
fun Double.compareDelta(other: Double) = if (this.equalsDelta(other)) 0
else if (this.lessThanDelta(other)) -1 else 1


fun <T> Array<T>.addInitial(element: T): Array<T> {
    add(element)
    return this
}

fun <T> ArrayList<T>.addInitial(element: T): ArrayList<T> {
    add(element)
    return this
}

infix fun <T> ArrayList<T>.add(element: T?): T? {
    if (element == null)
        return null
    add(element)
    return element
}

infix fun <T> ArrayList<T>.add(elements: List<T>?): T? {
    if (elements.isNullOrEmpty())
        return null
    addAll(elements)
    return elements.first()
}


fun getChangeListener(method: (event: ChangeListener.ChangeEvent?, actor: Actor?) -> Unit): ChangeListener {
    return object : ChangeListener() {
        override fun changed(event: ChangeEvent?, actor: Actor?) {
            method.invoke(event, actor)
        }
    }
}


infix fun MutableList<Entity>.has(other: String): Boolean = this.any { it.name == other }

infix fun MutableList<Entity>.has(other: KClass<Attribute>): Boolean = this.any { it has other}

infix fun Entity.hasIdentity(identity: KClass<out Identity>): Boolean {
    return get(identity) != null || identity == Identity.Any::class
}

infix fun MutableList<Entity>.hasIdentity(identity: KClass<out Identity>): Boolean {
    for (i in indices) {
        if (get(i).get(identity) != null || identity == Identity.Any::class)
            return true
    }
    return false
}

fun EntityName.id(): Int = Entities.getId(this)
fun EntityId.name(): String = Entities.getName(this)


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

fun Table.remove(actor: Actor) {
    getCell(actor).pad(0f).space(0f)
    removeActor(actor)
}


var Entity.x: Int
    get() = get(Position::class)!!.x
    set(value) { get(Position::class)!!.x = value }

var Entity.y: Int
    get() = get(Position::class)!!.y
    set(value) { get(Position::class)!!.y = value }