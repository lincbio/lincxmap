package com.lincbio.lincxmap.android.view;

import java.util.ArrayList;
import java.util.List;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.utils.Toasts;
import com.lincbio.lincxmap.dip.CircleSampleSelector;
import com.lincbio.lincxmap.dip.Point;
import com.lincbio.lincxmap.dip.SampleSelector;
import com.lincbio.lincxmap.pojo.Product;
import com.lincbio.lincxmap.pojo.Template;
import com.lincbio.lincxmap.pojo.TemplateItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
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
	private final static PathEffect DASH = new DashPathEffect(new float[] { 5, 5, 5, 5 }, 1);

	private final Paint paint = new Paint();
	private final Point pos = new Point();
	private final Rect bgbounds0 = new Rect();
	private final Rect bgbounds1 = new Rect();
	private final Rect bgbounds2 = new Rect();
	private final Rect txtbounds = new Rect();
	private final List<CircleSampleSelector> selectors = new ArrayList<CircleSampleSelector>();
	private final Display display;
	private final DatabaseHelper dbHelper;
	private final SharedPreferences pref;

	private boolean dragging;
	private CircleSampleSelector selection;
	private Bitmap background;
	private Template template;

	public XmapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
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

	public void setBackground(Bitmap bg) {
		Bitmap bmp = this.background;
		this.background = bg;

		if (null != bmp) {
			bmp.recycle();
		}
		this.repaint();
	}

	public void setBackground(String path) {
		Bitmap bmp = this.background;
		this.background = BitmapFactory.decodeFile(path);

		if (null != bmp) {
			bmp.recycle();
		}
		this.repaint();
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template t) {
		this.template = t;
		this.selectors.clear();

		String dftGap = getContext().getString(R.string.default_selector_gap);
		String dftSize = getContext().getString(R.string.default_selector_size);
		String dftId = getContext().getString(R.string.sample_identifier_name);
		String sgap = this.pref.getString(KEY_SAMPLE_SELECTOR_GAP, dftGap);
		String sdim = this.pref.getString(KEY_SAMPLE_SELECTOR_SIZE, dftSize);
		String ssid = this.pref.getString(KEY_SAMPLE_IDENTIFIER, dftId);
		int gap = Integer.parseInt(sgap);
		int diameter = Integer.parseInt(sdim);
		int w = this.template.getColumnCount() * (diameter + gap) - gap;
		int h = this.template.getRowCount() * (diameter + gap) - gap;
		float radius = diameter / 2.0f;
		float dx = (this.display.getWidth() - w + diameter) / 2, x = dx;
		float dy = (this.display.getHeight() - h + diameter) / 2, y = dy;
		int rc = t.getRowCount();
		int cc = t.getColumnCount();

		CircleSampleSelector[][] circles = new CircleSampleSelector[rc][cc];
		for (int row = 0; row < t.getRowCount(); ++row) {
			y = dy + row * (diameter + gap);

			for (int col = 0; col < t.getColumnCount(); ++col) {
				x = dx + col * (diameter + gap);
				CircleSampleSelector c = new CircleSampleSelector(x, y, radius);
				circles[row][col] = c;
				this.selectors.add(c);
			}
		}

		int i = 0;
		String sin = getContext().getString(R.string.sample_identifier_name);
		for (TemplateItem ti : t.getItems()) {
			++i;

			try {
				CircleSampleSelector c = circles[ti.getX()][ti.getY()];
				if (ssid.equals(sin)) {
					c.setData(this.dbHelper.getProduct(ti.getProductId()));
				} else {
					c.setData(String.valueOf(i));
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				continue;
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
				onDraw(canvas);
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
		// TODO nothing
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
		int sw = this.background.getWidth();
		int sh = this.background.getHeight();
		float sr = sw * 1.0f / sh;
		float dr = _w * 1.0f / _h;
		float scaling = 1.0f;

		if (sr > dr) {
			dw = _w;
			dh = (int) (_w / sr);
		} else if (sr < dr) {
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
		this.bgbounds1.set(0 , 0, sw, sh);
		this.bgbounds2.set(dx, dy, dw + dx, dh + dy);
		this.paint.setColor(Color.BLACK);
		this.paint.setStyle(Style.FILL);

		// clear background with black
		canvas.drawRect(this.bgbounds0, this.paint);
		// draw background image
		canvas.drawBitmap(this.background, this.bgbounds1, this.bgbounds2, null);

		if (null != this.template) {
			this.paint.setColor(Color.GREEN);
			this.paint.setStyle(Style.STROKE);

			if (null != this.selection) {
				CircleSampleSelector c = this.selection;

				synchronized (this.selection) {
					this.paint.setColor((Color.BLUE & 0x00FFFFFF) | 0x3F000000);
					this.paint.setStyle(Style.FILL);
					canvas.drawCircle(c.getX(), c.getY(), c.radius, this.paint);
					this.paint.setColor(Color.GREEN);
					this.paint.setStyle(Style.STROKE);
				}
			}

			for (int i = 0; i < this.selectors.size(); ++i) {
				CircleSampleSelector c = this.selectors.get(i);
				c.setDeltaX(dx);
				c.setDeltaY(dy);
				c.setScaling(scaling);

				this.paint.setPathEffect(XmapView.DASH);
				canvas.drawCircle(c.getX(), c.getY(), c.radius, this.paint);
				this.paint.setPathEffect(null);
				Object data = c.getData();
				String text = data instanceof Product
							? ((Product) data).getName()
							: String.valueOf(i + 1);
				this.paint.getTextBounds(text, 0, text.length(), this.txtbounds);
				float top = c.getY() - this.paint.ascent() / 2.0f;
				float left = c.getX() - this.txtbounds.width() / 2.0f;
				canvas.drawText(text, left, top, this.paint);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			this.dragging = true;
			this.selection = null;
			for (CircleSampleSelector c : this.selectors) {
				if (c.contains(event.getX(), event.getY())) {
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
					this.selection.move(this.pos.x, this.pos.y, event.getX(),
							event.getY());
				}
			}
			break;
		default:
			break;
		}
		this.pos.set(event.getX(), event.getY());
		this.repaint();
		return true;
	}
}
