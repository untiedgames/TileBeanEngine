package com.untiedgames.TileBeanEngine;

import com.badlogic.gdx.audio.Sound;
import com.untiedgames.TileBeanEngine.Input.MouseState;

import imgui.ImGui;

public class DemoInput extends Game {

	TextureAssetHandle tex_asset_char_idle;
	TextureAssetHandle tex_asset_char_run;
	TextureAssetHandle tex_asset_char_wow;
	SoundAssetHandle sound_asset_wow;
	Object2DHandle obj_handle;
	boolean sound_played = false;
	float hold_time = 0;
	
	public void initialize() {
		// Load animations.
		TextureAsset tex_asset_char_idle = new TextureAsset("char_idle", "gfx/character/idle.anim");
		tex_asset_char_idle.load();
		this.tex_asset_char_idle = TileBeanEngine.assets.add(tex_asset_char_idle);

		TextureAsset tex_asset_char_run = new TextureAsset("char_run", "gfx/character/run.anim");
		tex_asset_char_run.load();
		this.tex_asset_char_run = TileBeanEngine.assets.add(tex_asset_char_run);

		TextureAsset tex_asset_char_wow = new TextureAsset("char_wow", "gfx/character/wow.anim");
		tex_asset_char_wow.load();
		this.tex_asset_char_wow = TileBeanEngine.assets.add(tex_asset_char_wow);

		SoundAsset sound_asset_wow = new SoundAsset("sound_wow", "sound/wow.ogg");
		sound_asset_wow.load();
		this.sound_asset_wow = TileBeanEngine.assets.add(sound_asset_wow);
		
		// Create a new game object.
		Object2D obj = new Object2D();
		obj_handle = TileBeanEngine.world.add(obj);

		// Add a Sprite component. Sprites can display images and animations.
		Sprite sprite = new Sprite();
		sprite.setGraphics(this.tex_asset_char_idle);
		sprite.play();
		TileBeanEngine.world.addComponent(obj_handle, sprite);
		
		obj.scale_x = obj.scale_y = 6;
	}

	public void shutdown() {
		TileBeanEngine.assets.clear();
		TileBeanEngine.world.clear();
	}

	public void update(float delta) {
		Object2D obj = TileBeanEngine.world.get(obj_handle);
		Sprite sprite = (Sprite)TileBeanEngine.world.getComponent(obj_handle, Sprite.class.hashCode());

		boolean moved = false;
		boolean can_move = !sprite.getGraphics().equals(tex_asset_char_wow); // If the character is doing the "wow" animation, he can't move.

		if (TileBeanEngine.input.isKeyDown(Input.Keys.SPACE)) {
			can_move = false;
			if (!sprite.getGraphics().equals(tex_asset_char_wow)) {
				sprite.setGraphics(tex_asset_char_wow);
				sprite.stop();
			}
		}

		if (TileBeanEngine.input.wasKeyDown(Input.Keys.SPACE) && !TileBeanEngine.input.isKeyDown(Input.Keys.SPACE)) {
			// The spacebar has been released
			sprite.setGraphics(tex_asset_char_wow);
			sprite.play();
			can_move = false;
			sound_played = false;
			hold_time = TileBeanEngine.input.getKeyStatePrev(Input.Keys.SPACE).getDuration();
		}

		if (can_move) {
			if (TileBeanEngine.input.isKeyDown(Input.Keys.LEFT)) {
				obj.x -= 300f * delta;
				obj.scale_x = -Math.abs(obj.scale_x);
				moved = true;
			}

			if (TileBeanEngine.input.isKeyDown(Input.Keys.RIGHT)) {
				obj.x += 300f * delta;
				obj.scale_x = Math.abs(obj.scale_x);
				moved = true;
			}
		}

		if (can_move) {
			if (!moved) {
				// Character has not moved, ensure the idle animation is playing
				if (!sprite.getGraphics().equals(tex_asset_char_idle)) {
					sprite.setGraphics(tex_asset_char_idle);
					sprite.play();
				}
			} else {
				// Character is moving, ensure the run animation is playing
				if (!sprite.getGraphics().equals(tex_asset_char_run)) {
					sprite.setGraphics(tex_asset_char_run);
					sprite.play();
				}
			}
		} else {
			if (!sprite.isPlaying()) {
				// Character's "wow" animation has finished, set the animation back to idle
				sprite.setGraphics(tex_asset_char_idle);
				sprite.play();
			} else if (!sound_played && (int)sprite.getCurrentFrame() == 6) {
				// Play a nice, crusty "WOW" sound effect
				Sound sound = TileBeanEngine.assets.get(sound_asset_wow).getSound().get();
				long id = sound.play();
				sound.setVolume(id, Math.min(hold_time, 1.0f)); // Max 100% volume
				sound_played = true;
			}
		}
	}

	public void runGUI() {
		ImGui.textWrapped("This is a demonstration of handling input from the player, and a small demo of sound playback.\nPress left or right arrows to move, and press spacebar to \"WOW!\"\n\nThe longer you hold spacebar, the louder the \"WOW\" will be. You can hold it up to 1 second to reach 100% volume.\n\nAdditionally, this demo shows the raw mouse position and the mouse position in the game world.");
		
		Sprite sprite = (Sprite)TileBeanEngine.world.getComponent(obj_handle, Sprite.class.hashCode());
		Input.InputState space_state = TileBeanEngine.input.getKeyState(Input.Keys.SPACE);
		if (space_state.isPressed() || sprite.getGraphics().equals(tex_asset_char_wow)) {
			ImGui.text("Volume");
			ImGui.sameLine();
			float volume;
			if (space_state.isPressed()) volume = space_state.getDuration();
			else volume = hold_time;
			ImGui.progressBar(Math.min(volume, 1.0f)); // Display the actual volume the sound effect will use

			ImGui.text("Spacebar hold time: " + String.format("%.2f", volume) + " seconds");
		}

		MouseState mouse_state = TileBeanEngine.input.getMouseState();
		
		ImGui.text("Mouse raw location: " + mouse_state.getRawX() + ", " + mouse_state.getRawY());
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("The raw mouse location is relative to the top-left of the window.");
		}

		// These are floats but I'm casting them to int just so they display nicer
		ImGui.text("Mouse world location: " + (int)mouse_state.getWorldX() + ", " + (int)mouse_state.getWorldY());
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("The world mouse location is relative to the origin of the game world, and respects the camera position, rotation, and zoom.\nIn this demo, the camera is stationary at {0, 0} so the center of the window will have a mouse world location of {0, 0}.");
		}

	}

}
