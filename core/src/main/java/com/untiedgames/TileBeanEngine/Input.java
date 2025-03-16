package com.untiedgames.TileBeanEngine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;

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

	public class Keys extends com.badlogic.gdx.Input.Keys {} // Bring in the keycode definitions from libGDX

	private InputState[] states_keyboard = new InputState[Keys.MAX_KEYCODE];
	private InputState[] states_keyboard_prev = new InputState[Keys.MAX_KEYCODE];

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

	public void update(float delta) {
		for (int i = 0; i < states_keyboard.length; i++) {
			if (states_keyboard[i].is_pressed) states_keyboard[i].duration += delta;
			else states_keyboard[i].duration = 0;
		}
	}

	public void nextFrame() {
		for (int i = 0; i < states_keyboard.length; i++) {
			states_keyboard_prev[i].is_pressed = states_keyboard[i].is_pressed;
			states_keyboard_prev[i].duration = states_keyboard[i].duration;
		}
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
			//Do nothing. We don't use this.
			return false;
		}

		public boolean keyUp(int keycode) {
			input.states_keyboard[keycode].is_pressed = false;
			return false;
		}

		public boolean mouseMoved(int x, int y) {
			//TODO
			return false;
		}

		public boolean touchDown(int x, int y, int pointer, int button) {
			//TODO
			return false;
		}

		public boolean touchDragged(int x, int y, int pointer) {
			//TODO
			return false;
		}

		public boolean touchUp(int x, int y, int pointer, int button) {
			//TODO
			return false;
		}

		public boolean touchCancelled(int screen_x, int screen_y, int pointer, int button) {
			//TODO
			return false;
		}

		public boolean scrolled(float amount_x, float amount_y) {
			//TODO
			return false;
		}

	}

}
