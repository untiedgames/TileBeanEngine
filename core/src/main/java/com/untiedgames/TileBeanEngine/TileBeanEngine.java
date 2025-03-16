package com.untiedgames.TileBeanEngine;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.ScreenUtils;
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

	public static Color bg_color = new Color(0.15f, 0.15f, 0.2f, 1f);
	public static Color letterbox_color = new Color(0, 0, 0, 1f);
	
	// Rendering variables

	private static SpriteBatch spritebatch;
	private static OrthographicCamera internal_camera;
	private static Camera camera;
	private static int render_target_width = 1920;
	private static int render_target_height = 1080;
	private static FrameBuffer render_target;
	
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
		camera = new Camera(render_target_width, render_target_height);
		spritebatch = new SpriteBatch();
		render_target = new FrameBuffer(Format.RGBA8888, render_target_width, render_target_height, false);
		internal_camera = new OrthographicCamera(render_target_width, render_target_height);
		
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
		//camera.setSize(1920, 1080);
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
		ImGuiIO io = ImGui.getIO();

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
		render_target.begin();
		ScreenUtils.clear(bg_color);
		spritebatch.begin();
		for (Component c : world.getComponentsOfClass(Sprite.class.hashCode())) {
			Sprite s = (Sprite)c;
			s.draw(spritebatch);
		}
		spritebatch.end();
		render_target.end();

		// Present the render target to the screen (Letterboxed)

		ScreenUtils.clear(letterbox_color);
		float win_size_x = io.getDisplaySizeX();
		float win_size_y = io.getDisplaySizeY();
		float scale_w = win_size_x / (float)render_target_width;
		float scale_h = win_size_y / (float)render_target_height;
		float window_aspect_ratio = win_size_x / win_size_y;
		float aspect_ratio = (float)render_target_width / (float)render_target_height;
		if (window_aspect_ratio < aspect_ratio) {
			scale_h = scale_w;
		} else {
			scale_w = scale_h;
		}

		internal_camera.viewportWidth = win_size_x;
		internal_camera.viewportHeight = win_size_y;
		internal_camera.update();
		spritebatch.setProjectionMatrix(internal_camera.combined);
		spritebatch.begin();
		Texture rt_tex = render_target.getColorBufferTexture();
		float rt_w = scale_w * render_target_width;
		float rt_h = scale_h * render_target_height;
		spritebatch.draw(rt_tex, -rt_w * .5f, -rt_h * .5f, rt_w, rt_h, 0, 0, 1920, 1080, false, true);
		spritebatch.end();

		// Run the GUI and then render it

		game.runGUI();

		ImGui.render();
		imgui_gl3.renderDrawData(ImGui.getDrawData());

		// Finish up
		
		input.nextFrame();

		// Handle ImGui viewports and input
		
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
