package com.untiedgames.TileBeanEngine;

/**
 * Simple class which represents a highlighted cell on a Grid.
 */
public class GridHighlight {

	public int x;
	public int y;
	public float r = .2f;
	public float g = .75f;
	public float b = 1.0f;
	public float a = 1;
	public float line_thickness = 0; // 0 = inherit grid line thickness

	public GridHighlight(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public GridHighlight(int x, int y, float r, float g, float b, float a) {
		this.x = x;
		this.y = y;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public GridHighlight(int x, int y, float r, float g, float b, float a, float line_thickness) {
		this.x = x;
		this.y = y;
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.line_thickness = line_thickness;
	}

}