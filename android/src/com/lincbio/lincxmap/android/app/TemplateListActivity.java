package com.lincbio.lincxmap.android.app;

import java.io.File;
import java.io.Serializable;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.utils.FileUtils;
import com.lincbio.lincxmap.pojo.ImageSource;
import com.lincbio.lincxmap.pojo.Template;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Detection Template List UI
 * 
 * @author Johnson Lee
 * 
 */
public class TemplateListActivity extends ListActivity implements
		OnItemClickListener, Constants {
	private String image;
	private Serializable template;
	private DatabaseHelper dbHelper = new DatabaseHelper(this);
	private MenuManager menuManager = new MenuManager(this) {
		private final Context context = TemplateListActivity.this;

		@Override
		@SuppressWarnings("unchecked")
		public void onMenuItemSelected(MenuItem item) {
			super.onMenuItemSelected(item);

			Intent intent = item.getIntent();
			if (null != intent) {
				startActivity(intent);
				return;
			}

			Template template = null;
			ArrayAdapter<Template> adapter = (ArrayAdapter<Template>) getListAdapter();
			AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();

			if (null != menuInfo) {
				Object obj = getListView().getItemAtPosition(menuInfo.position);

				if (obj instanceof Template) {
					template = (Template) obj;
				}
			}

			switch (item.getItemId()) {
			case R.id.menu_new_template:
				intent = new Intent(context, TemplateSpecActivity.class);
				startActivity(intent);
				break;
			case R.id.menu_mod_template:
				intent = new Intent(context, TemplateSpecActivity.class);
				intent.putExtra(PARAM_TEMPLATE_OBJECT, template);
				startActivity(intent);
				break;
			case R.id.menu_del_template:
				dbHelper.deleteTemplate(template.getId());
				adapter.remove(template);
				adapter.notifyDataSetChanged();
				break;
			}
		}

	};
	
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
					this.image = uri.getPath();
				} else if ("content".equalsIgnoreCase(scheme)) {
					ContentResolver cr = getContentResolver();
					Cursor c = cr.query(uri, null, null, null, null);

					if (null == c)
						break;

					try {
						if (c.moveToNext()) {
							this.image = c.getString(1);
						}
					} finally {
						c.close();
					}
				}
				break;
			default:
				return;
			}

			if (null == this.image)
				return;

			Intent intent = new Intent(this, DetectionActivity.class);
			intent.putExtra(PARAM_IMAGE_SOURCE, this.image);
			intent.putExtra(PARAM_TEMPLATE_OBJECT, this.template);
			startActivity(intent);
			break;
		default:
			this.finish();
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListView lstView = getListView();
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View header = inflater.inflate(android.R.layout.simple_list_item_2,
				null);
		TextView text1 = (TextView) header.findViewById(android.R.id.text1);
		TextView text2 = (TextView) header.findViewById(android.R.id.text2);

		text1.setText(R.string.label_new_template);
		text2.setText(R.string.label_new_template_detail);

		lstView.addHeaderView(header);
		lstView.setOnItemClickListener(this);
		lstView.setCacheColorHint(Color.TRANSPARENT);
		registerForContextMenu(lstView);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		int position = ((AdapterContextMenuInfo) menuInfo).position;
		Object obj = ((ListView) v).getItemAtPosition(position);

		if (!(obj instanceof Template))
			return;

		menu.setHeaderTitle(((Template) obj).getName());
		this.menuManager.createMenu(menu, R.menu.ctx_template_list);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		this.menuManager.onMenuItemSelected(item);
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menuManager.createMenu(menu, R.menu.opt_template_list);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Object obj = parent.getItemAtPosition(position);

		if (obj instanceof Template) {
			this.template = (Template) obj;
			CharSequence[] choices = getResources().getTextArray(
					R.array.array_image_sources);
			new AlertDialog.Builder(this).setTitle(R.string.title_choose_image)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setItems(choices, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = null;

							switch (which) {
							case ImageSource.IMAGE_SOURCE_CAPTURE:
								File f = FileUtils.newTempFile(".jpg");
								intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								intent.putExtra(MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(f));
								TemplateListActivity.this.image = f.toString();
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
					}).show();
		} else {
			startActivity(new Intent(this, TemplateSpecActivity.class));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		this.menuManager.onMenuItemSelected(item);
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		setListAdapter(new ArrayAdapter<Template>(this,
				android.R.layout.simple_list_item_single_choice,
				this.dbHelper.getTemplates()));
	}

	@Override
	protected void onDestroy() {
		this.dbHelper.close();
		super.onDestroy();
	}

}
