package com.untiedgames.TileBeanEngine;

import java.util.ArrayList;
import java.util.Optional;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;

public class Grid extends Drawable {

	public float cell_width;
	public float cell_height;
	public int width;
	public int height;
	public float line_thickness = 1;
	public boolean use_single_pixel_lines = true;
	public boolean show_grid = true;
	public boolean show_highlights = true;
	public ArrayList<GridHighlight> highlights;

	/**
	 * Creates a new grid of the specified width and height in cells, with the specified cell size.
	 */
	public Grid(int width, int height, int cell_width, int cell_height) {
		this.width = width;
		this.height = height;
		this.cell_width = cell_width;
		this.cell_height = cell_height;
		highlights = new ArrayList<>();
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

		Matrix4 original_matrix = shaperenderer.getTransformMatrix().cpy();
		Matrix4 m = new Matrix4();
		m.scale(obj.scale_x, obj.scale_y, 0);
		m.translate(obj.x, obj.y, 0);
		if (obj.rotation != 0.0f) m.rotate(0, 0, 1, obj.rotation * 180.0f / (float)Math.PI);
		Matrix4 transform_matrix = original_matrix.cpy().mul(m);
		shaperenderer.setTransformMatrix(transform_matrix);
		shaperenderer.setColor(obj.r, obj.g, obj.b, obj.a);

		shaperenderer.begin();
		
		float line_length_horiz = width * cell_width;
		float line_length_vert = height * cell_height;

		if (show_grid) {
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
		}

		if (show_highlights) {
			for (GridHighlight highlight : highlights) {
				shaperenderer.setColor(highlight.r, highlight.g, highlight.b, highlight.a);
				float thickness = highlight.line_thickness == 0 ? line_thickness : highlight.line_thickness;
				if (thickness <= 1.0f && use_single_pixel_lines) {
					shaperenderer.set(ShapeType.Line);
					shaperenderer.rect(highlight.x * cell_width, highlight.y * cell_height, cell_width, cell_height);
				} else {
					shaperenderer.set(ShapeType.Filled);
					float start_x = highlight.x * cell_width;
					float start_y = highlight.y * cell_height;
					float endpoint_x = start_x + cell_width;
					float endpoint_y = start_y + cell_height;
					shaperenderer.rectLine(start_x, start_y, endpoint_x, start_y, thickness);
					shaperenderer.rectLine(endpoint_x, start_y, endpoint_x, endpoint_y, thickness);
					shaperenderer.rectLine(endpoint_x, endpoint_y, start_x, endpoint_y, thickness);
					shaperenderer.rectLine(start_x, endpoint_y, start_x, start_y, thickness);
				}
			}
		}

		shaperenderer.end();
		shaperenderer.setTransformMatrix(original_matrix);
		Gdx.gl.glDisable(GL20.GL_BLEND);
		spritebatch.begin();
	}

	public void update(float delta) {}

}
