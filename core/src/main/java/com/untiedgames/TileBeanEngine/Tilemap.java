package com.untiedgames.TileBeanEngine;

import java.util.Optional;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.untiedgames.TileBeanEngine.TilesetAsset.TileInfo;

public class Tilemap extends Drawable {

	TilesetAssetHandle tileset_handle;
	int[] contents;
	int width;
	int height;

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
			}
		}
	}

}
