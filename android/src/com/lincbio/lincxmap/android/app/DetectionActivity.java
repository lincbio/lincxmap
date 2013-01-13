package com.lincbio.lincxmap.android.app;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.utils.Toasts;
import com.lincbio.lincxmap.android.view.XmapView;
import com.lincbio.lincxmap.dip.SampleDetector;
import com.lincbio.lincxmap.dip.SampleDetector.ProgressListener;
import com.lincbio.lincxmap.pojo.History;
import com.lincbio.lincxmap.pojo.Profile;
import com.lincbio.lincxmap.pojo.Sample;
import com.lincbio.lincxmap.pojo.Template;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Detection UI
 * 
 * @author Johnson Lee
 * 
 */
public class DetectionActivity extends Activity implements Constants, Callback,
		Runnable, ProgressListener {
	private final DatabaseHelper dbhelper = new DatabaseHelper(this);
	private final Handler handler = new Handler(this);
	private final MenuManager menuManager = new MenuManager(this);
	private final SampleDetector detector = new SampleDetector(this);

	private XmapView xmapView;
	private ProgressDialog dlgProgress;
	private String image;
	private Profile profile;
	private Template template;
	private History history;

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case 0:
			if (this.dlgProgress.isShowing()) {
				this.dlgProgress.setProgress((Integer) msg.obj);
			}
			break;
		case 1:
			this.dlgProgress.dismiss();
			Intent intent = new Intent(this, DetectionResultActivity.class);
			intent.putExtra(PARAM_SAMPLE_LIST, (Serializable) msg.obj);
			intent.putExtra(PARAM_HISTORY_OBJECT, this.history);
			startActivity(intent);
			break;
		default:
			Toasts.show(this, msg.obj);
			break;
		}
		return true;
	}

	@Override
	public void run() {
		Bitmap bmp = BitmapFactory.decodeFile(this.image);
		if (null == bmp) {
			this.handler.sendMessage(this.handler.obtainMessage(-1,
					getString(R.string.msg_invalid_image)));
			return;
		}

		List<Sample> samples = this.detector.detect(bmp, this.template,
				this.xmapView.getSelectors());
		this.dbhelper.addHistory(this.history, samples);
		this.handler.sendMessage(this.handler.obtainMessage(1, samples));
		bmp.recycle();
		System.gc();
	}

	@Override
	public void onProgressChanged(int progress) {
		this.handler.sendMessage(this.handler.obtainMessage(0, progress));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.detection);

		this.dlgProgress = new ProgressDialog(this);
		this.dlgProgress.setCancelable(false);
		this.dlgProgress.setMax(SampleDetector.MAX_PROGRESS);
		this.dlgProgress.setMessage(getString(R.string.msg_detecting));
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
	public Object onRetainNonConfigurationInstance() {
		return this;
	}

	@Override
	protected void onStart() {
		super.onStart();

		Bundle bundle = getIntent().getExtras();

		if (null == bundle || !bundle.containsKey(PARAM_IMAGE_SOURCE)
				|| !bundle.containsKey(PARAM_TEMPLATE_OBJECT))
			finish();

		this.image = bundle.getString(PARAM_IMAGE_SOURCE);
		this.profile = (Profile) bundle.getSerializable(PARAM_PROFILE_OBJECT);
		this.template = (Template) bundle
				.getSerializable(PARAM_TEMPLATE_OBJECT);
		this.history = new History(this.profile.getId(),
				this.profile.getName(), this.template.getName(),
				new Date().toLocaleString());
		this.xmapView = (XmapView) findViewById(R.id.xmap_view);
		this.xmapView.setTemplate(this.template);
		this.xmapView.setBackground(this.image);
	}

	public void onButtonDetectClick(View v) {
		this.dlgProgress.setProgress(SampleDetector.MIN_PROGRESS);
		this.dlgProgress.show();
		new Thread(this).start();
	}
}
