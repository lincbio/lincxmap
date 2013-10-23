package com.lincbio.lincxmap.android.view;

import com.lincbio.lincxmap.geom.Shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;

/**
 * Visual Shape which is able to be drawn on canvas
 * 
 * @author Johnson Lee
 * 
 */
public interface DrawableShape extends Shape {

	final int OPACITY = 0x5F000000;

	final int BACKGROUND = Color.BLACK;

	final int FOREGROUD = Color.BLUE;

	final PathEffect DASH = new DashPathEffect(new float[] { 5, 5, 5, 5 }, 1);

	void draw(Canvas canvas, Paint paint);

	void drawBoundary(Canvas canvas, Paint paint, float padding);

}
