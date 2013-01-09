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

/**
 * Product List(classified by product catalogue) Activity
 * 
 * @author Johnson Lee
 * 
 */
public class ProductActivity extends ExpandableListActivity {
	private final DatabaseHelper dbHelper = new DatabaseHelper(this);
	private final MenuManager menuManager = new MenuManager(this);

	private GenericGroupedListAdapter<Product> productAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getExpandableListView().setCacheColorHint(Color.TRANSPARENT);

		List<Product> products = this.dbHelper.getProducts();
		this.productAdapter = new GenericGroupedListAdapter<Product>(
				this, products, new ProductGroup(), R.layout.catalog_item,
				R.layout.product_item);
		this.setListAdapter(this.productAdapter);
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
