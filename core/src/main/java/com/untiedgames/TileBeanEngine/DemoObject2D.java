package com.untiedgames.TileBeanEngine;

import imgui.ImGui;
import imgui.type.ImFloat;

public class DemoObject2D extends Game {

	Object2DHandle obj_handle;
	Object2DHandle obj2_handle;
	
	public void initialize() {
		// Load a texture asset.
		TextureAsset tex_asset = new TextureAsset("libgdx_logo", "gfx/libgdx.png");
		tex_asset.load();
		TextureAssetHandle libgdx_logo = TileBeanEngine.assets.add(tex_asset);
		
		// Create a new game object, which the user can control via ImGui.
		Object2D obj = new Object2D();
		obj.z = 1;
		obj_handle = TileBeanEngine.world.add(obj, "test_object"); // Here we give the object an optional custom name "test_object". The name can be used to retrieve it later.

		// Add a Sprite component. Sprites can display images and animations.
		Sprite sprite = new Sprite();
		sprite.setGraphics(libgdx_logo);
		TileBeanEngine.world.addComponent(obj_handle, sprite);

		// Create a second game object, which the user can't control. Initially, it will be behind the user-controlled object above.
		Object2D obj2 = new Object2D();
		obj2.r = .5f;
		obj2.g = .5f;
		obj2.b = .5f;
		obj2_handle = TileBeanEngine.world.add(obj2);
		Sprite sprite2 = new Sprite();
		sprite2.setGraphics(libgdx_logo);
		TileBeanEngine.world.addComponent(obj2_handle, sprite2);
	}

	public void shutdown() {
		TileBeanEngine.assets.clear();
		TileBeanEngine.world.clear();
	}

	public void update(float delta) {
		TileBeanEngine.world.get(obj2_handle).rotation += delta;
	}

	public void runGUI() {
		ImGui.textWrapped("This is a demonstration of creating and manipulating an Object2D.\nYou can set the properties of the lighter object here.");
		
		// Since this is a demo, we'll show how to retrieve a handle by name. Naming objects is optional.
		Object2DHandle obj_handle_by_name = TileBeanEngine.world.getHandle("test_object");
		
		// Let's prove that the handle we already have and the handle we retrieved by name are the same!
		if (!obj_handle.equals(obj_handle_by_name)) throw new Error("This error should never happen.");
		
		// Retrieve the object using a handle.
		Object2D obj = TileBeanEngine.world.get(obj_handle);
		// Note: If we want to be safer, we should use tryGet instead of get. Since we know for sure this object exists, we can use get.

		float[] loc = { obj.x, obj.y };
		ImFloat im_z = new ImFloat(obj.z);
		ImFloat im_rotation = new ImFloat(obj.rotation);
		float[] scale = { obj.scale_x, obj.scale_y };
		float[] color = { obj.r, obj.g, obj.b, obj.a };
		
		if (ImGui.inputFloat2("Location", loc)) {
			obj.x = loc[0];
			obj.y = loc[1];
		}
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("The location of the object in the game world. This corresponds to the center of the object.\nTileBeanEngine uses a Y-down coordinate system.");
		}

		if (ImGui.inputFloat("Z (Depth)", im_z)) {
			obj.z = im_z.get();
		}
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("The Z value is the depth of the object in the game world.\nObjects are sorted from back to front for drawing.\nObjects with smaller Z values get drawn first, and objects with larger Z values get drawn last.\nThe darker object has a depth of 0. Try setting a negative depth, and the lighter object will appear behind the darker object!");
		}

		if (ImGui.inputFloat("Rotation", im_rotation)) {
			obj.rotation = im_rotation.get();
		}
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("Rotation is in radians.");
		}

		if (ImGui.inputFloat2("Scale", scale)) {
			obj.scale_x = scale[0];
			obj.scale_y = scale[1];
		}
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("The scale of the object. The default scale (100%) is {1.0, 1.0}. Negative scales are allowed, and can be used to \"flip\" objects.");
		}

		if (ImGui.colorPicker4("Color", color)) {
			obj.r = color[0];
			obj.g = color[1];
			obj.b = color[2];
			obj.a = color[3];
		}
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("The color tint of the object. The colors of the texture the object displays will be multiplied by this color.");
		}
		
	}

}
