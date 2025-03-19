package com.untiedgames.TileBeanEngine;

import java.util.HashMap;
import java.util.Optional;

/*
 * AssetManager holds all the graphics, sounds, and music for the game.
 * You can add new assets to it and retrieve them at any time.
*/
public class AssetManager {

	private GenArray<TextureAsset, TextureAssetHandle> collection_textures = new GenArray<>(TextureAssetHandle.class);
	private HashMap<String, TextureAssetHandle> name_map_textures = new HashMap<>();
	
	private GenArray<TilesetAsset, TilesetAssetHandle> collection_tilesets = new GenArray<>(TilesetAssetHandle.class);
	private HashMap<String, TilesetAssetHandle> name_map_tilesets = new HashMap<>();
	
	private GenArray<SoundAsset, SoundAssetHandle> collection_sounds = new GenArray<>(SoundAssetHandle.class);
	private HashMap<String, SoundAssetHandle> name_map_sounds = new HashMap<>();
	
	private GenArray<MusicAsset, MusicAssetHandle> collection_music = new GenArray<>(MusicAssetHandle.class);
	private HashMap<String, MusicAssetHandle> name_map_music = new HashMap<>();

	/**
	 * Adds an asset to the asset manager. (Does not load the asset. Do that manually.)
	 */
	public TextureAssetHandle add(TextureAsset asset) {
		if (asset == null) return TextureAssetHandle.empty();
		if (asset.handle != null) {
			if (asset.handle.isEmpty()) return (TextureAssetHandle)asset.handle; // Asset has been added to the AssetManager already, simply return its existing handle
			else return TextureAssetHandle.empty();
		}
		TextureAssetHandle ret = collection_textures.add(asset);
		asset.handle = ret;
		name_map_textures.put(asset.name, ret);
		return ret;
	}

	/**
	 * Adds an asset to the asset manager. (Does not load the asset. Do that manually.)
	 */
	public TilesetAssetHandle add(TilesetAsset asset) {
		if (asset == null) return TilesetAssetHandle.empty();
		if (asset.handle != null) {
			if (asset.handle.isEmpty()) return (TilesetAssetHandle)asset.handle; // Asset has been added to the AssetManager already, simply return its existing handle
			else return TilesetAssetHandle.empty();
		}
		TilesetAssetHandle ret = collection_tilesets.add(asset);
		asset.handle = ret;
		name_map_tilesets.put(asset.name, ret);
		return ret;
	}

	/**
	 * Adds an asset to the asset manager. (Does not load the asset. Do that manually.)
	 */
	public SoundAssetHandle add(SoundAsset asset) {
		if (asset == null) return SoundAssetHandle.empty();
		if (asset.handle != null) {
			if (asset.handle.isEmpty()) return (SoundAssetHandle)asset.handle; // Asset has been added to the AssetManager already, simply return its existing handle
			else return SoundAssetHandle.empty();
		}
		SoundAssetHandle ret = collection_sounds.add(asset);
		asset.handle = ret;
		name_map_sounds.put(asset.name, ret);
		return ret;
	}

	/**
	 * Adds an asset to the asset manager. (Does not load the asset. Do that manually.)
	 */
	public MusicAssetHandle add(MusicAsset asset) {
		if (asset == null) return MusicAssetHandle.empty();
		if (asset.handle != null) {
			if (asset.handle.isEmpty()) return (MusicAssetHandle)asset.handle; // Asset has been added to the AssetManager already, simply return its existing handle
			else return MusicAssetHandle.empty();
		}
		MusicAssetHandle ret = collection_music.add(asset);
		asset.handle = ret;
		name_map_music.put(asset.name, ret);
		return ret;
	}

	/**
	 * Removes an asset from the asset manager. If the asset is loaded, it will be unloaded.
	 */
	public void remove(TextureAssetHandle handle) {
		if (!handle.isEmpty() || collection_textures.expired(handle)) return;
		Optional<TextureAsset> opt = collection_textures.get(handle);
		if (opt.isPresent()) {
			TextureAsset asset = opt.get();
			asset.handle = null;
			asset.unload();
			name_map_textures.remove(asset.name);
		}
		collection_textures.remove(handle);
	}

	/**
	 * Removes an asset from the asset manager. If the asset is loaded, it will be unloaded.
	 */
	public void remove(TilesetAssetHandle handle) {
		if (!handle.isEmpty() || collection_tilesets.expired(handle)) return;
		Optional<TilesetAsset> opt = collection_tilesets.get(handle);
		if (opt.isPresent()) {
			TilesetAsset asset = opt.get();
			asset.handle = null;
			asset.unload();
			name_map_tilesets.remove(asset.name);
		}
		collection_tilesets.remove(handle);
	}

	/**
	 * Removes an asset from the asset manager. If the asset is loaded, it will be unloaded.
	 */
	public void remove(SoundAssetHandle handle) {
		if (!handle.isEmpty() || collection_sounds.expired(handle)) return;
		Optional<SoundAsset> opt = collection_sounds.get(handle);
		if (opt.isPresent()) {
			SoundAsset asset = opt.get();
			asset.handle = null;
			asset.unload();
			name_map_sounds.remove(asset.name);
		}
		collection_sounds.remove(handle);
	}

	/**
	 * Removes an asset from the asset manager. If the asset is loaded, it will be unloaded.
	 */
	public void remove(MusicAssetHandle handle) {
		if (!handle.isEmpty() || collection_music.expired(handle)) return;
		Optional<MusicAsset> opt = collection_music.get(handle);
		if (opt.isPresent()) {
			MusicAsset asset = opt.get();
			asset.handle = null;
			asset.unload();
			name_map_music.remove(asset.name);
		}
		collection_music.remove(handle);
	}

