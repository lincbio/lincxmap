package com.lincbio.lincxmap.pojo;

import java.io.Serializable;

public class Sample implements Serializable {
	private static final long serialVersionUID = 5128266217512274006L;

	private int sum;
	private String name;
	private double brightness;
	private double concentration;

	public Sample() {
	}

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
