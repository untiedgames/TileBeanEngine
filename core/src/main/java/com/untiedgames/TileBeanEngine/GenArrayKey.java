package com.untiedgames.TileBeanEngine;

public abstract class GenArrayKey {
	
	protected int index = Integer.MAX_VALUE;
	protected int generation = Integer.MAX_VALUE;

	// The default constructor is explicitly provided to allow for the easy creation of "null" keys.
	public GenArrayKey() {}

	public GenArrayKey(int index, int generation) {
		this.index = index;
		this.generation = generation;
	}

	public boolean isValid() {
		return index != Integer.MAX_VALUE;
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		GenArrayKey other = (GenArrayKey)obj;
		return index == other.index && generation == other.generation;
	}

}
