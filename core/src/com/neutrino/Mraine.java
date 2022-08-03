package com.neutrino;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.neutrino.game.Initialize;
import com.neutrino.game.Render;

public class Mraine extends ApplicationAdapter {
	SpriteBatch batch;
	Render render;
	Initialize initialize;
	ExtendViewport viewport;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		render = new Render(batch);
		initialize = new Initialize();
		viewport = new ExtendViewport(500, 500);

		initialize.initialize();

	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 0);
		batch.begin();
		render.renderLevel(initialize.getLevel());
		batch.end();
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
}
