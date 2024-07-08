package com.neutrino.game.entities.characters.attributes

import com.neutrino.game.map.chunk.ChunkManager

class PlayerAi: Ai() {

    var playerMoving = false
        set(value) {
            field = value
            if (!value)
                ChunkManager.characterMethods.stopWalkAnimations()
        }
}