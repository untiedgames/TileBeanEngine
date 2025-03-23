package com.untiedgames.TileBeanEngine;

import java.util.Optional;

import com.untiedgames.TileBeanEngine.Input.MouseState;

import imgui.ImGui;
import imgui.type.ImBoolean;

public class DemoCollision extends Game {

	Object2DHandle obj_tilemap_handle;

	public void initialize() {
		// Load a tileset asset.
		TilesetAsset tileset_asset = new TilesetAsset("tileset", "map/grasslands_tileset.tsx");
		tileset_asset.load();
		TilesetAssetHandle tileset_handle = TileBeanEngine.assets.add(tileset_asset);

		// Load a tilemap asset.
		TilemapAsset tilemap_asset = new TilemapAsset("tilemap", "map/test_map.tmx");
		tilemap_asset.load();
		TilemapAssetHandle tilemap_handle = TileBeanEngine.assets.add(tilemap_asset);
		
		// Create a new game object.
		Object2D obj = new Object2D();
		obj_tilemap_handle = TileBeanEngine.world.add(obj);
		
		// Add a Tilemap component. Tilemaps can display a grid of tiles from a tileset.
		Optional<Tilemap> opt_tilemap = tilemap_asset.getLayer(0);
		if (!opt_tilemap.isPresent()) throw new Error("Failed to load demo tilemap.");
		Tilemap tilemap = opt_tilemap.get();
		tilemap.setTileset(tileset_handle);
		TileBeanEngine.world.addComponent(obj_tilemap_handle, tilemap);

		Object2D cam = TileBeanEngine.world.get(TileBeanEngine.getCameraHandle());
		cam.x = 240;
		cam.y = 135;
		cam.z = 4;
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
		

	}

	public void runGUI() {
		ImGui.textWrapped("This is a demonstration of collision.");

		Tilemap tilemap = (Tilemap)TileBeanEngine.world.getComponent(obj_tilemap_handle, Tilemap.class.hashCode());
		
		ImBoolean show_collision = new ImBoolean(tilemap.show_collision);
		if (ImGui.checkbox("Show Collision", show_collision)) {
			tilemap.show_collision = show_collision.get();
		}
	}

}
