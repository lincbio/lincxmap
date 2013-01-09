package com.lincbio.lincxmap.android.app;

import java.util.List;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.widget.GenericGroupedListAdapter;
import com.lincbio.lincxmap.android.widget.GenericGroupedListAdapter.Groupable;
import com.lincbio.lincxmap.pojo.Product;

import android.app.ExpandableListActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListAdapter;

/**
 * Product List(classified by product catalogue) Activity
 * 
 * @author Johnson Lee
 * 
 */
public class ProductActivity extends ExpandableListActivity {
	private final DatabaseHelper dbHelper = new DatabaseHelper(this);
	private final MenuManager menuManager = new MenuManager(this) {

		@Override
		public void onMenuItemSelected(MenuItem item) {
			super.onMenuItemSelected(item);

			switch (item.getItemId()) {
			case R.id.menu_add:
				// TODO
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getExpandableListView().setCacheColorHint(Color.TRANSPARENT);

		List<Product> products = this.dbHelper.getProducts();
		Groupable<Product> groupBy = new ProductGroup();
		ExpandableListAdapter adapter = new GenericGroupedListAdapter<Product>(
				this, products, groupBy, R.layout.catalog_item,
				R.layout.product_item);
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menuManager.createMenu(menu, R.menu.opt_product_list);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		this.menuManager.onMenuItemSelected(item);
		return super.onOptionsItemSelected(item);
	}

	private class ProductGroup implements Groupable<Product> {

		@Override
		public Object getGroupId(Product e) {
			return dbHelper.getCatalogue(e.getCatalogueId());
		}

	}

}
