package com.neutrino.game.gameplay.main

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.TextraLabel
import com.neutrino.GameStage
import com.neutrino.HudStage
import com.neutrino.game.UI.UiStage
import com.neutrino.game.domain.model.items.ItemType
import com.neutrino.game.domain.model.items.UseOn
import com.neutrino.game.entities.Entity
import com.neutrino.game.entities.characters.Player
import com.neutrino.game.entities.map.attributes.Position
import com.neutrino.game.entities.shared.util.HasRange
import com.neutrino.game.entities.shared.util.RangeType
import com.neutrino.game.map.chunk.ChunkManager
import com.neutrino.game.utility.Highlighting
import squidpony.squidmath.Coord

class GameplayItems(
    val gameplay: Gameplay,
    val gameStage: GameStage,
    val hudStage: HudStage,
    val uiStage: UiStage
) {
    internal fun useItems() {
        fun addCooldownIndicator(coords: Coord) {
            val cooldownLabel = TextraLabel("[@Cozette][%600][*]Cooldown", KnownFonts.getStandardFamily())
            cooldownLabel.name = "cooldown"
            gameStage.addActor(cooldownLabel)
            cooldownLabel.setPosition(coords.x * 64f, (6400f - coords.y * 64f) + 72f)
            cooldownLabel.addAction(Actions.moveBy(0f, 36f, 1f))
            cooldownLabel.addAction(
                Actions.sequence(
                    Actions.delay(1.25f),
                    Actions.removeActor()))
        }

        // If an item was used in eq, make an adequate use action
        val usedItemList = hudStage.usedItemList.ifEmpty { uiStage.usedItemList }
        // TODO Actions
        // Before was:
        // if (usedItemList.isNotEmpty() && !Player.hasActions()) {
        if (usedItemList.isNotEmpty()) {
            // If user clicked, stop using items
            if (gameStage.clickedCoordinates != null) {
                // Remove all items from the list, stopping them from being used
                while (usedItemList.isNotEmpty()) {
                    usedItemList.removeFirst()
                }
            }
            // Use the item
            val item = usedItemList.removeFirst()
            // TODO ECS Items Inventory
//            Player.ai.action = Action.ITEM(item, Player)
            // removing item from eq or decreasing its amount
//            val itemInEq = Player.inventory.itemList.find { it.item == item }!!
//            if (itemInEq.item.amount != null && itemInEq.item.amount!! > 1)
//                itemInEq.item.amount = itemInEq.item.amount!! - 1
//            else
//                Player.inventory.itemList.remove(itemInEq)

            uiStage.inventory.forceRefreshInventory = true
            hudStage.refreshHotBar()
        }

        // Use item on different characters
        val useItemOn = hudStage.useItemOn ?: uiStage.useItemOn
        if (useItemOn != null) {
            val range = (useItemOn as ItemType.USABLE).hasRange ?: object : HasRange {
                override var range: Int = 1
                override var rangeType: RangeType = RangeType.SQUARE
            }

            when (useItemOn.useOn) {
                UseOn.SELF_AND_OTHERS, UseOn.OTHERS_ONLY -> {
                    if (!gameplay.waitForAdditionalClick) {
                        gameplay.waitForAdditionalClick = true
                        gameStage.highlighting.highlightArea(range, Player.get(Position::class)!!.getPosition(), useItemOn.useOn == UseOn.OTHERS_ONLY, true)
                        gameStage.highlightRange = object: HasRange {
                            override var range: Int = 0
                            override var rangeType: RangeType = RangeType.SQUARE
                        }
                        gameStage.highlightMode = Highlighting.Companion.HighlightModes.ONLY_CHARACTERS
                        gameStage.skillRange = range
                    }
                    if (gameStage.clickedCoordinates == null)
                        return

                    if (!range.isInRange(Player.get(Position::class)!!.getPosition(), gameStage.clickedCoordinates!!)) {
                        gameplay.cancelUsage()
                        return
                    }

                    val clickedCharacter: Entity? = ChunkManager.getCharacterAt(gameStage.clickedCoordinates!!)
                    if (clickedCharacter == null || (useItemOn.useOn == UseOn.OTHERS_ONLY && clickedCharacter == Player)) {
                        gameStage.clickedCoordinates = null
                        return
                    }
                    // TODO ECS Events
//                    if (clickedCharacter.eventArray.hasCooldown((useItemOn as? CausesCooldown)?.cooldownType)) {
//                        addCooldownIndicator(gameStage.clickedCoordinates!!)
//                        gameStage.clickedCoordinates = null
//                        return
//                    }

                    // TODO ECS Items Inventory
//                    Player.ai.action = Action.ITEM(useItemOn, clickedCharacter)
                    // removing item from eq or decreasing its amount
//                    val itemInEq = Player.inventory.itemList.find { it.item == useItemOn }!!
//                    if (itemInEq.item.amount != null && itemInEq.item.amount!! > 1)
//                        itemInEq.item.amount = itemInEq.item.amount!! - 1
//                    else
//                        Player.inventory.itemList.remove(itemInEq)

                    uiStage.inventory.forceRefreshInventory = true
                    hudStage.refreshHotBar()
                    gameplay.cancelUsage()
                }
                UseOn.TILE -> {
                    if (!gameplay.waitForAdditionalClick) {
                        gameplay.waitForAdditionalClick = true
                        gameStage.highlighting.highlightArea(range, Player.get(Position::class)!!.getPosition(), false, true)
                        gameStage.highlightRange = if (useItemOn is HasRange) useItemOn else object: HasRange {
                            override var range: Int = 0
                            override var rangeType: RangeType = RangeType.SQUARE
                        }
                        gameStage.highlightMode = Highlighting.Companion.HighlightModes.AREA
                        gameStage.skillRange = range
                    }
                    if (gameStage.clickedCoordinates == null)
                        return

                    if (!range.isInRange(Player.get(Position::class)!!.getPosition(), gameStage.clickedCoordinates!!)) {
                        gameplay.cancelUsage()
                        return
                    }

                    val character: Entity? = ChunkManager.getCharacterAt(gameStage.clickedCoordinates!!)
                    if (character == null) {
                        gameplay.cancelUsage()
                        return
                    }
                    // TODO ECS Events
//                    if (character.eventArray.hasCooldown((useItemOn as? CausesCooldown)?.cooldownType)) {
//                        addCooldownIndicator(gameStage.clickedCoordinates!!)
//                        gameStage.clickedCoordinates = null
//                        return
//                    }

                    // TODO ECS Items Inventory
//                    Player.ai.action = Action.ITEM(useItemOn, character)
                    // removing item from eq or decreasing its amount
//                    val itemInEq = Player.inventory.itemList.find { it.item == useItemOn }!!
//                    if (itemInEq.item.amount != null && itemInEq.item.amount!! > 1)
//                        itemInEq.item.amount = itemInEq.item.amount!! - 1
//                    else
//                        Player.inventory.itemList.remove(itemInEq)

                    uiStage.inventory.forceRefreshInventory = true
                    hudStage.refreshHotBar()
                    // TODO implement using item on tile
                    //  tile = gameStage.clickedCoordinates!!
                    gameplay.cancelUsage()
                }
                else -> return
            }
        }
    }
}