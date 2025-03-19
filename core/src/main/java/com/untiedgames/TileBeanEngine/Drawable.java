package com.untiedgames.TileBeanEngine;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Drawable is the base class for anything that can be displayed in TileBeanEngine.
 * If you want to make a custom class that can be drawn by the engine, inherit from this.
 */
public abstract class Drawable extends Component {

	public abstract void draw(SpriteBatch spritebatch);

}
