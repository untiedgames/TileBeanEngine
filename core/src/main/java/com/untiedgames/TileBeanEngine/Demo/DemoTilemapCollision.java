package com.untiedgames.TileBeanEngine.Demo;

import java.util.Optional;

import com.badlogic.gdx.Input.Keys;
import com.untiedgames.TileBeanEngine.Collider;
import com.untiedgames.TileBeanEngine.Collision;
import com.untiedgames.TileBeanEngine.Game;
import com.untiedgames.TileBeanEngine.Grid;
import com.untiedgames.TileBeanEngine.GridHighlight;
import com.untiedgames.TileBeanEngine.Object2D;
import com.untiedgames.TileBeanEngine.Object2DHandle;
import com.untiedgames.TileBeanEngine.TBEMath;
import com.untiedgames.TileBeanEngine.TileBeanEngine;
import com.untiedgames.TileBeanEngine.TileCollisionInfo;
import com.untiedgames.TileBeanEngine.Tilemap;
import com.untiedgames.TileBeanEngine.AssetSystem.TilemapAsset;
import com.untiedgames.TileBeanEngine.AssetSystem.TilesetAsset;
import com.untiedgames.TileBeanEngine.AssetSystem.TilesetAssetHandle;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;

public class DemoTilemapCollision extends Game {

	Object2DHandle obj_tilemap_handle;
	Object2DHandle obj_handle;
	Object2DHandle obj_grid_handle;
	ImBoolean show_overlapped_tiles = new ImBoolean(false);
	ImBoolean show_only_collided_tiles = new ImBoolean(true);
	ImFloat gravity = new ImFloat(20);
	boolean is_collider_box = false;
	ImFloat collider_width = new ImFloat(24);
	ImFloat collider_height = new ImFloat(24);
	float[] collider_rotation = {0};
	ImInt collider_num_points = new ImInt(16);
	float obj_velocity_y = 0;

	public void initialize() {
		TileBeanEngine.show_colliders = true; // Start the demo with colliders visible.

		// Load a tileset asset.
		TilesetAsset tileset_asset = new TilesetAsset("tileset", "map/grasslands_tileset/grasslands_tileset.tsx");
		tileset_asset.load();
		TilesetAssetHandle tileset_handle = TileBeanEngine.assets.add(tileset_asset);

		// Load a tilemap asset.
		TilemapAsset tilemap_asset = new TilemapAsset("tilemap", "map/test_map.tmx");
		tilemap_asset.load();
		TileBeanEngine.assets.add(tilemap_asset);
		
		// Create a new game object.
		Object2D obj_tilemap = new Object2D();
		obj_tilemap_handle = TileBeanEngine.world.add(obj_tilemap);
		
		// Add a Tilemap component. Tilemaps can display a grid of tiles from a tileset.
		Optional<Tilemap> opt_tilemap = tilemap_asset.getLayer(0);
		if (!opt_tilemap.isPresent()) throw new Error("Failed to load demo tilemap.");
		Tilemap tilemap = opt_tilemap.get();
		tilemap.setTileset(tileset_handle);
		tilemap.show_collision = true;
		TileBeanEngine.world.addComponent(obj_tilemap_handle, tilemap);

		// Create a game object which the user can control.
		Object2D obj = new Object2D();
		obj_handle = TileBeanEngine.world.add(obj);
		obj.x = 216;
		obj.y = 135;

		setupCollision();

		// Create a grid to display over the tiles.
		Object2D obj_grid = new Object2D();
		obj_grid_handle = TileBeanEngine.world.add(obj_grid);
		obj_grid.z = 1;
		Grid grid = new Grid(tilemap.getWidth(), tilemap.getHeight(), tilemap.getTileWidth(), tilemap.getTileHeight());
		grid.use_single_pixel_lines = false;
		grid.show_grid = false; // We're only going to show highlights with this grid.
		TileBeanEngine.world.addComponent(obj_grid_handle, grid);

		// Set the camera for the demo.
		Object2D cam = TileBeanEngine.world.get(TileBeanEngine.getCameraHandle());
		cam.x = 240;
		cam.y = 135;
		cam.z = 4;
	}

