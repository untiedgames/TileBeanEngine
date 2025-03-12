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

	public void clear() {
		contents.clear();
		components.clear();
	}

	public Optional<Object2D> get(Object2DHandle handle) {
		return contents.get(handle);
	}

	public void addComponent(Object2DHandle target, Component component) {
		component.setOwner(target, wk);
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

	// Returns a set of all components that are of the class with the given hash code, including derived classes.
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
		/*
		if (component_type_info.containsKey(hash)) {
			ArrayList<Integer> type_info = component_type_info.get(hash);

		}
		if (components.containsKey(hash)) {
			ArrayList<Component> list = components.get(hash);
			Component[] ret = new Component[list.size()];
			return components.get(hash).toArray(ret);
		}
		Component[] ret = {};
		return ret;
		*/
	}

	// This little class acts as a "key" that Component.setOwner requires.
	// It's only accessible by World and therefore only World may call Component.setOwner.
	public class WorldKey {
		
		private WorldKey() {}
	
	}

}
