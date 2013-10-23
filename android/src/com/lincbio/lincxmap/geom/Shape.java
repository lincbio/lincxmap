package com.lincbio.lincxmap.geom;

import java.io.Serializable;

/**
 * Geometry shape
 */
public interface Shape extends Serializable {

	/**
	 * Returns the x coordinate
	 * 
	 * @return the x coordinate of this shape
	 */
	public float getX();

	/**
	 * Sets the x coordinate
	 * 
	 * @param x
	 *            the x coordinate of this shape
	 */
	public void setX(float x);

	/**
	 * Returns the y coordinate
	 * 
	 * @return the y coordinate of this shape
	 */
	public float getY();

	/**
	 * Sets the y coordinate
	 * 
	 * @param y
	 *            the y coordinate of this shape
	 */
	public void setY(float y);

	/**
	 * Returns the width of its outer rectangle
	 * 
	 * @return the width of its outer rectangle
	 */
	public float getWidth();

	/**
	 * Sets the width of this shape
	 * 
	 * @param width
	 *            the width of this shape
	 */
	public void setWidth(float width);

	/**
	 * Returns the height of this shape
	 * 
	 * @return the height of this shape
	 */
	public float getHeight();

	/**
	 * Sets the height of this shape
	 * 
	 * @return the height of this shape
	 */
	public void setHeight(float height);

	/**
	 * Moves this shape with specified deviation
	 * 
	 * @param deltaX
	 *            The deviation on x coordinate axis
	 * @param deltaY
	 *            The deviation on y coordinate axis
	 */
	public void move(float deltaX, float deltaY);

	/**
	 * Moves this shape from current position to the specified position
	 * 
	 * @param x
	 *            The x coordinate of destination position
	 * @param y
	 *            The y coordinate of destination position
	 */
	public void moveTo(float x, float y);

	/**
	 * Test wheather this shape contains the point with specified coordinate
	 */
	public boolean contains(float x, float y);

}
