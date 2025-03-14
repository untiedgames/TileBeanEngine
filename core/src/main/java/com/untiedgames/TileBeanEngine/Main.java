package com.untiedgames.TileBeanEngine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import imgui.ImGui;

import java.util.HashSet;
import java.util.Optional;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

	int rotation_counter = 0;

	class TestGame extends Game {
		Texture image;
		Object2DHandle obj_handle;
		Object2DHandle obj_handle2;
		
		public void initialize() {
			image = new Texture("libgdx.png");

			Object2D obj = new Object2D();
			obj_handle = TileBeanEngine.world.add(obj);
			TileBeanEngine.world.addComponent(obj_handle, new Sprite(image));
			
			TweenRotation tween_rotation = new TweenRotation();
			TileBeanEngine.world.addComponent(obj_handle, tween_rotation);
			
			TimerManager timer_manager = new TimerManager();
			TileBeanEngine.world.addComponent(obj_handle, timer_manager);
			timer_manager.start("timer_rotation", 2.0f, -1, false);
			
			/*
			Object2D obj2 = new Object2D();
			obj2.x = 0;
			obj2.y = 0;
			obj_handle2 = TileBeanEngine.world.add(obj2);
			TileBeanEngine.world.addComponent(obj_handle2, new DerivedSprite(image));
			
			// Tween demo
			TweenLocation tween = new TweenLocation();
			TileBeanEngine.world.addComponent(obj_handle2, tween);
			tween.start(Tween.TYPE.LINEAR, 10, 300, 300);
			*/
		}

		public void update(float delta) {
			// Check obj_handle's timer. If it's finished, we'll rotate the object using a tween.
			TimerManager timer_manager = (TimerManager)TileBeanEngine.world.getComponent(obj_handle, TimerManager.class.hashCode());
			TimerInstance timer_rotation = timer_manager.get("timer_rotation");
			if (timer_rotation.isFinished()) {
				rotation_counter++;
				TweenRotation tween_rotation = (TweenRotation)TileBeanEngine.world.getComponent(obj_handle, TweenRotation.class.hashCode());
				tween_rotation.start(Tween.TYPE.ELASTICOUT, 2.0f, (float)rotation_counter * (float)Math.PI * .5f);
				timer_rotation.clearFinished();
			}

			// Testing World's type info
			/*
			HashSet<Component> set = TileBeanEngine.world.getComponentsOfClass(DerivedSprite.class.hashCode());
			for (Component c : set) {
				TileBeanEngine.world.get(c.getOwner()).get().x++;
			}
			*/
		}

		public void runGUI() {
			ImGui.showDemoWindow();
		}

	}

	class DerivedSprite extends Sprite {

		public DerivedSprite(Texture texture) {
			super(texture);
		}

	}

	@Override
	public void create() {
		TileBeanEngine.initialize();
		TileBeanEngine.game = new TestGame();
		TileBeanEngine.game.initialize();
	}

	@Override
	public void resize(int width, int height) {
		TileBeanEngine.onResize(width, height);
	}

	@Override
	public void render() {
		ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
		TileBeanEngine.run();
	}

	@Override
	public void dispose() {
		TileBeanEngine.shutdown();
	}
}
