package com.untiedgames.TileBeanEngine;
import java.util.Optional;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;

public class Camera extends Component {

	private int width = 0;
	private int height = 0;
	private OrthographicCamera internal_camera;

	public Camera(int width, int height) {
		this.width = width;
		this.height = height;
		internal_camera = new OrthographicCamera(width, height);
	}

	public void initialize() {
		TileBeanEngine.world.get(getOwner()).z = 1;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		internal_camera.viewportWidth = width;
		internal_camera.viewportHeight = height;
		internal_camera.update();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setActive() {
		SpriteBatch spritebatch = TileBeanEngine.getSpriteBatch();
		spritebatch.setProjectionMatrix(internal_camera.combined);

		Optional<Object2D> opt = TileBeanEngine.world.tryGet(getOwner());
		if (opt.isPresent()) {
			Object2D obj = opt.get();
			if (obj.z != 0.0f) {
				Matrix4 m = new Matrix4();
				m.scale(obj.z, -obj.z, 0);
				if (obj.rotation != 0.0f) m.rotate(0, 0, 1, obj.rotation * 180.0f / (float)Math.PI);
				m.translate(-obj.x, -obj.y, 0);
				spritebatch.setTransformMatrix(m);
			}
		}
	}

	public void update(float delta) {}

}
