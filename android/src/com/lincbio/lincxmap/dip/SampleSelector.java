package com.lincbio.lincxmap.dip;

import com.lincbio.lincxmap.pojo.Product;

/**
 * Sample selector
 * 
 * @author Johnson Lee
 * 
 */
public abstract class SampleSelector {
	protected float x;
	protected float y;
	protected float deltaX;
	protected float deltaY;
	protected float scaling;
	private Product product;

	public SampleSelector() {
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getDeltaX() {
		return deltaX;
	}

	public void setDeltaX(float deltaX) {
		this.deltaX = deltaX;
	}

	public float getDeltaY() {
		return deltaY;
	}

	public void setDeltaY(float deltaY) {
		this.deltaY = deltaY;
	}

	public float getScaling() {
		return scaling;
	}

	public void setScaling(float scaling) {
		this.scaling = scaling;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void move(float dx, float dy) {
		this.x += dx;
		this.y += dy;
	}

	public void move(float x1, float y1, float x2, float y2) {
		this.x += x2 - x1;
		this.y += y2 - y1;
	}

	public abstract boolean contains(float x, float y);

	public abstract Rectangle getBounds();

}
