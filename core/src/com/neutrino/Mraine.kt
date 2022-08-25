package com.neutrino

import com.badlogic.gdx.Screen
import com.github.tommyettinger.textra.KnownFonts.setAssetPrefix
import com.neutrino.game.Constants
import ktx.app.KtxGame

class Mraine: KtxGame<Screen>() {
    override fun create() {
        setAssetPrefix("textraFonts/")
        Constants

        addScreen(GameScreen())
        setScreen<GameScreen>()
    }
}