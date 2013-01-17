package com.lincbio.lincxmap.android.app;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.utils.Toasts;
import com.lincbio.lincxmap.android.widget.GenericGroupedListAdapter;
import com.lincbio.lincxmap.android.widget.GenericGroupedListAdapter.Groupable;
import com.lincbio.lincxmap.pojo.Catalog;
import com.lincbio.lincxmap.pojo.Product;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;

/**
 * Product List(classified by product catalogue) Activity
 * 
 * @author Johnson Lee
 * 
 */
public class ProductActivity extends Activity {
	private final DatabaseHelper dbHelper = new DatabaseHelper(this);
	private final MenuManager menuManager = new MenuManager(this) {

		@Override
		public void onMenuItemSelected(MenuItem item) {
			super.onMenuItemSelected(item);

			ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item
					.getMenuInfo();
			int i = ExpandableListView
					.getPackedPositionGroup(info.packedPosition);
			int j = ExpandableListView
					.getPackedPositionChild(info.packedPosition);

			switch (item.getItemId()) {
			case R.id.menu_add_product: {
				Catalog catalog = (Catalog) productAdapter.getGroup(i);
				createAddProductDialog(catalog).show();
				break;
			}
			case R.id.menu_del_catalog: {
				Catalog catalog = (Catalog) productAdapter.getGroup(i);
				createDeleteCatalogDialog(catalog).show();
				break;
			}
			case R.id.menu_del_product: {
				Product product = (Product) productAdapter.getChild(i, j);
				createDeleteProductDialog(product).show();
				break;
			}
			}
		}

	};

	private EditText txtSearch;
	private ExpandableListView productView;
	private GenericGroupedListAdapter<Product> productAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.product);

		this.productAdapter = new GenericGroupedListAdapter<Product>(this,
				new ProductGroup(), R.layout.catalog_item,
				R.layout.product_item);
		this.txtSearch = (EditText) findViewById(R.id.product_search);
		this.productView = (ExpandableListView) findViewById(R.id.product_list);
		this.productView.setAdapter((ExpandableListAdapter) productAdapter);
		this.registerForContextMenu(this.productView);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) menuInfo;
		int i = ExpandableListView.getPackedPositionGroup(info.packedPosition);
		int j = ExpandableListView.getPackedPositionChild(info.packedPosition);

		switch (ExpandableListView.getPackedPositionType(info.packedPosition)) {
		case ExpandableListView.PACKED_POSITION_TYPE_GROUP:
			Catalog catalog = (Catalog) this.productAdapter.getGroup(i);
			menu.setHeaderTitle(catalog.getName());
			this.menuManager.createMenu(menu, R.menu.ctx_catalog);
			break;
		case ExpandableListView.PACKED_POSITION_TYPE_CHILD:
			Product product = (Product) this.productAdapter.getChild(i, j);
			menu.setHeaderTitle(product.getName());
			this.menuManager.createMenu(menu, R.menu.ctx_product);
			break;
		}

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		this.menuManager.onMenuItemSelected(item);
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		this.productAdapter.reset(this.dbHelper.getProducts());
	}

	public void onButtonAddClick(View view) {
		this.createAddProductDialog(null).show();
	}

	public void onButtonSearchClick(View view) {
		String text = this.txtSearch.getText().toString();

		if (text.trim().length() <= 0) {
			this.productAdapter.reset(this.dbHelper.getProducts());
		} else {
			this.productAdapter.reset(this.dbHelper.getProducts(text));
		}
	}

	private AlertDialog.Builder createAddProductDialog(final Catalog catalog) {
		final View v = getLayoutInflater()
				.inflate(R.layout.product_input, null);
		final EditText txtName = (EditText) v.findViewById(R.id.product_name);
		final EditText txtCatalog = (EditText) v
				.findViewById(R.id.product_catalog);

		if (null != catalog) {
			txtCatalog.setText(catalog.getName());
			txtCatalog.setEnabled(false);
			txtName.requestFocus();
		}
		
		OnClickListener ok = new OnClickListener() {
			Context ctx = ProductActivity.this;

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = txtName.getText().toString();
				String catalog = txtCatalog.getText().toString();

				if (catalog.length() <= 0) {
					Toasts.show(ctx, R.string.msg_product_catalog_required);
					return;
				}

				if (name.length() <= 0) {
					Toasts.show(ctx, R.string.msg_product_name_required);
					return;
				}

				try {
					dbHelper.addProduct(catalog, new Product(name));
					onButtonSearchClick(null);
					Toasts.show(ctx, R.string.msg_add_product_succeed);
				} catch (Throwable t) {
					Toasts.show(ctx, t);
				}
			}

		};
		return new AlertDialog.Builder(this)
				.setTitle(R.string.title_add_product).setView(v)
				.setPositiveButton(android.R.string.ok, ok)
				.setNegativeButton(android.R.string.cancel, MenuManager.CANCEL);
	}

	private AlertDialog.Builder createDeleteProductDialog(final Product product) {
		OnClickListener ok = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbHelper.deleteProduct(product);
				onButtonSearchClick(null);
			}

		};
		return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(android.R.string.dialog_alert_title)
				.setMessage(R.string.msg_confirm_delete)
				.setPositiveButton(android.R.string.ok, ok)
				.setNegativeButton(android.R.string.cancel, MenuManager.CANCEL);
	}

	private AlertDialog.Builder createDeleteCatalogDialog(final Catalog catalog) {
		OnClickListener ok = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbHelper.deleteCatalog(catalog);
				onButtonSearchClick(null);
			}

		};
		return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(android.R.string.dialog_alert_title)
				.setMessage(R.string.msg_confirm_delete)
				.setPositiveButton(android.R.string.ok, ok)
				.setNegativeButton(android.R.string.cancel, MenuManager.CANCEL);
	}

	private class ProductGroup implements Groupable<Product> {

		@Override
		public Object getGroupId(Product e) {
			return dbHelper.getCatalogue(e.getCatalogId());
		}

	}

}
