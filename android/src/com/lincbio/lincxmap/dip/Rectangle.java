package com.lincbio.lincxmap.dip;

public abstract class Rectangle {

	public Rectangle() {
	}

	public abstract double getX();

	public abstract double getY();

	public abstract double getWidth();

	public abstract double getHeight();

	public static class Float extends Rectangle {
		public float x;
		public float y;
		public float width;
		public float height;

		public Float() {
		}

		public Float(float x, float y, float width, float height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public double getWidth() {
			return width;
		}

		public double getHeight() {
			return height;
		}
	}

	public static class Double extends Rectangle {
		public double x;
		public double y;
		public double width;
		public double height;

		public Double() {
		}

		public Double(double x, double y, double width, double height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		@Override
		public double getX() {
			return this.x;
		}

		@Override
		public double getY() {
			return this.y;
		}

		@Override
		public double getWidth() {
			return this.width;
		}

		@Override
		public double getHeight() {
			return this.height;
		}

	}
}
