package com.lincbio.lincxmap.android.app;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.Constants;
import com.lincbio.lincxmap.pojo.Sample;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

public class DetectionResultActivity extends ListActivity implements Constants {
	private static final DecimalFormat CV_FORMAT = new DecimalFormat("0.00");

	private MenuManager menuManager = new MenuManager(this) {

		@Override
		public void onMenuItemSelected(MenuItem item) {
			super.onMenuItemSelected(item);
			
			switch (item.getItemId()) {
			case R.id.menu_send_result:
				String subject = getString(R.string.title_send_result);
				String content = dumpResult();
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, subject);
				intent.putExtra(Intent.EXTRA_TEXT, content);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, subject));
				break;
			}
		}

	};
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

	private String dumpResult() {
		StringBuilder buf = new StringBuilder();
		String colon = getString(R.string.colon);
		String nm = getString(R.string.label_sample_name);
		String bv = getString(R.string.label_sample_bv);
		String cv = getString(R.string.label_sample_cv);

		ListAdapter adapter = getListAdapter();
		int n = adapter.getCount();

		buf.append("------------------------------\n");

		for (int i = 0; i < n; i++) {
			Sample sample = (Sample) adapter.getItem(i);
			buf.append(nm).append(colon).append(sample.getName()).append("\n");
			buf.append(bv).append(colon).append(sample.getBrightness()).append("\n");
			buf.append(cv).append(colon).append(sample.getConcentration()).append("\n");
			buf.append("------------------------------\n");
		}

		return buf.toString();
	}
	
	private static class SampleAdapter extends ArrayAdapter<Sample> {
		private TextView txtSampleName;
		private TextView txtSampleSum;
		private TextView txtSampleBv;
		private TextView txtSampleCv;
		private LayoutInflater inflater;

		public SampleAdapter(Context context, List<Sample> objects) {
			super(context, 0, objects);
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			
			if (null == convertView) {
				view = this.inflater.inflate(R.layout.sample, parent, false);
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
