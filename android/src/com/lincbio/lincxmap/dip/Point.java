package com.lincbio.lincxmap.dip;

public class Point {
	public float x;
	public float y;

	public Point() {
	}
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public double distance(Point p) {
		return this.distance(p.x, p.y);
	}

	public double distance(float x, float y) {
		return Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
	}
}
