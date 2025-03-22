package com.untiedgames.TileBeanEngine;

import com.untiedgames.TileBeanEngine.TilesetAsset.TILETYPE;

public class PrimitiveCollisionShape {

	public static CollisionShape FULL = new CollisionShape(0, 0, 1, 0, 1, 1, 0, 1);

	public static CollisionShape get(TILETYPE type) {
		switch (type) {
			case EMPTY:
				return null;
			case FULL:
				return FULL;
			default:
				return null;
		}
	}

}
