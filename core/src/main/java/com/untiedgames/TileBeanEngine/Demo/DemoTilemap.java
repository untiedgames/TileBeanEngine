package com.untiedgames.TileBeanEngine.Demo;

import com.untiedgames.TileBeanEngine.Camera;
import com.untiedgames.TileBeanEngine.Game;
import com.untiedgames.TileBeanEngine.Grid;
import com.untiedgames.TileBeanEngine.GridHighlight;
import com.untiedgames.TileBeanEngine.Object2D;
import com.untiedgames.TileBeanEngine.Object2DHandle;
import com.untiedgames.TileBeanEngine.TileBeanEngine;
import com.untiedgames.TileBeanEngine.Tilemap;
import com.untiedgames.TileBeanEngine.AssetSystem.TilesetAsset;
import com.untiedgames.TileBeanEngine.AssetSystem.TilesetAssetHandle;
import com.untiedgames.TileBeanEngine.Input.MouseState;

import imgui.ImGui;
import imgui.type.ImBoolean;

public class DemoTilemap extends Game {

	Object2DHandle obj_tilemap_handle;
	Object2DHandle obj_grid_handle;
	int selected_tile = Integer.MAX_VALUE;

	public void initialize() {
		// Load a tileset asset.
		TilesetAsset tileset_asset = new TilesetAsset("tileset", "map/grasslands_tileset.tsx");
		tileset_asset.load();
		TilesetAssetHandle tileset_handle = TileBeanEngine.assets.add(tileset_asset);
		
		// Create a new game object.
		Object2D obj = new Object2D();
		obj_tilemap_handle = TileBeanEngine.world.add(obj);
		
		Object2D cam = TileBeanEngine.world.get(TileBeanEngine.getCameraHandle());
		cam.z = 2;

		int tilemap_w = tileset_asset.getTexture().get().getWidth() / tileset_asset.getTileWidth(); // Total width of the tilemap
		int tilemap_h = tileset_asset.getTexture().get().getHeight() / tileset_asset.getTileHeight(); // Total height of the tilemap
		
		// Add a Tilemap component. Tilemaps can display a grid of tiles from a tileset.
		Tilemap tilemap = new Tilemap(tilemap_w, tilemap_h);
		tilemap.setTileset(tileset_handle);
		TileBeanEngine.world.addComponent(obj_tilemap_handle, tilemap);

		// Place some tiles from the tileset onto the tilemap.
		for (int y = 0; y < tilemap_h; y++) {
			for (int x = 0; x < tilemap_w; x++) {
				int id = tileset_asset.getTileInfo(x, y).getID();
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

	public void update(float delta) {
		Object2DHandle camera_handle = TileBeanEngine.getCameraHandle();
		Object2D obj_cam = TileBeanEngine.world.get(camera_handle);
		Camera cam = (Camera)TileBeanEngine.world.getComponent(camera_handle, Camera.class.hashCode());
		Tilemap tilemap = (Tilemap)TileBeanEngine.world.getComponent(obj_tilemap_handle, Tilemap.class.hashCode());
		
		MouseState mouse_state = TileBeanEngine.input.getMouseState();
		MouseState mouse_state_prev = TileBeanEngine.input.getMouseStatePrev();

		if (mouse_state.isMiddleButtonPressed()) {
			// Pan the camera
			float window_width = TileBeanEngine.getWindowWidth();
			float window_height = TileBeanEngine.getWindowHeight();
			float sc = 1;
			if (window_width < window_height) sc = (window_width / (float)cam.getWidth());
			else sc = (window_height / (float)cam.getHeight());
			float dx = mouse_state.getRawX() - mouse_state_prev.getRawX();
			float dy = mouse_state.getRawY() - mouse_state_prev.getRawY();
			obj_cam.x -= (dx / obj_cam.z) / sc;
			obj_cam.y -= (dy / obj_cam.z) / sc;
		}

		int selected_tile_x = (int)Math.floor(mouse_state.getWorldX() / tilemap.getTileWidth());
		int selected_tile_y = (int)Math.floor(mouse_state.getWorldY() / tilemap.getTileHeight());

		if (mouse_state.isRightButtonPressed()) {
			// Pick a tile
			selected_tile = tilemap.getTileID(selected_tile_x, selected_tile_y);
		}

		if (mouse_state.isLeftButtonPressed()) {
			// Place a tile
			tilemap.placeTile(selected_tile, selected_tile_x, selected_tile_y);
		}

		// Zoom the camera
		float scroll = mouse_state.getScrollY() - mouse_state_prev.getScrollY();
		obj_cam.z -= scroll;
		if (obj_cam.z <= 1) obj_cam.z = 1;

		// Highlight the grid cell the mouse is over
		Grid grid = (Grid)TileBeanEngine.world.getComponent(obj_grid_handle, Grid.class.hashCode());
		grid.highlights.clear();
		GridHighlight highlight = new GridHighlight(selected_tile_x, selected_tile_y);
		highlight.line_thickness = 2;
		grid.highlights.add(highlight);
	}

	public void runGUI() {
		ImGui.textWrapped("This is a demonstration of creating and drawing a Tilemap.\nIf a tileset asset has a collision metadata file, it can be loaded automatically.\n\nUse middle mouse to pan the camera and the mouse wheel to zoom in and out.\n\nUse the right mouse button to select a tile, and use the left mouse button to place the selected tile.");

		if (selected_tile == Integer.MAX_VALUE) {
			ImGui.text("No tile selected.");
		} else {
			ImGui.text("Selected tile ID: " + selected_tile);
		}

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
