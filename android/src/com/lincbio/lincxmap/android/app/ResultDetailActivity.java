package com.lincbio.lincxmap.android.app;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.android.widget.GenericListAdapter;
import com.lincbio.lincxmap.pojo.Result;

import android.app.ListActivity;
import android.graphics.Color;
import android.os.Bundle;

/**
 * Result detail UI
 * 
 * @author Johnson Lee
 * 
 */
public class ResultDetailActivity extends ListActivity implements Constants {
	private final DatabaseHelper dbHelper = new DatabaseHelper(this);

	private GenericListAdapter<Result> resultAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getListView().setCacheColorHint(Color.TRANSPARENT);
		this.resultAdapter = new GenericListAdapter<Result>(this,
				R.layout.result_item);
		this.setListAdapter(this.resultAdapter);
	}

	@Override
	protected void onStart() {
		super.onStart();

		Bundle bundle = getIntent().getExtras();

		if (null == bundle || !bundle.containsKey(PARAM_HISTORY_ID)) {
			finish();
			return;
		}

		long historyId = bundle.getLong(PARAM_HISTORY_ID);
		this.resultAdapter.setData(this.dbHelper.getResults(historyId));
	}

}
