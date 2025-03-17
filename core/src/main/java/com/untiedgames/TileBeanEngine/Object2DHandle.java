package com.untiedgames.TileBeanEngine;

/**
 * An Object2DHandle is a way to access objects (entities) in the game world.
 * If an object is removed from the game world, any handles that once referred to it become invalid.
 * Validity can be checked via TileBeanEngine.world.exists(handle).
 */
public class Object2DHandle extends GenArrayKey {

	public Object2DHandle(int index, int generation) {
		super(index, generation);
	}

	public static Object2DHandle empty() {
		return new Object2DHandle(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

}
