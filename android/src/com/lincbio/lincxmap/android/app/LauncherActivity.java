package com.lincbio.lincxmap.android.app;

import java.io.File;
import java.util.List;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.utils.FileUtils;
import com.lincbio.lincxmap.android.utils.Xml2Sqlite;
import com.lincbio.lincxmap.android.view.FlowLayout;
import com.lincbio.lincxmap.pojo.Template;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * LincXmap Launcher UI
 * 
 * @author Johnson Lee
 * 
 */
public class LauncherActivity extends Activity implements Callback, Constants,
		Runnable {
	private static final String KEY_INITIALIZED = "initialized";

	static {
		System.loadLibrary("lincxmap");
	}

	private final Handler handler = new Handler(this);
	private final MenuManager menuManager = new MenuManager(this);

	private LinearLayout toolbar;
	private FlowLayout desktop;
	private SharedPreferences pref;
	private LayoutInflater layoutInflater;
	private DatabaseHelper dbhelper;
	private Xml2Sqlite xml2sqlite;

	@Override
	public void run() {
		SharedPreferences pref = getSharedPreferences(getClass().getName(),
				MODE_PRIVATE);

		try {
			this.xml2sqlite.parse(R.xml.products);

			Editor editor = pref.edit();
			editor.putBoolean(KEY_INITIALIZED, true);
			editor.commit();

			this.handler.sendMessage(this.handler.obtainMessage(0));
		} catch (Throwable t) {
			t.printStackTrace();
			this.handler.sendMessage(this.handler.obtainMessage(-1, t));
		} finally {
			dbhelper.close();
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what < 0) {
			Throwable t = (Throwable) msg.obj;
			Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
		}

		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launcher);

		this.dbhelper = new DatabaseHelper(this);
		this.xml2sqlite = new Xml2Sqlite(this, this.dbhelper);
		this.pref = getSharedPreferences(getClass().getName(), MODE_PRIVATE);
		this.toolbar = (LinearLayout) findViewById(R.id.toolbar);
		this.desktop = (FlowLayout) findViewById(R.id.desktop);
		this.layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		View item;
		ImageView icon;
		TextView label;

		// create tool bar
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(CATEGORY_TOOLBAR);
		PackageManager pm = getPackageManager();
		List<ResolveInfo> items = pm.queryIntentActivities(intent, 0);

		for (ResolveInfo ri : items) {
			item = this.layoutInflater.inflate(R.layout.toolbar_item,
					this.toolbar, false);
			label = (TextView) item.findViewById(R.id.toolbar_item_label);
			label.setText(ri.activityInfo.labelRes);
			this.toolbar.addView(item);
		}

		// query database to fetch detection templates
		List<Template> templates = this.dbhelper.getTemplates();
		templates.add(new Template(-1, "", 0, 0));

		for (Template template : templates) {
			item = this.layoutInflater.inflate(R.layout.desktop_item,
					this.desktop, false);
			icon = (ImageView) item.findViewById(R.id.desktop_item_icon);
			label = (TextView) item.findViewById(R.id.desktop_item_label);
			label.setText(template.getName());
			icon.setBackgroundResource(-1 == template.getId() ? R.drawable.ic_desktop_add
					: R.drawable.ic_desktop);
			this.desktop.addView(item);
		}

		File tmpdir = FileUtils.getTempDir();
		if (!tmpdir.exists()) {
			tmpdir.mkdirs();
		}

		if (this.pref.getBoolean(KEY_INITIALIZED, false)) {
			this.handler.sendMessage(this.handler.obtainMessage(0));
		} else {
			this.pref = PreferenceManager.getDefaultSharedPreferences(this);
			new Thread(this).start();
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

}
