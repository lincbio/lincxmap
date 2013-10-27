package com.lincbio.lincxmap.android.view;

import java.util.List;

import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.utils.Bitmaps;
import com.lincbio.lincxmap.dip.SampleSelector;
import com.lincbio.lincxmap.dip.SampleSelectorBuilder;
import com.lincbio.lincxmap.geom.Shape;
import com.lincbio.lincxmap.pojo.Template;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class XmapView extends SurfaceView implements Callback, Constants {
	private final static int GAP = 5;

	private final Paint paint = new Paint();

	/**
	 * Cursor position
	 */
	private final Point pos = new Point();

	/**
	 * Screen bounds
	 */
	private final Rect bgbounds0 = new Rect();

	/**
	 * Image bounds
	 */
	private final Rect bgbounds1 = new Rect();

	/**
	 * Scaled image bounds
	 */
	private final Rect bgbounds2 = new Rect();

	/**
	 * Text bounds
	 */
	private final Rect txtbounds = new Rect();

	private final SampleSelectorBuilder builder;

	private boolean dragging;
	private SampleSelector selection;
	/**
	 * Original image bounds
	 */
	private Rect bgBounds;
	private String bgpath;
	/**
	 * Scaled image as background
	 */
	private Bitmap background;
	private Template template;
	private Painter painter;
	private Thread paintThread;
	private List<SampleSelector> selectors;

	public XmapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		this.builder = new SampleSelectorBuilder(context, this);
		this.paint.setAntiAlias(true);
		this.painter = new Painter(getHolder(), this);
		this.getHolder().addCallback(this);
		this.setFocusable(true);
	}

	public XmapView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public XmapView(Context context) {
		this(context, null);
	}

	public void setBackground(String path) {
		this.bgpath = path;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template t) {
		this.template = t;
	}

	public SampleSelector[] getSelectors() {
		SampleSelector[] selectors = new SampleSelector[this.selectors.size()];
		return this.selectors.toArray(selectors);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Bitmap bmp = this.background;
		this.bgBounds = Bitmaps.getBounds(this.bgpath);
		this.background = Bitmaps.load(this.bgpath, width, height);

		if (null != bmp && !bmp.isRecycled()) {
			bmp.recycle();
			bmp = null;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (null == this.paintThread || !this.paintThread.isAlive()) {
			this.painter.setRunning(true);
			this.paintThread = new Thread(this.painter);
			this.paintThread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (null == this.paintThread || !this.paintThread.isAlive())
			return;

		this.painter.setRunning(false);

		while (true) {
			try {
				this.paintThread.join();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (null != this.background && !this.background.isRecycled()) {
			this.background.recycle();
			this.background = null;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (null == this.background)
			return;

		int dw, dh, dx, dy;
		int _w = this.getWidth();
		int _h = this.getHeight();
		int sw = this.bgBounds.width();
		int sh = this.bgBounds.height();
		int rw = this.background.getWidth();
		int rh = this.background.getHeight();
		float sr = sw * 1.0f / sh;
		float dr = _w * 1.0f / _h;
		float scaling = 1.0f;

		if (sr > dr) { // vertical
			dw = _w;
			dh = (int) (_w / sr);
		} else if (sr < dr) { // landscape
			dw = (int) (_h * sr);
			dh = _h;
		} else {
			dw = _w;
			dh = _h;
		}

		scaling = dw * 1.0f / sw;
		dx = (int) ((_w - dw) / 2.0f);
		dy = (int) ((_h - dh) / 2.0f);

		this.bgbounds0.set(0, 0, _w, _h);
		this.bgbounds1.set(0, 0, rw, rh);
		this.bgbounds2.set(dx, dy, dw + dx, dh + dy);

		// clear background with black
		canvas.clipRect(this.bgbounds2);
		// draw background image
		canvas.drawBitmap(this.background, this.bgbounds1, this.bgbounds2, null);

		if (null == this.template)
			return;

		synchronized (this) {
			for (int i = 0; i < this.selectors.size(); ++i) {
				SampleSelector c = this.selectors.get(i);
				c.setDeltaX(dx);
				c.setDeltaY(dy);
				c.setScaling(scaling);

				// draw sample selector
				DrawableShape shape = (DrawableShape) c.shape;
				shape.drawBoundary(canvas, this.paint, XmapView.GAP);
				shape.draw(canvas, this.paint);

				// draw sample name
				float x = c.shape.getX();
				float y = c.shape.getY();
				String text = c.product.getName();
				int nchar = text.length();
				this.paint.getTextBounds(text, 0, nchar, this.txtbounds);
				x += (c.shape.getWidth() - this.txtbounds.width()) / 2.0f;
				y += (c.shape.getHeight() - this.paint.ascent()) / 2.0f;
				canvas.drawText(text, x, y, this.paint);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		this.setMeasuredDimension(parentWidth, parentHeight);

		synchronized (this) {
			this.selectors = this.builder.build(this.template);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			this.dragging = true;
			this.selection = null;
			for (SampleSelector c : this.selectors) {
				if (c.shape.contains(event.getX(), event.getY())) {
					this.selection = c;
					break;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			this.dragging = false;
			break;
		case MotionEvent.ACTION_MOVE:
			if (this.dragging && null != this.selection) {
				Shape shape = this.selection.shape;
				float dx = event.getX() - this.pos.x;
				float dy = event.getY() - this.pos.y;
				float left = shape.getX();
				float top = shape.getY();
				float right = shape.getWidth() + left;
				float bottom = shape.getHeight() + top;

				if (left + dx < this.bgbounds2.left)
					dx = this.bgbounds2.left - left;
				if (right + dx > this.bgbounds2.right)
					dx = this.bgbounds2.right - right;
				if (top + dy < this.bgbounds2.top)
					dy = this.bgbounds2.top - top;
				if (bottom + dy > this.bgbounds2.bottom)
					dy = this.bgbounds2.bottom - bottom;

				synchronized (this.selection) {
					this.selection.shape.move(dx, dy);
				}
			}
			break;
		default:
			break;
		}

		this.pos.set((int) event.getX(), (int) event.getY());
		return true;
	}

	private static class Painter implements Runnable {
		private SurfaceHolder holder;
		private XmapView view;
		private boolean running;

		public Painter(SurfaceHolder holder, XmapView view) {
			this.holder = holder;
			this.view = view;
		}

		public void setRunning(boolean running) {
			this.running = running;
		}

		@Override
		public void run() {
			Canvas canvas;

			while (this.running) {
				canvas = null;

				try {
					canvas = this.holder.lockCanvas();

					synchronized (this.holder) {
						this.view.onDraw(canvas);
					}
				} finally {
					if (null != canvas) {
						this.holder.unlockCanvasAndPost(canvas);
					}
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		}
	}
}
