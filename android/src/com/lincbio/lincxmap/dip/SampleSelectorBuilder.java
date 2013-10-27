package com.lincbio.lincxmap.dip;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.view.DrawableCircle;
import com.lincbio.lincxmap.android.view.DrawableRectangle;
import com.lincbio.lincxmap.android.view.DrawableShape;
import com.lincbio.lincxmap.pojo.Product;
import com.lincbio.lincxmap.pojo.Template;
import com.lincbio.lincxmap.pojo.TemplateItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

public class SampleSelectorBuilder implements Constants {
	private final Context context;
	private final DatabaseHelper dbHelper;
	private final View view;

	public SampleSelectorBuilder(Context context, View view) {
		this.context = context;
		this.dbHelper = new DatabaseHelper(context);
		this.view = view;
	}

	public List<SampleSelector> build(Template t) {
		int[] size = this.getSelectorSize();
		int gap = this.getSelectorGap();
		int width = size[0];
		int height = size[1];
		int w = t.getColumnCount() * (width + gap) - gap;
		int h = t.getRowCount() * (height + gap) - gap;
		float x, y;
		float dx = (this.view.getWidth() - w) / 2.0f;
		float dy = (this.view.getHeight() - h) / 2.0f;
		List<TemplateItem> items = t.getItems();
		List<SampleSelector> selectors = new ArrayList<SampleSelector>();

		for (int i = 0, row = 0; row < t.getRowCount(); ++row) {
			y = dy + row * (height + gap);

			for (int col = 0; col < t.getColumnCount(); ++col) {
				long pid = items.get(i++).getProductId();

				x = dx + col * (width + gap);
				selectors.add(this.buildSelector(pid, x, y, width, height));
			}
		}

		return selectors;
	}

	private SampleSelector buildSelector(long pid, float x, float y, float w,
			float h) {
		DrawableShape shape = this.buildShape(x, y, w, h);
		Product product = this.dbHelper.getProduct(pid);
		return new SampleSelector(product, shape);
	}

	private DrawableShape buildShape(float x, float y, float w, float h) {
		switch (this.getSelectorType()) {
		case 0:
			return new DrawableCircle(x, y, w / 2);
		case 1:
			return new DrawableRectangle(x, y, w, h);
		default:
			return null;
		}
	}

	private final SharedPreferences getSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(this.context);
	}

	private int getSelectorGap() {
		SharedPreferences sp = this.getSharedPreferences();
		String defGap = this.getDefaultSelectorGap();
		return Integer.parseInt(sp.getString(KEY_SAMPLE_SELECTOR_GAP, defGap));
	}

	private int[] getSelectorSize() {
		int[] dim = { 0, 0 };
		SharedPreferences sp = this.getSharedPreferences();
		String defSize = this.getDefaultSelectorSize();
		String defType = this.getDefaultSelectorType();
		String type = sp.getString(KEY_SAMPLE_SELECTOR_TYPE, defType);
		String size = sp.getString(KEY_SAMPLE_SELECTOR_SIZE, defSize);

		if (!defType.equals(type)) {
			Matcher matcher = PATTERN_SIZE.matcher(size);

			if (matcher.matches()) {
				dim[0] = Integer.parseInt(matcher.group(1));
				dim[1] = Integer.parseInt(matcher.group(2));
			}
		} else {
			dim[0] = dim[1] = Integer.parseInt(size);
		}

		return dim;
	}

	private int getSelectorType() {
		SharedPreferences sp = this.getSharedPreferences();
		String defType = this.getDefaultSelectorType();
		return Integer.parseInt(sp.getString(KEY_SAMPLE_SELECTOR_TYPE, defType));
	}

	private String getDefaultSelectorGap() {
		return this.context.getString(R.string.default_sample_selector_gap);
	}

	private String getDefaultSelectorSize() {
		return this.context.getString(R.string.default_sample_selector_size);
	}

	private String getDefaultSelectorType() {
		return this.context.getString(R.string.default_sample_selector_type);
	}

}
