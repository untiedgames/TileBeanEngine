package com.untiedgames.TileBeanEngine;

import java.util.Optional;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * A Collider is a component which can allow an object to collide with other objects.
 * Each collider has a set of vertices which represent a shape in the object's local space.
 * For example, if your player is 32x48 pixels, you could make a collider easily with Collider.makeBoxCollider(32, 48).
 * That collider's vertices would then be a 32x48 rectangle centered on {0, 0}.
 * 
 * For simplicity, TileBeanEngine supports only one Collider per object.
 * A real-world game engine might have a ColliderManager component for complex objects, with the ability to add one or more Colliders.
 * (See TimerManager vs. Timer for how that might work.)
 */
public class Collider extends Component {
	
	/**
	 * Returns a new Collider that has the vertices of a rectangle of the given width and height.
	 */
	public static Collider makeBoxCollider(float width, float height) {
		return makeBoxCollider(width, height, 0);
	}

	/**
	 * Returns a new Collider that has the vertices of a rectangle of the given width and height.
	 * The polygon will be rotated by the provided rotation in radians.
	 */
	public static Collider makeBoxCollider(float width, float height, float rotation) {
		float hw = width * .5f;
		float hh = height * .5f;
		if (rotation == 0.0f) return new Collider(-hw, -hh, hw, -hh, hw, hh, -hw, hh);
		else {
			Vector2 p0 = TBEMath.rotateAboutPoint(-hw, -hh, 0, 0, rotation);
			Vector2 p1 = TBEMath.rotateAboutPoint(hw, -hh, 0, 0, rotation);
			Vector2 p2 = TBEMath.rotateAboutPoint(hw, hh, 0, 0, rotation);
			Vector2 p3 = TBEMath.rotateAboutPoint(-hw, hh, 0, 0, rotation);
			return new Collider(p0.x, p0.y, p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
		}
	}

	/**
	 * Returns a new collider that has the vertices of a regular polygon of the given radius and number of points.
	 * (makeCircleCollider just sounds better than makeRegularPolygonCollider.)
	 */
	public static Collider makeCircleCollider(float radius, int num_points) {
		return makeCircleCollider(radius, num_points, 0);
	}

	/**
	 * Returns a new collider that has the vertices of a regular polygon of the given radius and number of points.
	 * The polygon will be rotated by the provided rotation in radians.
	 * (makeCircleCollider just sounds better than makeRegularPolygonCollider.)
	 */
	public static Collider makeCircleCollider(float radius, int num_points, float rotation) {
		if (num_points < 3) num_points = 3;
		if (num_points > 128) num_points = 128;
		int n = num_points * 2;
		float[] points = new float[n];
		for(int i = 0; i < n; i += 2) {
			double angle = ((double)i / (double)n * 2.0 * Math.PI) + rotation;
			points[i] = (float)(radius * Math.cos(angle));
			points[i + 1] = (float)(radius * Math.sin(angle));
		}
		return new Collider(points);
	}

	protected float[] vertices; // Array of vertices in the format x, y, x, y...

	public Collider(float... vertices) {
		this.vertices = vertices;
	}

	public void update(float delta) {}

	/**
	 * Returns a float array of the vertices, as if they were transformed by their owner's location, rotation, and scale.
	 * The array is in the format x, y, x, y...
	 * This is used in collision calculations as well as in drawing.
	 */
	public float[] getTransformedVertices() {
		Optional<Object2D> opt_obj = TileBeanEngine.world.tryGet(getOwner());
		if (!opt_obj.isPresent()) return new float[0];
		Object2D obj = opt_obj.get();

		// Get the parent object's transformation matrix, so we can calculate where our Collider's vertices are in that space.
		Matrix4 m = new Matrix4(); // If this engine supported embedded objects, this would be your parent matrix, not an identity matrix.
		m.translate(obj.x, obj.y, 0);
		m.rotate(0, 0, 1, TBEMath.toDegrees(obj.rotation)); // libGDX operates in degrees instead of radians, which is incredibly uncommon and a major pitfall in my opinion.
		m.scale(obj.scale_x, obj.scale_y, 0);
		Matrix4 m_bak = m.cpy();
		float[] ret = new float[vertices.length];
		Vector3 v = new Vector3();
		for (int i = 0; i < vertices.length; i += 2) {
			// Apply the transformation matrix to our Collider's vertices. The result we want is the translation.
			m.translate(vertices[i], vertices[i + 1], 0);
			m.getTranslation(v);
			ret[i] = v.x;
			ret[i + 1] = v.y;
			m.set(m_bak);
		}

		return ret;
	}

}
