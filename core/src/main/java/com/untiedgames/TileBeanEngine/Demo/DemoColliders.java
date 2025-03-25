package com.untiedgames.TileBeanEngine.Demo;

import com.untiedgames.TileBeanEngine.Collider;
import com.untiedgames.TileBeanEngine.Collision;
import com.untiedgames.TileBeanEngine.CollisionInfo;
import com.untiedgames.TileBeanEngine.Game;
import com.untiedgames.TileBeanEngine.Object2D;
import com.untiedgames.TileBeanEngine.Object2DHandle;
import com.untiedgames.TileBeanEngine.Sprite;
import com.untiedgames.TileBeanEngine.TBEMath;
import com.untiedgames.TileBeanEngine.TileBeanEngine;
import com.untiedgames.TileBeanEngine.AssetSystem.TextureAsset;
import com.untiedgames.TileBeanEngine.AssetSystem.TextureAssetHandle;
import com.untiedgames.TileBeanEngine.Collision.RESPONSE;
import com.untiedgames.TileBeanEngine.Input.MouseState;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;

public class DemoColliders extends Game {

	Object2DHandle obj_handle;
	Object2DHandle obj2_handle;
	ImBoolean is_rotating = new ImBoolean(false);
	boolean is_second_collider_box = true;
	ImFloat second_collider_width = new ImFloat(32);
	ImFloat second_collider_height = new ImFloat(32);
	float[] second_collider_rotation = {0};
	ImInt second_collider_num_points = new ImInt(16);
	Collision.RESPONSE response_first = Collision.RESPONSE.RESOLVE;
	Collision.RESPONSE response_second = Collision.RESPONSE.RESOLVE;

	public void initialize() {
		TileBeanEngine.show_colliders = true; // Start the demo with colliders visible.

		// Load animations.
		TextureAsset tex_asset_char_idle = new TextureAsset("char_idle", "gfx/character/idle.anim");
		tex_asset_char_idle.load();
		TextureAssetHandle anim_handle = TileBeanEngine.assets.add(tex_asset_char_idle);

		// Create a new game object.
		Object2D obj = new Object2D();
		obj_handle = TileBeanEngine.world.add(obj);

		// Add a Sprite component. Sprites can display images and animations.
		Sprite sprite = new Sprite();
		sprite.setGraphics(anim_handle);
		sprite.play();
		TileBeanEngine.world.addComponent(obj_handle, sprite);

		// Add a Collider component. The Collider class has convenience methods to easily make box colliders and circle colliders.
		Collider c = Collider.makeBoxCollider(tex_asset_char_idle.getTexture().get().getWidth(), tex_asset_char_idle.getTexture().get().getHeight());
		TileBeanEngine.world.addComponent(obj_handle, c);

		// Create a second game object, which the user can control.
		Object2D obj2 = new Object2D();
		obj2_handle = TileBeanEngine.world.add(obj2);

		setupCollision();

		// Set the camera for the demo.
		Object2D cam = TileBeanEngine.world.get(TileBeanEngine.getCameraHandle());
		cam.z = 4;
	}

	/**
	 * Removes any existing Collider from the second object, and adds a new one based on the demo's current settings.
	 */
	private void setupCollision() {
		TileBeanEngine.world.removeComponent(obj2_handle, Collider.class.hashCode());

		// Add a Collider component to the second object.
		Collider c;
		if (is_second_collider_box) {
			c = Collider.makeBoxCollider(second_collider_width.get(), second_collider_height.get(), TBEMath.toRadians(second_collider_rotation[0]));
		} else {
			c = Collider.makeCircleCollider(second_collider_width.get(), second_collider_num_points.get(), TBEMath.toRadians(second_collider_rotation[0]));
		}
		TileBeanEngine.world.addComponent(obj2_handle, c);
	}

	public void shutdown() {
		TileBeanEngine.assets.clear();
		TileBeanEngine.world.clear();
		TileBeanEngine.show_colliders = false;
	}

	public void update(float delta) {
		Object2D obj = TileBeanEngine.world.get(obj_handle);
		Object2D obj2 = TileBeanEngine.world.get(obj2_handle);
		Collider collider = (Collider)TileBeanEngine.world.getComponent(obj_handle, Collider.class.hashCode());
		Collider collider2 = (Collider)TileBeanEngine.world.getComponent(obj2_handle, Collider.class.hashCode());

		if (is_rotating.get()) {
			obj.rotation += .1f * delta;
		}
		
		MouseState mouse_state = TileBeanEngine.input.getMouseState();
		obj2.x = mouse_state.getWorldX();
		obj2.y = mouse_state.getWorldY();

		CollisionInfo info = Collision.detect(collider, collider2);
		// You can examine a CollisionInfo without resolving a collision, or pass it to Collision.resolve to resolve it.
		Collision.resolve(info, response_first, response_second);
		// You could also call Collision.resolve(info), which is equivalent to Collision.resolve(info, Collision.RESPONSE.RESOLVE, Collision.RESPONSE.RESOLVE).
	}

