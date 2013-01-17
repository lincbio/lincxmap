package com.lincbio.lincxmap.android.app;

import java.util.List;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.utils.Toasts;
import com.lincbio.lincxmap.android.widget.GenericListAdapter;
import com.lincbio.lincxmap.pojo.Profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Profile of detectees
 * 
 * @author Johnson Lee
 * 
 */
public class ProfileActivity extends Activity implements Constants {
	private final DatabaseHelper dbHelper = new DatabaseHelper(this);
	private final MenuManager menuManager = new MenuManager(this) {

		@Override
		public void onMenuItemSelected(MenuItem item) {
			super.onMenuItemSelected(item);

			final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
					.getMenuInfo();

			switch (item.getItemId()) {
			case R.id.menu_del_profile:
				Profile profile = (Profile) profileView
						.getItemAtPosition(menuInfo.position);
				createDeleteProfileDialog(profile).show();
				break;

			}
		}

	};

	private ListView profileView;
	private EditText txtSearch;
	private GenericListAdapter<Profile> profileAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.profile);
		this.txtSearch = (EditText) findViewById(R.id.profile_search);
		this.profileView = (ListView) findViewById(R.id.profile_list);
		this.profileView.setOnItemClickListener(new OnItemClickListener() {
			Context ctx = ProfileActivity.this;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Profile profile = (Profile) parent.getItemAtPosition(position);
				Intent intent = new Intent(ctx, HistoryActivity.class);
				intent.putExtra(PARAM_PROFILE_OBJECT, profile);
				startActivity(intent);
			}

		});
		this.profileAdapter = new GenericListAdapter<Profile>(this,
				R.layout.profile_item);
		this.profileView.setAdapter(this.profileAdapter);
		this.registerForContextMenu(this.profileView);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		int position = ((AdapterContextMenuInfo) menuInfo).position;
		Profile profile = (Profile) ((ListView) v).getItemAtPosition(position);
		menu.setHeaderTitle(profile.getName());
		this.menuManager.createMenu(menu, R.menu.ctx_profile);
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
		this.profileAdapter.reset(this.dbHelper.getProfiles());
	}

	public void onButtonAddClick(View view) {
		this.createAddProfileDialog().show();
	}

	public void onButtonBarcodeClick(View view) {
		// TODO
	}

	public void onButtonSearchClick(View view) {
		String txt = this.txtSearch.getText().toString().trim();
		List<Profile> profiles = 0 == txt.length() ? this.dbHelper
				.getProfiles() : this.dbHelper.getProfiles(txt);

		this.profileAdapter.reset(profiles);
	}

	private AlertDialog.Builder createAddProfileDialog() {
		final View v = getLayoutInflater()
				.inflate(R.layout.profile_input, null);
		final EditText txtName = (EditText) v.findViewById(R.id.profile_name);
		final EditText txtSn = (EditText) v.findViewById(R.id.profile_sn);

		OnClickListener ok = new OnClickListener() {
			Context ctx = ProfileActivity.this;

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String name = txtName.getText().toString();
				String sn = txtSn.getText().toString();

				if (name.length() <= 0) {
					Toasts.show(ctx, R.string.msg_profile_name_required);
					return;
				}

				if (sn.length() <= 0) {
					Toasts.show(ctx, R.string.msg_profile_sn_required);
					return;
				}

				try {
					dbHelper.addProfile(new Profile(name, sn));
					onButtonSearchClick(null);
					Toasts.show(ctx, R.string.msg_add_profile_succeed);
				} catch (Throwable t) {
					Toasts.show(ctx, t);
				}
			}

		};

		return new AlertDialog.Builder(this)
				.setTitle(R.string.title_add_profile).setView(v)
				.setPositiveButton(android.R.string.ok, ok)
				.setNegativeButton(android.R.string.cancel, MenuManager.CANCEL);

	}

	private AlertDialog.Builder createDeleteProfileDialog(final Profile profile) {
		OnClickListener ok = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbHelper.deleteProfile(profile);
				onButtonSearchClick(null);
			}

		};

		return new AlertDialog.Builder(ProfileActivity.this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(android.R.string.dialog_alert_title)
				.setMessage(R.string.msg_confirm_delete)
				.setPositiveButton(android.R.string.ok, ok)
				.setNegativeButton(android.R.string.cancel, MenuManager.CANCEL);
	}
}
