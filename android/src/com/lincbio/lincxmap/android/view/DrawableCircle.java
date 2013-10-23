package com.lincbio.lincxmap.android.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.lincbio.lincxmap.geom.Circle;

public class DrawableCircle extends Circle implements DrawableShape {
	private static final long serialVersionUID = -4552883517310123510L;

	public DrawableCircle() {
		super();
	}

	public DrawableCircle(float x, float y, float radius) {
		super(x, y, radius);
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		paint.setStyle(Style.FILL);
		paint.setColor((FOREGROUD & 0xFFFFFF) | OPACITY);
		canvas.drawCircle(this.x + this.radius, this.y + this.radius,
				this.radius, paint);
		paint.setColor(DrawableShape.FOREGROUD);
		paint.setStyle(Style.STROKE);
	}

	@Override
	public void drawBoundary(Canvas canvas, Paint paint, float padding) {
		paint.setStyle(Style.STROKE);
		paint.setPathEffect(DASH);
		paint.setColor(FOREGROUD);
		canvas.drawCircle(this.x + this.radius, this.y + this.radius,
				this.radius + padding, paint);
		paint.setPathEffect(null);
	}
}
