package com.untiedgames.TileBeanEngine;

public abstract class Game {

	public abstract void initialize();

	public abstract void shutdown();

	public abstract void update(float delta);

	public abstract void runGUI();

}
