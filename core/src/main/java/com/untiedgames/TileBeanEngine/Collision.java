package com.untiedgames.TileBeanEngine;

import java.util.Optional;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Collision {

	public static enum RESPONSE {
		NONE,
		RESOLVE
	}

	/**
	 * Detect a potential collision between two colliders.
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

		Optional<Object2D> opt_obj_first = TileBeanEngine.world.tryGet(first.getOwner());
		Optional<Object2D> opt_obj_second = TileBeanEngine.world.tryGet(second.getOwner());
		if (!opt_obj_first.isPresent() || !opt_obj_second.isPresent()) return ret;

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

	public static CollisionInfo detect(Collider collider, Tilemap tilemap) {
		return null;
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
