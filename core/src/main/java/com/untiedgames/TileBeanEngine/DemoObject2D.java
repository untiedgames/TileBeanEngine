package com.untiedgames.TileBeanEngine;

import imgui.ImGui;
import imgui.type.ImFloat;

public class DemoObject2D extends Game {

	Object2DHandle obj_handle;
	
	public void initialize() {
		// Load a texture asset.
		TextureAsset tex_asset = new TextureAsset("libgdx_logo", "gfx/libgdx.png");
		tex_asset.load();
		TextureAssetHandle libgdx_logo = TileBeanEngine.assets.add(tex_asset);
		
		// Create a new game object.
		Object2D obj = new Object2D();
		obj_handle = TileBeanEngine.world.add(obj);

		// Add a Sprite component. Sprites can display images and animations.
		Sprite sprite = new Sprite();
		sprite.setGraphics(libgdx_logo);
		TileBeanEngine.world.addComponent(obj_handle, sprite);
	}

	public void shutdown() {
		TileBeanEngine.assets.clear();
		TileBeanEngine.world.clear();
	}

	public void update(float delta) {}

	public void runGUI() {
		ImGui.textWrapped("This is a demonstration of creating and manipulating an Object2D.");
		
		Object2D obj = TileBeanEngine.world.get(obj_handle);
		float[] loc = { obj.x, obj.y };
		ImFloat im_rotation = new ImFloat(obj.rotation);
		float[] scale = { obj.scale_x, obj.scale_y };
		float[] color = { obj.r, obj.g, obj.b, obj.a };
		
		if (ImGui.inputFloat2("Location", loc)) {
			obj.x = loc[0];
			obj.y = loc[1];
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

		if (ImGui.colorPicker4("Color", color)) {
			obj.r = color[0];
			obj.g = color[1];
			obj.b = color[2];
			obj.a = color[3];
		}
		
	}

}
