package com.untiedgames.TileBeanEngine;

public class TimerInstance extends Component {

	public enum STATE {
		STOPPED,
		RUNNING,
		PAUSED
	}

	private String name;
	private float duration = 1.0f;
	private float remaining = 1.0f;
	private int repeat_count = -1; // How many times the timer should repeat after its duration elapses. (-1 for infinite repeats.)
	private STATE state = STATE.STOPPED;
	private boolean is_finished = false;
	boolean remove_on_complete = true;

	TimerInstance(String name) {
		this.name = name;
	}

	TimerInstance(String name, float duration) {
		this.name = name;
		this.duration = duration;
	}

	TimerInstance(String name, float duration, int repeat_count, boolean remove_on_complete) {
		this.name = name;
		this.duration = duration;
		this.repeat_count = repeat_count;
		this.remove_on_complete = remove_on_complete;
	}

	String getName() {
		return name;
	}

	void start() {
		remaining = duration;
		state = STATE.RUNNING;
	}

	void start(float duration) {
		this.duration = duration;
		start();
	}

	void start(float duration, int repeat_count) {
		this.duration = duration;
		this.repeat_count = repeat_count;
		start();
	}

	void pause() {
		if (state == STATE.RUNNING) state = STATE.PAUSED;
	}

	void resume() {
		if (state == STATE.PAUSED) state = STATE.RUNNING;
	}

	boolean isRunning() {
		return state == STATE.RUNNING;
	}

	boolean isPaused() {
		return state == STATE.PAUSED;
	}

	boolean isStopped() {
		return state == STATE.STOPPED;
	}

	// Returns true if the timer has ever reached zero.
	// Use in conjunction with clearFinished to "consume" the timer result and reset the timer to an unfinished state.
	boolean isFinished() {
		return is_finished;
	}

	void clearFinished() {
		is_finished = false;
	}

	float getDuration() {
		return duration;
	}

	float getRemaining() {
		return remaining;
	}

	int getRepeatCount() {
		return repeat_count;
	}

	public void update(float delta) {
		if (state == STATE.RUNNING) {
			remaining -= delta;
			if (remaining <= 0.0f) {
				if (repeat_count >= 0 || repeat_count == -1) {
					remaining += duration;
					if (repeat_count != -1) repeat_count--;
				} else state = STATE.STOPPED;

				is_finished = true;
			}
		}
	}

}
