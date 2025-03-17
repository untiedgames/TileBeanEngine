package com.untiedgames.TileBeanEngine;

/**
 * A MusicAssetHandle is a way to access music assets in the asset manager.
 * If an asset is removed from the asset manager, any handles that once referred to it become invalid.
 * Validity can be checked via TileBeanEngine.assets.exists(handle).
 */
public class MusicAssetHandle extends GenArrayKey {

	public MusicAssetHandle(int index, int generation) {
		super(index, generation);
	}

	public static MusicAssetHandle empty() {
		return new MusicAssetHandle(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

}
