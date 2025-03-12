package com.untiedgames.TileBeanEngine;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Iterator;

public class GenArray<T, U extends GenArrayKey> implements Iterable<GenArrayEntry<T, U>> {

	private Class<U> u_class;
	private ArrayList<GenArrayEntry<T, U>> contents;
	private int first_free = Integer.MAX_VALUE; // Represents the first index which *might* be free to put something in.

	public GenArray(Class<U> u_class) {
		this.u_class = u_class;
		contents = new ArrayList<GenArrayEntry<T, U>>();
	}

	public int size() {
		return contents.size();
	}

	public void clear() {
		for (GenArrayEntry<T, U> entry : contents) {
			if (entry.hasValue()) {
				entry.key.generation++;
				entry.data = Optional.empty();
			}
		}
		first_free = 0;
	}

	public boolean expired(U key) {
		if (key.index >= contents.size()) return true;
		if (contents.get(key.index).key.generation != key.generation) return true;
		return false;
	}

	public U add(T item) {
		for (int i = first_free; i < contents.size(); i++) {
			GenArrayEntry<T, U> entry = contents.get(i);
			if (entry.data.isEmpty()) {
				first_free++;
				entry.data = Optional.of(item);
				return entry.key;
			}
		}
		try {
			U ret = u_class.getDeclaredConstructor(int.class, int.class).newInstance(contents.size(), 0);
			contents.add(new GenArrayEntry<>(ret, Optional.of(item)));
			return ret;
		} catch(Exception e) {
			throw new Error("Invalid constructor invoked in GenArray.add"); // Your U class doesn't have the required constructor signature U(int index, int generation)
		}
	}

	public void remove(U key) {
		if (key.index >= contents.size()) return; // Invalid
		GenArrayEntry<T, U> entry = contents.get(key.index);
		if (key.generation != entry.key.generation) return; // Expired
		first_free = Math.min(first_free, key.index);
		try {
			entry.key = u_class.getDeclaredConstructor(int.class, int.class).newInstance(entry.key.index, entry.key.generation + 1);
		} catch(Exception e) {
			throw new Error("Invalid constructor invoked in GenArray.remove"); // Your U class doesn't have the required constructor signature U(int index, int generation)
		}
		entry.data = Optional.empty();
	}

	public Optional<T> get(U key) {
		if (key.index >= contents.size()) return Optional.empty(); // Invalid
		GenArrayEntry<T, U> entry = contents.get(key.index);
		if (key.generation != entry.key.generation) return Optional.empty(); // Expired
		return entry.data;
	}

	public Iterator<GenArrayEntry<T, U>> iterator() {
		return new GenArrayIterator();
	}

	class GenArrayIterator implements Iterator<GenArrayEntry<T, U>> {
		
		private int index = 0;

		public boolean hasNext() {
			return index < size();
		}

		public GenArrayEntry<T, U> next() {
			return contents.get(index++);
		}
		
	}
	
}
