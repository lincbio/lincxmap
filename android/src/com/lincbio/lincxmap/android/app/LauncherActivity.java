package com.lincbio.lincxmap.android.app;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.view.FlowLayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

/**
 * LincXmap Launcher UI
 * 
 * @author Johnson Lee
 *
 */
public class LauncherActivity extends Activity {
	private LinearLayout navbar;
	private FlowLayout desktop;
	private MenuManager menuManager = new MenuManager(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        this.navbar = (LinearLayout) findViewById(R.id.navbar);
        this.desktop = (FlowLayout) findViewById(R.id.desktop);
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
    
}
