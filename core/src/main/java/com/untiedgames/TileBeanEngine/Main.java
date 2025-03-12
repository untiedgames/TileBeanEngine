package com.untiedgames.TileBeanEngine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.HashSet;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
	Texture image;
	Object2DHandle obj_handle;
	Object2DHandle obj_handle2;

	class DerivedSprite extends Sprite {

		public DerivedSprite(Texture texture) {
			super(texture);
		}

	}

	@Override
	public void create() {
		TileBeanEngine.initialize();
		image = new Texture("libgdx.png");

		Object2D obj = new Object2D();
		obj_handle = TileBeanEngine.world.add(obj);
		TileBeanEngine.world.addComponent(obj_handle, new Sprite(image));

		Object2D obj2 = new Object2D();
		obj2.x = 0;
		obj2.y = 0;
		obj_handle2 = TileBeanEngine.world.add(obj2);
		TileBeanEngine.world.addComponent(obj_handle2, new DerivedSprite(image));
		
		// Tween demo
		TweenLocation tween = new TweenLocation();
		TileBeanEngine.world.addComponent(obj_handle2, tween);
		tween.start(Tween.TYPE.LINEAR, 10, 300, 300);
	}

	@Override
	public void resize(int width, int height) {
		TileBeanEngine.onResize(width, height);
	}

	@Override
	public void render() {
		ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
		TileBeanEngine.run();
		TileBeanEngine.world.get(obj_handle).get().rotation += .01f;

		// Testing World's type info
		/*
		HashSet<Component> set = TileBeanEngine.world.getComponentsOfClass(DerivedSprite.class.hashCode());
		for (Component c : set) {
			TileBeanEngine.world.get(c.getOwner()).get().x++;
		}
		*/
	}

	@Override
	public void dispose() {
		TileBeanEngine.shutdown();
		image.dispose();
	}
}
