package com.untiedgames.TileBeanEngine;
import java.util.Optional;

// TweenColor represents a change in color of a game object over time.
public class TweenColor extends Tween {

	public TweenColor() {
		initial_values = new float[4];
		target_values = new float[4];
		result_values = new float[4];
	}

	public void start(TYPE type, float time, float... target_values) {
		super.start(type, time, target_values);
		Optional<Object2D> opt = TileBeanEngine.world.tryGet(owner);
		if (opt.isPresent()) {
			Object2D obj = opt.get();
			initial_values[0] = obj.r;
			initial_values[1] = obj.g;
			initial_values[2] = obj.b;
			initial_values[3] = obj.a;
		}
	}

	protected void setValues(float... values) {
		Optional<Object2D> opt = TileBeanEngine.world.tryGet(owner);
		if (opt.isPresent()) {
			Object2D obj = opt.get();
			obj.r = values[0];
			obj.g = values[1];
			obj.b = values[2];
			obj.a = values[3];
		}
	}

}
