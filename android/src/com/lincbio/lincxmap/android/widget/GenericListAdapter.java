package com.lincbio.lincxmap.android.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GenericListAdapter<E> extends GenericObjectAdapter<E> {
	private static final String SERVICE = Context.LAYOUT_INFLATER_SERVICE;

	protected final int resId;
	protected final List<E> list = new ArrayList<E>();

	protected LayoutInflater inflater;

	public GenericListAdapter(Context context, int resId) {
		super(context);
		this.resId = resId;
		this.inflater = (LayoutInflater) context.getSystemService(SERVICE);
	}

	public GenericListAdapter(Context context, int resId, List<E> list) {
		this(context, resId);

		if (null != list && !list.isEmpty()) {
			this.list.addAll(list);
		}
	}

	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		if (position < 0 || position >= this.list.size())
			return null;

		return this.list.get(position);
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

	public void clear() {
		this.list.clear();
		super.notifyDataSetChanged();
	}

	public void reset(List<E> data) {
		this.list.clear();
		this.list.addAll(data);
		super.notifyDataSetChanged();
	}

}
