package com.untiedgames.TileBeanEngine;
import java.util.Optional;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Sprite is the main way to display things in TileBeanEngine.
 * It provides both update and draw methods, which can be overridden in derived classes.
 */
public class Sprite extends Drawable {

	private TextureAssetHandle texture_handle = TextureAssetHandle.empty();
	private boolean is_playing = false;
	private float current_frame = 0;
	private float play_speed = 1;

	public boolean is_looping = true;

	////////////////////////
	// Animation controls //
	////////////////////////
	
	/**
	 * Returns a handle to the TextureAsset that this Sprite is displaying.
	 */
	public TextureAssetHandle getGraphics() {
		return texture_handle;
	}

	/**
	 * Sets the TextureAsset that this Sprite will display.
	 */
	public void setGraphics(TextureAssetHandle handle) {
		if (handle == null) {
			texture_handle = TextureAssetHandle.empty();
			return;
		}

		this.texture_handle = handle;
		current_frame = 0;
		Optional<TextureAsset> opt_texture_asset = TileBeanEngine.assets.tryGet(texture_handle);
		if (opt_texture_asset.isPresent()) is_looping = opt_texture_asset.get().isLooping();
	}

	/**
	 * Changes the animation state of the Sprite to "playing."
	 */
	public void play() {
		is_playing = true;
	}

	/**
	 * Changes the animation state of the Sprite to "playing" and sets the current animation frame.
	 */
	public void gotoAndPlay(int frame) {
		current_frame = frame;
		if (current_frame < 0) current_frame = 0;
		is_playing = true;
	}

	public boolean isPlaying() {
		return is_playing;
	}

	/**
	 * Changes the animation state of the Sprite to "stopped."
	 */
	public void stop() {
		is_playing = false;
	}

	/**
	 * Changes the animation state of the Sprite to "stopped and sets the current animation frame."
	 */
	public void gotoAndStop(int frame) {
		current_frame = frame;
		if (current_frame < 0) current_frame = 0;
		is_playing = false;
	}

	/**
	 * Returns the float representation of the current frame. Cast to int (round down) to get the index of the frame which will be displayed.
	 */
	public float getCurrentFrame() {
		return current_frame;
	}

	public int getTotalFrames() {
		Optional<TextureAsset> opt_texture_asset = TileBeanEngine.assets.tryGet(texture_handle);
		if (!opt_texture_asset.isPresent()) return 0; // Texture has been removed or wasn't set
		return opt_texture_asset.get().getTotalFrames();
	}

	/////////////////////
	// Update and draw //
	/////////////////////

	/**
	 * Override me to do custom logic!
	 * Be sure to call super.update in your override method if you want this Sprite to animate.
	 */
	public void update(float delta) {
		if (!is_playing || play_speed == 0.0f) return; // Nothing to do

		Optional<TextureAsset> opt_texture_asset = TileBeanEngine.assets.tryGet(texture_handle);
		if (!opt_texture_asset.isPresent()) return; // Texture has been removed or wasn't set

		TextureAsset texture_asset = opt_texture_asset.get();
		float fps = texture_asset.getFPS();
		if (fps == 0.0f) return; // Nothing to do
		int total_frames = texture_asset.getTotalFrames();
		if (total_frames <= 1) return; // Nothing to do

		current_frame += play_speed * delta * fps;

		// Forward looping
		while (current_frame >= total_frames) {
			if (is_looping) {
				current_frame -= (float)total_frames;
			} else {
				current_frame = (float)total_frames - 1;
				stop();
			}
		}

		// Backward looping
		while (current_frame < 0) {
			if (is_looping) {
				current_frame += (float)total_frames;
			} else {
				current_frame = 0;
				stop();
			}
		}

		// Final range check
		if (current_frame < 0.0f) current_frame = 0.0f;
		else if (current_frame > total_frames - .00001f) current_frame = total_frames - .00001f;
	}

	/**
	 * Override me to do custom drawing!
	 * Be sure to call drawInternal or super.draw in your override method if you want to draw this Sprite.
	 */
	public void draw(SpriteBatch spritebatch) {
		drawInternal(spritebatch);
	}

	/**
	 * Draws the sprite's graphics.
	 * In a custom draw method, you can call this method to draw the sprite's graphics at any time.
	 */
	protected final void drawInternal(SpriteBatch spritebatch) {
		Optional<TextureAsset> opt_texture_asset = TileBeanEngine.assets.tryGet(texture_handle);
		if (opt_texture_asset.isPresent()) {
			TextureAsset texture_asset = opt_texture_asset.get();
			Optional<Texture> opt_texture = texture_asset.getTexture((int)current_frame);
			if (opt_texture.isPresent()) {
				Texture texture = opt_texture.get();
				Optional<Object2D> opt_obj = TileBeanEngine.world.tryGet(getOwner());
				if (opt_obj.isPresent()) {
					Object2D obj = opt_obj.get();
					int w = texture.getWidth();
					int h = texture.getHeight();
					float w_half = (float)w * .5f;
					float h_half = (float)h * .5f;
					spritebatch.setColor(obj.r, obj.g, obj.b, obj.a);
					spritebatch.draw(texture, obj.x - w_half, obj.y - h_half, w_half, h_half, w, h, obj.scale_x, -obj.scale_y, obj.rotation * 180f / (float)Math.PI, 0, 0, w, h, false, false);
				}
			}
		}
	}

}
