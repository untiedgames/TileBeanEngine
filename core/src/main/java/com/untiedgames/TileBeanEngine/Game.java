package com.untiedgames.TileBeanEngine;

/**
 * Game is a class which represents the "scene" that is currently being displayed.
 * A Game has four functions which must be implemented:
 * - initialize(): Performs setup, loads assets, etc.
 * - shutdown(): Performs cleanup, unloads assets, etc.
 * - update(float delta): Performs game logic. Called once per frame.
 * - runGUI(): Runs a graphical user interface (GUI) for the game.
 */
public abstract class Game {

	/**
	 * Performs setup. This is a good place to load assets.
	*/
	public abstract void initialize();

	/**
	 * Performs cleanup. You should unload assets here.
	 */
	public abstract void shutdown();

	/**
	 * Performs game logic. This function is called once per frame.
	 * The delta parameter represents the time in seconds that has elapsed since the last frame.
	 */
	public abstract void update(float delta);

	/**
	 * Displays an interactive graphical user interface (GUI) for the game.
	 * See Dear ImGui.
	 */
	public abstract void runGUI();

}
