package com.neutrino

import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import ktx.app.KtxGame

class Mraine: KtxGame<Screen>() {
    override fun create() {
        addScreen(GameScreen())
        setScreen<GameScreen>()
    }
}