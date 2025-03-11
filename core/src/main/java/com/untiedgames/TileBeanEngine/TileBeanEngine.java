package com.untiedgames.TileBeanEngine;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;

public class TileBeanEngine {

	private static SpriteBatch spritebatch;
	public static ArrayList<Sprite> sprites;
	private static Camera camera;

	public static void initialize() {
		spritebatch = new SpriteBatch();
		sprites = new ArrayList<Sprite>();
		camera = new Camera(1920, 1080);
	}

	public static void shutdown() {
		spritebatch.dispose();
	}

	public static void onResize(int width, int height) {
		camera.setSize(1920, 1080);
	}

	public static SpriteBatch getSpriteBatch() {
		return spritebatch;
	}

	public static void run() {
		for (Sprite sprite : sprites) {
			sprite.update(1);
		}

		camera.setActive();
		spritebatch.begin();
		for (Sprite sprite : sprites) {
			sprite.draw(spritebatch);
		}
		spritebatch.end();
	}

}
