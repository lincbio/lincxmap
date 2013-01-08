package com.lincbio.lincxmap.pojo;

import java.io.Serializable;

public class Product implements Serializable {
	private static final long serialVersionUID = -8429715707676901318L;

	private long id;
	private long catalogueId;
	private String name;

	public Product() {
	}

	public Product(long id, long catalogueId, String name) {
		this.id = id;
		this.catalogueId = catalogueId;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCatalogueId() {
		return catalogueId;
	}

	public void setCatalogueId(long catalogueId) {
		this.catalogueId = catalogueId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		return (int) this.id;
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
