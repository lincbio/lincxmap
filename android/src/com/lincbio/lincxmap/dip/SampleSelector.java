package com.lincbio.lincxmap.dip;

import com.lincbio.lincxmap.geom.Shape;
import com.lincbio.lincxmap.pojo.Product;

/**
 * Sample selector
 * 
 * @author Johnson Lee
 * 
 */
public class SampleSelector {
	protected float deltaX;
	protected float deltaY;
	/**
	 * Normal mode in default
	 */
	protected float scaling = 1;

	public final Shape shape;
	public final Product product;

	public SampleSelector(Product product, Shape shape) {
		this.product = product;
		this.shape = shape;
	}

	public float getDeltaX() {
		return this.deltaX;
	}

	public void setDeltaX(float deltaX) {
		this.deltaX = deltaX;
	}

	public float getDeltaY() {
		return this.deltaY;
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

	public Shape getShape() {
		return shape;
	}

	public Product getProduct() {
		return product;
	}
	
}
