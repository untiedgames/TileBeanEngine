package com.untiedgames.TileBeanEngine;

import java.util.ArrayList;
import java.util.Optional;

import com.badlogic.gdx.math.Vector2;
import com.untiedgames.TileBeanEngine.AssetSystem.TilesetAsset;
import com.untiedgames.TileBeanEngine.AssetSystem.TilesetAsset.TileInfo;

public class Collision {

	public static enum RESPONSE {
		NONE,
		RESOLVE
	}

	/**
	 * Detect a potential collision between two Colliders.
	 * Returns a CollisionInfo containing the collision result. If there was no collision, it will indicate that via CollisionInfo.exists.
	 */
	public static CollisionInfo detect(Collider first, Collider second) {
		CollisionInfo ret = new CollisionInfo();
		ret.first = first;
		ret.second = second;

		/*

		This is a slightly naive implementation of collision: It's competent enough for this demo, but not necessarily performant.
		It's written this way to give you a good sense of each step in performing collision detection.
		
		The logic and math used for collision detection, however, is fairly representative of what you might find.
		First, we'll perform a bounding box check (our "broad phase"),
		and following that we'll use the separating axis theorem (our narrow phase) to detect a collision between the two shapes, if one exists.
		
		A real-world engine might:
			- Prepare and cache data needed for collision en masse beforehand
			- Avoid allocations
			- Utilize additional broad-phase detection, such as use of spatial partitioning, to perform less narrow-phase collisions
			- "Know" ahead of time when a collider is an axis-aligned bounding box (AABB), which enables simpler calculations

		*/

		// First, we'll get the transformation matrix for each object, and use it to calculate where the collider's vertices are in that space.

		float[] first_verts = first.getTransformedVertices();
		float[] second_verts = second.getTransformedVertices();
		
		// Determine the bounding boxes of each shape by finding their leftmost, rightmost, topmost, and bottommost vertices.

		float first_left = Float.MAX_VALUE;
		float first_right = -Float.MAX_VALUE;
		float first_top = Float.MAX_VALUE;
		float first_bottom = -Float.MAX_VALUE;
		for (int i = 0; i < first_verts.length; i += 2) {
			if (first_verts[i] < first_left) first_left = first_verts[i];
			if (first_verts[i] > first_right) first_right = first_verts[i];
			if (first_verts[i + 1] < first_top) first_top = first_verts[i + 1];
			if (first_verts[i + 1] > first_bottom) first_bottom = first_verts[i + 1];
		}

		float second_left = Float.MAX_VALUE;
		float second_right = -Float.MAX_VALUE;
		float second_top = Float.MAX_VALUE;
		float second_bottom = -Float.MAX_VALUE;
		for (int i = 0; i < second_verts.length; i += 2) {
			if (second_verts[i] < second_left) second_left = second_verts[i];
			if (second_verts[i] > second_right) second_right = second_verts[i];
			if (second_verts[i + 1] < second_top) second_top = second_verts[i + 1];
			if (second_verts[i + 1] > second_bottom) second_bottom = second_verts[i + 1];
		}

		// Perform bounding box check.

		if (first_left > second_right || first_right < second_left || first_top > second_bottom || first_bottom < second_top) return ret; // The two objects' bounding boxes do not overlap, therefore the objects do not overlap.

		// Perform separating axis theorem (SAT) check.
		// To understand the separating axis theorem, imagine two convex shapes on a flat surface.
		// If a straight line can be drawn between the shapes, then that is an axis of separation, and the shapes must not overlap.
		// Such a line can be parallel to one of the normals of each shape.
		// (Once again, this is not a performant implementation- it's written for clarity.)

		// First, we need to get the normals and projections of each shape onto their own normals. (See Collision.projectToAxis)

		Vector2[] first_normals = new Vector2[first_verts.length / 2];
		Vector2[] first_projections = new Vector2[first_normals.length];
		for (int i = 0; i < first_verts.length; i += 2) {
			float x1 = first_verts[i];
			float y1 = first_verts[i + 1];
			float x2 = first_verts[(i + 2) % first_verts.length];
			float y2 = first_verts[(i + 3) % first_verts.length];
			Vector2 normal = new Vector2(y2 - y1, -(x2 - x1)).nor();
			first_normals[i / 2] = normal;
			first_projections[i / 2] = projectToAxis(first_verts, normal);
		}

		Vector2[] second_normals = new Vector2[second_verts.length / 2];
		Vector2[] second_projections = new Vector2[second_normals.length];
		for (int i = 0; i < second_verts.length; i += 2) {
			float x1 = second_verts[i];
			float y1 = second_verts[i + 1];
			float x2 = second_verts[(i + 2) % second_verts.length];
			float y2 = second_verts[(i + 3) % second_verts.length];
			Vector2 normal = new Vector2(y2 - y1, -(x2 - x1)).nor();
			second_normals[i / 2] = normal;
			second_projections[i / 2] = projectToAxis(second_verts, normal);
		}

		ret.exists = true; // Assume the shapes are overlapping. If we find an axis of separation, then they aren't overlapping.

		float magnitude = Float.MAX_VALUE; // Tracks the minimum overlap of the shapes.
		for (int i = 0; i < first_normals.length; i++) {
			Vector2 axis = first_normals[i];
			
			Vector2 projection1 = first_projections[i];
			Vector2 projection2 = projectToAxis(second_verts, axis);

			if (projection1.x > projection2.y || projection2.x > projection1.y) {
				// No overlap, this is an axis of separation.
				ret.exists = false;
				ret.penetration_x = ret.penetration_y = ret.axis_x = ret.axis_y = 0;
				break;
			}

			float d;
			if (projection1.x < projection2.x) d = projection2.x - projection1.y;
			else d = projection2.y - projection1.x;

			float abs_mag = Math.abs(d);
			if (abs_mag < magnitude) { // We always want to look for the minimum overlap.
				magnitude = abs_mag;
				ret.penetration_x = -axis.x * d;
				ret.penetration_y = -axis.y * d;
				ret.axis_x = axis.x;
				ret.axis_y = axis.y;
			}
		}

		if (ret.exists) {
			// We still haven't found an axis of separation, so check the second shape.

			for (int i = 0; i < second_normals.length; i++) {
				Vector2 axis = second_normals[i];

				Vector2 projection1 = projectToAxis(first_verts, axis);
				Vector2 projection2 = second_projections[i];

				if (projection1.x > projection2.y || projection2.x > projection1.y) {
					// No overlap, this is an axis of separation.
					ret.exists = false;
					ret.penetration_x = ret.penetration_y = ret.axis_x = ret.axis_y = 0;
					break;
				}

				float d;
				if (projection1.x < projection2.x) d = projection2.x - projection1.y;
				else d = projection2.y - projection1.x;

				float abs_mag = Math.abs(d);
				if (abs_mag < magnitude) {
					magnitude = abs_mag;
					ret.penetration_x = -axis.x * d;
					ret.penetration_y = -axis.y * d;
					ret.axis_x = axis.x;
					ret.axis_y = axis.y;
				}
			}
		}

		return ret;
	}

