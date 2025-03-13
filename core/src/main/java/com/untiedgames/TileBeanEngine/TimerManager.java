package com.untiedgames.TileBeanEngine;
import java.util.ArrayList;
import java.util.Optional;

// TimerManager is a component which supports adding, removing, and manipulating multiple TimerInstances.
// It's a good example of a component which can support many instances of a "sub-component."
// The TimerInstance component may also be used individually, if your object only needs one timer.
public class TimerManager extends Component {

	private ArrayList<TimerInstance> timers;

	public TimerManager() {
		timers = new ArrayList<TimerInstance>();
	}

	// Adds a timer to the timer manager with the given name and duration.
	// If a timer with the given name already exists, it is replaced.
	public TimerInstance add(String name, float duration) {
		remove(name);
		return add(name, duration, 0, true);
	}

	// Adds a timer to the timer manager with the given name, duration, and repeat count.
	// If a timer with the given name already exists, it is replaced.
	// If remove_on_complete is true, the timer will be removed from the timer manager when it is completed (remaining <= 0 && repeat_count == 0 && is_finished == false).
	public TimerInstance add(String name, float duration, int repeat_count, boolean remove_on_complete) {
		remove(name);
		TimerInstance t = new TimerInstance(name, duration, repeat_count, remove_on_complete);
		timers.add(t);
		return t;
	}

	// Adds a timer to the timer manager with the given name and duration, then starts it.
	// If a timer with the given name already exists, it is replaced.
	public TimerInstance start(String name, float duration) {
		TimerInstance t = add(name, duration);
		t.start();
		return t;
	}

	// Adds a timer to the timer manager with the given name, duration, and repeat count, then starts it.
	// If a timer with the given name already exists, it is replaced.
	// If remove_on_complete is true, the timer will be removed from the timer manager when it is completed (remaining <= 0 && repeat_count == 0 && is_finished == false).
	public TimerInstance start(String name, float duration, int repeat_count, boolean remove_on_complete) {
		TimerInstance t = add(name, duration, repeat_count, remove_on_complete);
		t.start();
		return t;
	}

	// Retrieves a timer with the given name from the timer manager, or empty if not present.
	public Optional<TimerInstance> tryGet(String name) {
		for (TimerInstance t : timers) {
			if (t.getName().equals(name)) return Optional.of(t);
		}
		return Optional.empty();
	}

	// The less-safe version of tryGet. Use this when you expect a timer with the given name to be there.
	public TimerInstance get(String name) {
		for (TimerInstance t : timers) {
			if (t.getName().equals(name)) return t;
		}
		return null;
	}

	// Returns true if this timer manager has a timer with the given name, false otherwise.
	public boolean has(String name) {
		for (TimerInstance t : timers) {
			if (t.getName().equals(name)) return true;
		}
		return false;
	}

	// Removes the timer with the given name from the timer manager if present.
	public void remove(String name) {
		for (int i = 0; i < timers.size();) {
			if (timers.get(i).getName().equals(name)) {
				timers.remove(i);
			} else i++;
		}
	}

	public void update(float delta) {
		for (int i = 0; i < timers.size();) {
			boolean removed = false;
			TimerInstance t = timers.get(i);
			t.update(delta);
			if(t.remove_on_complete) {
				if (t.isStopped() && t.getRemaining() <= 0.0f && t.getRepeatCount() == 0 && !t.isFinished()) {
					// This combination of conditions indicates that a timer is "complete" and that its result has been consumed by a combo of isFinished and clearFinished
					timers.remove(i);
					removed = true;
				}
			}
			if (!removed) i++;
		}
	}

}
