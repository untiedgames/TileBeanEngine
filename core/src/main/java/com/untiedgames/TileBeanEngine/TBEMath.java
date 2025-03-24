package com.untiedgames.TileBeanEngine;

import com.badlogic.gdx.math.Vector2;

public class TBEMath {

	///////////////////////////
	// Useful math functions //
	///////////////////////////

	/**
	 * Returns the distance between the two given points.
	 */
	public static float dist(float x1, float y1, float x2, float y2) {
		return (float)Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	/**
	 * Calculates the rotation of the given point about the given anchor point, and returns the result. (Rotation is in radians.)
	 */
	public static Vector2 rotateAboutPoint(float point_x, float point_y, float anchor_x, float anchor_y, float rotation) {
		float dx = anchor_x - point_x;
		float dy = anchor_y - point_y;
		double n = Math.sqrt(dx * dx + dy * dy);
		rotation += Math.atan2(dy, dx);
		point_x = (float)(anchor_x - n * Math.cos(-rotation));
		point_y = (float)(anchor_y + n * Math.sin(-rotation));
		return new Vector2(point_x, point_y);
	}

	public static float toDegrees(float angle) {
		return angle * 180.0f / (float)Math.PI;
	}

	public static float toRadians(float angle) {
		return angle * (float)Math.PI / 180.0f;
	}

	public static float normalizeAngle(float angle) {
		while (angle < 0) angle += 2.0f * (float)Math.PI;
		while (angle >= 2.0f * (float)Math.PI) angle -= 2.0f * (float)Math.PI;
		return angle;
	}

	public static float dotProduct(float x1, float y1, float x2, float y2) {
		return x1 * x2 + y1 * y2;
	}

	public static float dotProduct(float x1, float y1, float z1, float x2, float y2, float z2) {
		return x1 * x2 + y1 * y2 + z1 * z2;
	}

	/////////////////////////////
	// Interpolation functions //
	/////////////////////////////

	public static float lerp(float start, float end, float value) {
		return ((1.0f - value) * start) + (value * end);
	}
	
	public static float sinerp(float start, float end, float value) {
		return lerp(start, end, (float)Math.sin(value * Math.PI * .5f));
	}
	
	public static float coserp(float start, float end, float value) {
		return lerp(start, end, 1.0f - (float)Math.cos(value * Math.PI * .5f));
	}
	
	public static float easeInOut(float start, float end, float value) {
		return lerp(start, end, value * value * (3f - 2f * value));
	}
	
	public static float expoIn(float start, float end, float value) {
		if(value == 0) return start;
		else return lerp(start, end, (float)Math.pow(2, 10 * (value - 1f)));
	}
	
	public static float expoOut(float start, float end, float value) {
		if(value == 1) return end;
		else return lerp(start, end, -(float)Math.pow(2, -10 * value) + 1);
	}
	
	public static float expoInOut(float start, float end, float value) {
		if(value == 0) return start;
		if(value == 1) return end;
		if((value *= 2) < 1) return lerp(start, end, .5f * (float)Math.pow(2, 10 * (value - 1)));
		return lerp(start, end, .5f * (-(float)Math.pow(2, -10 * --value) + 2));
	}
	
	public static float elasticOut(float start, float end, float value) {
		if(value == 0) return 0;
		if(value == 1) return 1;
		float p = .3f, a = 1, s = p / 4f;
		return lerp(start, end, a * (float)Math.pow(2, -10 * value) * (float)Math.sin((value - s) * (2f * Math.PI)/p) + 1);
	}
	
	public static float bounceOut(float start, float end, float value) {
		if(value < 1f / 2.75f) return lerp(start, end, 7.5625f * value * value);
		else if(value < 2f / 2.75f) return lerp(start, end, 7.5625f * (value -= (1.5f / 2.75f)) * value + .75f);
		else if(value < 2.5f / 2.75f) return lerp(start, end, 7.5625f * (value -= (2.25f / 2.75f)) * value + .9375f);
		else return lerp(start, end, 7.5625f * (value -= (2.625f / 2.75f)) * value + .984375f);
	}

}
