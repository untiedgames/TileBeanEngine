package com.untiedgames.TileBeanEngine;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class TileBeanEngine {

	private static SpriteBatch spritebatch;
	public static World world;
	private static Camera camera;

	public static void initialize() {
		spritebatch = new SpriteBatch();
		world = new World();
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
		for (ArrayList<Component> list : world.components.values()) {
			for (Component c : list) {
				c.update(1.0f / 60.0f); //TODO: Need an accumulator + proper game loop
			}
		}

		camera.setActive();
		spritebatch.begin();
		for (Component c : world.getComponentsOfClass(Sprite.class.hashCode())) {
			Sprite s = (Sprite)c;
			s.draw(spritebatch);
		}
		spritebatch.end();
	}

}