	/**
	 * Removes any existing Collider from the second object, and adds a new one based on the demo's current settings.
	 */
	private void setupCollision() {
		TileBeanEngine.world.removeComponent(obj_handle, Collider.class.hashCode());

		// Add a Collider component to the second object.
		Collider c;
		if (is_collider_box) {
			c = Collider.makeBoxCollider(collider_width.get(), collider_height.get(), TBEMath.toRadians(collider_rotation[0]));
		} else {
			c = Collider.makeCircleCollider(collider_width.get(), collider_num_points.get(), TBEMath.toRadians(collider_rotation[0]));
		}
		TileBeanEngine.world.addComponent(obj_handle, c);
	}

	public void shutdown() {
		TileBeanEngine.assets.clear();
		TileBeanEngine.world.clear();
		TileBeanEngine.show_colliders = false;
	}

	public void update(float delta) {
		Object2D obj = TileBeanEngine.world.get(obj_handle);
		Tilemap tilemap = (Tilemap)TileBeanEngine.world.getComponent(obj_tilemap_handle, Tilemap.class.hashCode());
		Collider collider = (Collider)TileBeanEngine.world.getComponent(obj_handle, Collider.class.hashCode());
		Grid grid = (Grid)TileBeanEngine.world.getComponent(obj_grid_handle, Grid.class.hashCode());

		// Player movement
		if (TileBeanEngine.input.isKeyDown(Keys.LEFT)) {
			obj.x -= 100.0f * delta;
		}
		if (TileBeanEngine.input.isKeyDown(Keys.RIGHT)) {
			obj.x += 100.0f * delta;
		}
		if (TileBeanEngine.input.isKeyDown(Keys.UP)) {
			obj.y -= 100.0f * delta;
		}
		if (TileBeanEngine.input.isKeyDown(Keys.DOWN)) {
			obj.y += 100.0f * delta;
		}

		// Gravity
		obj.y += obj_velocity_y;
		obj_velocity_y += gravity.get() * delta;

		// Show overlapped tiles if requested
		if (show_overlapped_tiles.get()) {
			updateGridHighlights();
		} else {
			grid.highlights.clear();
		}
		
		// Detect collision(s)
		TileCollisionInfo[] info_list = Collision.detect(collider, tilemap);

		// Show collided tiles if requested
		if (show_only_collided_tiles.get()) {
			for (TileCollisionInfo info : info_list) {
				grid.highlights.add(new GridHighlight(info.tile_x, info.tile_y));
			}
		}

		float y_prev = obj.y; // Save the player's previous Y position
		
		// Resolve collision(s)
		Collision.resolve(info_list);
		
		if (obj.y < y_prev) obj_velocity_y = 0; // If the player was moved *up* out of a tile, they must be standing on something, so reset their Y velocity.
	}

	public void runGUI() {
		ImGui.textWrapped("This is a demonstration of Collider vs. Tilemap collision.\nUse the keyboard's left/right/up/down arrows to move the Collider.\n(*If you interact with the GUI you may need to click back into the game area before it accepts keyboard input again.)");

		Tilemap tilemap = (Tilemap)TileBeanEngine.world.getComponent(obj_tilemap_handle, Tilemap.class.hashCode());
		
		ImBoolean show_collision = new ImBoolean(tilemap.show_collision);
		if (ImGui.checkbox("Show Tilemap collision", show_collision)) {
			tilemap.show_collision = show_collision.get();
		}

		if (ImGui.checkbox("Show overlapped tiles", show_overlapped_tiles)) {
			if (show_overlapped_tiles.get()) show_only_collided_tiles.set(false);
		}
		
		if (ImGui.checkbox("Show only collided-with tiles", show_only_collided_tiles)) {
			if (show_only_collided_tiles.get()) show_overlapped_tiles.set(false);
		}

		ImGui.inputFloat("Gravity", gravity);

		if (ImGui.button("Reset collider position")) {
			Object2D obj = TileBeanEngine.world.get(obj_handle);
			obj.x = 200;
			obj.y = 135;
			obj_velocity_y = 0;
		}

		ImGui.separator();

		ImGui.text("Collider");

		ImInt mouse_collider_shape = new ImInt(0);
		if (!is_collider_box) mouse_collider_shape.set(1);
		if (ImGui.combo("Shape", mouse_collider_shape, new String[]{"Box", "Circle"})) {
			is_collider_box = (mouse_collider_shape.get() == 0);
			setupCollision();
		}

		String size_input_str = "Width";
		if (!is_collider_box) size_input_str = "Radius";
		if (ImGui.inputFloat(size_input_str, collider_width)) {
			setupCollision();
		}

		if (is_collider_box) {
			if (ImGui.inputFloat("Height", collider_height)) {
				setupCollision();
			}
		}

		if (ImGui.sliderFloat("Rotation", collider_rotation, 0, 360)) {
			setupCollision();
		}
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("The rotation may be specified for Collision.makeBoxCollider and Collision.makeCircleCollider to control the orientation of the collision shape.\nFor example, this could be useful if you want to make a triangle collider with the base of the triangle facing down.\nFor convenience, this input widget accepts degrees. (The functions themselves accept radians.)");
		}

