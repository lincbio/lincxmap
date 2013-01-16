package com.lincbio.lincxmap.pojo;

import java.io.Serializable;

public class Product implements Serializable {
	private static final long serialVersionUID = -8429715707676901318L;

	private long id;
	private long catalogId;
	private String name;

	public Product() {
	}

	public Product(String name) {
		this.name = name;
	}

	public Product(long catalogId, String name) {
		this(name);
		this.catalogId = catalogId;
	}

	public Product(long id, long catalogId, String name) {
		this(catalogId, name);
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCatalogId() {
		return catalogId;
	}

	public void setCatalogId(long catalogId) {
		this.catalogId = catalogId;
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

		return p.id == this.id && p.catalogId == this.catalogId;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
