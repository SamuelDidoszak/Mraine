package com.neutrino

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Gdx.files
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.tommyettinger.textra.Font
import com.github.tommyettinger.textra.KnownFonts
import com.github.tommyettinger.textra.KnownFonts.setAssetPrefix
import com.neutrino.game.Initialize
import com.neutrino.game.Render
import com.neutrino.game.domain.model.characters.Player
import com.neutrino.game.domain.use_case.characters.CharactersUseCases
import ktx.app.KtxScreen

class GameScreen: KtxScreen {
    private val initialize: Initialize = Initialize()
    val render: Render = Render()
    private val extendViewport: ExtendViewport = ExtendViewport(1600f, 900f);
    private val startXPosition = 0f
    private val startYPosition = 800f - 16
    val player: Player = Player

//    private val fontFamily: Font.FontFamily = Font.FontFamily(Skin(files.internal("data/uiskin.json")))
//    val charactersUseCases = CharactersUseCases(initialize.level.characterMap, fontFamily.connected[0])
//    val font: () -> Unit = {setAssetPrefix("fonts/")}
    lateinit var charactersUseCases: CharactersUseCases

    private val stage = GameStage(extendViewport)
    val batch: Batch = stage.batch

    init {
        extendViewport.camera.position.set(800f, 400f, 0f)
        Gdx.input.inputProcessor = stage
        initialize.initialize()
        stage.level = initialize.level
        stage.startXPosition = startXPosition
        stage.startYPosition = startYPosition

        extendViewport.camera.position.set((startXPosition + initialize.level.sizeX) * 8,
            (startYPosition + initialize.level.sizeY.toFloat()) * 2/3f, 0f)

        render.loadAdditionalTextures()
        initialize.setRandomPlayerPosition()

        charactersUseCases = CharactersUseCases(initialize.level.characterMap, KnownFonts.getStandardFamily())


        stage.addActor(initialize.level)
        initialize.level.addActor(Player)
        Player.setAnimation("buddy")
        charactersUseCases.addCharacter(Player)

    }

    override fun render(delta: Float) {
        ScreenUtils.clear(0f, 0f, 0f, 0f)
        extendViewport.apply()
        batch.projectionMatrix = extendViewport.camera.combined
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        render.addAnimations()
        stage.act(delta)
        stage.draw()

        // Player draw isn't called from stage.draw(). Have to call it manually
        batch.begin()
        Player.draw(batch, 1f)
        batch.end()


    }

    override fun dispose() {
        /** Here, dispose of every static state and every thread, because they can survive restarting the application */
        batch.dispose()
    }

    override fun resume() {
        /** Here, Recreate every OpenGL generated texture and references to shaders  */
        super.resume()
    }

    override fun resize(width: Int, height: Int) {
        extendViewport.update(width, height)
    }

}