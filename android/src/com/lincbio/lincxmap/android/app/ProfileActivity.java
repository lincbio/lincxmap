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
import android.view.View;
import android.widget.AdapterView;
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

	private ListView profileView;
	private EditText txtSearch;
	private GenericListAdapter<Profile> profilesAdapter;

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
		this.profilesAdapter = new GenericListAdapter<Profile>(this,
				R.layout.profile_item);
		this.profileView.setAdapter(this.profilesAdapter);
	}

	@Override
	protected void onStart() {
		super.onStart();
		this.profilesAdapter.setData(this.dbHelper.getProfiles());
	}

	public void onButtonAddClick(View view) {
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
					Toasts.show(ctx, R.string.msg_add_profile_succeed);
				} catch (Throwable t) {
					Toasts.show(ctx, t);
				}
			}

		};
		OnClickListener cancel = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}

		};
		new AlertDialog.Builder(this).setTitle(R.string.title_add_profile)
				.setView(v).setPositiveButton(android.R.string.ok, ok)
				.setNegativeButton(android.R.string.cancel, cancel).show();
	}

	public void onButtonBarcodeClick(View view) {
		// TODO
	}

	public void onButtonSearchClick(View view) {
		String txt = this.txtSearch.getText().toString().trim();
		List<Profile> profiles = 0 == txt.length() ? this.dbHelper
				.getProfiles() : this.dbHelper.getProfiles(txt);

		this.profilesAdapter.setData(profiles);
	}
}
