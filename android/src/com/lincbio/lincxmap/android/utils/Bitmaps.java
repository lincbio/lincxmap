package com.lincbio.lincxmap.android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;

public class Bitmaps {

	private Bitmaps() {
	}

	private static int calculateInSampleSize(Options opts, int w, int h) {
		final int height = opts.outHeight;
		final int width = opts.outWidth;

		int inSampleSize = 1;

		if (height > h || width > w) {
			int heightRatio = Math.round((float) height / (float) h);
			int widthRatio = Math.round((float) width / (float) w);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public static Bitmap load(String path, int width, int height) {
		if (width <= 0 || height <= 0)
			return null;
		
		final Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		opts.inJustDecodeBounds = false;
		opts.inSampleSize = calculateInSampleSize(opts, width, height);

		return BitmapFactory.decodeFile(path, opts);
	}

	public static Rect getBounds(String path) {
		final Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);

		return new Rect(0, 0, opts.outWidth, opts.outHeight);
	}
}
