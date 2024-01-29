package com.neutrino.game.map.generation

import com.neutrino.game.map.generation.util.GenerationParams


class Generator(
    val main: Boolean,
    val priority: Int,
    private val generate: (params: GenerationParams) -> Unit) {

    fun generate(params: GenerationParams) {
        generate.invoke(params)
    }
}
