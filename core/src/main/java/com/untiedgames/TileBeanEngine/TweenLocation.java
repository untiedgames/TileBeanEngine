package com.untiedgames.TileBeanEngine;
import java.util.Optional;

/**
 * TweenLocation represents a change in location of a game object over time.
 */
public class TweenLocation extends Tween {

	public TweenLocation() {
		initial_values = new float[3];
		target_values = new float[3];
		result_values = new float[3];
	}

	public void start(TYPE type, float time, float... target_values) {
		super.start(type, time, target_values);
		Optional<Object2D> opt = TileBeanEngine.world.tryGet(getOwner());
		if (opt.isPresent()) {
			Object2D obj = opt.get();
			initial_values[0] = obj.x;
			initial_values[1] = obj.y;
			initial_values[2] = obj.z;
		}
	}

	protected void setValues(float... values) {
		Optional<Object2D> opt = TileBeanEngine.world.tryGet(getOwner());
		if (opt.isPresent()) {
			Object2D obj = opt.get();
			obj.x = values[0];
			obj.y = values[1];
			obj.z = values[2];
		}
	}

}
