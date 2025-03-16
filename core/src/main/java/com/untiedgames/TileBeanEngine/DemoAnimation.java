package com.untiedgames.TileBeanEngine;

import imgui.ImGui;
import imgui.type.ImInt;

public class DemoAnimation extends Game {

	String[] animations = { "gfx/character/idle.anim", "gfx/character/run.anim", "gfx/character/wow.anim", "gfx/wow/loop.anim", "gfx/flag.anim" };
	String[] animation_names = { "TileBean Idle", "TileBean Run", "TileBean Wow", "Speech Bubble Wow", "Flag" };
	ImInt current_animation = new ImInt(0);
	Object2DHandle obj_handle;
	
	public void initialize() {
		// Load all animations required by the demo.
		for (int i = 0; i < animations.length; i++) {
			TextureAsset asset = new TextureAsset(animation_names[i], animations[i]);
			if (!asset.load()) {
				throw new Error("Failed to load internal asset for demo: " + animations[i]);
			}
			TileBeanEngine.assets.add(asset);
		}
		
		// Create a new game object.
		Object2D obj = new Object2D();
		obj_handle = TileBeanEngine.world.add(obj);
		obj.scale_x = 6;
		obj.scale_y = 6;

		// Add a Sprite component. Sprites can display images and animations.
		Sprite sprite = new Sprite();
		TileBeanEngine.world.addComponent(obj_handle, sprite);
		sprite.play();

		initializeAnimation();
	}

	public void shutdown() {
		TileBeanEngine.assets.clear();
		TileBeanEngine.world.clear();
	}

	private void initializeAnimation() {
		// Set the graphics of the sprite based on the current animation.
		Sprite sprite = (Sprite)TileBeanEngine.world.getComponent(obj_handle, Sprite.class.hashCode());
		sprite.setGraphics(TileBeanEngine.assets.getTextureAssetHandle(animation_names[current_animation.get()]));
	}

	public void update(float delta) {}

	public void runGUI() {
		ImGui.textWrapped("This is a demonstration of displaying animations using the Sprite class. You can change the animation below!");
		
		if (ImGui.combo("Animation", current_animation, animation_names)) {
			initializeAnimation();
		}

		Sprite sprite = (Sprite)TileBeanEngine.world.getComponent(obj_handle, Sprite.class.hashCode());
		
		if (ImGui.button("Play")) {
			sprite.play();
		}

		ImGui.sameLine();

		if (ImGui.button("Stop")) {
			sprite.stop();
		}

		float[] f = { sprite.getCurrentFrame() };
		if (ImGui.sliderFloat("Current frame", f, 0, sprite.getTotalFrames())) {
			if (sprite.isPlaying()) sprite.gotoAndPlay((int)f[0]);
			else sprite.gotoAndStop((int)f[0]);
		}
		
	}

}
