package com.lincbio.lincxmap.android.app;

import java.util.ArrayList;
import java.util.List;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.pojo.Product;
import com.lincbio.lincxmap.pojo.Template;
import com.lincbio.lincxmap.pojo.TemplateItem;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TableRow.LayoutParams;

/**
 * Template Editor UI
 * 
 * @author Johnson Lee
 * 
 */
public class SampleLayoutActivity extends Activity implements
		OnItemSelectedListener, Constants {
	private DatabaseHelper dbHelper = new DatabaseHelper(this);
	private MenuManager menuManager = new MenuManager(this);
	private List<Spinner> spinners = new ArrayList<Spinner>();

	private TableLayout matrix;
	private Template template;

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		Product product = (Product) parent.getItemAtPosition(position);

		if (parent instanceof Spinner) {
			Spinner spin = (Spinner) parent;
			spin.setPrompt(product.getName());

			if (spin.getTag() instanceof TemplateItem) {
				TemplateItem ti = (TemplateItem) spin.getTag();
				ti.setProductId(product.getId());
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO nothing
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.template_edit);

		Bundle extras = getIntent().getExtras();
		this.template = (Template) extras
				.getSerializable(PARAM_TEMPLATE_OBJECT);
		this.matrix = (TableLayout) findViewById(R.id.matrix);

		if (null == template)
			return;

		TableRow row = null;
		Spinner spin = null;
		List<Product> products = this.dbHelper.getProducts();
		ArrayAdapter<Product> adapter = new ArrayAdapter<Product>(this,
				android.R.layout.simple_spinner_item, products);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		for (int y = 0; y < this.template.getRowCount(); ++y) {
			row = new TableRow(this);
			row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));

			for (int x = 0; x < this.template.getColumnCount(); ++x) {
				spin = new Spinner(this);
				spin.setAdapter(adapter);
				spin.setOnItemSelectedListener(this);
				spin.setPromptId(R.string.title_choose_product);
				spin.setTag(new TemplateItem(0, this.template.getId(), 0, x, y));
				row.addView(spin);
				this.spinners.add(spin);
			}

			this.matrix.addView(row);
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
	protected void onDestroy() {
		this.dbHelper.close();
		super.onDestroy();
	}

	public void onButtonFinishClicked(View v) {
		List<TemplateItem> items = new ArrayList<TemplateItem>();
		
		for (Spinner spin : this.spinners) {
			if (spin.getTag() instanceof TemplateItem) {
				items.add((TemplateItem) spin.getTag());
			}
		}
		
		this.dbHelper.addTemplate(this.template, items);
		this.finish();
	}

	public void onButtonPrevClicked(View v) {
		this.finish();
	}

}
