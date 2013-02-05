package com.lincbio.lincxmap.android.app;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.utils.ReportGenerator;
import com.lincbio.lincxmap.android.widget.GenericListAdapter;
import com.lincbio.lincxmap.pojo.History;
import com.lincbio.lincxmap.pojo.Profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Detection history UI
 * 
 * @author Johnson Lee
 * 
 */
public class HistoryListActivity extends Activity implements Constants {
	private final DatabaseHelper dbHelper = new DatabaseHelper(this);
	private final ReportGenerator reporter = new ReportGenerator(this);
	private final MenuManager menuManager = new MenuManager(this) {

		@Override
		public void onMenuItemSelected(MenuItem item) {
			super.onMenuItemSelected(item);

			final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
					.getMenuInfo();

			switch (item.getItemId()) {
			case R.id.menu_clear:
				createDeleteAllDialog().show();
				break;
			case R.id.menu_send_result: {
				History history = (History) historyView
						.getItemAtPosition(menuInfo.position);
				String subject = getString(R.string.title_send_result);
				String content = reporter.generateReport(history.getId());
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, subject);
				intent.putExtra(Intent.EXTRA_TEXT, content);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, subject));
				break;
			}
			case R.id.menu_del_history: {
				History history = (History) historyView
						.getItemAtPosition(menuInfo.position);
				createDeleteHistoryDialog(history).show();
				break;
			}
			}
		}

	};

	private EditText txtSearch;
	private ListView historyView;
	private GenericListAdapter<History> historyAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.history);
		this.historyAdapter = new GenericListAdapter<History>(this,
				R.layout.history_item);
		this.txtSearch = (EditText) findViewById(R.id.history_search);
		this.historyView = (ListView) findViewById(R.id.history_list);
		this.historyView.setAdapter(this.historyAdapter);
		this.historyView.setOnItemClickListener(new OnItemClickListener() {
			Context ctx = HistoryListActivity.this;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				History history = (History) parent.getItemAtPosition(position);
				Intent intent = new Intent(ctx, ResultDetailActivity.class);
				intent.putExtra(PARAM_HISTORY_ID, history.getId());
				startActivity(intent);
			}

		});
		this.registerForContextMenu(this.historyView);
	}

	public HistoryListActivity() {
		this.reporter.setDatabaseHelper(this.dbHelper);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menuManager.createMenu(menu, R.menu.opt_history_list);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		int position = ((AdapterContextMenuInfo) menuInfo).position;
		History history = (History) ((ListView) v).getItemAtPosition(position);
		menu.setHeaderTitle(history.getOwner() + "-" + history.getLabel());
		this.menuManager.createMenu(menu, R.menu.ctx_history);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		this.menuManager.onMenuItemSelected(item);
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		this.menuManager.onMenuItemSelected(item);
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Bundle bundle = this.getIntent().getExtras();

		if (null != bundle && bundle.containsKey(PARAM_PROFILE_OBJECT)) {
			Profile profile = (Profile) bundle
					.getSerializable(PARAM_PROFILE_OBJECT);
			this.setTitle(profile.getName());
			this.historyAdapter.reset(this.dbHelper.getHistories(profile));
		} else {
			this.historyAdapter.reset(this.dbHelper.getHistories());
		}
	}

	public void onButtonDeleteClick(View view) {
		this.createDeleteAllDialog().show();
	}
	
	public void onButtonSearchClick(View view) {
		String text = this.txtSearch.getText().toString();
		
		if (text.trim().length() <= 0) {
			this.historyAdapter.reset(this.dbHelper.getHistories());
		} else {
			this.historyAdapter.reset(this.dbHelper.getHistories(text));
		}
	}
	
	private AlertDialog.Builder createDeleteAllDialog() {
		OnClickListener clearAllOK = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbHelper.deleteAllHistory();
				historyAdapter.clear();
			}
		};

		return new AlertDialog.Builder(HistoryListActivity.this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(android.R.string.dialog_alert_title)
				.setMessage(R.string.msg_confirm_delete_all)
				.setPositiveButton(android.R.string.ok, clearAllOK)
				.setNegativeButton(android.R.string.cancel, MenuManager.CANCEL);
	}

	private AlertDialog.Builder createDeleteHistoryDialog(final History history) {
		OnClickListener deleteOK = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbHelper.deleteHistory(history);
				historyAdapter.reset(dbHelper.getHistories());
			}

		};

		return new AlertDialog.Builder(HistoryListActivity.this)
				.setTitle(android.R.string.dialog_alert_title)
				.setMessage(R.string.msg_confirm_delete)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.ok, deleteOK)
				.setNegativeButton(android.R.string.cancel, MenuManager.CANCEL);

	}
}
