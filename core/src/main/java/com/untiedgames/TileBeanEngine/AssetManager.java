package com.untiedgames.TileBeanEngine;

import java.util.HashMap;
import java.util.Optional;

public class AssetManager {

	private GenArray<TextureAsset, TextureAssetHandle> collection_textures;
	private HashMap<String, TextureAssetHandle> name_map_textures;

	public AssetManager() {
		collection_textures = new GenArray<>(TextureAssetHandle.class);
		name_map_textures = new HashMap<>();
	}

	// Adds an asset to the asset manager. (Does not load the asset. Do that manually.)
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

	// Removes an asset from the asset manager. If the asset is loaded, it will be unloaded.
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

	// Removes all assets from the asset manager and unloads them.
	public void clear() {
		for(GenArrayEntry<TextureAsset, TextureAssetHandle> entry : collection_textures) {
			if (entry.hasValue()) {
				Optional<TextureAsset> opt = entry.getData();
				opt.get().unload();
			}
		}
		collection_textures.clear();
		name_map_textures.clear();
	}

	// Retrieves an asset from the collection if present.
	public Optional<TextureAsset> tryGet(TextureAssetHandle handle) {
		return collection_textures.get(handle);
	}

	// The less-safe version of tryGet. Use this when you expect the asset to be there.
	public TextureAsset get(TextureAssetHandle handle) {
		return collection_textures.get(handle).get();
	}

	// Retrieves a TextureAsset from the collection if present.
	public Optional<TextureAsset> getTextureAsset(String name) {
		if (name_map_textures.containsKey(name)) {
			return collection_textures.get(name_map_textures.get(name));
		}
		return Optional.empty();
	}

	public TextureAssetHandle getTextureAssetHandle(String name) {
		if (name_map_textures.containsKey(name)) {
			return name_map_textures.get(name);
		}
		return TextureAssetHandle.empty();
	}

}
