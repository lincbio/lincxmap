package com.lincbio.lincxmap.pojo;

import java.io.Serializable;

public class ProductArgument implements Serializable {
	private static final long serialVersionUID = 496731558033533856L;

	private long id;
	private long productId;
	private int index;
	private String value;

	public ProductArgument() {
	}

	public ProductArgument(int index) {
		this.index = index;
	}

	public ProductArgument(String value) {
		this.value = value;
	}

	public ProductArgument(int index, String value) {
		this.index = index;
		this.value = value;
	}

	public ProductArgument(long id, long productId, int index, String value) {
		this(index, value);
		this.productId = productId;
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}

}
