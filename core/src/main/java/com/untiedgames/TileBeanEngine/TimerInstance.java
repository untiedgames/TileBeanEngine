package com.untiedgames.TileBeanEngine;

/**
 * TimerInstance is a component which acts like a stopwatch. You give it a duration and tell it to start, and it will count down to zero over time.
 * The isFinished method can be used in conjunction with clearFinished() to perform tasks when the timer is up. (See isFinished comments below for example)
 */
public class TimerInstance extends Component {

	public enum STATE {
		STOPPED,
		RUNNING,
		PAUSED
	}

	private String name;
	private float duration = 1.0f; // The duration of the timer, in seconds.
	private float remaining = 1.0f; // How much time the timer has remaining, in seconds.
	private int repeat_count = -1; // How many times the timer should repeat after its duration elapses. (-1 for infinite repeats.)
	private STATE state = STATE.STOPPED;
	private boolean is_finished = false;

	public boolean remove_on_complete = true;

	/**
	 * Creates a timer with the given name and a default duration of 1 second.
	 */
	public TimerInstance(String name) {
		this.name = name;
	}

	/**
	 * Creates a timer with the given name and duration in seconds.
	 */
	public TimerInstance(String name, float duration) {
		this.name = name;
		this.duration = duration;
	}

	/**
	 * Creates a timer with the given name, duration in seconds, and repeat count.
	 * If remove_on_complete is true and this TimerInstance is managed by a TimerManager, the TimerManager will remove it on completion. (See TimerManager)
	 */
	public TimerInstance(String name, float duration, int repeat_count, boolean remove_on_complete) {
		this.name = name;
		this.duration = duration;
		this.repeat_count = repeat_count;
		this.remove_on_complete = remove_on_complete;
	}

	public String getName() {
		return name;
	}

	public void start() {
		remaining = duration;
		state = STATE.RUNNING;
	}

	public void start(float duration) {
		this.duration = duration;
		start();
	}

	public void start(float duration, int repeat_count) {
		this.duration = duration;
		this.repeat_count = repeat_count;
		start();
	}

	public void pause() {
		if (state == STATE.RUNNING) state = STATE.PAUSED;
	}

	public void resume() {
		if (state == STATE.PAUSED) state = STATE.RUNNING;
	}

	public boolean isRunning() {
		return state == STATE.RUNNING;
	}

	public boolean isPaused() {
		return state == STATE.PAUSED;
	}

	public boolean isStopped() {
		return state == STATE.STOPPED;
	}

	/**
	 * Returns true if the timer has ever reached zero.
	 * Use in conjunction with clearFinished to "consume" the timer result and reset the timer to an unfinished state.
	 * For example:
	 * if (timer.isFinished()) {
	 *     doSomething();
	 *     timer.clearFinished();
	 * }
	 */
	public boolean isFinished() {
		return is_finished;
	}

	/**
	 * Makes the timer "forget" that it has reached zero. (See example in isFinished comments)
	 */
	public void clearFinished() {
		is_finished = false;
	}

	public float getDuration() {
		return duration;
	}

	public float getRemaining() {
		return remaining;
	}

	/**
	 * Returns a percentage between 0.0 and 1.0 representing how close the timer is to finishing.
	 */
	public float getProgress() {
		if (duration == 0.0f) return 1;
		return ((duration - remaining) / duration);
	}

	public int getRepeatCount() {
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
