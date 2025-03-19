package com.untiedgames.TileBeanEngine;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;

public class TileBeanEngine {

	// Engine core features
	
	public static Game game; // Contains functions to perform game logic and run the GUI (if any) each frame.
	public static AssetManager assets; // Contains all graphics and other assets for the game.
	public static World world; // Contains all game objects and their components.
	public static Input input; // Handles input to the game.

	public static Color bg_color = new Color(0.15f, 0.15f, 0.2f, 1f); // The background color of the game world.
	public static Color letterbox_color = new Color(0, 0, 0, 1f); // The color of the letterbox (bars on either side of the window).

	private static float time = 0; // The time in seconds since the application started.
	private static float delta_accumulator = 0;

	public static double speed_multiplier = 1.0; // Acts as a fast-forward or slowdown modifier for the game.
	public static double logic_fps = 1.0 / 60.0; // The granularity of how often game logic is performed. For example, if the game is running at 60FPS and logic_fps is 1/120, you can expect logic to be performed twice per frame. Note: This is not equivalent to display FPS.
	private static int render_fps = 60; // The target frames per second for displaying the game. The default will be set to your monitor's refresh rate plus one.

	// Rendering variables

	private static SpriteBatch spritebatch;
	private static OrthographicCamera internal_camera;
	private static Object2DHandle camera_handle;
	private static int render_target_width = 1;
	private static int render_target_height = 1;
	static final int default_render_target_width = 1920;
	static final int default_render_target_height = 1080;
	private static FrameBuffer render_target;

	// FPS counter
	
	private static final int tick_count = 300;
	private static float[] ticks = new float[tick_count];
	private static int tick_index = 0;
	private static float tick_sum = 0;
	
	// Variables required for Dear ImGui

	private static long window_handle;
	private static ImGuiImplGlfw imgui_glfw;
	private static ImGuiImplGl3 imgui_gl3;
	private static InputProcessor temp_input_processor;

