package com.untiedgames.TileBeanEngine;

import java.util.Optional;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Grid extends Drawable {

	public float cell_width;
	public float cell_height;
	public int width;
	public int height;
	public float line_thickness = 1;
	public boolean use_single_pixel_lines = true;

	/**
	 * Creates a new grid of the specified width and height in cells, with the specified cell size.
	 */
	public Grid(int width, int height, int cell_width, int cell_height) {
		this.width = width;
		this.height = height;
		this.cell_width = cell_width;
		this.cell_height = cell_height;
	}

	public void draw(SpriteBatch spritebatch) {
		Optional<Object2D> opt_obj = TileBeanEngine.world.tryGet(getOwner());
		if (!opt_obj.isPresent()) return;
		Object2D obj = opt_obj.get();

		// Although Grid is a Drawable, it uses the ShapeRenderer instead of the SpriteBatch.
		spritebatch.end();
		ShapeRenderer shaperenderer = TileBeanEngine.getShapeRenderer();
		shaperenderer.setColor(obj.r, obj.g, obj.b, obj.a);
		Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shaperenderer.begin();
		
		float line_length_horiz = width * cell_width;
		float line_length_vert = height * cell_height;

		if (use_single_pixel_lines) {
			for (int y = 0; y <= height; y++) {
				shaperenderer.line(0, y * cell_height, line_length_horiz, y * cell_height);
			}
			for (int x = 0; x <= width; x++) {
				shaperenderer.line(x * cell_width, 0, x * cell_width, line_length_vert);
			}
		} else {
			shaperenderer.set(ShapeType.Filled);
			for (int y = 0; y <= height; y++) {
				shaperenderer.rectLine(0, y * cell_height, line_length_horiz, y * cell_height, line_thickness);
			}
			for (int x = 0; x <= width; x++) {
				shaperenderer.rectLine(x * cell_width, 0, x * cell_width, line_length_vert, line_thickness);
			}
		}

		shaperenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		spritebatch.begin();
	}

	public void update(float delta) {}

}
