package com.neutrino;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.neutrino.game.GameInput;
import com.neutrino.game.Initialize;
import com.neutrino.game.Render;

public class Mraine extends ApplicationAdapter {
	SpriteBatch batch;
	Render render;
	Initialize initialize;
	ExtendViewport extendViewport;
	GameInput input;
	Float startXPosition = 0f;
	Float startYPosition = 800f - 16;

	@Override
	public void create () {
		batch = new SpriteBatch();
		render = new Render(batch);
		initialize = new Initialize();
		extendViewport = new ExtendViewport(800, 800);
		extendViewport.getCamera().position.set(800, 400, 0);
		input = new GameInput(extendViewport.getCamera());
		Gdx.input.setInputProcessor(input);

		initialize.initialize();
		input.setLevel(initialize.getLevel());
		input.setStartXPosition(startXPosition);
		input.setStartXPosition(startYPosition);
		extendViewport.getCamera().position.set((startXPosition + initialize.getLevel().getSizeX()) * 8, (startYPosition + initialize.getLevel().getSizeY()) * 2/3, 0);

	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 0);
		extendViewport.apply();
		batch.setProjectionMatrix(extendViewport.getCamera().combined);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		render.renderLevel(initialize.getLevel(), startXPosition, startYPosition);
//		render.render();
		batch.end();

		// apply another viewport and draw with it
	}
	
	@Override
	public void dispose () {
		/** Here, dispose of every static state and every thread, because they can survive restarting the application*/
		batch.dispose();
	}

	@Override
	public void resume() {
		/** Here, Recreate every OpenGL generated texture and references to shaders */
		super.resume();
	}

	@Override
	public void resize(int width, int height) {
		extendViewport.update(width, height);
	}
}
