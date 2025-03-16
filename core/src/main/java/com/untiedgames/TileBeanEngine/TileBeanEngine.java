package com.untiedgames.TileBeanEngine;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import java.util.ArrayList;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;

public class TileBeanEngine {

	// Engine core features
	
	public static Game game; // Contains functions to perform game logic and run the GUI (if any) each frame.
	public static AssetManager assets; // Contains all graphics and other assets for the game.
	public static World world; // Contains all game objects and their components.
	public static Input input; // Handles input to the game.
	private static Camera camera;
	private static SpriteBatch spritebatch;
	
	// Variables required for Dear ImGui

	private static long window_handle;
	private static ImGuiImplGlfw imgui_glfw;
	private static ImGuiImplGl3 imgui_gl3;
	private static InputProcessor temp_input_processor;

	// Starts the engine.
	public static void initialize() {
		// Engine setup

		assets = new AssetManager();
		world = new World();
		input = new Input();
		camera = new Camera(1920, 1080);
		spritebatch = new SpriteBatch();
		
		// Dear Imgui setup

		imgui_glfw = new ImGuiImplGlfw();
		imgui_gl3 = new ImGuiImplGl3();
		long hwnd = ((Lwjgl3Graphics)Gdx.graphics).getWindow().getWindowHandle();
		window_handle = hwnd;
		ImGui.createContext();
		ImGuiIO io = ImGui.getIO();
		io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
		io.setIniFilename(null);
		io.getFonts().addFontDefault();
		io.getFonts().build();
		imgui_glfw.init(hwnd, true);
		imgui_gl3.init("#version 150");
	}

	public static void shutdown() {
		assets.clear();
		spritebatch.dispose();
		imgui_gl3.shutdown();
		imgui_glfw.shutdown();
		ImGui.destroyContext();
		Callbacks.glfwFreeCallbacks(window_handle);
	}

	public static void onResize(int width, int height) {
		camera.setSize(1920, 1080);
	}

	public static SpriteBatch getSpriteBatch() {
		return spritebatch;
	}

	public static void run() {
		// Reset the libGDX input processor if ImGui had requested input
		
		if (temp_input_processor != null) {
			Gdx.input.setInputProcessor(temp_input_processor);
			temp_input_processor = null;
		}

		// Start a new ImGui frame
		
		imgui_gl3.newFrame();
		imgui_glfw.newFrame();
		ImGui.newFrame();

		// Game loop (logic)

		input.update(1.0f / 60.0f); //TODO: Need an accumulator + proper game loop

		game.update(1.0f / 60.0f); //TODO: Need an accumulator + proper game loop

		for (ArrayList<Component> list : world.components.values()) {
			for (Component c : list) {
				c.update(1.0f / 60.0f); //TODO: Need an accumulator + proper game loop
			}
		}

		// Game loop (drawing)

		camera.setActive();
		spritebatch.begin();
		for (Component c : world.getComponentsOfClass(Sprite.class.hashCode())) {
			Sprite s = (Sprite)c;
			s.draw(spritebatch);
		}
		spritebatch.end();

		// Run the GUI and then render it

		game.runGUI();

		ImGui.render();
		imgui_gl3.renderDrawData(ImGui.getDrawData());

		// Finish up
		
		input.nextFrame();

		// Handle ImGui viewports and input

		ImGuiIO io = ImGui.getIO();
		
		if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
			long temp_current_context = GLFW.glfwGetCurrentContext();
			ImGui.updatePlatformWindows();
			ImGui.renderPlatformWindowsDefault();
			GLFW.glfwMakeContextCurrent(temp_current_context);
		}

		if (io.getWantCaptureKeyboard() || io.getWantCaptureMouse()) {
			temp_input_processor = Gdx.input.getInputProcessor();
			Gdx.input.setInputProcessor(null);
		}
	}

}
