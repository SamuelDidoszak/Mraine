package com.neutrino.game.utility

import com.badlogic.gdx.graphics.Color
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Character
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.characters.attributes.Ai
import com.neutrino.game.entities.items.Item
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.map_entities.attributes.Door
import com.neutrino.game.entities.map_entities.callables.InteractedCallable
import com.neutrino.game.entities.map_entities.util.Interactable
import com.neutrino.game.entities.shared.attributes.Identity
import com.neutrino.game.entities.shared.attributes.Shaders
import com.neutrino.game.entities.shared.util.HasRange
import com.neutrino.game.entities.systems.attack.attributes.DefensiveStats
import com.neutrino.game.entities.systems.attack.attributes.OffensiveStats
import com.neutrino.game.entities.systems.attack.callables.EntityDiedCallable
import com.neutrino.game.graphics.shaders.ColorOverlayShader
import com.neutrino.game.graphics.shaders.OutlineShader
import com.neutrino.game.graphics.shaders.ShaderParametered
import com.neutrino.game.map.chunk.ChunkManager
import com.neutrino.game.util.debug
import com.neutrino.game.util.hasIdentity

class Highlighting {
    private var outlinedOnHover: Entity? = null
    private var shaderOnHover: ShaderParametered? = null

    private val highlightedList = ArrayList<Pair<Entity, ShaderParametered>>()
    private val selectionHighlightedList = ArrayList<Pair<Entity, ShaderParametered>>()

    private var previousAttackPosition: Position? = null

    companion object {
        enum class HighlightModes {
            NORMAL,
            AREA,
            ONLY_CHARACTERS
        }
    }

    fun highlightOnHover(position: Position) {
        if (!addCharacterOutlineOnHover(position))
            addInteractableOutlineOnHover(position, null)
    }

    fun highlightArea(range: HasRange, center: Position, omitCenter: Boolean = false, highlightCharacters: Boolean = true) {
        highlightTiles(range, center, omitCenter, ColorOverlayShader.LIGHT_RED)
        if (highlightCharacters)
            highlightCharacters(range, center, omitCenter, ColorOverlayShader.LIGHT_RED)
    }

    private fun highlightTiles(range: HasRange, center: Position, omitCenter: Boolean, color: Color) {
        debug("Highlight", "highlighting tiles")
        for (tile in range.getTilesInRange(center, omitCenter)) {
            if (!ChunkManager.isChunkLoaded(tile.chunkCoords))
                continue
            for (entity in ChunkManager.getEntitiesAt(tile).asReversed()) {
                if (entity hasIdentity Identity.Floor::class) {
                    val shader = ColorOverlayShader(color)
                    addShader(entity, shader)
                    highlightedList.add(Pair(entity, shader))
                }
            }
        }
    }

    private fun highlightCharacters(range: HasRange, center: Position, omitCenter: Boolean, color: Color) {
        debug("Highlight", "highlighting characters")
        for (tile in range.getTilesInRange(center, omitCenter)) {
            if (!ChunkManager.isChunkLoaded(tile.chunkCoords))
                continue
            val character = ChunkManager.getCharacterAt(tile)

            if (character != null) {
                val shader = OutlineShader(ColorOverlayShader.DARK_RED, 2f)
                addShader(character, shader)
                highlightedList.add(Pair(character, shader))
            }
        }
    }

