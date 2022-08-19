package com.neutrino

import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import com.github.tommyettinger.textra.KnownFonts.setAssetPrefix
import ktx.app.KtxGame

class Mraine: KtxGame<Screen>() {
    override fun create() {
        setAssetPrefix("textraFonts/")

        addScreen(GameScreen())
        setScreen<GameScreen>()
    }
}