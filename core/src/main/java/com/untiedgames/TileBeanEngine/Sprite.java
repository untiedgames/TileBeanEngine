package com.untiedgames.TileBeanEngine;
import java.util.Optional;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Sprite extends Component {

	private Texture texture;

	public Sprite(Texture texture) {
		this.texture = texture;
	}

	public void update(float delta) {
		// Override me to do custom logic!
	}

	public void draw() {
		// Override me to do custom drawing!
		draw(TileBeanEngine.getSpriteBatch());
	}

	protected void draw(SpriteBatch spritebatch) {
		Optional<Object2D> opt = TileBeanEngine.world.tryGet(owner);
		if (opt.isPresent()) {
			Object2D obj = opt.get();
			int w = texture.getWidth();
			int h = texture.getHeight();
			float w_half = (float)w * .5f;
			float h_half = (float)h * .5f;
			spritebatch.draw(texture, obj.x - w_half, -obj.y - h_half, w_half, h_half, w, h, obj.scale_x, obj.scale_y, -obj.rotation * 180f / (float)Math.PI, 0, 0, w, h, false, false);
		}
	}

}
