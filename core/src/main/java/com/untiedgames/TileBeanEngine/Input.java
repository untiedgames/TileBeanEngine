package com.untiedgames.TileBeanEngine;

import java.util.Optional;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Vector2;

public class Input {

	public class InputState {
		
		private boolean is_pressed = false;
		private float duration = 0;

		public boolean isPressed() {
			return is_pressed;
		}

		public float getDuration() {
			return duration;
		}

	}

	public class MouseState {

		private int raw_x = 0;
		private int raw_y = 0;
		private float world_x = 0;
		private float world_y = 0;
		private float scroll_x = 0;
		private float scroll_y = 0;
		private boolean left_button_pressed = false;
		private boolean middle_button_pressed = false;
		private boolean right_button_pressed = false;

		public int getRawX() {
			return raw_x;
		}

		public int getRawY() {
			return raw_y;
		}

		public float getWorldX() {
			return world_x;
		}

		public float getWorldY() {
			return world_y;
		}

		public boolean isLeftButtonPressed() {
			return left_button_pressed;
		}

		public boolean isMiddleButtonPressed() {
			return middle_button_pressed;
		}

		public boolean isRightButtonPressed() {
			return right_button_pressed;
		}

		public float getScrollX() {
			return scroll_x;
		}

		public float getScrollY() {
			return scroll_y;
		}

	}

	public class Keys extends com.badlogic.gdx.Input.Keys {} // Bring in the keycode definitions from libGDX

	private InputState[] states_keyboard = new InputState[Keys.MAX_KEYCODE];
	private InputState[] states_keyboard_prev = new InputState[Keys.MAX_KEYCODE];
	private MouseState state_mouse = new MouseState();
	private MouseState state_mouse_prev = new MouseState();

	private boolean first_frame = true;

	public Input() {
		Gdx.input.setInputProcessor(new InputAdapter(this));
		for (int i = 0; i < states_keyboard.length; i++) {
			states_keyboard[i] = new InputState();
			states_keyboard_prev[i] = new InputState();
		}
	}

	public InputState getKeyState(int keycode) {
		if (keycode < states_keyboard.length) return states_keyboard[keycode];
		return new InputState();
	}

	public InputState getKeyStatePrev(int keycode) {
		if (keycode < states_keyboard_prev.length) return states_keyboard_prev[keycode];
		return new InputState();
	}

	public boolean isKeyDown(int keycode) {
		if (keycode < states_keyboard.length) return states_keyboard[keycode].is_pressed;
		return false;
	}

	public boolean wasKeyDown(int keycode) {
		if (keycode < states_keyboard_prev.length) return states_keyboard_prev[keycode].is_pressed;
		return false;
	}

	public MouseState getMouseState() {
		return state_mouse;
	}

	public MouseState getMouseStatePrev() {
		return state_mouse_prev;
	}

