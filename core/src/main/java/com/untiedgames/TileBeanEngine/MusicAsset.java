package com.untiedgames.TileBeanEngine;

import java.util.Optional;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

// MusicAsset is an asset type which can hold longer audio data.
// LibGDX differentiates between "sound" and "music" based on file size, which is unusual but we're going to roll with it.
// The file size limit for a libGDX Sound is 1 MB (1000000 b), whereas a libGDX Music has no file size limit.
public class MusicAsset extends Asset {

	private Music music;
	
	public MusicAsset(String name_and_path) {
		this(name_and_path, name_and_path, FILEMODE.INTERNAL);
	}

	public MusicAsset(String name, String path) {
		this(name, path, FILEMODE.INTERNAL);
	}

	public MusicAsset(String name, String path, FILEMODE file_mode) {
		super(name, path, file_mode);
	}

	// Loads the MusicAsset from its path.
	public boolean load() {
		if (music != null) return true; // Asset is already loaded
		try {
			FileHandle file = makeFileHandle(path, file_mode);
			music = Gdx.audio.newMusic(file);
			return true;
		} catch (Exception e) {
			System.err.println("Failed to load sound asset \"" + path + "\", file mode " + file_mode.toString() + "\nDetails: " + e.getMessage());
			return false;
		}
	}

	// Unloads the MusicAsset, performing any destruction of resources required.
	public void unload() {
		music.dispose();
		music = null;
	}

	// Returns the libGDX Music instance that this MusicAsset owns.
	public Optional<Music> getMusic() {
		return Optional.of(music);
	}

}
