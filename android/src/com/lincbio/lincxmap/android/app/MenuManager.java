package com.lincbio.lincxmap.android.app;

import com.lincbio.lincxmap.R;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class MenuManager {
	private Activity delegate;

	public MenuManager(Activity delegate) {
		this.delegate = delegate;
	}

	public final void createMenu(Menu menu, int menuResId) {
		this.delegate.getMenuInflater().inflate(menuResId, menu);
	}

	public void onMenuItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_about:
			item.setIntent(new Intent(this.delegate, AboutActivity.class));
			break;
		case R.id.menu_settings:
			item.setIntent(new Intent(this.delegate, SettingsActivity.class));
			break;
		default:
			break;
		}
	}

}
