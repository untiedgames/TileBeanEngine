package com.untiedgames.TileBeanEngine;

public abstract class Component {

	Object2DHandle owner;

	// This method is only callable by World, which provides a "key" to access this method.
	public void setOwner(Object2DHandle owner, World.WorldKey k) {
		this.owner = owner;
	}
	
	public final Object2DHandle getOwner() {
		return owner;
	}

	public abstract void update(float delta);

}
