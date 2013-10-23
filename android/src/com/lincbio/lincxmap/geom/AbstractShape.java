package com.lincbio.lincxmap.geom;

import com.lincbio.lincxmap.geom.Shape;

public abstract class AbstractShape implements Shape {
	private static final long serialVersionUID = -6080782752443925585L;

	protected float x;
	protected float y;
	protected float width;
	protected float height;

	public AbstractShape() {
	}

	public AbstractShape(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @override
	 */
	public float getY() {
		return y;
	}

	@Override
	public void setY(float y) {
		this.y = y;
	}

	@Override
	public float getWidth() {
		return this.width;
	}

	@Override
	public void setWidth(float width) {
		this.width = width;
	}

	@Override
	public float getHeight() {
		return this.height;
	}

	@Override
	public void setHeight(float height) {
		this.height = height;
	}

	@Override
	public void move(float deltaX, float deltaY) {
		this.x += deltaX;
		this.y += deltaY;
	}

	@Override
	public void moveTo(float x, float y) {
		this.x = x;
		this.y = y;
	}
}
