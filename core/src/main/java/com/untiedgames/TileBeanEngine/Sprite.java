package com.untiedgames.TileBeanEngine;
import java.util.Optional;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// Sprite is the main way to display things in TileBeanEngine.
// It provides both update and draw methods, which can be overridden in derived classes.
public class Sprite extends Component {

	private Texture texture;

	public Sprite(Texture texture) {
		this.texture = texture;
	}

	public void update(float delta) {
		// Override me to do custom logic!
	}

	public void draw(SpriteBatch spritebatch) {
		// Override me to do custom drawing!
		drawInternal(spritebatch);
	}

	// Draws the sprite's graphics.
	// In a custom draw method, you can call this method to draw the sprite's graphics at any time.
	protected final void drawInternal(SpriteBatch spritebatch) {
		Optional<Object2D> opt = TileBeanEngine.world.tryGet(owner);
		if (opt.isPresent()) {
			Object2D obj = opt.get();
			int w = texture.getWidth();
			int h = texture.getHeight();
			float w_half = (float)w * .5f;
			float h_half = (float)h * .5f;
			spritebatch.setColor(obj.r, obj.g, obj.b, obj.a);
			spritebatch.draw(texture, obj.x - w_half, -obj.y - h_half, w_half, h_half, w, h, obj.scale_x, obj.scale_y, -obj.rotation * 180f / (float)Math.PI, 0, 0, w, h, false, false);
		}
	}

}
