package com.untiedgames.TileBeanEngine;

import imgui.ImGui;
import imgui.type.ImBoolean;

public class DemoTilemap extends Game {

	Object2DHandle obj_tilemap_handle;
	Object2DHandle obj_grid_handle;

	public void initialize() {
		// Load a tileset asset.
		TilesetAsset tileset_asset = new TilesetAsset("tileset", "map/grasslands_tileset.tsx");
		tileset_asset.load();
		TilesetAssetHandle tileset_handle = TileBeanEngine.assets.add(tileset_asset);
		
		// Create a new game object.
		Object2D obj = new Object2D();
		obj_tilemap_handle = TileBeanEngine.world.add(obj);
		
		Object2D cam = TileBeanEngine.world.get(TileBeanEngine.getCameraHandle());
		cam.z = 4;

		int tilemap_w = 14; // Total width of the tilemap
		int tilemap_h = 7; // Total height of the tilemap
		int tilemap_place_w = 7; // Width of how many tiles to place on the tilemap
		int tilemap_place_h = 7; // Height of how many tiles to place on the tilemap
		int offset = 2; // Positional offset for the demo (the default tileset spritesheet has its first tile at {2, 2} in tile coords)

		boolean debug = false;
		if (debug) {
			// Debug: Set camera Z to 1 and place each tile of the tileset into the tilemap
			cam.z = 1;
			tilemap_w = tileset_asset.getTexture().get().getWidth() / tileset_asset.getTileWidth();
			tilemap_h = tileset_asset.getTexture().get().getHeight() / tileset_asset.getTileHeight();
			tilemap_place_w = tilemap_w;
			tilemap_place_h = tilemap_h;
			offset = 0;
		}

		// Add a Tilemap component. Tilemaps can display a grid of tiles from a tileset.
		Tilemap tilemap = new Tilemap(tilemap_w, tilemap_h);
		tilemap.setTileset(tileset_handle);
		TileBeanEngine.world.addComponent(obj_tilemap_handle, tilemap);

		// Place some tiles from the tileset onto the tilemap.
		for (int y = 0; y < tilemap_place_h; y++) {
			for (int x = 0; x < tilemap_place_w; x++) {
				int id = tileset_asset.getTileInfo(x + offset, y + offset).getID();
				tilemap.placeTile(id, x, y);
			}
		}

		// Create a grid to display over the tiles.
		Object2D obj_grid = new Object2D();
		obj_grid_handle = TileBeanEngine.world.add(obj_grid);
		obj_grid.z = 1;
		Grid grid = new Grid(tilemap_w, tilemap_h, 16, 16);
		grid.use_single_pixel_lines = false;
		TileBeanEngine.world.addComponent(obj_grid_handle, grid);
	}

	public void shutdown() {
		TileBeanEngine.assets.clear();
		TileBeanEngine.world.clear();
	}

	public void update(float delta) {}

	public void runGUI() {
		ImGui.textWrapped("This is a demonstration of creating and drawing a Tilemap.");

		Object2D obj_grid = TileBeanEngine.world.get(obj_grid_handle);
		Tilemap tilemap = (Tilemap)TileBeanEngine.world.getComponent(obj_tilemap_handle, Tilemap.class.hashCode());

		ImBoolean is_visible = new ImBoolean(obj_grid.is_visible);
		if (ImGui.checkbox("Show grid", is_visible)) {
			obj_grid.is_visible = is_visible.get();
			if (tilemap.show_collision) tilemap.show_collision = false;
		}
		
		ImBoolean show_collision = new ImBoolean(tilemap.show_collision);
		if (ImGui.checkbox("Show Collision", show_collision)) {
			tilemap.show_collision = show_collision.get();
			if (obj_grid.is_visible) obj_grid.is_visible = false;
		}
	}

}
