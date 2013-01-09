package com.lincbio.lincxmap.android.widget;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsSpinner;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

public class GenericListAdapter<E> extends BaseAdapter {
	private static final String SERVICE = Context.LAYOUT_INFLATER_SERVICE;

	protected final List<E> list = new ArrayList<E>();
	protected final Context context;
	protected final int resId;
	protected LayoutInflater inflater;

	public GenericListAdapter(Context context, int resId) {
		this.context = context;
		this.resId = resId;
		this.inflater = (LayoutInflater) context.getSystemService(SERVICE);
	}

	public GenericListAdapter(Context context, List<E> list, int resId) {
		this(context, resId);

		if (null != list && !list.isEmpty()) {
			this.list.addAll(list);
		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent);
	}

	protected View createViewFromResource(int position, View convertView,
			ViewGroup parent) {
		View view;

		if (convertView == null) {
			view = this.inflater.inflate(resId, parent, false);
		} else {
			view = convertView;
		}

		return adapt(view, this.list.get(position));
	}

	protected View adapt(View view, Object object) {
		Field[] fields = object.getClass().getDeclaredFields();
		byte[] adaptable = new byte[fields.length];

		Arrays.fill(adaptable, (byte) 1);

		for (int i = 0; i < fields.length; i++) {
			String tag = fields[i].getName();

			if (adaptable[i] == 0)
				continue;

			View v = view.findViewWithTag(tag);

			if (v == null) {
				adaptable[i] = 0;
				continue;
			}

			String name = fields[i].getName();
			name = Character.toUpperCase(name.charAt(0)) + name.substring(1);

			try {
				Method getter = object.getClass().getMethod("get" + name);

				if (null == getter)
					continue;

				setViewObject(v, getter.invoke(object));
			} catch (Throwable t) {
				t.printStackTrace();
				continue;
			}
		}

		return view;
	}

	public void setViewImage(ImageView v, int resId) {
		v.setImageResource(resId);
	}

	public void setViewImage(ImageView v, Bitmap bitmap) {
		v.setImageBitmap(bitmap);
	}

	public void setViewImage(ImageView v, Drawable drawable) {
		v.setImageDrawable(drawable);
	}

	public void setViewImage(ImageView v, Uri uri) {
		v.setImageURI(uri);
	}

	public void setViewImage(final ImageView v, String url) {
		// TODO
	}

	protected void setViewObject(View v, Object data) {
		if (v instanceof Checkable) {
			if (data instanceof Boolean) {
				((Checkable) v).setChecked((Boolean) data);
			} else if ("true".equals(data)) {
				((Checkable) v).setChecked(true);
			} else if ("false".equals(data)) {
				((Checkable) v).setChecked(false);
			} else if (v instanceof TextView) {
				setViewText((TextView) v, data.toString());
			}
		} else if (v instanceof TextView) {
			setViewText((TextView) v, data.toString());
		} else if (v instanceof ImageView) {
			if (data instanceof Integer || int.class.equals(data.getClass())) {
				setViewImage((ImageView) v, ((Integer) data).intValue());
			} else if (data instanceof Drawable) {
				setViewImage((ImageView) v, (Drawable) data);
			} else if (data instanceof Bitmap) {
				setViewImage((ImageView) v, (Bitmap) data);
			} else if (data instanceof Uri) {
				setViewImage((ImageView) v, (Uri) data);
			} else {
				setViewImage((ImageView) v, data.toString());
			}
		} else if (v instanceof AbsSpinner) {
			int position = -1;
			if (Integer.class.isAssignableFrom(data.getClass())) {
				position = (Integer) data;
			} else if (data instanceof String) {
				try {
					position = Integer.parseInt((String) data);
				} catch (NumberFormatException e) {
				}
			}
			((AbsSpinner) v).setSelection(position);
		}
	}

	public void setViewText(TextView v, String text) {
		v.setText(text);
	}

	public void clearData() {
		this.list.clear();
		super.notifyDataSetChanged();
	}

	public void appendData(List<E> data) {
		this.list.addAll(data);
		super.notifyDataSetChanged();
	}

	public void setData(List<E> data) {
		this.list.clear();
		this.list.addAll(data);
		super.notifyDataSetChanged();
	}

}
