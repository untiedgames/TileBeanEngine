package com.untiedgames.TileBeanEngine;

/** A TilemapAssetHandle is a way to access sound assets in the asset manager.
 * If an asset is removed from the asset manager, any handles that once referred to it become invalid.
 * Validity can be checked via TileBeanEngine.assets.exists(handle).
 */
public class TilemapAssetHandle extends GenArrayKey {

	public TilemapAssetHandle(int index, int generation) {
		super(index, generation);
	}

	public static TilemapAssetHandle empty() {
		return new TilemapAssetHandle(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

}
