package com.untiedgames.TileBeanEngine.AssetSystem;

import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.untiedgames.TileBeanEngine.TileCollisionShape;

/**
 * TilesetAsset is an asset which can hold a tileset.
 */
public class TilesetAsset extends Asset {

	

	public class TileInfo {

		private int id = Integer.MAX_VALUE; // The ID of the tile. The top-left tile of the tileset has ID 0, the one to its right has ID 1, and so on. Integer.MAX_VALUE represents an unassigned tile.
		private int x = 0; // The X coordinate (in tiles) of this tile on the tileset texture.
		private int y = 0; // The Y coordinate (in tiles) of this tile on the tileset texture.
		private TileCollisionShape.TYPE tile_type = TileCollisionShape.TYPE.EMPTY;
		
		public int getID() {
			return id;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public TileCollisionShape.TYPE getTileType() {
			return tile_type;
		}

		public boolean isEmpty() {
			return id == Integer.MAX_VALUE || tile_type == TileCollisionShape.TYPE.EMPTY;
		}

		public boolean isUnassigned() {
			return id == Integer.MAX_VALUE;
		}

	}

	private TextureAsset texture_asset;
	private int tile_width;
	private int tile_height;
	private HashMap<Integer, TileInfo> tiles;
	
	public TilesetAsset(String name_and_path) {
		this(name_and_path, name_and_path, FILEMODE.INTERNAL, 16, 16);
	}

	public TilesetAsset(String name, String path) {
		this(name, path, FILEMODE.INTERNAL, 16, 16);
	}

	public TilesetAsset(String name_and_path, int tile_width, int tile_height) {
		this(name_and_path, name_and_path, FILEMODE.INTERNAL, tile_width, tile_height);
	}

	public TilesetAsset(String name, String path, int tile_width, int tile_height) {
		this(name, path, FILEMODE.INTERNAL, tile_width, tile_height);
	}

	public TilesetAsset(String name, String path, FILEMODE file_mode, int tile_width, int tile_height) {
		super(name, path, file_mode);
		if (!Asset.getExtension(path).equals("tsx")) {
			texture_asset = new TextureAsset(name, path);
		}
		this.tile_width = tile_width;
		this.tile_height = tile_height;
		tiles = new HashMap<>();
	}

	public boolean isLoaded() {
		if (texture_asset != null) return texture_asset.isLoaded();
		return false;
	}

