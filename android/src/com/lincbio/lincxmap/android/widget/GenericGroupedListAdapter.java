package com.lincbio.lincxmap.android.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;

public class GenericGroupedListAdapter<E> extends GenericListAdapter<E>
		implements ExpandableListAdapter {
	private final Map<Object, List<E>> groups = new HashMap<Object, List<E>>();
	private final int childResId;
	private final Groupable<E> groupBy;

	public interface Groupable<E> {
		public Object getGroupId(E e);
	}

	public GenericGroupedListAdapter(Context context, Groupable<E> groupBy,
			int parentResId, int childResId) {
		super(context, parentResId);
		this.groupBy = groupBy;
		this.childResId = childResId;
	}

	public GenericGroupedListAdapter(Context context, List<E> list,
			Groupable<E> groupBy, int parentResId, int childResId) {
		this(context, groupBy, parentResId, childResId);

		for (E e : list) {
			Object key = groupBy.getGroupId(e);

			if (!this.groups.containsKey(key)) {
				List<E> subList = new ArrayList<E>();
				this.groups.put(key, subList);
			}

			this.groups.get(key).add(e);
		}
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return this.groups.get(getGroup(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View v;

		if (convertView == null) {
			v = this.inflater.inflate(this.childResId, parent, false);
		} else {
			v = convertView;
		}

		return this.adapt(v, getChild(groupPosition, childPosition));
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.groups.get(getGroup(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.groups.keySet().toArray()[groupPosition];
	}

	@Override
	public int getGroupCount() {
		return this.groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View v;

		if (convertView == null) {
			v = this.inflater.inflate(this.resId, parent, false);
		} else {
			v = convertView;
		}

		return this.adapt(v, getGroup(groupPosition));
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public long getCombinedChildId(long groupId, long childId) {
		return 0x8000000000000000L | ((groupId & 0x7FFFFFFF) << 32)
				| (childId & 0xFFFFFFFF);
	}

	@Override
	public long getCombinedGroupId(long groupId) {
		return (groupId & 0x7FFFFFFF) << 32;
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		// TODO
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		// TODO
	}

	@Override
	public void clearData() {
		this.groups.clear();
		super.notifyDataSetChanged();
	}

	@Override
	public void setData(List<E> data) {
		this.groups.clear();

		for (E e : data) {
			Object key = this.groupBy.getGroupId(e);

			if (!this.groups.containsKey(key)) {
				List<E> subList = new ArrayList<E>();
				this.groups.put(key, subList);
			}

			this.groups.get(key).add(e);
		}

		super.notifyDataSetChanged();
	}

}
