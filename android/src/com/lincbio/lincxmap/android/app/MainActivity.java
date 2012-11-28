package com.lincbio.lincxmap.android.app;

import java.io.File;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.utils.FileUtils;
import com.lincbio.lincxmap.android.utils.Xml2Sqlite;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.preference.PreferenceManager;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * LincXmap Application Main UI
 * 
 * @author Johnson Lee
 * 
 */
public class MainActivity extends Activity implements Callback, Constants,
		Runnable {

	static {
		System.loadLibrary("lincxmap");
	}

	private static final String CLASS_NAME = MainActivity.class.getName();
	private static final String KEY_INITIALIZED = "initialized";

	private Handler handler = new Handler(this);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		SharedPreferences pref = getSharedPreferences(CLASS_NAME, MODE_PRIVATE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		File tmpdir = FileUtils.getTempDir();
		if (!tmpdir.exists()) {
			tmpdir.mkdirs();
		}

		if (pref.getBoolean(KEY_INITIALIZED, false)) {
			this.handler.sendMessage(this.handler.obtainMessage(0));
		} else {
			pref = PreferenceManager.getDefaultSharedPreferences(this);
			new Thread(this).start();
		}
	}

	@Override
	public void run() {
		DatabaseHelper dbhelper = new DatabaseHelper(this);
		Xml2Sqlite xml2sqlite = new Xml2Sqlite(this, dbhelper);
		SharedPreferences pref = getSharedPreferences(CLASS_NAME, MODE_PRIVATE);

		try {
			xml2sqlite.parse(R.xml.products);

			Editor editor = pref.edit();
			editor.putBoolean(KEY_INITIALIZED, true);
			editor.commit();
			handler.sendMessage(handler.obtainMessage(0));
		} catch (Throwable t) {
			t.printStackTrace();
			handler.sendMessage(handler.obtainMessage(-1, t));
		} finally {
			dbhelper.close();
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what < 0) {
			Throwable t = (Throwable) msg.obj;
			Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
			this.finish();
		} else {
			startActivity(new Intent(this, TemplateListActivity.class));
		}

		return true;
	}

}
