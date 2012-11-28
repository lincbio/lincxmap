package com.lincbio.lincxmap.android.app;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.utils.Toasts;
import com.lincbio.lincxmap.pojo.Template;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class TemplateSpecActivity extends Activity implements Constants {
	private DatabaseHelper dbHelper = new DatabaseHelper(this);
	private MenuManager menuManager = new MenuManager(this);

	private EditText txtName;
	private EditText txtRows;
	private EditText txtCols;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.template_spec);

		this.txtName = (EditText) findViewById(R.id.template_name);
		this.txtRows = (EditText) findViewById(R.id.template_rows);
		this.txtCols = (EditText) findViewById(R.id.template_cols);

		Bundle extras = getIntent().getExtras();
		if (null == extras || !extras.containsKey(PARAM_TEMPLATE_OBJECT))
			return;

		Template tpl = (Template) extras.getSerializable(PARAM_TEMPLATE_OBJECT);
		this.txtName.setText(tpl.getName());
		this.txtRows.setText(String.valueOf(tpl.getRowCount()));
		this.txtCols.setText(String.valueOf(tpl.getColumnCount()));
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
	protected void onDestroy() {
		this.dbHelper.close();
		super.onDestroy();
	}

	public void onButtonNextClicked(View view) {
		try {
			startActivity(getNextIntent());
		} catch (Throwable t) {
			Toasts.show(this, t.getMessage());
		}
	}

	public void onButtonFinishClicked(View view) {
		try {
			getNextIntent();
			finish();
		} catch (Throwable t) {
			Toasts.show(this, t.getMessage());
		}
	}

	public void onButtonPrevClicked(View view) {
		this.finish();
	}

	private Intent getNextIntent() {
		Template tpl = null;
		Bundle extras = getIntent().getExtras();
		String name = this.txtName.getText().toString();
		int rows = Integer.parseInt(this.txtRows.getText().toString());
		int cols = Integer.parseInt(this.txtCols.getText().toString());
		Intent intent = new Intent(this, TemplateEditorActivity.class);

		if (null == extras || !extras.containsKey(PARAM_TEMPLATE_OBJECT)) {
			if (null == name || name.length() <= 0)
				Toasts.show(this, R.string.msg_empty_template_name);
			tpl = new Template(0, name, rows, cols);
			tpl.setId(this.dbHelper.newTemplate(tpl));
		} else {
			tpl = (Template) extras.getSerializable(PARAM_TEMPLATE_OBJECT);

			boolean updated = false;
			
			if (!name.equals(tpl.getName())) {
				tpl.setName(name);
				updated = true;
			}

			if (rows != tpl.getRowCount()) {
				tpl.setRowCount(rows);
				updated = true;
			}

			if (cols != tpl.getColumnCount()) {
				tpl.setColumnCount(cols);
				updated = true;
			}

			if (updated) {
				this.dbHelper.updateTempldate(tpl);
			}
		}

		return intent.putExtra(PARAM_TEMPLATE_OBJECT, tpl);
	}

}
