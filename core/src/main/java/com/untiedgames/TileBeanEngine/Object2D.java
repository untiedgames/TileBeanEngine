package com.untiedgames.TileBeanEngine;

public abstract class Object2D {

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

}
