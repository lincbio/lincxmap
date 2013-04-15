package com.lincbio.lincxmap.android.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import android.content.Context;

import com.lincbio.lincxmap.R;
import com.lincbio.lincxmap.android.database.DatabaseHelper;
import com.lincbio.lincxmap.pojo.History;
import com.lincbio.lincxmap.pojo.Profile;
import com.lincbio.lincxmap.pojo.Result;
import com.lincbio.lincxmap.pojo.Template;

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

	public DatabaseHelper getDatabaseHelper() {
		return dbHelper;
	}

	public void setDatabaseHelper(DatabaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public String generateTextReport(History history) {
		return this.generateTextReport(history.getId());
	}

	public String generateTextReport(long historyId) {
		StringBuilder buf = new StringBuilder();
		String name = this.context.getString(R.string.label_profile_name);
		String sn = this.context.getString(R.string.label_profile_sn);
		String label = this.context.getString(R.string.label_sample_name);
		String cv = this.context.getString(R.string.label_sample_cv);
		String ref = this.context.getString(R.string.label_sample_ref);
		String remark = this.context.getString(R.string.label_sample_remark);
		String title = this.context.getString(R.string.fmt_result_title);
		String entry = this.context.getString(R.string.fmt_result_entry);
		History history = this.dbHelper.getHistory(historyId);
		Profile profile = this.dbHelper.getProfile(history.getProfileId());
		List<Result> results = this.dbHelper.getResults(historyId);

		buf.append("--------------------------------------------------\n");
		buf.append(name).append(profile.getName()).append("\n");
		buf.append(sn).append(profile.getSerialNumber()).append("\n");
		buf.append("--------------------------------------------------\n");
		buf.append(String.format(title, label, cv, ref, remark)).append("\n");

		for (Result result : results) {
			buf.append(
					String.format(entry, result.getSampleName(),
							result.getConcentration(), result.getMinValue(),
							result.getMaxValue(), result.getFlag())).append(
					"\n");
		}

		buf.append("--------------------------------------------------\n");

		return buf.toString();
	}

	/**
	 * Generate a CSV report like this:
	 * 
	 * <pre>
	 *  * | A | B | C | D |...
	 * ---+---+---+---+---+...
	 *  1 |   |   |   |   |...
	 * ---+---+---+---+---+...
	 *  2 |   |   |   |   |...
	 * ---+---+---+---+---+...
	 *  3 |   |   |   |   |...
	 * ---+---+---+---+---+...
	 *  4 |   |   |   |   |...
	 * ---+---+---+---+---+...
	 * ...|   |   |   |   |...
	 * </pre>
	 * 
	 * @param history
	 *            {@link History} object
	 * @param template
	 *            {@link Template} object
	 * @return CSV file
	 * @throws IOException
	 */
	public File generateCSVReport(History history, Template template)
			throws IOException {
		int y = template.getRowCount();
		int x = template.getColumnCount();
		File csv = FileUtils.newTempFile("csv");
		List<Result> results = this.dbHelper.getResults(history.getId());

		PrintWriter out = null;

		try {
			out = new PrintWriter(csv);
			out.print('*');

			for (int j = 0; j < x; j++) {
				out.print(',');
				out.print((char) ('A' + j));
			}
			out.println();

			for (int i = 0; i < y; i++) {
				out.print(i + 1);

				for (int j = 0; j < x; j++) {
					out.print(',');
					out.print(results.get(i * y + j).getConcentration());
				}

				out.println();
			}
			out.println();
		} finally {
			out.close();
		}

		return csv;
	}
}
