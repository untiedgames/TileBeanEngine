package com.untiedgames.TileBeanEngine.Demo;

import com.badlogic.gdx.Gdx;
import com.untiedgames.TileBeanEngine.Camera;
import com.untiedgames.TileBeanEngine.Game;
import com.untiedgames.TileBeanEngine.Object2D;
import com.untiedgames.TileBeanEngine.Object2DHandle;
import com.untiedgames.TileBeanEngine.Sprite;
import com.untiedgames.TileBeanEngine.TileBeanEngine;
import com.untiedgames.TileBeanEngine.AssetSystem.TextureAsset;
import com.untiedgames.TileBeanEngine.AssetSystem.TextureAssetHandle;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImFloat;
import imgui.type.ImInt;

public class DemoCamera extends Game {

	private ImBoolean auto_move = new ImBoolean(true);

	public void initialize() {
		// Load a texture asset.
		TextureAsset tex_asset = new TextureAsset("libgdx_logo", "gfx/libgdx.png");
		tex_asset.load();
		TextureAssetHandle libgdx_logo = TileBeanEngine.assets.add(tex_asset);
		
		// Create a new game object.
		Object2D obj = new Object2D();
		Object2DHandle obj_handle = TileBeanEngine.world.add(obj);

		// Add a Sprite component. Sprites can display images and animations.
		Sprite sprite = new Sprite();
		sprite.setGraphics(libgdx_logo);
		TileBeanEngine.world.addComponent(obj_handle, sprite);
	}

	public void shutdown() {
		TileBeanEngine.assets.clear();
		TileBeanEngine.world.clear();
		TileBeanEngine.setResolution(TileBeanEngine.getDefaultResolutionX(), TileBeanEngine.getDefaultResolutionY());
		TileBeanEngine.setRenderFPS(Gdx.graphics.getDisplayMode().refreshRate + 1);
	}

	public void update(float delta) {
		if (auto_move.get()) {
			float time = TileBeanEngine.getTime();
			Object2D obj = TileBeanEngine.world.get(TileBeanEngine.getCameraHandle());
			obj.x = 200.0f * (float)Math.sin(time);
			obj.y = 200.0f * (float)Math.cos(time);
			obj.z = 1.0f + .5f * (float)Math.sin(time);
			obj.rotation += .1f * delta;
		}
	}

	public void runGUI() {
		ImGui.textWrapped("This is a demonstration of how the Camera class works.\nIn this demo, an object is in the game world at {0, 0} and the camera is moving.\nYou can move the camera yourself by unchecking the auto-move checkbox.");
		
		Object2DHandle camera_handle = TileBeanEngine.getCameraHandle();
		Object2D obj = TileBeanEngine.world.get(camera_handle);
		Camera cam = (Camera)TileBeanEngine.world.getComponent(camera_handle, Camera.class.hashCode());
		
		if (ImGui.checkbox("Auto-move camera", auto_move)) {
			obj.x = 0;
			obj.y = 0;
			obj.z = 1;
			obj.rotation = 0;
		}

		float[] loc = { obj.x, obj.y };
		ImFloat im_z = new ImFloat(obj.z);
		ImFloat im_rotation = new ImFloat(obj.rotation);
		int[] size = { cam.getWidth(), cam.getHeight() };
		
		if (auto_move.get()) {
			ImGui.beginDisabled();
		}

		if (ImGui.inputFloat2("Location", loc)) {
			obj.x = loc[0];
			obj.y = loc[1];
		}
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("The location of the camera in the game world. This corresponds to the center of the camera.\nTileBeanEngine uses a Y-down coordinate system.");
		}

		if (ImGui.inputFloat("Z (Zoom)", im_z)) {
			obj.z = im_z.get();
		}
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("For the camera, the Z value represents the camera zoom.\nA Z value of 1.0 is 100%% zoom, 2.0 = 200%% zoom, and so on.");
		}

		if (ImGui.inputFloat("Rotation", im_rotation)) {
			obj.rotation = im_rotation.get();
		}
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("Rotation is in radians.");
		}
		
		if (auto_move.get()) {
			ImGui.endDisabled();
		}

		if (ImGui.inputInt2("Resolution (size)", size)) {
			TileBeanEngine.setResolution(size[0], size[1]);
		}
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("The resolution of the game world.\nThe default resolution is 1920x1080.\nThis does not affect the window size, rather it refers to the texture that the game renders to.");
		}

		ImInt fps = new ImInt(TileBeanEngine.getRenderFPS());
		if (ImGui.inputInt("Render FPS", fps)) {
			if (fps.get() >= 1.0) {
				TileBeanEngine.setRenderFPS(fps.get());
			}
		}
		if (ImGui.isItemHovered()) {
			ImGui.setTooltip("The target frames per second (FPS) that the game should display at. By default, this is set to the refresh rate of your monitor plus one.\nWhen \"auto-move camera\" is checked, note how the motion continues at the same rate even when a low FPS is set.\nThis is because the camera is moving based on the delta time (the time elapsed between frames) and is therefore independent of FPS.");
		}
		
	}

}
