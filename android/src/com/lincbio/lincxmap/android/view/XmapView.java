package com.lincbio.lincxmap.android.view;

import java.util.ArrayList;
import java.util.List;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.utils.Bitmaps;
import com.lincbio.lincxmap.android.utils.Toasts;
import com.lincbio.lincxmap.dip.SampleSelector;
import com.lincbio.lincxmap.pojo.Product;
import com.lincbio.lincxmap.pojo.Template;
import com.lincbio.lincxmap.pojo.TemplateItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;
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

	/**
	 * Selector list
	 */
	private final List<SampleSelector> selectors = new ArrayList<SampleSelector>();
	private final Display display;
	private final DatabaseHelper dbHelper;
	private final SharedPreferences pref;

	private boolean dragging;
	private SampleSelector selection;
	private Rect bgBounds;
	private String bgpath;
	private Bitmap background;
	private Template template;

	public XmapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		this.display = wm.getDefaultDisplay();
		this.dbHelper = new DatabaseHelper(context);
		this.pref = PreferenceManager.getDefaultSharedPreferences(context);
		this.paint.setAntiAlias(true);
		getHolder().addCallback(this);
		setFocusable(true);
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
		this.selectors.clear();

		String dftGap = getContext().getString(R.string.default_selector_gap);
		String dftSize = getContext().getString(R.string.default_selector_size);
		String sgap = this.pref.getString(KEY_SAMPLE_SELECTOR_GAP, dftGap);
		String sdim = this.pref.getString(KEY_SAMPLE_SELECTOR_SIZE, dftSize);
		int gap = Integer.parseInt(sgap);
		int diameter = Integer.parseInt(sdim);
		int w = this.template.getColumnCount() * (diameter + gap) - gap;
		int h = this.template.getRowCount() * (diameter + gap) - gap;
		float radius = diameter / 2.0f;
		float dx = (this.display.getWidth() - w + diameter) / 2, x = dx;
		float dy = (this.display.getHeight() - h + diameter) / 2, y = dy;
		int rc = t.getRowCount();
		int cc = t.getColumnCount();

		List<TemplateItem> items = t.getItems();
		SampleSelector[][] circles = new SampleSelector[rc][cc];

		for (int i = 0, row = 0; row < t.getRowCount(); ++row) {
			y = dy + row * (diameter + gap);

			for (int col = 0; col < t.getColumnCount(); ++col) {
				x = dx + col * (diameter + gap);

				TemplateItem ti = items.get(i++);
				DrawableShape shape = new DrawableCircle(x, y, radius);
				Product product = this.dbHelper.getProduct(ti.getProductId());
				SampleSelector c = new SampleSelector(product, shape);
				circles[row][col] = c;
				this.selectors.add(c);
			}
		}
	}

	public SampleSelector[] getSelectors() {
		SampleSelector[] selectors = new SampleSelector[this.selectors.size()];
		return this.selectors.toArray(selectors);
	}

	public final void repaint() {
		Canvas canvas = null;
		SurfaceHolder holder = getHolder();

		try {
			if (null == (canvas = holder.lockCanvas()))
				return;

			synchronized (holder) {
				draw(canvas);
			}
		} catch (Throwable t) {
			Toasts.show(getContext(), t);
		} finally {
			if (null != canvas) {
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Bitmap bmp = this.background;
		this.bgBounds = Bitmaps.getBounds(this.bgpath);
		this.background = Bitmaps.load(this.bgpath, width, height);

		if (null != bmp) {
			bmp.recycle();
		}
		this.repaint();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		this.repaint();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		synchronized (holder) {
			Bitmap bmp = this.background;
			this.background = null;

			if (null != bmp) {
				bmp.recycle();
			}

			this.selection = null;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
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

		// draw all selectors
		for (int i = 0; i < this.selectors.size(); ++i) {
			SampleSelector c = this.selectors.get(i);
			DrawableShape shape = (DrawableShape) c.shape;
			c.setDeltaX(dx);
			c.setDeltaX(dy);
			c.setScaling(scaling);

			shape.drawBoundary(canvas, this.paint, XmapView.GAP);
			shape.draw(canvas, this.paint);

			// draw sample name
			String text = c.product.getName();
			this.paint.getTextBounds(text, 0, text.length(), this.txtbounds);
			float top = c.shape.getY() - this.paint.ascent() / 2.0f;
			float left = c.shape.getX() - this.txtbounds.width() / 2.0f;
			canvas.drawText(text, left, top, this.paint);
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
				synchronized (this.selection) {
					this.selection.shape.move(event.getX() - this.pos.x,
							event.getY() - this.pos.y);
				}
			}
			break;
		default:
			break;
		}

		this.pos.set((int) event.getX(), (int) event.getY());
		this.repaint();
		return true;
	}
}
