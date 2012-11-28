package com.lincbio.lincxmap.android.utils;

import android.content.Context;
import android.widget.Toast;

public class Toasts {

	public static void show(Context ctx, Object obj) {
		Toast.makeText(ctx, obj.toString(), Toast.LENGTH_LONG).show();
	}
	
	public static void show(Context ctx, Throwable t) {
		Toast.makeText(ctx, t.getMessage(), Toast.LENGTH_LONG).show();
	}
	
	public static void show(Context ctx, int resId) {
		Toast.makeText(ctx, ctx.getString(resId), Toast.LENGTH_LONG).show();
	}
	
}
