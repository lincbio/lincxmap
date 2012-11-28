package com.lincbio.lincxmap.pojo;

import java.io.Serializable;

public class ImageSource implements Serializable {
	private static final long serialVersionUID = 3083363758966455258L;

	public static final int IMAGE_SOURCE_CAPTURE = 0;
	public static final int IMAGE_SOURCE_GALLERY = 1;

	private int id;
	private String name;

	public ImageSource() {
		super();
	}

	public ImageSource(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