	public void runGUI() {
		ImGui.textWrapped("This is a demonstration of collision between two colliders.\nThe character's collider is a square the size of his PNG image.\nThe other collider is attached to the mouse, and can be adjusted below.");

		ImBoolean show_colliders = new ImBoolean(TileBeanEngine.show_colliders);
		if (ImGui.checkbox("Show Colliders", show_colliders)) {
			TileBeanEngine.show_colliders = show_colliders.get();
		}
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("Shows all Collider components in the world.\nThis can be controlled via the boolean TileBeanEngine.show_colliders.");
		}

		ImGui.separator();

		ImGui.text("Mouse Collider");

		ImInt mouse_collider_shape = new ImInt(0);
		if (!is_second_collider_box) mouse_collider_shape.set(1);
		if (ImGui.combo("Shape", mouse_collider_shape, new String[]{"Box", "Circle"})) {
			is_second_collider_box = (mouse_collider_shape.get() == 0);
			setupCollision();
		}

		String size_input_str = "Width";
		if (!is_second_collider_box) size_input_str = "Radius";
		if (ImGui.inputFloat(size_input_str, second_collider_width)) {
			setupCollision();
		}

		if (is_second_collider_box) {
			if (ImGui.inputFloat("Height", second_collider_height)) {
				setupCollision();
			}
		}

		if (ImGui.sliderFloat("Rotation", second_collider_rotation, 0, 360)) {
			setupCollision();
		}
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("The rotation may be specified for Collision.makeBoxCollider and Collision.makeCircleCollider to control the orientation of the collision shape.\nFor example, this could be useful if you want to make a triangle collider with the base of the triangle facing down.\nFor convenience, this input widget accepts degrees. (The functions themselves accept radians.)");
		}

		if (!is_second_collider_box) {
			if (ImGui.inputInt("Number of points", second_collider_num_points)) {
				setupCollision();
			}
			if (ImGui.isItemHovered()) {
				ImGui.setTooltip("The number of points to use with Collision.createCircleCollider.\nDespite its name, it really creates regular polygons.");
			}
			if (second_collider_num_points.get() < 3) {
				ImGui.textColored(255, 0, 0, 255, "The minimum number of points is 3.");
				if (ImGui.isItemHovered()) {
					ImGui.setTooltip("The reason for a minimum number of points is obvious- You can't create a polygon with less than 3 points.\nAdditionally, libGDX will \"helpfully\" throw an exception with its ShapeRenderer.");
				}
			} else if (second_collider_num_points.get() > 128) {
				ImGui.textColored(255, 0, 0, 255, "The maximum number of points is 128.");
				if (ImGui.isItemHovered()) {
					ImGui.setTooltip("There is no theoretical maximum number of points for the collision math, but 128 is a practical one.\nToo many points would also overwhelm the libGDX ShapeRenderer.");
				}
			}
		}

		ImGui.separator();
		
		ImGui.checkbox("Rotate TileBean", is_rotating);
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("Slowly rotates the character. You can see that the collider rotates with the character.\nWhile it's rotating, try placing your mouse near the character and then leaving the mouse still, and see what happens when they collide!");
		}

		if (ImGui.button("Reset TileBean")) {
			Object2D obj = TileBeanEngine.world.get(obj_handle);
			obj.rotation = 0;
			obj.x = 0;
			obj.y = 0;
			is_rotating.set(false);
		}

		ImGui.separator();

		ImInt response_int = new ImInt(0);
		if (response_first == RESPONSE.RESOLVE) response_int.set(1);
		if (ImGui.combo("TileBean Response", response_int, new String[]{"NONE", "RESOLVE"})) {
			if (response_int.get() == 0) response_first = RESPONSE.NONE;
			if (response_int.get() == 1) response_first = RESPONSE.RESOLVE;
		}
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("This determines the response of each shape. If both are set to \"RESOLVE\", then both shapes will be pushed out of each other.\nIf one is set to \"NONE\" and the other is set to \"RESOLVE\", only one will be pushed out of the other.");
		}

		response_int.set(0);
		if (response_second == RESPONSE.RESOLVE) response_int.set(1);
		if (ImGui.combo("Mouse Collider Response", response_int, new String[]{"NONE", "RESOLVE"})) {
			if (response_int.get() == 0) response_second = RESPONSE.NONE;
			if (response_int.get() == 1) response_second = RESPONSE.RESOLVE;
		}
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("This determines the response of each shape. If both are set to \"RESOLVE\", then both shapes will be pushed out of each other.\nIf one is set to \"NONE\" and the other is set to \"RESOLVE\", only one will be pushed out of the other.");
		}

	}

}
