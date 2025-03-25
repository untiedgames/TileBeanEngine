package com.untiedgames.TileBeanEngine.Demo;

import com.untiedgames.TileBeanEngine.Component;
import com.untiedgames.TileBeanEngine.Game;
import com.untiedgames.TileBeanEngine.Object2D;
import com.untiedgames.TileBeanEngine.Object2DHandle;
import com.untiedgames.TileBeanEngine.Sprite;
import com.untiedgames.TileBeanEngine.TileBeanEngine;
import com.untiedgames.TileBeanEngine.TimerInstance;
import com.untiedgames.TileBeanEngine.TimerManager;
import com.untiedgames.TileBeanEngine.Tween;
import com.untiedgames.TileBeanEngine.TweenColor;
import com.untiedgames.TileBeanEngine.TweenLocation;
import com.untiedgames.TileBeanEngine.TweenRotation;
import com.untiedgames.TileBeanEngine.TweenScale;
import com.untiedgames.TileBeanEngine.AssetSystem.TextureAsset;
import com.untiedgames.TileBeanEngine.AssetSystem.TextureAssetHandle;

import imgui.ImGui;
import imgui.type.ImInt;

public class DemoTweenAndTimer extends Game {

	// Tween modes that the user can choose from in this demo.
	private enum MODE {
		LOCATION,
		ROTATION,
		SCALE,
		COLOR
	}

	MODE mode = MODE.ROTATION;
	Tween.TYPE tween_type = Tween.TYPE.ELASTICOUT;
	Object2DHandle obj_handle;
	int tween_counter;
	
	public void initialize() {
		// Load a texture asset.
		TextureAsset tex_asset = new TextureAsset("tilebeanengine_logo", "gfx/tilebeanengine_logo.png");
		tex_asset.load();
		TextureAssetHandle tilebeanengine_logo = TileBeanEngine.assets.add(tex_asset);

		// Create a new game object.
		Object2D obj = new Object2D();
		obj_handle = TileBeanEngine.world.add(obj);

		// Add a Sprite component. Sprites can display images and animations.
		Sprite sprite = new Sprite();
		sprite.setGraphics(tilebeanengine_logo);
		TileBeanEngine.world.addComponent(obj_handle, sprite);
		
		// Create a timer manager, which can manage one or more timers.
		TimerManager timer_manager = new TimerManager();
		TileBeanEngine.world.addComponent(obj_handle, timer_manager);

		// Start a 2 second timer that repeats infinitely (-1 repeats).
		timer_manager.start("timer", 2.0f, -1, false);

		initializeTween();
	}

	public void shutdown() {
		TileBeanEngine.assets.clear();
		TileBeanEngine.world.clear();
	}

	private void initializeTween() {
		// We don't keep track of the last tween mode, so remove any tween component that the object possibly had.
		for (Component c : TileBeanEngine.world.getComponents(obj_handle)) {
			if (c instanceof Tween) TileBeanEngine.world.removeComponent(obj_handle, c.getClass().hashCode());
		}

		// Reset the object to its initial state
		Object2D obj = TileBeanEngine.world.get(obj_handle);
		obj.x = 0;
		obj.y = 0;
		obj.rotation = 0;
		obj.scale_x = 1;
		obj.scale_y = 1;
		obj.r = 1;
		obj.g = 1;
		obj.b = 1;
		obj.a = 1;

		// Add a new Tween component to the object, based on our current mode
		Tween tween = null;
		switch(mode) {
			case LOCATION:
				tween = new TweenLocation();
				obj.x = -200;
				obj.y = -200;
				break;
			case ROTATION:
				tween = new TweenRotation();
				break;
			case SCALE:
				tween = new TweenScale();
				break;
			case COLOR:
				tween = new TweenColor();
				break;
		}
		TileBeanEngine.world.addComponent(obj_handle, tween);

		tween_counter = 0;
	}

