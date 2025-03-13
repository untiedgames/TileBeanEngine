package com.untiedgames.TileBeanEngine;

// A Tween represents the details of a change in value over time, like "inbetweening" in animation.
// For example, a Tween can be used to animate an object moving from one point to another, or scaling from one size to another.
// The tween type represents how the value changes on the way from its initial values to its target values.
public abstract class Tween extends Component {

	public enum TYPE {
		LINEAR,
		EASEIN,
		EASEOUT,
		EASEINOUT,
		EXPOIN,
		EXPOOUT,
		EXPOINOUT,
		ELASTICOUT,
		BOUNCEOUT
	}

	protected TYPE type = TYPE.LINEAR;
	protected float progress = 0;
	protected float time = 0;
	protected float[] initial_values;
	protected float[] target_values;
	protected float[] result_values;
	protected boolean is_running = false;

	protected abstract void setValues(float... values);

	public boolean isRunning() {
		return is_running;
	}

	public float getProgress() {
		return progress;
	}

	public float getTime() {
		return time;
	}

	// Starts the tween using the given type, time, and target values.
	// If the tween is already running, this will override the previous tween animation.
	public void start(TYPE type, float time, float... target_values) {
		this.type = type;
		this.time = time;
		for (int i = 0; i < Math.min(this.target_values.length, target_values.length); i++) {
			this.target_values[i] = target_values[i];
		}
		is_running = true;
		progress = 0;
	}
	
	public void update(float delta) {
		if (!is_running) return;

		progress += delta;
		if (progress >= time) {
			is_running = false;
		}

		process(type, progress / time, initial_values, target_values, result_values);
		setValues(result_values);
	}

	// Given initial and target values and a result array, calculates the result values for the given tween type and the elapsed progress through the tween (t).
	// The value for t should be between 0 and 1, where 0 represents the initial values and 1 represents the target values.
	public static void process(TYPE type, float t, float[] initial_values, float[] target_values, float[] result) {
		switch(type) {
			case LINEAR:
				for (int i = 0; i < initial_values.length; i++) result[i] = TBEMath.lerp(initial_values[i], target_values[i], t);
				break;
			case EASEIN:
				for (int i = 0; i < initial_values.length; i++) result[i] = TBEMath.coserp(initial_values[i], target_values[i], t);
				break;
			case EASEOUT:
				for (int i = 0; i < initial_values.length; i++) result[i] = TBEMath.sinerp(initial_values[i], target_values[i], t);
				break;
			case EASEINOUT:
				for (int i = 0; i < initial_values.length; i++) result[i] = TBEMath.easeInOut(initial_values[i], target_values[i], t);
				break;
			case EXPOIN:
				for (int i = 0; i < initial_values.length; i++) result[i] = TBEMath.expoIn(initial_values[i], target_values[i], t);
				break;
			case EXPOOUT:
				for (int i = 0; i < initial_values.length; i++) result[i] = TBEMath.expoOut(initial_values[i], target_values[i], t);
				break;
			case EXPOINOUT:
				for (int i = 0; i < initial_values.length; i++) result[i] = TBEMath.expoInOut(initial_values[i], target_values[i], t);
				break;
			case ELASTICOUT:
				for (int i = 0; i < initial_values.length; i++) result[i] = TBEMath.elasticOut(initial_values[i], target_values[i], t);
				break;
			case BOUNCEOUT:
				for (int i = 0; i < initial_values.length; i++) result[i] = TBEMath.bounceOut(initial_values[i], target_values[i], t);
				break;
		}
	}

}
