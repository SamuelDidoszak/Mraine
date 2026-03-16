package com.neutrino.game.map.generation.worldgen.generators.util

interface GenerationMask {
    fun contains(x: Int, y: Int): Boolean
}