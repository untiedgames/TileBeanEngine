package com.untiedgames.TileBeanEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.HashSet;
import java.util.Map;

/**
 * The World class is an entity-component system (ECS) which contains all objects in the game world (entities) and their specialized parts (components).
 */
public class World {

	private WorldKey wk;
	private GenArray<Object2D, Object2DHandle> contents; // Collection of all entities in the ECS
	HashMap<Integer, ArrayList<Component>> components; // Map of class hash codes to component lists
	HashMap<Integer, ArrayList<Integer>> component_type_info; // Map of class hash codes to lists of hash codes of all classes along inheritance path to Component
	HashMap<Object2DHandle, ArrayList<Integer>> object_component_types; // Map of entities to hash codes of classes of components that they own
	HashMap<Object2DHandle, ArrayList<Component>> object_components; // Map of entities to components that they own
	HashMap<String, Object2DHandle> names; // Map of entity names to entity handles

	public World() {
		wk = new WorldKey();
		contents = new GenArray<Object2D, Object2DHandle>(Object2DHandle.class);
		components = new HashMap<>();
		component_type_info = new HashMap<>();
		object_component_types = new HashMap<>();
		object_components = new HashMap<>();
		names = new HashMap<>();
	}

	/**
	 * Adds an object to the world and returns a handle to it.
	 */
	public Object2DHandle add(Object2D obj) {
		return add(obj, null);
	}

	/**
	 * Adds an object to the world and returns a handle to it.
	 * The given name will be associated with the object, and can be used to retrieve it later as long as it's in the world.
	 * If the given name is already in use, the object it refers to will be updated.
	 */
	public Object2DHandle add(Object2D obj, String name) {
		if (obj == null) return Object2DHandle.empty();
		if (obj.handle != null) {
			if (obj.handle.isEmpty()) return obj.handle; // Object has been added to the world already, simply return its existing handle
			else return Object2DHandle.empty();
		}
		Object2DHandle ret = contents.add(obj);
		obj.handle = ret;
		object_component_types.put(ret, new ArrayList<Integer>());
		object_components.put(ret, new ArrayList<Component>());
		if (name != null) {
			if (!name.isEmpty()) names.put(name, ret);
		}
		return ret;
	}

	/**
	 * Removes an object from the world.
	 */
	public void remove(Object2DHandle handle) {
		if (!handle.isEmpty() || contents.expired(handle)) return;

		String name = getName(handle);
		if (!name.isEmpty()) {
			names.remove(name);
		}

		Optional<Object2D> opt = contents.get(handle);
		if (opt.isPresent()) {
			Object2D obj = opt.get();
			obj.handle = null;
		}
		contents.remove(handle);
		
		//TODO: This could be better if we iterate object_components instead and then only those lists
		for (ArrayList<Component> list : components.values()) {
			for (int i = 0; i < list.size();) {
				if (list.get(i).getOwner().equals(handle)) {
					list.remove(i);
				} else i++;
			}
		}

		object_component_types.remove(handle);
		object_components.remove(handle);
	}

	/**
	 * Removes everything from the world.
	 * (This includes the camera, but a new one will be created automatically. If you're holding a handle to the previous camera, it will have been invalidated.)
	 */
	public void clear() {
		contents.clear();
		components.clear();
		object_component_types.clear();
		object_components.clear();
		TileBeanEngine.setupCamera();
	}

	/**
	 * Returns true if the object that the handle refers to exists, false otherwise.
	 */
	public boolean exists(Object2DHandle handle) {
		return !contents.expired(handle);
	}

	/**
	 * Retrieves an object from the world, if present.
	 */
	public Optional<Object2D> tryGet(Object2DHandle handle) {
		return contents.get(handle);
	}

	/**
	 * The less-safe version of tryGet. Use this when you expect the object to be there.
	 */
	public Object2D get(Object2DHandle handle) {
		return contents.get(handle).get();
	}

	/**
	 * Retrieves an object handle associated with the given name, if present.
	 */
	public Object2DHandle getHandle(String name) {
		if (names.containsKey(name)) {
			return names.get(name);
		}
		return Object2DHandle.empty();
	}

	/**
	 * Returns the name associated with the given handle, or empty string if not found.
	 */
	public String getName(Object2DHandle handle) {
		for (Map.Entry<String, Object2DHandle> entry : names.entrySet()) {
			if (entry.getValue().equals(handle)) return entry.getKey();
		}
		return "";
	}

