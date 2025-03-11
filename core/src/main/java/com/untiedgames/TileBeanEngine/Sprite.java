package com.untiedgames.TileBeanEngine;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Sprite {

	private Texture texture;

	public float x = 0;
	public float y = 0;
	public float z = 0;
	public float rotation = 0;
	public float scale_x = 1;
	public float scale_y = 1;

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
		int w = texture.getWidth();
		int h = texture.getHeight();
		float w_half = (float)w * .5f;
		float h_half = (float)h * .5f;
		spritebatch.draw(texture, x - w_half, -y - h_half, w_half, h_half, w, h, scale_x, scale_y, -rotation * 180f / (float)Math.PI, 0, 0, w, h, false, false);
	}

}
