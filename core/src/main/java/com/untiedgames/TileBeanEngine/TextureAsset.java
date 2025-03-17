package com.untiedgames.TileBeanEngine;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Optional;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

/**
 * TextureAsset is an asset which can hold images as libGDX Textures.
 * It can hold one image, or many images to represent an animation.
 * This is a little simplified compared to a real-world implementation, which might support animations packed into spritesheets.
 */
public class TextureAsset extends Asset {

	private ArrayList<Texture> textures;
	private float fps = 0;
	private boolean is_looping = true;
	
	public TextureAsset(String name_and_path) {
		this(name_and_path, name_and_path, FILEMODE.INTERNAL);
	}

	public TextureAsset(String name, String path) {
		this(name, path, FILEMODE.INTERNAL);
	}

	public TextureAsset(String name, String path, FILEMODE file_mode) {
		super(name, path, file_mode);
		textures = new ArrayList<>();
	}

	/**
	 * Loads the TextureAsset from its path. Supports .png and .anim files.
	 * Returns true on success, false otherwise.
	 * If it cannot be loaded, an error message will be printed in the console.
	 */
	public boolean load() {
		if (!textures.isEmpty()) return true; // Asset is already loaded
		try {
			FileHandle file = makeFileHandle(path, file_mode); 
			if (getExtension(path).equals("png")) {
				textures.add(new Texture(file));
				return true;
			} else if (getExtension(path).equals("anim")) {
				String data = file.readString();
				Scanner sc = new Scanner(data);
				if (data.indexOf('\r') != -1) sc.useDelimiter("\r\n");
				else sc.useDelimiter("\n");
				boolean has_error = false;
				while (sc.hasNext()) {
					Scanner sc2 = new Scanner(sc.next());
					sc2.useDelimiter("=");
					if (!sc2.hasNext()) {
						has_error = true;
						sc2.close();
						break;
					}
					String key = sc2.next();
					if (!sc2.hasNext()) {
						has_error = true;
						sc2.close();
						break;
					}
					if (key.equals("frame")) textures.add(new Texture(makeFileHandle(sc2.next(), file_mode)));
					else if (key.equals("fps")) fps = sc2.nextFloat();
					else if (key.equals("is_looping")) is_looping = sc2.nextBoolean();
					else {
						has_error = true;
						sc2.close();
						break;
					}
					sc2.close();
				}
				
				sc.close();

				if (has_error) {
					throw new Exception("Invalid .anim format");
				}

				return true;
			}
		} catch (Exception e) {
			System.err.println("Failed to load texture asset \"" + path + "\", file mode " + file_mode.toString() + "\nDetails: " + e.getMessage());
			return false;
		}

		return false;
	}

	/**
	 * Unloads the TextureAsset, performing any destruction of resources required.
	 */
	public void unload() {
		for (Texture texture : textures) {
			texture.dispose();
		}
		textures.clear();
	}

	public int getTotalFrames() {
		return textures.size();
	}

	/**
	 * Convenience method equivalent to getTexture(0).
	 */
	public Optional<Texture> getTexture() {
		return getTexture(0);
	}

	/**
	 * Returns the texture at the given frame index, if present.
	 */
	public Optional<Texture> getTexture(int frame) {
		if (textures.isEmpty()) return Optional.empty();
		if (frame < 0 || frame >= textures.size()) return Optional.empty();
		return Optional.of(textures.get(frame));
	}

	public float getFPS() {
		return fps;
	}

	public boolean isLooping() {
		return is_looping;
	}

}
