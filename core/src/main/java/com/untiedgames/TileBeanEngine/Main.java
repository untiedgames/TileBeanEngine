package com.untiedgames.TileBeanEngine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
	Texture image;

	@Override
	public void create() {
		TileBeanEngine.initialize();
		image = new Texture("libgdx.png");

		Sprite s = new Sprite(image);
		s.x = 0;
		s.y = 0;
		s.rotation = 1;
		TileBeanEngine.sprites.add(s);
	}

	@Override
	public void resize(int width, int height) {
		TileBeanEngine.onResize(width, height);
	}

	@Override
	public void render() {
		ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
		TileBeanEngine.run();
		TileBeanEngine.sprites.get(0).rotation += .01f;
		//TileBeanEngine.sprites.get(0).y -= 1;
	}

	@Override
	public void dispose() {
		TileBeanEngine.shutdown();
		image.dispose();
	}
}
