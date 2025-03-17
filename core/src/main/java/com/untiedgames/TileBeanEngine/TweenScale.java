package com.untiedgames.TileBeanEngine;
import java.util.Optional;

/**
 * TweenScale represents a change in scale of a game object over time.
 */
public class TweenScale extends Tween {

	public TweenScale() {
		initial_values = new float[2];
		target_values = new float[2];
		result_values = new float[2];
	}

	public void start(TYPE type, float time, float... target_values) {
		super.start(type, time, target_values);
		Optional<Object2D> opt = TileBeanEngine.world.tryGet(getOwner());
		if (opt.isPresent()) {
			Object2D obj = opt.get();
			initial_values[0] = obj.scale_x;
			initial_values[1] = obj.scale_y;
		}
	}

	protected void setValues(float... values) {
		Optional<Object2D> opt = TileBeanEngine.world.tryGet(getOwner());
		if (opt.isPresent()) {
			Object2D obj = opt.get();
			obj.scale_x = values[0];
			obj.scale_y = values[1];
		}
	}

}
