package com.lincbio.lincxmap.android.app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.utils.Toasts;
import com.lincbio.lincxmap.pojo.Catalog;
import com.lincbio.lincxmap.pojo.Product;
import com.lincbio.lincxmap.pojo.ProductArgument;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ProductActivity extends Activity implements Constants {
	private final DatabaseHelper dbHelper = new DatabaseHelper(this);

	private EditText txtName;
	private EditText txtCatalog;
	private Spinner cmbModel;
	private LinearLayout lstArg;
	private Catalog catalog;
	private Product product;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.product_input);
		this.txtName = (EditText) findViewById(R.id.product_name);
		this.txtCatalog = (EditText) findViewById(R.id.product_catalog);
		this.cmbModel = (Spinner) findViewById(R.id.product_model);
		this.lstArg = (LinearLayout) findViewById(R.id.product_args);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Bundle bundle = getIntent().getExtras();

		if (null == bundle)
			return;

		if (bundle.containsKey(PARAM_CATALOG_OBJECT)) {
			Serializable obj = bundle.getSerializable(PARAM_CATALOG_OBJECT);

			if (null != obj && obj instanceof Catalog) {
				this.catalog = (Catalog) obj;
				this.txtCatalog.setText(this.catalog.getName());
				this.txtCatalog.setEnabled(false);
				this.txtCatalog.setFocusable(false);
			}
		}

		if (bundle.containsKey(PARAM_PRODUCT_OBJECT)) {
			Serializable obj = bundle.getSerializable(PARAM_PRODUCT_OBJECT);

			if (null != obj && obj instanceof Product) {
				this.product = (Product) obj;
				this.txtName.setText(this.product.getName());
				// TODO select model
				this.loadArgs(this.product);
			}
		}

	}

	public void onButtonAddArgClick(View view) {
		View v = getLayoutInflater().inflate(R.layout.product_arg, null);
		this.lstArg.addView(v);
		this.refreshArgsIndex();
	}

	public void onButtonDeleteArgClick(View view) {
		ViewGroup parent = (ViewGroup) view.getParent();
		this.lstArg.removeView(parent);
		this.refreshArgsIndex();
	}

	public void onButtonOkClick(View v) {
		Bundle bundle = getIntent().getExtras();

		if (null != bundle && bundle.containsKey(PARAM_PRODUCT_OBJECT)) {
			this.updateProduct(this.product);
		} else {
			this.newProduct();
		}
	}

	public void onButtonCancelClick(View v) {
		this.finish();
	}

	private void newProduct() {
		int index = this.cmbModel.getSelectedItemPosition();
		CharSequence[] models = getResources().getTextArray(
				R.array.product_model_ids);
		String model = models[index].toString();
		String name = this.txtName.getText().toString();
		String catalog = this.txtCatalog.getText().toString();

		if (catalog.length() <= 0) {
			Toasts.show(this, R.string.msg_product_catalog_required);
			return;
		}

		if (name.length() <= 0) {
			Toasts.show(this, R.string.msg_product_name_required);
			return;
		}

		List<ProductArgument> args = new ArrayList<ProductArgument>();

		for (int i = 0; i < this.lstArg.getChildCount(); i++) {
			View argView = this.lstArg.getChildAt(i);
			EditText txtValue = (EditText) argView.findViewWithTag("value");
			String value = String.valueOf(txtValue.getText());

			if (value.trim().length() <= 0) {
				txtValue.requestFocus();
				Toasts.show(this, R.string.msg_product_arg_value_required);
				return;
			}

			args.add(new ProductArgument(i, value));
		}

		try {
			this.dbHelper.addProduct(catalog, new Product(name, model), args);
			Toasts.show(this, R.string.msg_add_product_succeed);
			this.finish();
		} catch (Throwable t) {
			Toasts.show(this, t);
		}
	}

	private void updateProduct(Product product) {
		int index = this.cmbModel.getSelectedItemPosition();
		CharSequence[] models = getResources().getTextArray(
				R.array.product_model_ids);
		String model = models[index].toString();
		String name = this.txtName.getText().toString();

		if (name.length() <= 0) {
			Toasts.show(this, R.string.msg_product_name_required);
			return;
		}

		List<ProductArgument> args = new ArrayList<ProductArgument>();

		for (int i = 0; i < this.lstArg.getChildCount(); i++) {
			View argView = this.lstArg.getChildAt(i);
			EditText txtValue = (EditText) argView.findViewWithTag("value");
			String value = String.valueOf(txtValue.getText());

			if (value.trim().length() <= 0) {
				txtValue.requestFocus();
				Toasts.show(this, R.string.msg_product_arg_value_required);
				return;
			}

			args.add(new ProductArgument(i, value));
		}

		product.setName(name);
		product.setModel(model);
		
		try {
			this.dbHelper.updateProduct(product, args);
			Toasts.show(this, R.string.msg_update_product_succeed);
			this.finish();
		} catch (Throwable t) {
			Toasts.show(this, t);
		}
	}

	private void refreshArgsIndex() {
		for (int i = 0; i < this.lstArg.getChildCount(); i++) {
			View argView = this.lstArg.getChildAt(i);
			TextView txtIndex = (TextView) argView.findViewWithTag("index");
			txtIndex.setText(String.valueOf(i));
		}
	}

	private void loadArgs(Product product) {
		long id = product.getId();
		List<ProductArgument> args = this.dbHelper.getProductArguments(id);

		for (int i = 0; i < args.size(); i++) {
			ProductArgument arg = args.get(i);
			View v = getLayoutInflater().inflate(R.layout.product_arg, null);
			TextView txtIndex = (TextView) v.findViewWithTag("index");
			EditText txtValue = (EditText) v.findViewWithTag("value");
			txtIndex.setText(String.valueOf(arg.getIndex()));
			txtValue.setText(arg.getValue());
			this.lstArg.addView(v);
		}
	}

}
