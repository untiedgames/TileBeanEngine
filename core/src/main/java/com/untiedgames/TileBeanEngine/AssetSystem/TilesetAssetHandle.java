package com.untiedgames.TileBeanEngine.AssetSystem;

import com.untiedgames.TileBeanEngine.GenArrayKey;

/**
 * A TilesestAssetHandle is a way to access tileset assets in the asset manager.
 * If an asset is removed from the asset manager, any handles that once referred to it become invalid.
 * Validity can be checked via TileBeanEngine.assets.exists(handle).
 */
public class TilesetAssetHandle extends GenArrayKey {

	public TilesetAssetHandle(int index, int generation) {
		super(index, generation);
	}

	public static TilesetAssetHandle empty() {
		return new TilesetAssetHandle(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

}
