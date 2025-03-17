package com.untiedgames.TileBeanEngine;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Iterator;

/**
 * GenArray is a generational array collection. (Also found in the wild as "generational vectors," from C++.)
 * A generational array returns keys (handles) when you add things to it, and you use those keys to retrieve entries in the array.
 * The usefulness of a generational array is that when items are removed or replaced in the collection, any handles which have been given out
 * become invalidated, and can no longer be used to retrieve an entry.
 */
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
		if (key == null) return true;
		if (key.index >= contents.size()) return true;
		if (contents.get(key.index).key.generation != key.generation) return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public U add(T item) {
		if (item == null) {
			try {
				return (U)u_class.getMethod("empty").invoke(null);
			} catch(Exception e) {
				throw new Error("Invalid \"empty()\" method invoked in GenArray.add"); // Your U class doesn't have the required "empty" method with signature empty()
			}
		}

		for (int i = first_free; i < contents.size(); i++) {
			GenArrayEntry<T, U> entry = contents.get(i);
			if (!entry.data.isPresent()) {
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
		if (key == null) return;
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
		if (key == null) return Optional.empty();
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
