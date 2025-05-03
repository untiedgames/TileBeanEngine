package com.untiedgames.TileBeanEngine;

public class PrimitiveTileCollisionShape {

	public static TileCollisionShape FULL = new TileCollisionShape(0, 0, 1, 0, 1, 1, 0, 1);

	public static TileCollisionShape HALF_TOP = new TileCollisionShape(0, 0, 1, 0, 1, .5f, 0, .5f);

	public static TileCollisionShape HALF_BOTTOM = new TileCollisionShape(0, .5f, 1, .5f, 1, 1, 0, 1);

	public static TileCollisionShape HALF_LEFT = new TileCollisionShape(0, 0, .5f, 0, .5f, 1, 0, 1);

	public static TileCollisionShape HALF_RIGHT = new TileCollisionShape(.5f, 0, 1, 0, 1, 1, .5f, 1);

	public static TileCollisionShape SLOPE_FLOOR_LEFT_45 = new TileCollisionShape(0, 1, 1, 0, 1, 1);
	
	public static TileCollisionShape SLOPE_FLOOR_RIGHT_45 = new TileCollisionShape(0, 0, 1, 1, 0, 1);
	
	public static TileCollisionShape SLOPE_CEIL_LEFT_45 = new TileCollisionShape(0, 0, 1, 0, 1, 1);
	
	public static TileCollisionShape SLOPE_CEIL_RIGHT_45 = new TileCollisionShape(0, 0, 1, 0, 0, 1);

	public static TileCollisionShape SLOPE_FLOOR_LEFT_30_T = new TileCollisionShape(0, 1, 1, .5f, 1, 1);
	
	public static TileCollisionShape SLOPE_FLOOR_LEFT_30_Q = new TileCollisionShape(0, .5f, 1, 0, 1, 1, 0, 1);
	
	public static TileCollisionShape SLOPE_FLOOR_RIGHT_30_T = new TileCollisionShape(0, .5f, 1, 1, 0, 1);
	
	public static TileCollisionShape SLOPE_FLOOR_RIGHT_30_Q = new TileCollisionShape(0, 0, 1, .5f, 1, 1, 0, 1);
	
	public static TileCollisionShape SLOPE_CEIL_LEFT_30_T = new TileCollisionShape(0, 0, 1, 0, 1, .5f);
	
	public static TileCollisionShape SLOPE_CEIL_LEFT_30_Q = new TileCollisionShape(0, 0, 1, 0, 1, 1, 0, .5f);
	
	public static TileCollisionShape SLOPE_CEIL_RIGHT_30_T = new TileCollisionShape(0, 0, 1, 0, 0, .5f);
	
	public static TileCollisionShape SLOPE_CEIL_RIGHT_30_Q = new TileCollisionShape(0, 0, 1, 0, 1, .5f, 0, 1);
	
	public static TileCollisionShape SLOPE_FLOOR_LEFT_60_T = new TileCollisionShape(1, 0, 1, 1, .5f, 1);
	
	public static TileCollisionShape SLOPE_FLOOR_LEFT_60_Q = new TileCollisionShape(.5f, 0, 1, 0, 1, 1, 0, 1);
	
	public static TileCollisionShape SLOPE_FLOOR_RIGHT_60_T = new TileCollisionShape(0, 0, .5f, 1, 0, 1);
	
	public static TileCollisionShape SLOPE_FLOOR_RIGHT_60_Q = new TileCollisionShape(0, 0, .5f, 0, 1, 1, 0, 1);
	
	public static TileCollisionShape SLOPE_CEIL_LEFT_60_T = new TileCollisionShape(.5f, 0, 1, 0, 1, 1);
	
	public static TileCollisionShape SLOPE_CEIL_LEFT_60_Q = new TileCollisionShape(0, 0, 1, 0, 1, 1, .5f, 1);
	
	public static TileCollisionShape SLOPE_CEIL_RIGHT_60_T = new TileCollisionShape(0, 0, .5f, 0, 0, 1);
	
	public static TileCollisionShape SLOPE_CEIL_RIGHT_60_Q = new TileCollisionShape(0, 0, 1, 0, .5f, 1, 0, 1);

	public static TileCollisionShape get(TileCollisionShape.TYPE type) {
		switch (type) {
			case EMPTY:
				return null;
			case FULL:
				return FULL;
			case HALF_TOP:
				return HALF_TOP;
			case HALF_BOTTOM:
				return HALF_BOTTOM;
			case HALF_LEFT:
				return HALF_LEFT;
			case HALF_RIGHT:
				return HALF_RIGHT;
			case SLOPE_FLOOR_LEFT_45:
				return SLOPE_FLOOR_LEFT_45;
			case SLOPE_FLOOR_RIGHT_45:
				return SLOPE_FLOOR_RIGHT_45;
			case SLOPE_CEIL_LEFT_45:
				return SLOPE_CEIL_LEFT_45;
			case SLOPE_CEIL_RIGHT_45:
				return SLOPE_CEIL_RIGHT_45;
			case SLOPE_FLOOR_LEFT_30_T:
				return SLOPE_FLOOR_LEFT_30_T;
			case SLOPE_FLOOR_LEFT_30_Q:
				return SLOPE_FLOOR_LEFT_30_Q;
			case SLOPE_FLOOR_RIGHT_30_T:
				return SLOPE_FLOOR_RIGHT_30_T;
			case SLOPE_FLOOR_RIGHT_30_Q:
				return SLOPE_FLOOR_RIGHT_30_Q;
			case SLOPE_CEIL_LEFT_30_T:
				return SLOPE_CEIL_LEFT_30_T;
			case SLOPE_CEIL_LEFT_30_Q:
				return SLOPE_CEIL_LEFT_30_Q;
			case SLOPE_CEIL_RIGHT_30_T:
				return SLOPE_CEIL_RIGHT_30_T;
			case SLOPE_CEIL_RIGHT_30_Q:
				return SLOPE_CEIL_RIGHT_30_Q;
			case SLOPE_FLOOR_LEFT_60_T:
				return SLOPE_FLOOR_LEFT_60_T;
			case SLOPE_FLOOR_LEFT_60_Q:
				return SLOPE_FLOOR_LEFT_60_Q;
			case SLOPE_FLOOR_RIGHT_60_T:
				return SLOPE_FLOOR_RIGHT_60_T;
			case SLOPE_FLOOR_RIGHT_60_Q:
				return SLOPE_FLOOR_RIGHT_60_Q;
			case SLOPE_CEIL_LEFT_60_T:
				return SLOPE_CEIL_LEFT_60_T;
			case SLOPE_CEIL_LEFT_60_Q:
				return SLOPE_CEIL_LEFT_60_Q;
			case SLOPE_CEIL_RIGHT_60_T:
				return SLOPE_CEIL_RIGHT_60_T;
			case SLOPE_CEIL_RIGHT_60_Q:
				return SLOPE_CEIL_RIGHT_60_Q;
			default:
				return null;
		}
	}

}