    fun highlightAttackArea(range: HasRange, center: Position, requireCharacter: Boolean) {
        debug("Highlight", "highlighting attack area")
        if (center == previousAttackPosition)
            return

        deHighlight(true)
        previousAttackPosition = center

        if ((requireCharacter && ChunkManager.getCharacterAt(center) == null) ||
            (requireCharacter && ChunkManager.getCharacterAt(center) == Player))
            return

        for (tile in range.getTilesInRange(center)) {
            if (!ChunkManager.isChunkLoaded(tile.chunkCoords))
                continue
            val character = ChunkManager.getCharacterAt(tile)
            // TODO ECS Shaders
            if (character != null) {
                val shader = OutlineShader(ColorOverlayShader.DARK_RED, 2f)
                addShader(character, shader)
                selectionHighlightedList.add(Pair(character, shader))
            }

            val entities = ChunkManager.getEntitiesAt(tile)
            var floorHighlighted = false
            for (z in entities.size - 1 downTo 0) {
                if (entities[z] has DefensiveStats::class && entities[z] !is Item) {
                    val shader = OutlineShader(ColorOverlayShader.DARK_RED, 2f)
                    addShader(entities[z], shader)
                    selectionHighlightedList.add(Pair(entities[z], shader))
                }
                if (!floorHighlighted && entities[z] hasIdentity Identity.Floor::class) {
                    val shader = ColorOverlayShader(ColorOverlayShader.DARK_RED)
                    addShader(entities[z], shader)
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
                removeShader(highlighted.first, highlighted.second)
            }
            highlightedList.clear()
        }
        if (isMouseSelection != false) {
            for (highlighted in selectionHighlightedList) {
                removeShader(highlighted.first, highlighted.second)
            }
            selectionHighlightedList.clear()
        }
    }

    fun deHighlightOnHover() {
        removeShader(outlinedOnHover, shaderOnHover)
        outlinedOnHover = null
    }

    private fun addInteractableOutlineOnHover(position: Position, interaction: Interactable? = null): Boolean {
        val entity = position.chunk.getEntityWithAction(position.x, position.y) ?: getAttackable(position)
        if (entity != null && entity == outlinedOnHover)
            return true
        if (entity != null && entity is Item)
            return false

        removeShader(outlinedOnHover, shaderOnHover)
        outlinedOnHover = null

        if (entity == null)
            return false

        val interaction = interaction ?: Interactable.getPrimaryInteraction(entity)

        if (interaction == null && entity hasNot DefensiveStats::class)
            return false


        val requiredDistance = interaction?.requiredDistance ?: Player.get(OffensiveStats::class)!!.range
        if ((position.x !in Player.get(Position::class)!!.x - requiredDistance .. Player.get(Position::class)!!.x + requiredDistance) ||
            (position.y !in Player.get(Position::class)!!.y - requiredDistance .. Player.get(Position::class)!!.y + requiredDistance))
            return false

        outlinedOnHover = entity
        val color = if (interaction != null) OutlineShader.OUTLINE_GREEN else OutlineShader.OUTLINE_RED
        shaderOnHover = OutlineShader(color, 2f)
        addShader(outlinedOnHover!!, shaderOnHover!!)
        if (interaction != null && outlinedOnHover?.has(Door::class) == true) {
            outlinedOnHover!!.attach(object : InteractedCallable() {
                override fun call(entity: Entity, vararg data: Any?) {
                    if (data[0] !is Door)
                        return
                    if (entity.get(Door::class)!!.open) {
                        removeShader(outlinedOnHover, shaderOnHover)
                        entity.detach(this)
                    }
            } })
        } else {
            outlinedOnHover!!.attach(object : EntityDiedCallable() {
                override fun call(entity: Entity, vararg data: Any?) {
                    removeShader(outlinedOnHover, shaderOnHover)
                    entity.detach(this)
            } })
        }
        return outlinedOnHover != null
    }

    private fun addCharacterOutlineOnHover(position: Position): Boolean {
        val character: Entity? = ChunkManager.getCharacterAt(position)
        if (character != null && character == outlinedOnHover)
            return true
        if (character == Player || (character == null && outlinedOnHover != null && outlinedOnHover !is Character))
            return false

        removeShader(outlinedOnHover, shaderOnHover)
        outlinedOnHover = null

        if (character == null || !Player.getSuper(Ai::class)!!.canAttack(character.get(Position::class)!!.x, character.get(Position::class)!!.y))
            return false

        outlinedOnHover = character
        if (character.get(DefensiveStats::class)?.isAlive() != true)
            return false

        shaderOnHover = OutlineShader(
            OutlineShader.OUTLINE_RED,
            2f
        )
        addShader(outlinedOnHover!!, shaderOnHover!!)
        outlinedOnHover!!.attach(object : EntityDiedCallable() {
            override fun call(entity: Entity, vararg data: Any?) {
                removeShader(outlinedOnHover, shaderOnHover)
                entity.detach(this)
            }
        })
        return outlinedOnHover != null
    }

    private fun addShader(entity: Entity, shader: ShaderParametered) {
        if (entity hasNot Shaders::class)
            entity.addAttribute(Shaders())
        entity.get(Shaders::class)!!.shaders.add(shader)
    }

    private fun removeShader(entity: Entity?, shader: ShaderParametered?) {
        if (entity == null || shader == null)
            return
        entity.get(Shaders::class)?.shaders?.remove(shader)
        if (entity.get(Shaders::class)?.shaders?.isEmpty() == true)
            entity.removeAttribute(Shaders::class)
    }

    private fun getAttackable(position: Position): Entity? {
        return position.chunk.map[position.y][position.x].asReversed().firstOrNull { it has DefensiveStats::class && it !is Item }
    }
}