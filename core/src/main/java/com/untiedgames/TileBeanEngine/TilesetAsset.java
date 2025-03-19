package com.untiedgames.TileBeanEngine;

import java.util.HashMap;
import java.util.Optional;

import com.badlogic.gdx.graphics.Texture;

/**
 * TilesetAsset is an asset which can hold a tileset.
 */
public class TilesetAsset extends Asset {

	/**
	 * This enum represents tile collision shapes.
	 * Their nomenclature is based on platformer games.
	 * For example, for 60-degree slopes:
	 * 
	 *                             /|\
	 * SLOPE_FLOOR_LEFT_60 -----> / | \ <---- SLOPE_FLOOR_RIGHT_60
	 *                           /__|__\
	 *                           \  |  /
	 * SLOPE_CEIL_LEFT_60 ------> \ | / <---- SLOPE_FLOOR_RIGHT_60
	 *                             \|/
	 * 
	 * See TileType enum comments for further reference.
	 * (This diagram is best viewed in the comments, not the tooltip.)
	 */
	public enum TILETYPE {
		EMPTY,					// No collision shape.
		FULL,					// Box collision shape of size { tile_width, tile_height }.
		SLOPE_FLOOR_LEFT_30,	// 30-degree sloped triangle collision shape with the collision resolution normal facing up-left.
		SLOPE_FLOOR_RIGHT_30,	// 30-degree sloped triangle collision shape with the collision resolution normal facing up-right.
		SLOPE_CEIL_LEFT_30,		// 30-degree sloped triangle collision shape with the collision resolution normal facing down-left.
		SLOPE_CEIL_RIGHT_30,	// 30-degree sloped triangle collision shape with the collision resolution normal facing down-right.
		SLOPE_FLOOR_LEFT_45,	// 45-degree sloped triangle collision shape with the collision resolution normal facing up-left.
		SLOPE_FLOOR_RIGHT_45,	// 45-degree sloped triangle collision shape with the collision resolution normal facing up-right.
		SLOPE_CEIL_LEFT_45,		// 45-degree sloped triangle collision shape with the collision resolution normal facing down-left.
		SLOPE_CEIL_RIGHT_45,	// 45-degree sloped triangle collision shape with the collision resolution normal facing down-right.
		SLOPE_FLOOR_LEFT_60,	// 60-degree sloped triangle collision shape with the collision resolution normal facing up-left.
		SLOPE_FLOOR_RIGHT_60,	// 60-degree sloped triangle collision shape with the collision resolution normal facing up-right.
		SLOPE_CEIL_LEFT_60,		// 60-degree sloped triangle collision shape with the collision resolution normal facing down-left.
		SLOPE_CEIL_RIGHT_60		// 60-degree sloped triangle collision shape with the collision resolution normal facing down-right.
	}

	public class TileInfo {

		private int id = Integer.MAX_VALUE; // The ID of the tile. The top-left tile of the tileset has ID 0, the one to its right has ID 1, and so on. Integer.MAX_VALUE represents an unassigned tile.
		private int x = 0; // The X coordinate (in tiles) of this tile on the tileset texture.
		private int y = 0; // The Y coordinate (in tiles) of this tile on the tileset texture.
		private TILETYPE tile_type = TILETYPE.EMPTY;
		
		public int getID() {
			return id;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public TILETYPE getTileType() {
			return tile_type;
		}

		public boolean isEmpty() {
			return id == Integer.MAX_VALUE || tile_type == TILETYPE.EMPTY;
		}

		public boolean isUnassigned() {
			return id == Integer.MAX_VALUE;
		}

	}

	private TextureAsset texture_asset;
	private int tile_width;
	private int tile_height;
	private HashMap<Integer, TileInfo> tiles;
	
	public TilesetAsset(String name_and_path, int tile_width, int tile_height) {
		this(name_and_path, name_and_path, FILEMODE.INTERNAL, tile_width, tile_height);
	}

	public TilesetAsset(String name, String path, int tile_width, int tile_height) {
		this(name, path, FILEMODE.INTERNAL, tile_width, tile_height);
	}

	public TilesetAsset(String name, String path, FILEMODE file_mode, int tile_width, int tile_height) {
		super(name, path, file_mode);
		texture_asset = new TextureAsset(name, path);
		this.tile_width = tile_width;
		this.tile_height = tile_height;
		tiles = new HashMap<>();
	}

	public boolean isLoaded() {
		return texture_asset.isLoaded();
	}

	/**
	 * Loads the TilesetAsset from its path. Supports .png and .anim, although animated tilesets are beyond the scope of this engine.
	 * Returns true on success, false otherwise.
	 * If it cannot be loaded, an error message will be printed in the console.
	 */
	public boolean load() {
		if (texture_asset.isLoaded()) return true; // Asset is already loaded
		if (!texture_asset.load()) return false;
		Texture texture = texture_asset.getTexture().get();
		int id_ctr = 0;
		for (int y = 0; y < texture.getHeight(); y += tile_height) {
			for (int x = 0; x < texture.getWidth(); x += tile_width) {
				TileInfo tile = new TileInfo();
				tile.id = id_ctr;
				tile.x = x / tile_width;
				tile.y = y / tile_height;
				tiles.put(id_ctr++, tile);
			}
		}
		return true;
	}

	/**
	 * Unloads the TilesetAsset, performing any destruction of resources required.
	 */
	public void unload() {
		texture_asset.unload();
	}

	/**
	 * Returns the texture the tileset owns.
	 */
	public Optional<Texture> getTexture() {
		return texture_asset.getTexture();
	}

	/**
	 * Returns the width of this tileset's tiles, in pixels.
	 */
	public int getTileWidth() {
		return tile_width;
	}

	/**
	 * Returns the height of this tileset's tiles, in pixels.
	 */
	public int getTileHeight() {
		return tile_height;
	}

	public TileInfo getTileInfo(int id) {
		if (tiles.containsKey(id)) {
			return tiles.get(id);
		}
		return new TileInfo();
	}

}
