package com.lincbio.lincxmap.android.app;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.widget.GenericListAdapter;
import com.lincbio.lincxmap.pojo.History;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Detection history UI
 * 
 * @author Johnson Lee
 * 
 */
public class HistoryActivity extends ListActivity {
	private final DatabaseHelper dbHelper = new DatabaseHelper(this);
	private final MenuManager menuManager = new MenuManager(this) {

		@Override
		public void onMenuItemSelected(MenuItem item) {
			super.onMenuItemSelected(item);

			switch (item.getItemId()) {
			case R.id.menu_clear:
				OnClickListener ok = new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dbHelper.deleteAllHistory();
						listAdapter.clearData();
					}
				};
				OnClickListener cancel = new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				};
				new AlertDialog.Builder(HistoryActivity.this)
						.setTitle(android.R.string.dialog_alert_title)
						.setMessage(R.string.msg_confirm_clear)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton(android.R.string.ok, ok)
						.setNegativeButton(android.R.string.cancel, cancel)
						.show();
				break;
			}
		}

	};

	private GenericListAdapter<History> listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getListView().setCacheColorHint(Color.TRANSPARENT);
		this.listAdapter = new GenericListAdapter<History>(this,
				this.dbHelper.getHistories(), R.layout.history_item);
		this.setListAdapter(this.listAdapter);
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
		String subject = getString(R.string.title_send_result);
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, "");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, subject));
	}

}