		if (!is_collider_box) {
			if (ImGui.inputInt("Number of points", collider_num_points)) {
				setupCollision();
			}
			if (ImGui.isItemHovered()) {
				ImGui.setTooltip("The number of points to use with Collision.createCircleCollider.\nDespite its name, it really creates regular polygons.");
			}
			if (collider_num_points.get() < 3) {
				ImGui.textColored(255, 0, 0, 255, "The minimum number of points is 3.");
				if (ImGui.isItemHovered()) {
					ImGui.setTooltip("The reason for a minimum number of points is obvious- You can't create a polygon with less than 3 points.\nAdditionally, libGDX will \"helpfully\" throw an exception with its ShapeRenderer.");
				}
			} else if (collider_num_points.get() > 128) {
				ImGui.textColored(255, 0, 0, 255, "The maximum number of points is 128.");
				if (ImGui.isItemHovered()) {
					ImGui.setTooltip("There is no theoretical maximum number of points for the collision math, but 128 is a practical one.\nToo many points would also overwhelm the libGDX ShapeRenderer.");
				}
			}
		}
	}

	/**
	 * This function re-does a bit of work done in Collision.detect, just so we can show grid highlights for each tile that the collider is overlapping.
	 */
	private void updateGridHighlights() {
		Collider collider = (Collider)TileBeanEngine.world.getComponent(obj_handle, Collider.class.hashCode());
		Object2D obj_tilemap = TileBeanEngine.world.get(obj_tilemap_handle);
		Tilemap tilemap = (Tilemap)TileBeanEngine.world.getComponent(obj_tilemap_handle, Tilemap.class.hashCode());
		Grid grid = (Grid)TileBeanEngine.world.getComponent(obj_grid_handle, Grid.class.hashCode());

		float[] verts = collider.getTransformedVertices();
		
		float left = Float.MAX_VALUE;
		float right = -Float.MAX_VALUE;
		float top = Float.MAX_VALUE;
		float bottom = -Float.MAX_VALUE;
		for (int i = 0; i < verts.length; i += 2) {
			if (verts[i] < left) left = verts[i];
			if (verts[i] > right) right = verts[i];
			if (verts[i + 1] < top) top = verts[i + 1];
			if (verts[i + 1] > bottom) bottom = verts[i + 1];
		}

		left -= obj_tilemap.x * obj_tilemap.scale_x;
		right -= obj_tilemap.x * obj_tilemap.scale_x;
		top -= obj_tilemap.y * obj_tilemap.scale_y;
		bottom -= obj_tilemap.y * obj_tilemap.scale_y;

		float tile_width = (float)tilemap.getTileWidth() * obj_tilemap.scale_x;
		float tile_height = (float)tilemap.getTileHeight() * obj_tilemap.scale_y;

		grid.highlights.clear();
		for (int y = (int)(top / tile_height); y <= (int)(bottom / tile_height); y++) {
			for (int x = (int)(left / tile_width); x <= (int)(right / tile_width); x++) {
				GridHighlight highlight = new GridHighlight(x, y);
				grid.highlights.add(highlight);
			}
		}
	}

}