	/**
	 * Starts the engine.
	 */
	public static void initialize() {
		// Engine setup

		assets = new AssetManager();
		world = new World();
		input = new Input();
		spritebatch = new SpriteBatch();
		
		setupCamera();
		setResolution(default_render_target_width, default_render_target_height);
		setRenderFPS(Gdx.graphics.getDisplayMode().refreshRate + 1);
		for (int i = 0; i < tick_count; i++) ticks[i] = 60;
		tick_sum = tick_count * 60;
		
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

	/**
	 * Shuts down the engine.
	 */
	public static void shutdown() {
		assets.clear();
		spritebatch.dispose();
		imgui_gl3.shutdown();
		imgui_glfw.shutdown();
		ImGui.destroyContext();
		Callbacks.glfwFreeCallbacks(window_handle);
	}

	public static void onResize(int width, int height) {}

	/**
	 * Sets the resolution of the render target and camera.
	 * (This does not change the window size, but rather how the game looks inside the window.)
	 */
	public static void setResolution(int width, int height) {
		if (render_target_width == width && render_target_height == height) return;
		if (width <= 0 || height <= 0) return;
		if (width >= 4096 || height >= 4096) return; // Finding out the max texture size is outside the scope of this project, so we'll just say 4096 is a reasonable max.
		render_target_width = width;
		render_target_height = height;
		
		if (render_target != null) render_target.dispose();
		render_target = new FrameBuffer(Format.RGB888, render_target_width, render_target_height, false);

		if (internal_camera == null) internal_camera = new OrthographicCamera(render_target_width, render_target_height);
		else {
			internal_camera.viewportWidth = render_target_width;
			internal_camera.viewportHeight = render_target_height;
			internal_camera.update();
		}

		Optional<Component> opt_cam = world.tryGetComponent(camera_handle, Camera.class.hashCode());
		if (opt_cam.isPresent()) {
			((Camera)opt_cam.get()).setSize(render_target_width, render_target_height);
		}
	}

	/**
	 * Returns the target render frames per second (FPS). This is not necessarily equivalent to the actual, current FPS of the game.
	 */
	public static int getRenderFPS() {
		return render_fps;
	}

	/**
	 * Sets the render frames per second (FPS).
	 */
	public static void setRenderFPS(int render_fps) {
		TileBeanEngine.render_fps = render_fps;
		Gdx.graphics.setForegroundFPS(render_fps);
	}

	/**
	 * Returns the actual, current FPS of the game.
	 */
	public static int getFPS() {
		return (int)Math.round(tick_sum / (float)tick_count);
	}

	/**
	 * Displays a frames per second (FPS) graph using Dear ImGui.
	 */
	public static void displayFPSGraph() {
		float average = 0.0f;
		for (int i = 0; i < tick_count; i++) {
			average += ticks[i];
		}
		average /= (float)tick_count;
		average = 1.0f / average;
		average *= 1000.0f;
		ImGui.plotLines("###fps_graph", ticks, tick_count, tick_index, "\n\n\nms / frame: " + String.format("%.2f", average), 0, 60.0f, new ImVec2(ImGui.getWindowSize().x, 60.0f));
	}

	/**
	 * Retrieves the libGDX SpriteBatch, which can be used for custom drawing.
	 */
	public static SpriteBatch getSpriteBatch() {
		return spritebatch;
	}

	// Used for initialization.
	static void setupCamera() {
		Object2D obj = new Object2D();
		camera_handle = TileBeanEngine.world.add(obj);
		Camera cam = new Camera(render_target_width, render_target_height);
		world.addComponent(camera_handle, cam);
	}

	/**
	 * Returns a handle to the current camera.
	 */
	public static Object2DHandle getCameraHandle() {
		return camera_handle;
	}

	/**
	 * Sets the current camera to the specified handle.
	 * If the handle does not have a Camera component, a warning will be printed in the console.
	 */
	public static void setCamera(Object2DHandle handle) {
		camera_handle = handle;
		Optional<Component> opt_cam = world.tryGetComponent(camera_handle, Camera.class.hashCode());
		if (!opt_cam.isPresent()) {
			System.err.println("Warning: No camera component is present on handle passed to setCamera.");
		}
	}

	/**
	 * Returns the time since the application started, in seconds.
	 */
	public static float getTime() {
		return time;
	}

	/**
	 * Performs game logic and drawing, and runs the GUI (if any).
	 */
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
		
		float total_delta = Gdx.graphics.getDeltaTime();
		delta_accumulator += total_delta;
		delta_accumulator *= speed_multiplier;

		if (delta_accumulator >= 5) delta_accumulator = 0; // Detect and discard significant lag frames (5+ seconds)

		while (delta_accumulator > 0.0) {
			double delta = Math.min(delta_accumulator, logic_fps);
			
			time += (float)delta;

			input.update((float)delta);
			game.update((float)delta);

			for (ArrayList<Component> list : world.components.values()) {
				for (Component c : list) {
					c.update((float)delta);
				}
			}

			delta_accumulator -= delta;
			if (delta_accumulator < 0.001) delta_accumulator = 0;
		}

		// FPS counter
		
		if (total_delta != 0.0f) {
			float tick = 1.0f / (float)total_delta;
			tick_sum -= ticks[tick_index];
			tick_sum += tick;
			ticks[tick_index] = tick;
			if (++tick_index == tick_count) tick_index = 0;
		}
		
		// Game loop (drawing)

		Optional<Component> opt_cam = world.tryGetComponent(camera_handle, Camera.class.hashCode());
		if (opt_cam.isPresent()) {
			((Camera)opt_cam.get()).setActive();
		}
		render_target.begin();
		ScreenUtils.clear(bg_color);
		spritebatch.begin();
		HashSet<Component> drawables_set = world.getComponentsOfClass(Drawable.class.hashCode());
		ArrayList<Drawable> drawables = new ArrayList<>();
		for (Component c : drawables_set) drawables.add((Drawable)c);
		Collections.sort(drawables);
		for (Drawable d : drawables) {
			d.draw(spritebatch);
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
		spritebatch.setTransformMatrix(new Matrix4());
		spritebatch.begin();
		spritebatch.setColor(1, 1, 1, 1);
		Texture rt_tex = render_target.getColorBufferTexture();
		float rt_w = scale_w * render_target_width;
		float rt_h = scale_h * render_target_height;
		spritebatch.draw(rt_tex, -rt_w * .5f, -rt_h * .5f, rt_w, rt_h, 0, 0, render_target_width, render_target_height, false, true);
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
