package com.untiedgames.TileBeanEngine;

// Component is a class which represents a specialized part of an object or entity.
// Components have a handle representing their owner and an update method which can be used to perform logic each frame.
public abstract class Component {

	Object2DHandle owner = Object2DHandle.empty();

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

}