	/**
	 * Adds a component to an object.
	 * If the component already has an owner or if the object already has a component of the same type, nothing happens.
	 */
	public void addComponent(Object2DHandle handle, Component component) {
		if (!handle.isEmpty() || contents.expired(handle)) return; // Invalid or removed handle
		if (component.getOwner().isEmpty()) return; // Component is already added
		
		int hash = component.getClass().hashCode();
		if (object_components.containsKey(handle)) {
			ArrayList<Integer> list = object_component_types.get(handle);
			if (list.contains(hash)) return; // Entity already has component of this type, and only one is allowed
			
			// Component will be added to entity: Update collections
			list.add(hash);
			object_components.get(handle).add(component);
		}

		component.setOwner(handle, wk);
		ArrayList<Component> list;
		if (components.containsKey(hash)) {
			list = components.get(hash);
		} else {
			list = new ArrayList<Component>();
			components.put(hash, list);

			// Construct a new list of type info.
			// For example, the type info list for Sprite should look like:
			// { Sprite.class.hashCode(), Drawable.class.hashCode(), Component.class.hashCode() }
			//         Sprite ------> is a -------> Drawable -----> is a -------> Component
			ArrayList<Integer> type_info = new ArrayList<>();
			Class<?> c = component.getClass();
			while (true) {
				//System.out.println("Adding class " + c.getName() + " to type info " + component.getClass().getName() + " with hash " + c.hashCode()); // Debug
				type_info.add(c.hashCode());
				if (!components.containsKey(c.hashCode())) components.put(c.hashCode(), new ArrayList<Component>()); // Be safe and create all component lists in the inheritance chain
				if (c.hashCode() == Component.class.hashCode()) break;
				c = c.getSuperclass();
			}
			component_type_info.put(hash, type_info);
		}
		list.add(component);

		component.initialize();
	}

	/**
	 * Removes a component from an object.
	 * If the object does not have a component with the specified class hash code, nothing happens.
	 */
	public void removeComponent(Object2DHandle handle, int hash) {
		if (!handle.isEmpty() || contents.expired(handle)) return; // Invalid or removed handle

		if (object_component_types.containsKey(handle)) {
			ArrayList<Integer> obj_types = object_component_types.get(handle);
			if (obj_types.contains((Integer)hash)) {
				obj_types.remove((Integer)hash);

				ArrayList<Component> obj_comps = object_components.get(handle);
				Component c = null;
				for (int i = 0; i < obj_comps.size(); i++) {
					c = obj_comps.get(i);
					if (c.getClass().hashCode() == hash) {
						obj_comps.remove(i);
						break;
					}
				}

				components.get(hash).remove(c);

				c.setOwner(Object2DHandle.empty(), wk);
			}
		}
	}

	/**
	 * Retrieves a component of an object with the given class hash code, if present.
	 */
	public Optional<Component> tryGetComponent(Object2DHandle handle, int hash) {
		if (!handle.isEmpty() || contents.expired(handle)) return Optional.empty(); // Invalid or removed handle

		if (components.containsKey(hash)) {
			ArrayList<Component> list = components.get(hash);
			for (Component c : list) {
				if (c.getOwner().equals(handle)) return Optional.of(c);
			}
		}
		return Optional.empty();
	}

	/**
	 * The less-safe version of tryGetComponent. Use this when you expect the component to be there.
	 */
	public Component getComponent(Object2DHandle handle, int hash) {
		if (!handle.isEmpty() || contents.expired(handle)) return null; // Invalid or removed handle

		if (components.containsKey(hash)) {
			ArrayList<Component> list = components.get(hash);
			for (Component c : list) {
				if (c.getOwner().equals(handle)) return c;
			}
		}
		return null;
	}

	/**
	 * Returns an array of all components owned by the given object.
	 */
	public Component[] getComponents(Object2DHandle handle) {
		if (!handle.isEmpty() || contents.expired(handle)) return null; // Invalid or removed handle

		if (object_components.containsKey(handle)) {
			Object[] array = object_components.get(handle).toArray();
			return Arrays.copyOf(array, array.length, Component[].class);
		}
		return new Component[]{};
	}

	/**
	 * Returns a set of all components that are of the class with the given class hash code, including derived classes.
	 * For example, if you call getComponentsOfClass(SomeClassDerivedFromSprite.class.hashCode()), you will get all components which are SomeClassDerivedFromSprite,
	 * whereas if you call getComponentsOfClass(Sprite.class.hashCode()) you will get all components of type Sprite and of type SomeClassDerivedFromSprite.
	 */
	public HashSet<Component> getComponentsOfClass(int hash) {
		HashSet<Component> ret = new HashSet<Component>();
		for(Integer key : component_type_info.keySet()) {
			ArrayList<Integer> type_info = component_type_info.get(key);
			if (type_info.contains(hash)) {
				if (components.containsKey(key)) { // This check is performed because there's no guarantee all component types have been instantiated at least once to create their lists
					ArrayList<Component> list = components.get(key);
					for (Component c : list) {
						ret.add(c);
					}
				}
			}
		}
		return ret;
	}

	/**
	 * This little class acts as a "key" that Component.setOwner requires.
	 * It's only accessible by World and therefore only World may call Component.setOwner.
	 */
	public class WorldKey {
		
		private WorldKey() {}
	
	}

}
