package com.untiedgames.TileBeanEngine;

public class CollisionShape {

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
	 * ... And within each slope that spans more than one tile:
	 * 
	 *              /|
	 *             / |  <---- SLOPE_FLOOR_LEFT_60_T (T = triangle)
	 *            /  |
	 *           /___|
	 *          /    |
	 *         /     |  <---- SLOPE_FLOOR_LEFT_60_Q (Q = quad)
	 *        /      |
	 *       /_______|
	 * 
	 * See TileType enum comments for further reference.
	 * (This diagram is best viewed in the comments, not the tooltip.)
	 */
	public enum TYPE {
		EMPTY,					// No collision shape.
		FULL,					// Box collision shape of size { tile_width, tile_height }.
		SLOPE_FLOOR_LEFT_45,	// 45-degree sloped triangle collision shape with the collision resolution normal facing up-left.
		SLOPE_FLOOR_RIGHT_45,	// 45-degree sloped triangle collision shape with the collision resolution normal facing up-right.
		SLOPE_CEIL_LEFT_45,		// 45-degree sloped triangle collision shape with the collision resolution normal facing down-left.
		SLOPE_CEIL_RIGHT_45,	// 45-degree sloped triangle collision shape with the collision resolution normal facing down-right.
		SLOPE_FLOOR_LEFT_30_T,	// Triangle part of 30-degree sloped triangle collision shape with the collision resolution normal facing up-left.
		SLOPE_FLOOR_LEFT_30_Q,	// Quad part of 30-degree sloped triangle collision shape with the collision resolution normal facing up-left.
		SLOPE_FLOOR_RIGHT_30_T,	// Triangle part of 30-degree sloped triangle collision shape with the collision resolution normal facing up-right.
		SLOPE_FLOOR_RIGHT_30_Q,	// Quad part of 30-degree sloped triangle collision shape with the collision resolution normal facing up-right.
		SLOPE_CEIL_LEFT_30_T,	// Triangle part of 30-degree sloped triangle collision shape with the collision resolution normal facing down-left.
		SLOPE_CEIL_LEFT_30_Q,	// Quad part of 30-degree sloped triangle collision shape with the collision resolution normal facing down-left.
		SLOPE_CEIL_RIGHT_30_T,	// Triangle part of 30-degree sloped triangle collision shape with the collision resolution normal facing down-right.
		SLOPE_CEIL_RIGHT_30_Q,	// Quad part of 30-degree sloped triangle collision shape with the collision resolution normal facing down-right.
		SLOPE_FLOOR_LEFT_60_T,	// Triangle part of 60-degree sloped triangle collision shape with the collision resolution normal facing up-left.
		SLOPE_FLOOR_LEFT_60_Q,	// Quad part of 60-degree sloped triangle collision shape with the collision resolution normal facing up-left.
		SLOPE_FLOOR_RIGHT_60_T,	// Triangle part of 60-degree sloped triangle collision shape with the collision resolution normal facing up-right.
		SLOPE_FLOOR_RIGHT_60_Q,	// Quad part of 60-degree sloped triangle collision shape with the collision resolution normal facing up-right.
		SLOPE_CEIL_LEFT_60_T,	// Triangle part of 60-degree sloped triangle collision shape with the collision resolution normal facing down-left.
		SLOPE_CEIL_LEFT_60_Q,	// Quad part of 60-degree sloped triangle collision shape with the collision resolution normal facing down-left.
		SLOPE_CEIL_RIGHT_60_T,	// Triangle part of 60-degree sloped triangle collision shape with the collision resolution normal facing down-right.
		SLOPE_CEIL_RIGHT_60_Q	// Quad part of 60-degree sloped triangle collision shape with the collision resolution normal facing down-right.
	}

	private float[] verts;

	/**
	 * Construct a new CollisionShape with the given vertices, in the format x, y, x, y...
	 * The vertices represent a percentage-based position in the tile.
	 * For example: {0, 0} is the upper-left corner, and {1, 1} is the bottom-right corner.
	 * The vertices must be specified in winding order, preferably clockwise.
	 */
	public CollisionShape(float... verts) {
		this.verts = verts;
	}

	public int count() {
		return verts.length;
	}

	public float get(int index) {
		return verts[index];
	}
}
