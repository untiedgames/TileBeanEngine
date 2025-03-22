package com.untiedgames.TileBeanEngine;

public class PrimitiveCollisionShape {

	public static CollisionShape FULL = new CollisionShape(0, 0, 1, 0, 1, 1, 0, 1);

	public static CollisionShape SLOPE_FLOOR_LEFT_45 = new CollisionShape(0, 1, 1, 0, 1, 1);
	
	public static CollisionShape SLOPE_FLOOR_RIGHT_45 = new CollisionShape(0, 0, 1, 1, 0, 1);
	
	public static CollisionShape SLOPE_CEIL_LEFT_45 = new CollisionShape(0, 0, 1, 0, 1, 1);
	
	public static CollisionShape SLOPE_CEIL_RIGHT_45 = new CollisionShape(0, 0, 1, 0, 0, 1);

	public static CollisionShape SLOPE_FLOOR_LEFT_30_T = new CollisionShape(0, 1, 1, .5f, 1, 1);
	
	public static CollisionShape SLOPE_FLOOR_LEFT_30_Q = new CollisionShape(0, .5f, 1, 0, 1, 1, 0, 1);
	
	public static CollisionShape SLOPE_FLOOR_RIGHT_30_T = new CollisionShape(0, .5f, 1, 1, 0, 1);
	
	public static CollisionShape SLOPE_FLOOR_RIGHT_30_Q = new CollisionShape(0, 0, 1, .5f, 1, 1, 0, 1);
	
	public static CollisionShape SLOPE_CEIL_LEFT_30_T = new CollisionShape(0, 0, 1, 0, 1, .5f);
	
	public static CollisionShape SLOPE_CEIL_LEFT_30_Q = new CollisionShape(0, 0, 1, 0, 1, 1, 0, .5f);
	
	public static CollisionShape SLOPE_CEIL_RIGHT_30_T = new CollisionShape(0, 0, 1, 0, 0, .5f);
	
	public static CollisionShape SLOPE_CEIL_RIGHT_30_Q = new CollisionShape(0, 0, 1, 0, 1, .5f, 0, 1);
	
	public static CollisionShape SLOPE_FLOOR_LEFT_60_T = new CollisionShape(1, 0, 1, 1, .5f, 1);
	
	public static CollisionShape SLOPE_FLOOR_LEFT_60_Q = new CollisionShape(.5f, 0, 1, 0, 1, 1, 0, 1);
	
	public static CollisionShape SLOPE_FLOOR_RIGHT_60_T = new CollisionShape(0, 0, .5f, 1, 0, 1);
	
	public static CollisionShape SLOPE_FLOOR_RIGHT_60_Q = new CollisionShape(0, 0, .5f, 0, 1, 1, 0, 1);
	
	public static CollisionShape SLOPE_CEIL_LEFT_60_T = new CollisionShape(.5f, 0, 1, 0, 1, 1);
	
	public static CollisionShape SLOPE_CEIL_LEFT_60_Q = new CollisionShape(0, 0, 1, 0, 1, 1, .5f, 1);
	
	public static CollisionShape SLOPE_CEIL_RIGHT_60_T = new CollisionShape(0, 0, .5f, 0, 0, 1);
	
	public static CollisionShape SLOPE_CEIL_RIGHT_60_Q = new CollisionShape(0, 0, 1, 0, .5f, 1, 0, 1);

	public static CollisionShape get(CollisionShape.TYPE type) {
		switch (type) {
			case EMPTY:
				return null;
			case FULL:
				return FULL;
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
