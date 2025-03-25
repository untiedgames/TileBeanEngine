package com.untiedgames.TileBeanEngine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.untiedgames.TileBeanEngine.Demo.DemoAnimation;
import com.untiedgames.TileBeanEngine.Demo.DemoCamera;
import com.untiedgames.TileBeanEngine.Demo.DemoColliders;
import com.untiedgames.TileBeanEngine.Demo.DemoInput;
import com.untiedgames.TileBeanEngine.Demo.DemoObject2D;
import com.untiedgames.TileBeanEngine.Demo.DemoTilemap;
import com.untiedgames.TileBeanEngine.Demo.DemoTilemapCollision;
import com.untiedgames.TileBeanEngine.Demo.DemoTilemapLoading;
import com.untiedgames.TileBeanEngine.Demo.DemoTweenAndTimer;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiViewport;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

	ImBoolean show_demo_window = new ImBoolean(false); // Whether or not to show the Dear ImGui demo window
	ImInt current_demo = new ImInt(4);
	String[] demo_titles = { "Object2D", "Tween & Timer", "Animation", "Input & Sound", "Camera", "Tilemap Basics", "Tilemap Loading (*.tmx)", "Colliders", "Tilemap Collision" };
	Game[] demos = { new DemoObject2D(), new DemoTweenAndTimer(), new DemoAnimation(), new DemoInput(), new DemoCamera(), new DemoTilemap(), new DemoTilemapLoading(), new DemoColliders(), new DemoTilemapCollision() };
	String version_str;

	class TestGame extends Game {
		
		public void initialize() {
			demos[current_demo.get()].initialize();
			FileHandle f = Gdx.files.internal("version.txt");
			if (f.exists()) {
				version_str = " v" + f.readString();
			}
		}

		public void shutdown() {
			demos[current_demo.get()].shutdown();
			TileBeanEngine.assets.clear();
			TileBeanEngine.world.clear();
		}

		public void update(float delta) {
			demos[current_demo.get()].update(delta);
		}

		public void runGUI() {
			ImGuiIO io = ImGui.getIO();
			ImGuiViewport main_viewport = ImGui.getMainViewport();
			ImGui.setNextWindowViewport(main_viewport.getID());
			ImGui.setNextWindowPos(main_viewport.getPos(), ImGuiCond.Always);
			ImGui.setNextWindowSize(300, io.getDisplaySizeY(), ImGuiCond.Once);
			ImGui.setNextWindowSizeConstraints(300, io.getDisplaySizeY(), 500, io.getDisplaySizeY());
			
			ImGui.begin("TileBeanEngine Demo" + version_str, null, ImGuiWindowFlags.NoDocking);
			ImGui.textWrapped("Welcome to TileBeanEngine! You're running the engine JAR, which also functions as a little demo suite. Use the dropdown below to change demos.");
			
			ImGui.checkbox("Show ImGui demo window", show_demo_window);
			if (ImGui.isItemHovered()) {
				ImGui.setTooltip("If checked, the Dear ImGui demo window will be displayed. You can use the demo window to learn how to use Dear ImGui, and see what it's capable of.");
			}

			int last_demo = current_demo.get();
			if (ImGui.combo("Current Demo", current_demo, demo_titles)) {
				demos[last_demo].shutdown();
				demos[current_demo.get()].initialize();
			} else {
				ImGui.separator();
				demos[current_demo.get()].runGUI();
			}

			ImGui.separator();

			ImGui.text("FPS: " + TileBeanEngine.getFPS());
			TileBeanEngine.displayFPSGraph();

			ImGui.end();

			if (show_demo_window.get()) {
				ImGui.showDemoWindow();
			}
		}

	}

	@Override
	public void create() {
		TileBeanEngine.initialize();
		TileBeanEngine.game = new TestGame();
		TileBeanEngine.game.initialize();
	}

	@Override
	public void resize(int width, int height) {
		TileBeanEngine.onResize(width, height);
	}

	@Override
	public void render() {
		TileBeanEngine.run();
	}

	@Override
	public void dispose() {
		TileBeanEngine.shutdown();
	}
}
