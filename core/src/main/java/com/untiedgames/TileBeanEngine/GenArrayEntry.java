package com.untiedgames.TileBeanEngine;
import java.util.Optional;

public class GenArrayEntry<T, U extends GenArrayKey> {
	
	U key;
	Optional<T> data;

	public GenArrayEntry(U key, Optional<T> data) {
		this.key = key;
		this.data = data;
	}

	public U getKey() {
		return key;
	}

	public Optional<T> getData() {
		return data;
	}

	public boolean hasValue() {
		return data.isPresent();
	}
}
