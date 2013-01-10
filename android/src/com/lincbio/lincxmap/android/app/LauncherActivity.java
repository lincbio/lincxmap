package com.lincbio.lincxmap.android.app;

import java.util.ArrayList;
import java.util.List;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * LincXmap Launcher UI
 * 
 * @author Johnson Lee
 * 
 */
@SuppressWarnings("deprecation")
public class LauncherActivity extends ActivityGroup implements Constants,
		OnPageChangeListener {
	private final List<View> pageList = new ArrayList<View>();
	private final MenuManager menuManager = new MenuManager(this);

	private LinearLayout toolbar;
	private ImageView cursor;
	private ViewPager pager;
	private int cursorWidth;
	private int cursorOffset;
	private int selectedIndex;

	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// TODO
	}

	@Override
	public void onPageSelected(int position) {
		int width = this.cursorOffset * 2 + this.cursorWidth;
		int from = Math.max(this.cursorOffset, width * this.selectedIndex);
		int to = width * position;
		Animation anim = new TranslateAnimation(from, to, 0, 0);
		anim.setFillAfter(true);
		anim.setDuration(300);
		this.cursor.startAnimation(anim);
		this.selectedIndex = position;
		this.pager.getAdapter().notifyDataSetChanged();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.launcher);

		this.cursor = (ImageView) findViewById(R.id.cursor);
		this.toolbar = (LinearLayout) findViewById(R.id.toolbar);
		this.pager = (ViewPager) findViewById(R.id.pages);

		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(), R.drawable.cursor, opts);
		this.cursorWidth = opts.outWidth;

		ImageView sep;
		ImageView icon;
		TextView label;

		// create tool bar
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(CATEGORY_TOOLBAR);
		PackageManager pm = getPackageManager();
		LocalActivityManager lam = getLocalActivityManager();
		List<ResolveInfo> items = pm.queryIntentActivities(intent, 0);

		for (int i = 0; i < items.size(); i++) {
			final int index = i;
			final ResolveInfo ri = items.get(i);
			final ActivityInfo ai = ri.activityInfo;
			final View item = this.getLayoutInflater().inflate(
					R.layout.toolbar_item, this.toolbar, false);

			item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					pager.setCurrentItem(index, true);
				}
			});

			icon = (ImageView) item.findViewById(R.id.toolbar_item_icon);
			label = (TextView) item.findViewById(R.id.toolbar_item_label);
			icon.setImageResource(ri.activityInfo.icon);
			label.setText(ri.activityInfo.labelRes);
			label.setTextColor(Color.DKGRAY);
			this.toolbar.addView(item);

			if (i < items.size() - 1) {
				sep = new ImageView(this);
				sep.setImageResource(R.drawable.bg_toolbar_sep);
				sep.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.MATCH_PARENT));
				this.toolbar.addView(sep);
			}

			intent = new Intent();
			intent.setClassName(ai.packageName, ai.name);
			Window win = lam.startActivity(ai.name, intent);
			this.pageList.add(win.getDecorView());
		}

		// initia pages
		this.pager.setAdapter(new PagerAdapter() {

			@Override
			public void destroyItem(ViewGroup view, int position, Object object) {
				((ViewPager) view).removeView(pageList.get(position));
			}

			@Override
			public Object instantiateItem(ViewGroup view, int position) {
				((ViewPager) view).addView(pageList.get(position), 0);
				return pageList.get(position);
			}

			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}

			@Override
			public int getCount() {
				return pageList.size();
			}

		});
		this.pager.setCurrentItem(0, true);
		this.pager.setOnPageChangeListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menuManager.createMenu(menu, R.menu.opt_common);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		this.menuManager.onMenuItemSelected(item);
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();

		int n, screenWidth;
		Matrix matrix = new Matrix();
		DisplayMetrics dm = new DisplayMetrics();

		n = this.pageList.size();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		this.cursorOffset = (screenWidth / n - this.cursorWidth) / 2;
		matrix.postTranslate(this.cursorOffset, 0);
		this.cursor.setImageMatrix(matrix);
	}

}
