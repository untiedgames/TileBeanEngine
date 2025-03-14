package com.untiedgames.TileBeanEngine;
import java.util.Optional;

// TweenRotation represents a change in rotation of a game object over time.
public class TweenRotation extends Tween {

	public TweenRotation() {
		initial_values = new float[1];
		target_values = new float[1];
		result_values = new float[1];
	}

	public void start(TYPE type, float time, float... target_values) {
		super.start(type, time, target_values);
		Optional<Object2D> opt = TileBeanEngine.world.tryGet(getOwner());
		if (opt.isPresent()) {
			Object2D obj = opt.get();
			initial_values[0] = obj.rotation;
		}
	}

	protected void setValues(float... values) {
		Optional<Object2D> opt = TileBeanEngine.world.tryGet(getOwner());
		if (opt.isPresent()) {
			Object2D obj = opt.get();
			obj.rotation = values[0];
		}
	}

}
