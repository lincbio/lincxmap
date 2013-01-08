package com.lincbio.lincxmap.pojo;

import java.io.Serializable;

public class Catalogue implements Serializable {
	private static final long serialVersionUID = 8594097451764011433L;

	private long id;
	private String name;

	public Catalogue() {
	}

	public Catalogue(long id, String name) {
		this.id = id;
		this.name = name;
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
		if (!(o instanceof Catalogue))
			return false;

		return ((Catalogue) o).id == this.id;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

}