	private static Collider temp_collider = new Collider(); // We'll use this during tilemap collision.

	/**
	 * Detect potential collisions between a Collider and a Tilemap.
	 * Collider vs. Tilemap collision does NOT support rotated Tilemaps, for simplicity.
	 * Because a Tilemap has multiple tiles, it represents (effectively) a collection of colliders.
	 * Therefore, an array is returned as there may be more than one collision detected.
	 * The user can then process the array and react to the tiles that have been collided with.
	 */
	public static TileCollisionInfo[] detect(Collider collider, Tilemap tilemap) {
		Optional<Object2D> opt_obj_tilemap = TileBeanEngine.world.tryGet(tilemap.getOwner());
		if (!opt_obj_tilemap.isPresent()) return new TileCollisionInfo[0];
		Object2D obj_tilemap = opt_obj_tilemap.get();
		Optional<TilesetAsset> opt_tileset_asset = TileBeanEngine.assets.tryGet(tilemap.getTileset());
		if (!opt_tileset_asset.isPresent()) return new TileCollisionInfo[0];
		TilesetAsset tileset_asset = opt_tileset_asset.get();
		
		// First, we'll get the transformed vertices of the collider and determine its bounding box.

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

		// Now that we have a bounding box, we can use it to determine which tiles we're over.
		
		// Account for the Tilemap's position (and scale)
		float tilemap_scaled_x = obj_tilemap.x * obj_tilemap.scale_x;
		float tilemap_scaled_y = obj_tilemap.y * obj_tilemap.scale_y;
		left -= tilemap_scaled_x;
		right -= tilemap_scaled_x;
		top -= tilemap_scaled_y;
		bottom -= tilemap_scaled_y;

		// Account for the Tilemap's scale
		float tile_width = (float)tilemap.getTileWidth() * obj_tilemap.scale_x;
		float tile_height = (float)tilemap.getTileHeight() * obj_tilemap.scale_y;

		// Note: Rotated Tilemaps are not supported by this implementation of collision, so we don't account for rotation here.

		ArrayList<TileCollisionInfo> ret = new ArrayList<>();
		int y_start = Math.max(0, (int)(top / tile_height));
		int y_end = Math.min(tilemap.getHeight() - 1, (int)(bottom / tile_height));
		int x_start = Math.max(0, (int)(left / tile_width));
		int x_end = Math.min(tilemap.getWidth() - 1, (int)(right / tile_width));
		for (int y = y_start; y <= y_end; y++) {
			for (int x = x_start; x <= x_end; x++) {
				int id = tilemap.getTileID(x, y);
				if (id == Integer.MAX_VALUE) continue; // Unassigned tile, nothing to do
				TileInfo tile = tileset_asset.getTileInfo(id);
				TileCollisionShape shape = PrimitiveTileCollisionShape.get(tile.getTileType());
				if (temp_collider.vertices.length != shape.count()) temp_collider.vertices = new float[shape.count()];
				for (int i = 0; i < shape.count(); i++) {
					if (i % 2 == 0) temp_collider.vertices[i] = tilemap_scaled_x + (x + shape.get(i)) * tile_width;
					else temp_collider.vertices[i] = tilemap_scaled_y + (y + shape.get(i)) * tile_height;
				}
				CollisionInfo info = detect(collider, temp_collider);
				if (info.exists) {
					TileCollisionInfo tile_collision_info = new TileCollisionInfo();
					tile_collision_info.collider = collider;
					tile_collision_info.tile_id = tile.getID();
					tile_collision_info.tile_x = x;
					tile_collision_info.tile_y = y;
					tile_collision_info.penetration_x = info.penetration_x;
					tile_collision_info.penetration_y = info.penetration_y;
					tile_collision_info.axis_x = info.axis_x;
					tile_collision_info.axis_y = info.axis_y;
					ret.add(tile_collision_info);
				}
			}
		}

		return ret.toArray(new TileCollisionInfo[0]);
	}

