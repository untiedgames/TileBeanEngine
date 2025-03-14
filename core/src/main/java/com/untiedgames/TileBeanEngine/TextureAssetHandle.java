package com.untiedgames.TileBeanEngine;

// A TextureAssetHandle is a way to access texture assets in the asset manager.
// If an asset is removed from the asset manager, any handles that once referred to it become invalid.
// Validity can be checked via TileBeanEngine.assets.exists(handle).
public class TextureAssetHandle extends GenArrayKey {

	public TextureAssetHandle(int index, int generation) {
		super(index, generation);
	}

	public static TextureAssetHandle empty() {
		return new TextureAssetHandle(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

}
