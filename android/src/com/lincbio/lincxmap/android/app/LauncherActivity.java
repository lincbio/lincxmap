package com.lincbio.lincxmap.android.app;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.utils.FileUtils;
import com.lincbio.lincxmap.android.utils.Toasts;
import com.lincbio.lincxmap.android.utils.Xml2Sqlite;
import com.lincbio.lincxmap.android.view.FlowLayout;
import com.lincbio.lincxmap.pojo.ImageSource;
import com.lincbio.lincxmap.pojo.Template;
import com.lincbio.lincxmap.pojo.TemplateItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
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
		Runnable, DialogInterface.OnClickListener {
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
	private Template selectedTemplate;
	private String selectedImage;

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

	public void onClick(DialogInterface dialog, int which) {
		Intent intent = null;

		switch (which) {
		case ImageSource.IMAGE_SOURCE_CAPTURE:
			File image = FileUtils.newTempFile(".jpg");
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
			this.selectedImage = image.toString();
			break;
		case ImageSource.IMAGE_SOURCE_GALLERY:
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			break;
		}

		if (null == intent)
			return;

		startActivityForResult(intent, which);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			switch (requestCode) {
			case ImageSource.IMAGE_SOURCE_CAPTURE:
				break;
			case ImageSource.IMAGE_SOURCE_GALLERY:
				Uri uri = data.getData();
				String scheme = uri.getScheme();

				if ("file".equalsIgnoreCase(scheme)) {
					this.selectedImage = uri.getPath();
				} else if ("content".equalsIgnoreCase(scheme)) {
					ContentResolver cr = getContentResolver();
					Cursor c = cr.query(uri, null, null, null, null);

					if (null == c)
						break;

					try {
						if (c.moveToNext()) {
							this.selectedImage = c.getString(1);
						}
					} finally {
						c.close();
					}
				}
				break;
			default:
				return;
			}

			if (null == this.selectedImage)
				return;
			
			long id = this.selectedTemplate.getId();
			List<TemplateItem> items = this.dbhelper.getTemplateItems(id);
			this.selectedTemplate.getItems().clear();
			this.selectedTemplate.getItems().addAll(items);
			
			Intent intent = new Intent(this, DetectionActivity.class);
			intent.putExtra(PARAM_IMAGE_SOURCE, this.selectedImage);
			intent.putExtra(PARAM_TEMPLATE_OBJECT, this.selectedTemplate);
			startActivity(intent);
			break;
		default:
			this.finish();
			break;
		}
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

		ImageView sep;
		ImageView icon;
		TextView label;

		// create tool bar
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(CATEGORY_TOOLBAR);
		PackageManager pm = getPackageManager();
		List<ResolveInfo> items = pm.queryIntentActivities(intent, 0);

		for (Iterator<ResolveInfo> it = items.iterator(); it.hasNext();) {
			final ResolveInfo ri = (ResolveInfo) it.next();
			final View item = this.layoutInflater.inflate(
					R.layout.toolbar_item, this.toolbar, false);
			item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ActivityInfo ai = ri.activityInfo;
					Intent intent = new Intent();
					intent.setClassName(ai.packageName, ai.name);
					startActivity(intent);
				}
			});

			icon = (ImageView) item.findViewById(R.id.toolbar_item_icon);
			label = (TextView) item.findViewById(R.id.toolbar_item_label);
			icon.setImageResource(ri.activityInfo.icon);
			label.setText(ri.activityInfo.labelRes);
			label.setTextColor(Color.DKGRAY);
			this.toolbar.addView(item);

			if (it.hasNext()) {
				sep = new ImageView(this);
				sep.setImageResource(R.drawable.bg_toolbar_sep);
				sep.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.MATCH_PARENT));
				this.toolbar.addView(sep);
			}
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

	@Override
	protected void onStart() {
		super.onStart();

		ImageView icon;
		TextView label;
		final Context ctx = this;

		this.desktop.removeAllViews();
		
		// query database to fetch detection templates
		String text = getString(R.string.label_toolbar_new);
		List<Template> templates = this.dbhelper.getTemplates();
		templates.add(new Template(-1, text, 0, 0));

		for (final Template tpl : templates) {
			final View item = this.layoutInflater.inflate(
					R.layout.desktop_item, this.desktop, false);
			final ImageView del = (ImageView) item
					.findViewById(R.id.desktop_item_del);

			icon = (ImageView) item.findViewById(R.id.desktop_item_icon);
			label = (TextView) item.findViewById(R.id.desktop_item_label);
			label.setText(tpl.getName());

			if (-1 == tpl.getId()) {
				icon.setImageResource(R.drawable.ic_desktop_add);
				item.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Class<?> cls = TemplateSpecActivity.class;
						startActivity(new Intent(ctx, cls));
					}
				});
			} else {
				del.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							dbhelper.deleteTemplate(tpl.getId());
							desktop.removeView(item);
						} catch (Throwable t) {
							Toasts.show(ctx, t);
						}
					}
				});
				icon.setImageResource(R.drawable.ic_desktop);
				item.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						View del = v.findViewById(R.id.desktop_item_del);
						del.setVisibility(View.VISIBLE);
						return true;
					}
				});
				item.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (View.VISIBLE == del.getVisibility()) {
							del.setVisibility(View.GONE);
							return;
						}

						CharSequence[] choices = getResources().getTextArray(
								R.array.array_image_sources);
						LauncherActivity.this.selectedTemplate = tpl;
						new AlertDialog.Builder(ctx)
								.setTitle(R.string.title_choose_image)
								.setIcon(android.R.drawable.ic_dialog_alert)
								.setItems(choices, LauncherActivity.this)
								.show();
					}
				});
			}

			this.desktop.addView(item);
		}
	}

}
