package com.lincbio.lincxmap.geom;

import com.lincbio.lincxmap.geom.AbstractShape;

/**
 * Circle shape in geometry
 */
public class Circle extends AbstractShape {
	private static final long serialVersionUID = 4147358144026366660L;

	protected float radius;

	/**
	 * Default constructor
	 */
	public Circle() {
	}

	/**
	 * Construct with specified dimension
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @param radius
	 *            The radius of this circle
	 */
	public Circle(float x, float y, float radius) {
		super(x, y, radius * 2, radius * 2);
		this.radius = radius;
	}

	/**
	 * Returns the diameter of this circle
	 * 
	 * @return the diameter of this circle
	 */
	public float getDiameter() {
		return this.radius * 2;
	}

	/**
	 * Set the diameter of this circle
	 * 
	 * @param diameter
	 *            The diameter of this circle
	 */
	public void setDiameter(float diameter) {
		this.radius = diameter / 2;
		this.width = this.height = diameter;
	}

	/**
	 * @override
	 */
	public void setWidth(float width) {
		this.setDiameter(width);
	}

	/**
	 * @override
	 */
	public void setHeight(float height) {
		this.setDiameter(height);
	}

	/**
	 * @override
	 */
	public boolean contains(float x, float y) {
		float oX = this.x + this.radius;
		float oY = this.y + this.radius;

		return Math.sqrt(Math.pow(x - oX, 2) + Math.pow(y - oY, 2)) <= this.radius;
	}
}
