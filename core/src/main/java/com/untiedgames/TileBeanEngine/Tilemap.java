package com.untiedgames.TileBeanEngine;

import java.util.ArrayList;
import java.util.Optional;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.untiedgames.TileBeanEngine.TilesetAsset.TileInfo;

public class Tilemap extends Drawable {

	private TilesetAssetHandle tileset_handle;
	private int[] contents;
	private int width;
	private int height;

	public boolean show_collision = false; // A debug switch that can be toggled on at any time to show tile collision over the drawn tiles.

	/**
	 * Creates a Tlemap with the given width and height, in tiles.
	 */
	public Tilemap(int width, int height) {
		this.width = width;
		this.height = height;
		int size = width * height;
		if (size > 0) {
			contents = new int[size];
			for(int i = 0; i < size; i++) {
				contents[i] = Integer.MAX_VALUE;
			}
		}
	}

	/**
	 * Returns a handle to the TilesetAsset that this Tilemap uses.
	 */
	public TilesetAssetHandle getTileset() {
		return tileset_handle;
	}

	/**
	 * Sets the TilesetAsset that this Tilemap will use.
	 */
	public void setTileset(TilesetAssetHandle handle) {
		if (handle == null) {
			tileset_handle = TilesetAssetHandle.empty();
			return;
		}

		this.tileset_handle = handle;
	}

	/**
	 * Places a tile with the given ID at the given position in tiles.
	 * If the position is outside the bounds of the Tilemap, nothing happens.
	 */
	public void placeTile(int id, int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) return;
		contents[y * width + x] = id;
	}

	/**
	 * Returns the ID of the tile at the given position in tiles.
	 * If the position is out of bounds, returns Integer.MAX_VALUE.
	 */
	public int getTileID(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) return Integer.MAX_VALUE;
		return contents[y * width + x];
	}

	public void update(float delta) {}

	public void draw(SpriteBatch spritebatch) {
		Optional<TilesetAsset> opt_tileset = TileBeanEngine.assets.tryGet(tileset_handle);
		if (!opt_tileset.isPresent()) return;

		TilesetAsset tileset = opt_tileset.get();
		if (!tileset.isLoaded()) return;
		int tile_width = tileset.getTileWidth();
		int tile_height = tileset.getTileHeight();
		Texture texture = tileset.getTexture().get();

		Optional<Object2D> opt_obj = TileBeanEngine.world.tryGet(getOwner());
		if (!opt_obj.isPresent()) return;
		Object2D obj = opt_obj.get();

		Object2DHandle cam_handle = TileBeanEngine.getCameraHandle();
		Optional<Object2D> opt_cam = TileBeanEngine.world.tryGet(cam_handle);
		
		int left = 0, right = width, top = 0, bottom = height;

		if (opt_cam.isPresent()) {
			Object2D cam = opt_cam.get();
			Optional<Component> opt_cam_component = TileBeanEngine.world.tryGetComponent(cam_handle, Camera.class.hashCode());
			if (opt_cam_component.isPresent()) {
				Camera cam_component = (Camera)opt_cam_component.get();
				left = (Math.round(cam.x) - cam_component.getWidth() / 2) / tile_width;
				right = (Math.round(cam.x) + cam_component.getWidth() / 2) / tile_width;
				top = (Math.round(cam.y) - cam_component.getHeight() / 2) / tile_height;
				bottom = (Math.round(cam.y) + cam_component.getHeight() / 2) / tile_height;
			}
		}

		Matrix4 original_matrix = spritebatch.getTransformMatrix();
		Matrix4 m = new Matrix4();
		m.scale(obj.scale_x, obj.scale_y, 0);
		m.translate(obj.x, obj.y, 0);
		if (obj.rotation != 0.0f) m.rotate(0, 0, 1, obj.rotation * 180.0f / (float)Math.PI);
		spritebatch.setTransformMatrix(original_matrix.mul(m));

		spritebatch.setColor(obj.r, obj.g, obj.b, obj.a);

		ShapeRenderer shaperenderer = TileBeanEngine.getShapeRenderer();
		ArrayList<Float> collision_verts = null;
		int collision_vert_ctr = 0;
		if (show_collision) {
			collision_verts = new ArrayList<>();
		}

		for (int y = top; y <= bottom; y++) {
			if (y < 0) continue;
			if (y >= height) break;
			for (int x = left; x <= right; x++) {
				if (x < 0) continue;
				if (x >= width) break;
				int tile_id = contents[y * width + x];
				TileInfo tile = tileset.getTileInfo(tile_id);
				if (tile.isUnassigned()) continue; // No tile to draw
				spritebatch.draw(texture, x * tile_width, y * tile_height, tile_width, tile_height, tile.getX() * tile_width, tile.getY() * tile_height, tile_width, tile_height, false, true);

				if (show_collision) {
					CollisionShape shape = PrimitiveCollisionShape.get(tile.getTileType());
					if (shape != null) {
						for (int i = 0; i < shape.count() + 2; i++) {
							// The vertices are packed in the collision shape in x, y, x, y... order, so we have to alternate width and height here
							if (collision_verts.size() % 2 == 0) {
								collision_verts.add(((float)x + shape.get(i % shape.count())) * (float)tile_width);
							} else {
								collision_verts.add(((float)y + shape.get(i % shape.count())) * (float)tile_height);
							}
						}
						// Add a pair of x/y sentinel values to indicate the end of the shape
						collision_verts.add(Float.MAX_VALUE);
						collision_verts.add(Float.MAX_VALUE);
					}
				}
			}
		}

		spritebatch.setTransformMatrix(original_matrix);

		if (show_collision) {
			// Draw the collision shapes using the vertices which we calculated earlier
			spritebatch.end();
			shaperenderer.setColor(1, 0, 0, 1);
			shaperenderer.setTransformMatrix(spritebatch.getTransformMatrix());
			shaperenderer.begin();

			for (int i = 0; i <= collision_verts.size() - 4; i += 2) {
				float v0 = collision_verts.get(i);
				float v1 = collision_verts.get(i + 1);
				float v2 = collision_verts.get(i + 2);
				float v3 = collision_verts.get(i + 3);
				if (v0 == Float.MAX_VALUE || v1 == Float.MAX_VALUE || v2 == Float.MAX_VALUE || v3 == Float.MAX_VALUE ) {
					continue;
				}
				shaperenderer.line(v0, v1, v2, v3);
			}

			shaperenderer.end();
			shaperenderer.setTransformMatrix(original_matrix);
			spritebatch.begin();
		}
	}

}
