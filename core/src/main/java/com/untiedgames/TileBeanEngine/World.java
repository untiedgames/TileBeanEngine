package com.untiedgames.TileBeanEngine;
import java.util.ArrayList;
import java.util.Optional;

public class World {

	private GenArray<Object2D, Object2DHandle> contents;
	ArrayList<Object2DHandle> sprites; // Engine-only list of Sprites for quicker iteration

	public World() {
		contents = new GenArray<Object2D, Object2DHandle>(Object2DHandle.class);
		sprites = new ArrayList<Object2DHandle>();
	}

	public Object2DHandle add(Object2D obj) {
		if (obj == null) return Object2DHandle.empty();
		Object2DHandle ret = contents.add(obj);
		if (obj instanceof Sprite) sprites.add(new Object2DHandle(ret.index, ret.generation));
		return ret;
	}

	public void remove(Object2DHandle handle) {
		contents.remove(handle);
		sprites.remove(handle);
	}

	public void clear() {
		contents.clear();
		sprites.clear();
	}

	public Optional<Object2D> get(Object2DHandle handle) {
		return contents.get(handle);
	}

}
