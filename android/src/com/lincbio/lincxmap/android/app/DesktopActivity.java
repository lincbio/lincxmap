package com.lincbio.lincxmap.android.app;

import java.util.List;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.view.FlowLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * LincXmap Desktop UI
 * 
 * @auth Johnson Lee
 */
public class DesktopActivity extends Activity implements Constants {
    private final MenuManager menuManager = new MenuManager(this);

    private FlowLayout desktop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.desktop);
        this.desktop = (FlowLayout) findViewById(R.id.desktop);

		// create tool bar
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(CATEGORY_TOOLBAR);
		PackageManager pm = getPackageManager();
		List<ResolveInfo> items = pm.queryIntentActivities(intent, 0);

		for (int i = 0; i < items.size(); i++) {
			final ResolveInfo ri = items.get(i);
			final ActivityInfo ai = ri.activityInfo;
			final View item = this.getLayoutInflater().inflate(
					R.layout.desktop_item, this.desktop, false);

			item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClassName(DesktopActivity.this, ai.name);
					startActivity(intent);
				}
			});

			ImageView icon = (ImageView) item.findViewById(R.id.desktop_item_icon);
			TextView label = (TextView) item.findViewById(R.id.desktop_item_label);
			icon.setImageResource(ai.icon);
			label.setText(ai.labelRes);
			this.desktop.addView(item);
		}
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

    public void onButtonPreferenceClick(View view) {
    	Intent intent = new Intent(this, SettingsActivity.class);
    	this.startActivity(intent);
    }
}
