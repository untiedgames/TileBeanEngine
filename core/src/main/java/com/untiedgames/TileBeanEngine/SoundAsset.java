package com.untiedgames.TileBeanEngine;

import java.util.Optional;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

/**
 * SoundAsset is an asset type which can hold short audio data.
 * LibGDX differentiates between "sound" and "music" based on file size, which is unusual but we're going to roll with it.
 * The file size limit for a libGDX Sound is 1 MB (1000000 b), whereas a libGDX Music has no file size limit.
 */
public class SoundAsset extends Asset {

	private Sound sound;
	
	public SoundAsset(String name_and_path) {
		this(name_and_path, name_and_path, FILEMODE.INTERNAL);
	}

	public SoundAsset(String name, String path) {
		this(name, path, FILEMODE.INTERNAL);
	}

	public SoundAsset(String name, String path, FILEMODE file_mode) {
		super(name, path, file_mode);
	}

	/**
	 * Loads the SoundAsset from its path.
	 * Returns true on success, false otherwise.
	 * If it cannot be loaded, an error message will be printed in the console.
	 */
	public boolean load() {
		if (sound != null) return true; // Asset is already loaded
		try {
			FileHandle file = makeFileHandle(path, file_mode);
			if (file.file().length() < 1000000) {
				sound = Gdx.audio.newSound(file);
			} else return false; // libGDX Sound class only supports file sizes under 1 MB
			return true;
		} catch (Exception e) {
			System.err.println("Failed to load sound asset \"" + path + "\", file mode " + file_mode.toString() + "\nDetails: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Unloads the SoundAsset, performing any destruction of resources required.
	 */
	public void unload() {
		if (sound != null) {
			sound.dispose();
			sound = null;
		}
	}

	/**
	 * Returns the libGDX Sound instance that this SoundAsset owns.
	 */
	public Optional<Sound> getSound() {
		return Optional.of(sound);
	}

}
