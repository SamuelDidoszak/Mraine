package com.neutrino.game.utility

import com.badlogic.gdx.graphics.Color
import com.neutrino.LevelArrays
import com.neutrino.game.domain.model.characters.Character
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.model.characters.utility.HasRange
import com.neutrino.game.domain.model.entities.utility.*
import com.neutrino.game.domain.model.systems.attack.utility.Attackable
import com.neutrino.game.domain.use_case.Shaderable
import com.neutrino.game.graphics.shaders.ColorOverlayShader
import com.neutrino.game.graphics.shaders.OutlineShader
import com.neutrino.game.graphics.shaders.ShaderParametered
import squidpony.squidmath.Coord

class Highlighting {
    private var outlinedOnHover: Shaderable? = null
    private var shaderOnHover: ShaderParametered? = null

    private val highlightedList = ArrayList<Pair<Shaderable, ShaderParametered>>()
    private val selectionHighlightedList = ArrayList<Pair<Shaderable, ShaderParametered>>()

    private var previousAttackCoord: Coord? = null

    companion object {
        enum class HighlightModes {
            NORMAL,
            AREA,
            ONLY_CHARACTERS
        }
    }

    fun highlightOnHover(coord: Coord) {
        if (!addCharacterOutlineOnHover(coord))
            addInteractableOutlineOnHover(coord)
    }

    fun highlightArea(range: HasRange, center: Coord, omitCenter: Boolean = false, highlightCharacters: Boolean = true) {
        highlightTiles(range, center, omitCenter, ColorOverlayShader.LIGHT_RED)
        if (highlightCharacters)
            highlightCharacters(range, center, omitCenter, ColorOverlayShader.LIGHT_RED)
    }

    private fun highlightTiles(range: HasRange, center: Coord, omitCenter: Boolean, color: Color) {
        for (tile in range.getTilesInRange(center, omitCenter)) {
            val entities = LevelArrays.getEntitiesAt(tile)
            for (z in entities.size - 1 downTo 0) {
                if (entities[z] is Floor) {
                    val shader = ColorOverlayShader(color)
                    entities[z].shaders.add(shader)
                    highlightedList.add(Pair(entities[z], shader))
                }
            }
        }
    }

    private fun highlightCharacters(range: HasRange, center: Coord, omitCenter: Boolean, color: Color) {
        for (tile in range.getTilesInRange(center, omitCenter)) {
            val character = LevelArrays.getCharacterAt(tile)
            if (character != null) {
                val shader = OutlineShader(ColorOverlayShader.DARK_RED, 2f, character.texture)
                character.shaders.add(shader)
                highlightedList.add(Pair(character, shader))
            }
        }
    }

    fun highlightAttackArea(range: HasRange, center: Coord, requireCharacter: Boolean) {
        if (center == previousAttackCoord)
            return

        deHighlight(true)
        previousAttackCoord = center

        if ((requireCharacter && LevelArrays.getCharacterAt(center) == null) ||
            (requireCharacter && LevelArrays.getCharacterAt(center) is Player))
            return

        for (tile in range.getTilesInRange(center)) {
            val character = LevelArrays.getCharacterAt(tile)
            if (character != null) {
                val shader = OutlineShader(ColorOverlayShader.DARK_RED, 2f, character.texture)
                character.shaders.add(shader)
                selectionHighlightedList.add(Pair(character, shader))
            }

            val entities = LevelArrays.getEntitiesAt(tile)
            var floorHighlighted = false
            for (z in entities.size - 1 downTo 0) {
                if (entities[z] is Attackable) {
                    val shader = OutlineShader(ColorOverlayShader.DARK_RED, 2f, entities[z].texture)
                    entities[z].shaders.add(shader)
                    selectionHighlightedList.add(Pair(entities[z], shader))
                }
                if (!floorHighlighted && entities[z] is Floor) {
                    val shader = ColorOverlayShader(ColorOverlayShader.DARK_RED)
                    entities[z].shaders.add(shader)
                    selectionHighlightedList.add(Pair(entities[z], shader))
                    floorHighlighted = true
                }
            }
        }
    }

    /**
     * Removes highlighting and outlines from everything (excluding normal onHover shaders)
     * @param isMouseSelection
     *      if null removes highlighting both from area and onMouse area
     *      if true removes highlighting from onMouse area
     *      if false removes highlighting from area
     */
    fun deHighlight(isMouseSelection: Boolean? = null) {
        if (isMouseSelection != true) {
            for (highlighted in highlightedList) {
                highlighted.first.shaders.remove(highlighted.second)
            }
            highlightedList.clear()
        }
        if (isMouseSelection != false) {
            for (highlighted in selectionHighlightedList) {
                highlighted.first.shaders.remove(highlighted.second)
            }
            selectionHighlightedList.clear()
        }
    }

    fun deHighlightOnHover() {
        outlinedOnHover?.shaders?.remove(shaderOnHover)
        outlinedOnHover = null
    }

    private fun addInteractableOutlineOnHover(coord: Coord): Boolean {
        val entity = LevelArrays.getLevel().getEntityWithAction(coord.x, coord.y)
        if (entity != null && entity == outlinedOnHover)
            return true
        if (entity is ItemEntity)
            return false

        outlinedOnHover?.shaders?.remove(shaderOnHover)
        outlinedOnHover = null

        if (entity == null)
            return false

        if (entity is Attackable) {
            if (!Player.ai.canAttack(coord.x, coord.y))
                return false
        }
        else if (entity is Interactable) {
            val requiredDistance = (entity as Interactable).getPrimaryInteraction()?.requiredDistance
                ?: return false
            if ((coord.x !in Player.xPos - requiredDistance .. Player.xPos + requiredDistance) ||
                (coord.y !in Player.yPos - requiredDistance .. Player.yPos + requiredDistance))
                return false
        }

        outlinedOnHover = entity
        shaderOnHover = OutlineShader(
                if ((outlinedOnHover as Interactable).getPrimaryInteraction() is Interaction.DESTROY) {
                    if ((outlinedOnHover as Destructable).destroyed)
                        OutlineShader.OUTLINE_CLEAR
                    else
                        OutlineShader.OUTLINE_RED
                }
                else
                    OutlineShader.OUTLINE_GREEN,
                2f,
                (outlinedOnHover as TextureHaver).texture
        )
        outlinedOnHover?.shaders?.add(shaderOnHover)
        return outlinedOnHover != null
    }

    private fun addCharacterOutlineOnHover(coord: Coord): Boolean {
        val character: Character? = LevelArrays.getCharacterAt(coord)
        if (character != null && character == outlinedOnHover)
            return true
        if (character == Player)
            return false

        outlinedOnHover?.shaders?.remove(shaderOnHover)
        outlinedOnHover = null

        if (character == null || !Player.ai.canAttack(character.xPos, character.yPos))
            return false

        outlinedOnHover = character
        if (character?.isAlive() != true)
            return false

        shaderOnHover = OutlineShader(
            OutlineShader.OUTLINE_RED,
            2f,
            (outlinedOnHover as TextureHaver).texture
        )
        outlinedOnHover?.shaders?.add(shaderOnHover)
        return outlinedOnHover != null
    }
}