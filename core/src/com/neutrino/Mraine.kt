package com.neutrino

import com.badlogic.gdx.Screen
import com.github.tommyettinger.textra.KnownFonts.setAssetPrefix
import com.neutrino.game.util.Scripts
import ktx.app.KtxGame

class Mraine: KtxGame<Screen>() {
    override fun create() {
        setAssetPrefix("textraFonts/")
        Scripts().evaluate()
        // TODO probably unnecessary, check with a constant seed and after floating point precision error fixes
//        Constants
//        Constants.DefaultEntityTexture.textures.first().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)

        addScreen(GameScreen())
        setScreen<GameScreen>()
    }
}