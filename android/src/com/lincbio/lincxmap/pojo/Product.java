package com.lincbio.lincxmap.pojo;

import java.io.Serializable;

public class Product implements Serializable {
	private static final long serialVersionUID = -8429715707676901318L;

	private int id;
	private int catalogueId;
	private String name;

	public Product() {
	}

	public Product(int id, int catalogueId, String name) {
		this.id = id;
		this.catalogueId = catalogueId;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCatalogueId() {
		return catalogueId;
	}

	public void setCatalogueId(int catalogueId) {
		this.catalogueId = catalogueId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Product))
			return false;

		Product p = (Product) o;

		return p.id == this.id && p.catalogueId == this.catalogueId;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
