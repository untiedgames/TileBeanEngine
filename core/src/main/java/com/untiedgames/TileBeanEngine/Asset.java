package com.untiedgames.TileBeanEngine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/*
 * An Asset holds data used in the game, which could be an image, animation, sound, or more.
 */
public abstract class Asset {

	public enum FILEMODE {
		INTERNAL, // Load from inside the jar
		EXTERNAL, // Load from a relative path outside the jar
		ABSOLUTE  // Load from an absolute file path
	}

	GenArrayKey handle; // The generic handle to this asset in the asset collection.

	protected String name; // The user-defined custom name for this asset.
	protected String path; // The path that this asset should be loaded from.
	protected FILEMODE file_mode; // The file mode to use when loading this asset.

	public Asset(String name, String path, FILEMODE file_mode) {
		this.name = name;
		this.path = path;
		this.file_mode = file_mode;
	}

	public GenArrayKey getHandle() {
		return handle;
	}

	/**
	 * Loads the asset from its path.
	 */
	public abstract boolean load();

	/**
	 *  Unloads the asset, performing any destruction of resources required.
	 */
	public abstract void unload();

	/**
	 * Helper function to create a file handle to the given path using the given file mode.
	 */
	protected static FileHandle makeFileHandle(String path, FILEMODE file_mode) {
		FileHandle file = null;
		switch(file_mode) {
			case INTERNAL:
				file = Gdx.files.internal(path);
				break;
			case EXTERNAL:
				file = Gdx.files.external(path);
				break;
			case ABSOLUTE:
				file = Gdx.files.absolute(path);
				break;
		}
		return file;
	}

	/**
	 * Returns the all-lowercase file extension of a given path, or empty string if no extension is present.
	 * For example, getExtension("image.PNG") returns "png" (without the dot).
	 */
	public static String getExtension(String path) {
		int index = path.lastIndexOf('.');
		if (index == -1) return "";
		return path.substring(index + 1).toLowerCase();
	}

}
