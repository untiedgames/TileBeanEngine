package com.untiedgames.TileBeanEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Camera {

	public float x = 0;
	public float y = 0;
	public float z = 1;
	public float rotation = 0;
	
	private int width = 0;
	private int height = 0;
	private OrthographicCamera internal_camera;
	private ScreenViewport internal_viewport;

	public Camera(int width, int height) {
		this.width = width;
		this.height = height;
		internal_camera = new OrthographicCamera(width, height);
		internal_viewport = new ScreenViewport(internal_camera);
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		internal_camera.viewportWidth = width;
		internal_camera.viewportHeight = height;
		internal_camera.update();
	}

	public void setActive() {
		TileBeanEngine.getSpriteBatch().setProjectionMatrix(internal_camera.combined);
	}

}
