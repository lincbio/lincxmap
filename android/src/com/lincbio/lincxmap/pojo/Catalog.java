package com.lincbio.lincxmap.pojo;

import java.io.Serializable;

public class Catalog implements Serializable {
	private static final long serialVersionUID = 8594097451764011433L;

	private long id;
	private String name;

	public Catalog() {
	}

	public Catalog(String name) {
		this.name = name;
	}

	public Catalog(long id, String name) {
		this(name);
		this.id = id;
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

	@Override
	public int hashCode() {
		return (int) this.id;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Catalog))
			return false;

		return ((Catalog) o).id == this.id;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
