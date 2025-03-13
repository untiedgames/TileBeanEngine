package com.untiedgames.TileBeanEngine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.HashSet;

public class World {

	private WorldKey wk;
	private GenArray<Object2D, Object2DHandle> contents;
	HashMap<Integer, ArrayList<Component>> components;
	HashMap<Integer, ArrayList<Integer>> component_type_info;

	public World() {
		wk = new WorldKey();
		contents = new GenArray<Object2D, Object2DHandle>(Object2DHandle.class);
		components = new HashMap<>();
		component_type_info = new HashMap<>();
	}

	// Adds an object to the world.
	public Object2DHandle add(Object2D obj) {
		if (obj == null) return Object2DHandle.empty();
		if (obj.self != null) {
			if (obj.self.isValid()) return obj.self; // Object has been added to the world already, simply return its existing handle
			else return Object2DHandle.empty();
		}
		Object2DHandle ret = contents.add(obj);
		obj.self = ret;
		return ret;
	}

	// Removes an object from the world.
	public void remove(Object2DHandle handle) {
		if (!handle.isValid() || contents.expired(handle)) return;
		Optional<Object2D> opt = contents.get(handle);
		if (opt.isPresent()) {
			Object2D obj = opt.get();
			obj.self = null;
		}
		contents.remove(handle);
		
		for (ArrayList<Component> list : components.values()) {
			for (int i = 0; i < list.size();) {
				if (list.get(i).getOwner().equals(handle)) {
					list.remove(i);
				} else i++;
			}
		}
	}

	// Removes everything from the world.
	public void clear() {
		contents.clear();
		components.clear();
	}

	// Retrieves an object from the world, if present.
	public Optional<Object2D> tryGet(Object2DHandle handle) {
		return contents.get(handle);
	}

	// The less-safe version of tryGet. Use this when you expect the object to be there.
	public Object2D get(Object2DHandle handle) {
		return contents.get(handle).get();
	}

	// Adds a component to an object.
	public void addComponent(Object2DHandle handle, Component component) {
		if (!handle.isValid() || contents.expired(handle)) return;
		component.setOwner(handle, wk);
		ArrayList<Component> list;
		int hash = component.getClass().hashCode();
		if (components.containsKey(hash)) {
			list = components.get(hash);
		} else {
			list = new ArrayList<Component>();
			components.put(hash, list);

			// Construct a new list of type info.
			// For example, the type info list for Sprite should look like:
			// { Sprite.class.hashCode(), Component.class.hashCode() }
			ArrayList<Integer> type_info = new ArrayList<>();
			Class<?> c = component.getClass();
			while (true) {
				System.out.println("Adding class " + c.getName() + " to type info " + component.getClass().getName());
				type_info.add(c.hashCode());
				if (c.hashCode() == Component.class.hashCode()) break;
				c = c.getSuperclass();
			}
			component_type_info.put(hash, type_info);
		}
		list.add(component);
	}

	// Retrieves a component of an object with the given class hash code, if present.
	public Optional<Component> tryGetComponent(Object2DHandle handle, int hash) {
		if (!handle.isValid() || contents.expired(handle)) return Optional.empty();
		if (components.containsKey(hash)) {
			ArrayList<Component> list = components.get(hash);
			for (Component c : list) {
				if (c.getOwner().equals(handle)) return Optional.of(c);
			}
		}
		return Optional.empty();
	}

	// The less-safe version of tryGetComponent. Use this when you expect the component to be there.
	public Component getComponent(Object2DHandle handle, int hash) {
		if (!handle.isValid() || contents.expired(handle)) return null;
		if (components.containsKey(hash)) {
			ArrayList<Component> list = components.get(hash);
			for (Component c : list) {
				if (c.getOwner().equals(handle)) return c;
			}
		}
		return null;
	}

	// Returns a set of all components that are of the class with the given class hash code, including derived classes.
	public HashSet<Component> getComponentsOfClass(int hash) {
		HashSet<Component> ret = new HashSet<Component>();
		//ArrayList<Component> ret = new ArrayList<Component>();
		for(Integer key : component_type_info.keySet()) {
			ArrayList<Integer> type_info = component_type_info.get(key);
			if (type_info.contains(hash)) {
				ArrayList<Component> list = components.get(key);
				for (Component c : list) {
					ret.add(c);
				}
			}
		}
		return ret;
	}

	// This little class acts as a "key" that Component.setOwner requires.
	// It's only accessible by World and therefore only World may call Component.setOwner.
	public class WorldKey {
		
		private WorldKey() {}
	
	}

}
