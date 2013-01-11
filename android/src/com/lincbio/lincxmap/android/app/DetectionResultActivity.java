package com.lincbio.lincxmap.android.app;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.pojo.Sample;

import android.app.ListActivity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class DetectionResultActivity extends ListActivity implements Constants {
	private static final DecimalFormat CV_FORMAT = new DecimalFormat("0.00");

	private MenuManager menuManager = new MenuManager(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		if (!extras.containsKey(PARAM_SAMPLE_LIST))
			return;

		Serializable obj = extras.getSerializable(PARAM_SAMPLE_LIST);
		if (!(obj instanceof List<?>))
			return;

		@SuppressWarnings("unchecked")
		List<Sample> samples = (List<Sample>) obj;
		getListView().setCacheColorHint(Color.TRANSPARENT);
		setListAdapter(new SampleAdapter(this, samples));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menuManager.createMenu(menu, R.menu.opt_detection_result);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		this.menuManager.onMenuItemSelected(item);
		return super.onOptionsItemSelected(item);
	}

	private static class SampleAdapter extends ArrayAdapter<Sample> {
		private TextView txtSampleName;
		private TextView txtSampleSum;
		private TextView txtSampleBv;
		private TextView txtSampleCv;
		private LayoutInflater inflater;

		public SampleAdapter(Context context, List<Sample> objects) {
			super(context, 0, objects);
			this.inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;

			if (null == convertView) {
				view = this.inflater.inflate(R.layout.sample_item, null);
			} else {
				view = convertView;
			}

			Sample sample = getItem(position);
			this.txtSampleName = (TextView) view.findViewById(R.id.sample_name);
			this.txtSampleBv = (TextView) view.findViewById(R.id.sample_bv);
			this.txtSampleCv = (TextView) view.findViewById(R.id.sample_cv);
			this.txtSampleSum = (TextView) view.findViewById(R.id.sample_sum);
			this.txtSampleSum.setText(String.valueOf(sample.getSum()));
			this.txtSampleName.setText(sample.getName());
			this.txtSampleBv.setText(String.valueOf(sample.getBrightness()));
			this.txtSampleCv.setText(CV_FORMAT.format(sample.getConcentration()));

			return view;
		}

	}
}
