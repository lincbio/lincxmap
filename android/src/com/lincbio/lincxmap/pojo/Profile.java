package com.lincbio.lincxmap.pojo;

import java.io.Serializable;

/**
 * Profile of detectee
 * 
 * @author Johnson Lee
 * 
 */
public class Profile implements Serializable {
	private static final long serialVersionUID = -4515790261525918838L;

	private long id;
	private String name;
	private String serialNumber;

	public Profile() {
	}

	public Profile(String name, String serialNumber) {
		this.name = name;
		this.serialNumber = serialNumber;
	}

	public Profile(long id, String name, String serialNumber) {
		this.id = id;
		this.name = name;
		this.serialNumber = serialNumber;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
