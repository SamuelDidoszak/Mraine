package com.neutrino.game.domain.model.items

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.neutrino.game.Constants
import com.neutrino.game.domain.model.entities.utility.TextureHaver
import com.neutrino.game.utility.Serialize
import kotlin.random.Random

@Serialize
abstract class Item: ItemType, TextureHaver, Cloneable {
    abstract val name: String
    abstract val description: String
    @Transient
    override var mirrored: Boolean = false
    /** Amount of items in stack. Null means that it's not stackable */
    open var amount: Int? = null
    /** Determines containers in which the item will spawn */
    abstract val itemTier: Int

    /** If 0, item won't be compared if it's better or worse */
    @Transient
    open var goldValueOg: Int = 0
    open var goldValue: Int = 0

    open var realValue: Int = 0

    override fun getTexture(name: String): TextureAtlas.AtlasRegion {
        return Constants.DefaultItemTexture.findRegion(name)
    }

    public override fun clone(): Any {
        return super.clone()
    }

    /** Randomize item parameters
     * @return this for chaining */
    open fun randomize(randomGenerator: Random): Item {
        return this
    }

    /** Randomize item parameters
     * @return this for chaining */
    open fun randomize(randomGenerator: Random, quality: Float, difficulty: Float): Item {
        return this
    }

    /** Checks if two items are the same by iterating over their method values and comparing them */
    public fun equalsIdentical(other: Item): Boolean {
        if (this.name != other.name)
            return false

        val thisFields = this::class.java.declaredFields
        val otherFields = other::class.java.declaredFields

        if (thisFields.size != otherFields.size)
            return false

        for (i in thisFields.indices) {
            thisFields[i].isAccessible = true
            otherFields[i].isAccessible = true
            if (thisFields[i].get(this) != otherFields[i].get(other)) {
                if (thisFields[i].name != "amount" && thisFields[i].name != "texture") {
                    println("\t\tUNEQUAL FIELD: ${thisFields[i].name}")
                    thisFields[i].isAccessible = false
                    otherFields[i].isAccessible = false
                    return false
                }
            }
            thisFields[i].isAccessible = false
            otherFields[i].isAccessible = false
        }
        return true
    }
}