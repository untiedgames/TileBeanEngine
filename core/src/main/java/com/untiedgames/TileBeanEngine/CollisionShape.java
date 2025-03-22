package com.untiedgames.TileBeanEngine;

public class CollisionShape {
	private float[] verts;

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
