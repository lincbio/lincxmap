package com.lincbio.lincxmap.android.utils;

import java.util.List;

import android.content.Context;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.pojo.History;
import com.lincbio.lincxmap.pojo.Result;

/**
 * Detection report generator
 * 
 * @author Johnson Lee
 * 
 */
public class ReportGenerator {
	private Context context;
	private DatabaseHelper dbHelper;

	public ReportGenerator(Context ctx) {
		this.context = ctx;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public DatabaseHelper getDbHelper() {
		return dbHelper;
	}

	public void setDbHelper(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public String generateReport(History history) {
		return this.generateReport(history.getId());
	}

	public String generateReport(long historyId) {
		StringBuilder buf = new StringBuilder();
		String name = this.context.getString(R.string.label_sample_name);
		String cv = this.context.getString(R.string.label_sample_cv);
		String ref = this.context.getString(R.string.label_sample_ref);
		String remark = this.context.getString(R.string.label_sample_remark);
		String title = this.context.getString(R.string.fmt_result_title);
		String entry = this.context.getString(R.string.fmt_result_entry);
		List<Result> results = this.dbHelper.getResults(historyId);

		buf.append("------------------------------\n");
		buf.append(String.format(title, name, cv, ref, remark));

		for (Result result : results) {
			buf.append(String.format(entry, result.getSampleName(),
					result.getConcentration(), result.getMinValue(),
					result.getMaxValue(), result.getFlag()));
		}

		buf.append("------------------------------\n");

		return buf.toString();
	}
}
