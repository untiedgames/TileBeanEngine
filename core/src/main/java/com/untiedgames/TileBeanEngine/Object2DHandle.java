package com.untiedgames.TileBeanEngine;

public class Object2DHandle extends GenArrayKey {

	public Object2DHandle(int index, int generation) {
		super(index, generation);
	}

	public static Object2DHandle empty() {
		return new Object2DHandle(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

}
