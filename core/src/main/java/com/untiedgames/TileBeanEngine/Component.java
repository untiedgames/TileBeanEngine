package com.untiedgames.TileBeanEngine;

import java.util.Optional;

// Component is a class which represents a specialized part of an object or entity.
// Components have a handle representing their owner and an update method which can be used to perform logic each frame.
public abstract class Component implements Comparable<Component> {

	private Object2DHandle owner = Object2DHandle.empty();

	// This method is only callable by World, which provides a "key" to access this method.
	public void setOwner(Object2DHandle owner, World.WorldKey k) {
		this.owner = owner;
	}

	// The initialize method is called when the component is added to an object.
	// A smart use of it might be to add other required components to the object.
	public void initialize() {
		// Override me!
	}
	
	public final Object2DHandle getOwner() {
		return owner;
	}

	// Performs logic each frame.
	public abstract void update(float delta);

	// Used to sort by depth.
	public int compareTo(Component other) {
		Optional<Object2D> opt = TileBeanEngine.world.tryGet(owner);
		Optional<Object2D> opt_other = TileBeanEngine.world.tryGet(other.owner);
		if (!opt.isPresent() || !opt_other.isPresent()) return -1;
		float z = opt.get().z;
		float z_other = opt_other.get().z;
		if (z < z_other) return -1;
		if (z > z_other) return 1;
		return 0;
	}

}
