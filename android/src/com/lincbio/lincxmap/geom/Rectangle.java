package com.lincbio.lincxmap.geom;

/**
 * Rectangle shape in geometry
 */
public class Rectangle extends AbstractShape {
	private static final long serialVersionUID = -2350842254263285966L;

	/**
     * Default constructor
     */
	public Rectangle() {
	}

    /**
     * Contruct with boundary
     * 
     * @param x
     *           The x coordinate
     * @param y
     *           The y coordinate
     * @param width
     *           The width of this shape
     * @param height
     *           The height of this shape
     */
    public Rectangle(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * @override
     */
    public boolean contains(float x, float y) {
        return this.x <= x && x <= (this.x + this.width)
                && this.y <= y && y <= (this.y + this.height);
    }
}
