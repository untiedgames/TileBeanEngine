package com.untiedgames.TileBeanEngine;

public class TBEMath {

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
