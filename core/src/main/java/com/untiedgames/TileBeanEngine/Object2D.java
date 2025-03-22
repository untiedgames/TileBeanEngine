package com.untiedgames.TileBeanEngine;

/**
 * Object2D is the "entity" of the entity-component system in TileBeanEngine.
 * It represents an object in the game world which has a location (x, y), depth (z), rotation, scale (scale_x, scale_y), and color (r, g, b, a).
 * (In a traditional ECS, the variables of this class might be in other components instead, but I want to make things a little simpler.)
 */
public class Object2D {

	Object2DHandle handle;
	
	public float x = 0; // The X-coordinate of the object.
	public float y = 0; // The Y-coordinate of the object. (In TileBeanEngine, negative Y is up.)
	public float z = 0; // The visual depth of the object. Smaller values are in back, larger values are in front.
	public float rotation = 0; // The rotation of the object in radians.
	public float scale_x = 1; // The X scale of the object. (1.0 = default scale)
	public float scale_y = 1; // The Y scale of the object. (1.0 = default scale)
	public float r = 1; // The red component of the object's color. (Range: 0.0 .. 1.0)
	public float g = 1; // The green component of the object's color. (Range: 0.0 .. 1.0)
	public float b = 1; // The blue component of the object's color. (Range: 0.0 .. 1.0)
	public float a = 1; // The alpha (transparency) component of the object's color. (Range: 0.0 .. 1.0)
	public boolean is_visible = true; // Whether or not the object is visible when drawn.

	public Object2DHandle getHandle() {
		return handle;
	}

}
