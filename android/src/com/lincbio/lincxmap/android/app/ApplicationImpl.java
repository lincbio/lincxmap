package com.lincbio.lincxmap.android.app;

import java.io.File;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.utils.FileUtils;
import com.lincbio.lincxmap.android.utils.Xml2Sqlite;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler.Callback;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class ApplicationImpl extends Application implements Callback,
		Constants, Runnable {
	private static final String KEY_INITIALIZED = "initialized";

	static {
		System.loadLibrary("lincxmap");
	}

	private final Handler handler = new Handler(this);
	private final DatabaseHelper dbhelper = new DatabaseHelper(this);
	private final Xml2Sqlite xml2sqlite = new Xml2Sqlite(this, this.dbhelper);

	@Override
	public void run() {
		String clazz = getClass().getName();
		SharedPreferences pref = getSharedPreferences(clazz, MODE_PRIVATE);

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
			this.dbhelper.close();
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
	public void onCreate() {
		super.onCreate();

		String clazz = getClass().getName();
		SharedPreferences pref = getSharedPreferences(clazz, MODE_PRIVATE);
		File tmpdir = FileUtils.getTempDir();

		if (!tmpdir.exists()) {
			tmpdir.mkdirs();
		}

		if (pref.getBoolean(KEY_INITIALIZED, false)) {
			this.handler.sendMessage(this.handler.obtainMessage(0));
		} else {
			new Thread(this).start();
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}
