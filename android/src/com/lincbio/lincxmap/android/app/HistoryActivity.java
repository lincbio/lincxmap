package com.lincbio.lincxmap.android.app;

import java.util.List;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.widget.GenericListAdapter;
import com.lincbio.lincxmap.pojo.History;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;

/**
 * Detection history UI
 * 
 * @author Johnson Lee
 * 
 */
public class HistoryActivity extends ListActivity {
	private final DatabaseHelper dbhelper = new DatabaseHelper(this);
	private final MenuManager menuManager = new MenuManager(this) {

		@Override
		public void onMenuItemSelected(MenuItem item) {
			super.onMenuItemSelected(item);

			switch (item.getItemId()) {
			case R.id.menu_clear:
				// TODO
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<History> histories = this.dbhelper.getHistories();
		ListAdapter adapter = new GenericListAdapter<History>(this, histories,
				R.layout.history_item);
		this.getListView().setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menuManager.createMenu(menu, R.menu.opt_history_list);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		this.menuManager.onMenuItemSelected(item);
		return super.onOptionsItemSelected(item);
	}

	public void onButtonSendClick(View view) {
		// TODO
	}

}