	public void update(float delta) {
		// Check obj_handle's timer. If it's finished, we'll rotate the object using a tween.
		TimerManager timer_manager = (TimerManager)TileBeanEngine.world.getComponent(obj_handle, TimerManager.class.hashCode());
		TimerInstance timer = timer_manager.get("timer");
		if (timer.isFinished()) {
			tween_counter++;
			switch(mode) {
				case LOCATION:
					TweenLocation tween_location = (TweenLocation)TileBeanEngine.world.getComponent(obj_handle, TweenLocation.class.hashCode());
					if (tween_counter % 2 == 0) tween_location.start(tween_type, 2.0f, -200, -200);
					else tween_location.start(tween_type, 2.0f, 200, 200);
					break;
				case ROTATION:
					TweenRotation tween_rotation = (TweenRotation)TileBeanEngine.world.getComponent(obj_handle, TweenRotation.class.hashCode());
					tween_rotation.start(tween_type, 2.0f, (float)tween_counter * (float)Math.PI * .5f);
					break;
				case SCALE:
					TweenScale tween_scale = (TweenScale)TileBeanEngine.world.getComponent(obj_handle, TweenScale.class.hashCode());
					if (tween_counter % 2 == 0) tween_scale.start(tween_type, 2.0f, 1, 1);
					else tween_scale.start(tween_type, 2.0f, 4, 4);
					break;
				case COLOR:
					TweenColor tween_color = (TweenColor)TileBeanEngine.world.getComponent(obj_handle, TweenColor.class.hashCode());
					if (tween_counter % 2 == 0) tween_color.start(Tween.TYPE.EASEOUT, 2.0f, 1, 1, 1, 1);
					else tween_color.start(tween_type, 2.0f, (float)Math.sin(tween_counter * .1f), (float)Math.sin(tween_counter * .2f), (float)Math.sin(tween_counter * .3f), 1);
					break;
			}
			timer.clearFinished();
		}
	}

	public void runGUI() {
		ImGui.textWrapped("This is a demonstration of Tween, TimerInstance, and TimerManager. It is also a basic example of how to load an asset and create a game object.\nA Tween is a change in value over time that can be applied to an object's properties, like \"inbetweening\" for an animation.\nYou can adjust the tween below!");
		
		MODE[] modes = MODE.values();
		String[] modes_str = new String[modes.length];
		ImInt tween_mode_value = new ImInt();
		for (int i = 0; i < modes.length; i++) {
			modes_str[i] = modes[i].toString();
			if (modes[i] == mode) tween_mode_value.set(i);
		}
		if (ImGui.combo("Tween mode", tween_mode_value, modes_str)) {
			mode = MODE.valueOf(modes_str[tween_mode_value.get()]);
			initializeTween();
		}

		Tween.TYPE[] types = Tween.TYPE.values();
		String[] types_str = new String[types.length];
		ImInt tween_type_value = new ImInt();
		for (int i = 0; i < types.length; i++) {
			types_str[i] = types[i].toString();
			if (types_str[i] == tween_type.toString()) tween_type_value.set(i);
		}
		if (ImGui.combo("Tween type", tween_type_value, types_str)) {
			tween_type = Tween.TYPE.valueOf(types_str[tween_type_value.get()]);
			Tween tween = null;
			switch(mode) {
				case LOCATION:
					tween = (Tween)TileBeanEngine.world.getComponent(obj_handle, TweenLocation.class.hashCode());
					break;
				case ROTATION:
					tween = (Tween)TileBeanEngine.world.getComponent(obj_handle, TweenRotation.class.hashCode());
					break;
				case SCALE:
					tween = (Tween)TileBeanEngine.world.getComponent(obj_handle, TweenScale.class.hashCode());
					break;
				case COLOR:
					tween = (Tween)TileBeanEngine.world.getComponent(obj_handle, TweenColor.class.hashCode());
					break;
			}
			tween.type = tween_type;
		}

		TimerManager timer_manager = (TimerManager)TileBeanEngine.world.getComponent(obj_handle, TimerManager.class.hashCode());
		TimerInstance timer = timer_manager.get("timer");
		ImGui.text("Timer progress:");
		ImGui.sameLine();
		ImGui.progressBar(timer.getProgress());
	}

}