	/**
	 * Loads the TilesetAsset from its path. Supports Tiled .tsx, .png, and .anim, although animated tilesets are beyond the scope of this engine.
	 * Returns true on success, false otherwise.
	 * If it cannot be loaded, an error message will be printed in the console.
	 */
	public boolean load() {
		if (Asset.getExtension(path).equals("tsx")) {
			FileHandle file = Asset.makeFileHandle(path, file_mode);
			String data = file.readString();
			// Load a Tiled *.tsx format tileset.
			// This is not a comprehensive loader, and it will only load the tile size and the image source.
			int tile_width_index = data.indexOf("tilewidth=\"");
			int tile_height_index = data.indexOf("tileheight=\"");
			int image_source_index = data.indexOf("image source=\"");
			if (tile_width_index == -1 || tile_height_index == -1 || image_source_index == -1) {
				System.err.println("Invalid *.tsx file: \"" + path + "\". Failed to locate tilewidth, tileheight, and/or image source.");
				return false;
			}
			tile_width_index += 11;
			tile_height_index += 12;
			image_source_index += 14;
			
			String texture_path;
			try {
				tile_width = Integer.parseInt(data.substring(tile_width_index, data.indexOf('\"', tile_width_index)));
				tile_height = Integer.parseInt(data.substring(tile_height_index, data.indexOf('\"', tile_height_index)));
				texture_path = data.substring(image_source_index, data.indexOf('\"', image_source_index));
			} catch (StringIndexOutOfBoundsException e) {
				System.err.println("Invalid *.tsx file: \"" + path + "\". Failed to parse tilewidth, tileheight, and/or image source.");
				return false;
			}

			if (texture_path.contains("..")) {
				if (file_mode == FILEMODE.INTERNAL) System.err.println("Warning: Relative paths are not officially supported in libGDX using the \"internal\" loading mode. This may not work.");
				// Path in tsx file is a relative path, so try to construct a valid path using the path to the tsx file as a base.
				try {
					texture_path = path.substring(0, path.lastIndexOf("/") + 1) + texture_path;
				} catch (StringIndexOutOfBoundsException e) {
					System.err.println("Failed to construct path from relative path in *.tsx file: \"" + path + "\"\nPath: \"" + texture_path + "\"");
					return false;
				}
			} else {
				if (file_mode == FILEMODE.INTERNAL) texture_path = path.substring(0, path.lastIndexOf('/') + 1) + texture_path; // Assume the texture is in the same directory.
			}
			texture_asset = new TextureAsset(texture_path);
		}

		if (texture_asset.isLoaded()) return true; // Asset is already loaded
		if (!texture_asset.load()) return false;

		// Create TileInfo for each tile.
		// Here, we'll also assign collision shapes to the tiles.
		// If there's a collision data file, any tiles which have a mapping assigned in that file will use that mapping.
		// For any other tiles, or if there's no collision data file:
		// We'll do some *very* basic auto-detection for collision shapes based on the tileset texture.
		// Any tiles which contain at least one opaque pixel will automatically be assigned the FULL collision type (a rectangular box).
		Texture texture = texture_asset.getTexture().get();
		int id_ctr = 0;
		TextureData texdat = texture.getTextureData();
		texdat.prepare();
		Pixmap pixmap = texdat.consumePixmap();
		Color c = new Color();
		for (int y = 0; y < texture.getHeight(); y += tile_height) {
			for (int x = 0; x < texture.getWidth(); x += tile_width) {
				TileInfo tile = new TileInfo();
				tile.id = id_ctr;
				tile.x = x / tile_width;
				tile.y = y / tile_height;

				boolean has_any_pixel = false;
				for (int tex_y = y; tex_y < Math.min(y + tile_height, pixmap.getHeight()); tex_y++) {
					for(int tex_x = x; tex_x < Math.min(x + tile_width, pixmap.getWidth()); tex_x++) {
						c.set(pixmap.getPixel(tex_x, tex_y));
						if (c.a != 0.0f) {
							has_any_pixel = true;
							break;
						}
					}
				}
				if (has_any_pixel) tile.tile_type = TileCollisionShape.TYPE.FULL;
				
				tiles.put(id_ctr++, tile);
			}
		}
		if (texdat.disposePixmap()) pixmap.dispose();

		try {
			String collision_path = path.substring(0, path.lastIndexOf('.')) + "_collision.txt";
			FileHandle file = Asset.makeFileHandle(collision_path, file_mode);
			if (file.exists()) {
				String data = file.readString();
				Scanner sc = new Scanner(data);
				if (data.indexOf('\r') != -1) sc.useDelimiter("\r\n");
				else sc.useDelimiter("\n");
				while (sc.hasNext()) {
					String line = sc.next();
					Scanner sc2 = new Scanner(line);
					while(sc2.hasNext()) {
						int x = Integer.parseInt(sc2.next());
						int y = Integer.parseInt(sc2.next());
						sc2.next(); // toss '='
						String value = sc2.next();
						TileInfo tile = getTileInfo(x / tile_width, y / tile_height);
						tile.tile_type = TileCollisionShape.TYPE.valueOf(value);
					}
					sc2.close();
				}
				sc.close();
			}
		} catch (Exception e) {
			System.err.println("Failed to load or parse collision file for tileset: \"" + path + "\"");
			System.err.println(e.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * Unloads the TilesetAsset, performing any destruction of resources required.
	 */
	public void unload() {
		texture_asset.unload();
		tiles.clear();
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

	/**
	 * Returns the TileInfo with the given ID.
	 * If not found, returns an unassigned TileInfo.
	 */
	public TileInfo getTileInfo(int id) {
		if (tiles.containsKey(id)) {
			return tiles.get(id);
		}
		return new TileInfo();
	}

	/**
	 * Returns the TileInfo with the given position in tiles.
	 * If the position is out of bounds, returns an unassigned TileInfo.
	 */
	public TileInfo getTileInfo(int x, int y) {
		for (TileInfo tile : tiles.values()) {
			if (tile.x == x && tile.y == y) return tile;
		}
		return new TileInfo();
	}

}