	public static void resolve(CollisionInfo info) {
		resolve(info, RESPONSE.RESOLVE, RESPONSE.RESOLVE);
	}

	public static void resolve(CollisionInfo info, RESPONSE response_first, RESPONSE response_second) {
		if (!info.exists) return; // There is no collision to resolve.

		float modifier = .5f;
		if (response_first == RESPONSE.NONE || response_second == RESPONSE.NONE) modifier = 1.0f;

		if (response_first == RESPONSE.RESOLVE) {
			Optional<Object2D> opt_obj = TileBeanEngine.world.tryGet(info.first.getOwner());
			if (opt_obj.isPresent()) {
				Object2D obj = opt_obj.get();
				obj.x -= info.penetration_x * modifier;
				obj.y -= info.penetration_y * modifier;
			}
		}

		if (response_second == RESPONSE.RESOLVE) {
			Optional<Object2D> opt_obj = TileBeanEngine.world.tryGet(info.second.getOwner());
			if (opt_obj.isPresent()) {
				Object2D obj = opt_obj.get();
				obj.x += info.penetration_x * modifier;
				obj.y += info.penetration_y * modifier;
			}
		}
	}

	public static void resolve(TileCollisionInfo[] info_list) {
		if (info_list.length == 0) return; // There is no collision to resolve.

		Optional<Object2D> opt_obj = TileBeanEngine.world.tryGet(info_list[0].collider.getOwner());
		if (opt_obj.isPresent()) {
			Object2D obj = opt_obj.get();
			
			// Determine the MAXIMUM resolution and resolve it.
			// We're going to separate it into the maximum of both the X and Y components, rather than use the maximum magnitude of each penetration vector.
			// The reason we use the maximum resolution and split the components is because doing so yields more favorable results in practice for platformer games.
			float max_x = 0;
			float max_y = 0;
			for (int i = 0; i < info_list.length; i++) {
				TileCollisionInfo info = info_list[i];
				if (Math.abs(info.penetration_x) > Math.abs(max_x)) max_x = info.penetration_x;
				if (Math.abs(info.penetration_y) > Math.abs(max_y)) max_y = info.penetration_y;
			}
			obj.x -= max_x;
			obj.y -= max_y;
		}
	}

	/**
	 * Returns a representation of a shape as if it were projected onto the given axis.
	 * You can imagine this as if a 2D cloud in your game were to cast a 2D shadow onto the ground.
	 * The given vertices must be in the format x, y, x, y...
	 */
	private static Vector2 projectToAxis(float[] vertices, Vector2 axis) {
		Vector2 projection = new Vector2(Float.MAX_VALUE, -Float.MAX_VALUE);
		for (int i = 0; i < vertices.length; i += 2) {
			float pt_projection = TBEMath.dotProduct(axis.x, axis.y, vertices[i], vertices[i + 1]);
			if (pt_projection < projection.x) projection.x = pt_projection;
			if (pt_projection > projection.y) projection.y = pt_projection;
		}
		return projection;
	}

}