	/**
	 * Removes all assets from the asset manager and unloads them.
	 */
	public void clear() {
		for(GenArrayEntry<TextureAsset, TextureAssetHandle> entry : collection_textures) {
			if (entry.hasValue()) {
				Optional<TextureAsset> opt = entry.getData();
				opt.get().unload();
			}
		}
		for(GenArrayEntry<TilesetAsset, TilesetAssetHandle> entry : collection_tilesets) {
			if (entry.hasValue()) {
				Optional<TilesetAsset> opt = entry.getData();
				opt.get().unload();
			}
		}
		for(GenArrayEntry<SoundAsset, SoundAssetHandle> entry : collection_sounds) {
			if (entry.hasValue()) {
				Optional<SoundAsset> opt = entry.getData();
				opt.get().unload();
			}
		}
		for(GenArrayEntry<MusicAsset, MusicAssetHandle> entry : collection_music) {
			if (entry.hasValue()) {
				Optional<MusicAsset> opt = entry.getData();
				opt.get().unload();
			}
		}
		collection_textures.clear();
		name_map_textures.clear();
		collection_tilesets.clear();
		name_map_tilesets.clear();
		collection_sounds.clear();
		name_map_sounds.clear();
		collection_music.clear();
		name_map_music.clear();
	}

	/**
	 * Retrieves an asset from the collection if present.
	 */
	public Optional<TextureAsset> tryGet(TextureAssetHandle handle) {
		return collection_textures.get(handle);
	}

	/**
	 * Retrieves an asset from the collection if present.
	 */
	public Optional<TilesetAsset> tryGet(TilesetAssetHandle handle) {
		return collection_tilesets.get(handle);
	}

	/**
	 * Retrieves an asset from the collection if present.
	 */
	public Optional<SoundAsset> tryGet(SoundAssetHandle handle) {
		return collection_sounds.get(handle);
	}

	/**
	 * Retrieves an asset from the collection if present.
	 */
	public Optional<MusicAsset> tryGet(MusicAssetHandle handle) {
		return collection_music.get(handle);
	}

	/**
	 * The less-safe version of tryGet. Use this when you expect the asset to be there.
	 */
	public TextureAsset get(TextureAssetHandle handle) {
		return collection_textures.get(handle).get();
	}

	/**
	 * The less-safe version of tryGet. Use this when you expect the asset to be there.
	 */
	public TilesetAsset get(TilesetAssetHandle handle) {
		return collection_tilesets.get(handle).get();
	}
	
	/**
	 * The less-safe version of tryGet. Use this when you expect the asset to be there.
	 */
	public SoundAsset get(SoundAssetHandle handle) {
		return collection_sounds.get(handle).get();
	}

	/**
	 * The less-safe version of tryGet. Use this when you expect the asset to be there.
	 */
	public MusicAsset get(MusicAssetHandle handle) {
		return collection_music.get(handle).get();
	}

	/**
	 * Retrieves a TextureAsset with the associated user-specified name from the collection if present.
	 */
	public Optional<TextureAsset> getTextureAsset(String name) {
		if (name_map_textures.containsKey(name)) {
			return collection_textures.get(name_map_textures.get(name));
		}
		return Optional.empty();
	}

	/**
	 * Retrieves a TilesetAsset with the associated user-specified name from the collection if present.
	 */
	public Optional<TilesetAsset> getTilesetAsset(String name) {
		if (name_map_tilesets.containsKey(name)) {
			return collection_tilesets.get(name_map_tilesets.get(name));
		}
		return Optional.empty();
	}

	/**
	 * Retrieves a SoundAsset with the associated user-specified name from the collection if present.
	 */
	public Optional<SoundAsset> getSoundAsset(String name) {
		if (name_map_sounds.containsKey(name)) {
			return collection_sounds.get(name_map_sounds.get(name));
		}
		return Optional.empty();
	}

	/**
	 * Retrieves a MusicAsset with the associated user-specified name from the collection if present.
	 */
	public Optional<MusicAsset> getMusicAsset(String name) {
		if (name_map_music.containsKey(name)) {
			return collection_music.get(name_map_music.get(name));
		}
		return Optional.empty();
	}

	/**
	 * Returns the TextureAssetHandle associated with the given user-specified name if present.
	 */
	public TextureAssetHandle getTextureAssetHandle(String name) {
		if (name_map_textures.containsKey(name)) {
			return name_map_textures.get(name);
		}
		return TextureAssetHandle.empty();
	}

	/**
	 * Returns the TilesetAssetHandle associated with the given user-specified name if present.
	 */
	public TilesetAssetHandle getTilesetAssetHandle(String name) {
		if (name_map_tilesets.containsKey(name)) {
			return name_map_tilesets.get(name);
		}
		return TilesetAssetHandle.empty();
	}

	/**
	 * Returns the SoundAssetHandle associated with the given user-specified name if present.
	 */
	public SoundAssetHandle getSoundAssetHandle(String name) {
		if (name_map_sounds.containsKey(name)) {
			return name_map_sounds.get(name);
		}
		return SoundAssetHandle.empty();
	}

	/**
	 * Returns the MusicAssetHandle associated with the given user-specified name if present.
	 */
	public MusicAssetHandle getMusicAssetHandle(String name) {
		if (name_map_music.containsKey(name)) {
			return name_map_music.get(name);
		}
		return MusicAssetHandle.empty();
	}

}
