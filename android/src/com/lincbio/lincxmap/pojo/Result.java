package com.lincbio.lincxmap.pojo;

import java.io.Serializable;

public class Result implements Serializable {
	private static final long serialVersionUID = -2688057598689179095L;

	private long id;
	private long historyId;
	private String sampleName;
	private double brightness;
	private double concentration;

	public Result() {
	}

	public Result(long historyId, String sampleName, double brightness,
			double concentration) {
		this.historyId = historyId;
		this.sampleName = sampleName;
		this.brightness = brightness;
		this.concentration = concentration;
	}

	public Result(long id, long historyId, String sampleName,
			double brightness, double concentration) {
		this(historyId, sampleName, brightness, concentration);
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getHistoryId() {
		return historyId;
	}

	public void setHistoryId(long historyId) {
		this.historyId = historyId;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public double getBrightness() {
		return brightness;
	}

	public void setBrightness(double brightness) {
		this.brightness = brightness;
	}

	public double getConcentration() {
		return concentration;
	}

	public void setConcentration(double concentration) {
		this.concentration = concentration;
	}

}
