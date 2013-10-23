package com.lincbio.lincxmap.android.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.lincbio.lincxmap.geom.Rectangle;

public class DrawableRectangle extends Rectangle implements DrawableShape {
	private static final long serialVersionUID = -4552883517310123510L;

	public DrawableRectangle() {
		super();
	}

	public DrawableRectangle(float x, float y, float width, float height) {
		super(x, y, width, height);
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		paint.setStyle(Style.FILL);
		paint.setColor((FOREGROUD & 0xFFFFFF) | OPACITY);
		canvas.drawRect(this.x, this.y, this.width, this.height, paint);
		paint.setColor(DrawableShape.FOREGROUD);
		paint.setStyle(Style.STROKE);
	}

	@Override
	public void drawBoundary(Canvas canvas, Paint paint, float padding) {
		paint.setStyle(Style.STROKE);
		paint.setPathEffect(DASH);
		paint.setColor(FOREGROUD);
		canvas.drawRect(this.x, this.y, this.width + padding, this.height
				+ padding, paint);
		paint.setPathEffect(null);
	}
}
