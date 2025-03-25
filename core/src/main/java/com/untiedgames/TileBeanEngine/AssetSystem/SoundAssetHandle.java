package com.untiedgames.TileBeanEngine.AssetSystem;

import com.untiedgames.TileBeanEngine.GenArrayKey;

/** A SoundAssetHandle is a way to access sound assets in the asset manager.
 * If an asset is removed from the asset manager, any handles that once referred to it become invalid.
 * Validity can be checked via TileBeanEngine.assets.exists(handle).
 */
public class SoundAssetHandle extends GenArrayKey {

	public SoundAssetHandle(int index, int generation) {
		super(index, generation);
	}

	public static SoundAssetHandle empty() {
		return new SoundAssetHandle(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

}