	public void update(float delta) {
		for (int i = 0; i < states_keyboard.length; i++) {
			if (states_keyboard[i].is_pressed) states_keyboard[i].duration += delta;
			else states_keyboard[i].duration = 0;
		}

		state_mouse.raw_x = Gdx.input.getX();
		state_mouse.raw_y = Gdx.input.getY();

		float window_width = TileBeanEngine.getWindowWidth();
		float window_height = TileBeanEngine.getWindowHeight();
		float sc = 1;
		Object2DHandle camera_handle = TileBeanEngine.getCameraHandle();
		Optional<Object2D> opt_obj_camera = TileBeanEngine.world.tryGet(camera_handle);
		if (opt_obj_camera.isPresent()) {
			Object2D obj_camera = opt_obj_camera.get();
			if (obj_camera.z != 0.0f) {
				Optional<Component> opt_cam = TileBeanEngine.world.tryGetComponent(camera_handle, Camera.class.hashCode());
				if (opt_cam.isPresent()) {
					Camera cam = (Camera)opt_cam.get();
					if (window_width < window_height) sc = (window_width / (float)cam.getWidth());
					else sc = (window_height / (float)cam.getHeight());
					state_mouse.world_x = ((Gdx.input.getX() - window_width / 2.0f) / obj_camera.z) / sc + obj_camera.x;
					state_mouse.world_y = ((Gdx.input.getY() - window_height / 2.0f) / obj_camera.z) / sc + obj_camera.y;
					Vector2 loc = TBEMath.rotateAboutPoint(state_mouse.world_x, state_mouse.world_y, obj_camera.x, obj_camera.y, obj_camera.rotation);
					state_mouse.world_x = loc.x;
					state_mouse.world_y = loc.y;
				}
			}
		}

		if (first_frame) {
			first_frame = false;
			state_mouse_prev.raw_x = state_mouse.raw_x;
			state_mouse_prev.raw_y = state_mouse.raw_y;
			state_mouse_prev.world_x = state_mouse.world_x;
			state_mouse_prev.world_y = state_mouse.world_y;
			state_mouse_prev.scroll_x = state_mouse.scroll_x;
			state_mouse_prev.scroll_y = state_mouse.scroll_y;
			state_mouse_prev.left_button_pressed = state_mouse.left_button_pressed;
			state_mouse_prev.middle_button_pressed = state_mouse.middle_button_pressed;
			state_mouse_prev.right_button_pressed = state_mouse.right_button_pressed;
		}
	}

	public void nextFrame() {
		for (int i = 0; i < states_keyboard.length; i++) {
			states_keyboard_prev[i].is_pressed = states_keyboard[i].is_pressed;
			states_keyboard_prev[i].duration = states_keyboard[i].duration;
		}
		state_mouse_prev.raw_x = state_mouse.raw_x;
		state_mouse_prev.raw_y = state_mouse.raw_y;
		state_mouse_prev.world_x = state_mouse.world_x;
		state_mouse_prev.world_y = state_mouse.world_y;
		state_mouse_prev.scroll_x = state_mouse.scroll_x;
		state_mouse_prev.scroll_y = state_mouse.scroll_y;
		state_mouse_prev.left_button_pressed = state_mouse.left_button_pressed;
		state_mouse_prev.middle_button_pressed = state_mouse.middle_button_pressed;
		state_mouse_prev.right_button_pressed = state_mouse.right_button_pressed;
	}

	public class InputAdapter implements InputProcessor {

		private Input input;

		public InputAdapter(Input input) {
			this.input = input;
		}

		public boolean keyDown(int keycode) {
			input.states_keyboard[keycode].is_pressed = true;
			return false;
		}

		public boolean keyTyped(char c) {
			return false;
		}

		public boolean keyUp(int keycode) {
			input.states_keyboard[keycode].is_pressed = false;
			return false;
		}

		public boolean mouseMoved(int x, int y) {
			return false;
		}

		public boolean touchDown(int x, int y, int pointer, int button) {
			switch(button) {
				case Buttons.LEFT:
					input.state_mouse.left_button_pressed = true;
					break;
				case Buttons.MIDDLE:
					input.state_mouse.middle_button_pressed = true;
					break;
				case Buttons.RIGHT:
					input.state_mouse.right_button_pressed = true;
					break;
				default:
					break;
			}
			return false;
		}

		public boolean touchDragged(int x, int y, int pointer) {
			return false;
		}

		public boolean touchUp(int x, int y, int pointer, int button) {
			switch(button) {
				case Buttons.LEFT:
					input.state_mouse.left_button_pressed = false;
					break;
				case Buttons.MIDDLE:
					input.state_mouse.middle_button_pressed = false;
					break;
				case Buttons.RIGHT:
					input.state_mouse.right_button_pressed = false;
					break;
				default:
					break;
			}
			return false;
		}

		public boolean touchCancelled(int screen_x, int screen_y, int pointer, int button) {
			return false;
		}

		public boolean scrolled(float amount_x, float amount_y) {
			input.state_mouse.scroll_x += amount_x;
			input.state_mouse.scroll_y += amount_y;
			return false;
		}

	}

}
