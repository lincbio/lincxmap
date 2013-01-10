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
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Splash of LincXmap
 * 
 * @author Johnson Lee
 * 
 */
public class SplashActivity extends Activity implements Callback, Constants,
		Runnable {

	static {
		System.loadLibrary("lincxmap");
	}

	private static final String CLASS_NAME = SplashActivity.class.getName();
	private static final String KEY_INITIALIZED = "initialized";

	private final Handler handler = new Handler(this);
	private final DatabaseHelper dbhelper = new DatabaseHelper(this);
	private final Xml2Sqlite xml2sqlite = new Xml2Sqlite(this, this.dbhelper);
	private SharedPreferences pref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.splash);
		this.pref = getSharedPreferences(CLASS_NAME, MODE_PRIVATE);

		File tmpdir = FileUtils.getTempDir();
		if (!tmpdir.exists()) {
			tmpdir.mkdirs();
		}

		if (this.pref.getBoolean(KEY_INITIALIZED, false)) {
			this.handler.sendMessage(this.handler.obtainMessage(0));
		} else {
			new Thread(this).start();
		}
	}

	@Override
	public void run() {
		try {
			this.xml2sqlite.parse(R.xml.products);

			Editor editor = this.pref.edit();
			editor.putBoolean(KEY_INITIALIZED, true);
			editor.commit();
			this.handler.sendMessage(this.handler.obtainMessage(0));
		} catch (Throwable t) {
			t.printStackTrace();
			this.handler.sendMessage(this.handler.obtainMessage(-1, t));
		} finally {
			this.dbhelper.close();
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what < 0) {
			Throwable t = (Throwable) msg.obj;
			Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
			this.finish();
		} else {
			startActivity(new Intent(this, LauncherActivity.class));
		}

		return true;
	}

}
