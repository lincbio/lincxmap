package com.lincbio.lincxmap.dip;

import java.util.List;

import android.graphics.Bitmap;

import com.lincbio.lincxmap.pojo.Sample;
import com.lincbio.lincxmap.pojo.Template;

/**
 * Sample Detector
 * 
 * @author Johnson Lee
 * 
 */
public final class SampleDetector {
	public static final int MIN_PROGRESS = 0;
	public static final int MAX_PROGRESS = 100;

	private ProgressListener prgListener;

	public static interface ProgressListener {
		void onProgressChanged(int progress);
	}

	public SampleDetector() {
	}

	public SampleDetector(ProgressListener pl) {
		this.prgListener = pl;
	}

	public ProgressListener getProgressListener() {
		return prgListener;
	}

	public void setProgressListener(ProgressListener pl) {
		this.prgListener = pl;
	}

	/**
	 * Detect the specified image pixel data
	 * 
	 * @param bmp
	 *            bitmap image
	 * @param t
	 *            {@link Template}
	 */
	public native final List<Sample> detect(Bitmap bmp, Template t,
			SampleSelector... selectors);
}
