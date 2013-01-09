package com.lincbio.lincxmap.android.app;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.widget.GenericListAdapter;
import com.lincbio.lincxmap.pojo.Profile;
import com.lincbio.lincxmap.pojo.Template;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ProfileChooser extends ListActivity implements
		OnItemClickListener, Constants {
	private final DatabaseHelper dbHelper = new DatabaseHelper(this);

	private String image;
	private Template template;
	private GenericListAdapter<Profile> profileAdapter;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Profile profile = (Profile) parent.getItemAtPosition(position);
		Intent intent = new Intent(this, DetectionActivity.class);
		intent.putExtra(PARAM_IMAGE_SOURCE, this.image);
		intent.putExtra(PARAM_PROFILE_OBJECT, profile);
		intent.putExtra(PARAM_TEMPLATE_OBJECT, this.template);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.profileAdapter = new GenericListAdapter<Profile>(this,
				R.layout.profile_item);
		this.getListView().setOnItemClickListener(this);
		this.setListAdapter(this.profileAdapter);
	}

	@Override
	protected void onStart() {
		super.onStart();

		Bundle bundle = getIntent().getExtras();

		if (null == bundle)
			return;

		this.profileAdapter.setData(this.dbHelper.getProfiles());
		this.image = bundle.getString(PARAM_IMAGE_SOURCE);
		this.template = (Template) bundle.getSerializable(PARAM_TEMPLATE_OBJECT);
	}
}
