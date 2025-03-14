package com.untiedgames.TileBeanEngine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

import imgui.ImGui;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

	int rotation_counter = 0;

	class TestGame extends Game {
		TextureAssetHandle libgdx_logo;
		Object2DHandle obj_handle;
		Object2DHandle obj_handle2;
		
		public void initialize() {
			TextureAsset tex = new TextureAsset("libgdx_logo", "libgdx.png");
			tex.load();
			libgdx_logo = TileBeanEngine.assets.add(tex);

			Object2D obj = new Object2D();
			obj_handle = TileBeanEngine.world.add(obj);

			Sprite sprite = new Sprite();
			sprite.setGraphics(libgdx_logo);
			TileBeanEngine.world.addComponent(obj_handle, sprite);
			
			TweenRotation tween_rotation = new TweenRotation();
			TileBeanEngine.world.addComponent(obj_handle, tween_rotation);
			
			TimerManager timer_manager = new TimerManager();
			TileBeanEngine.world.addComponent(obj_handle, timer_manager);
			timer_manager.start("timer_rotation", 2.0f, -1, false);
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
		}

		public void runGUI() {
			ImGui.showDemoWindow();
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
