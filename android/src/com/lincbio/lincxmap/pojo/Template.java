package com.lincbio.lincxmap.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Template implements Serializable {
	private static final long serialVersionUID = -8541926956447408880L;

	private int id;
	private String name;
	private int rows;
	private int cols;
	private final List<TemplateItem> items = new ArrayList<TemplateItem>();

	public Template() {
	}

	public Template(int id, String name, int rows, int cols) {
		this.id = id;
		this.name = name;
		this.rows = rows;
		this.cols = cols;
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
		this.name = null == name ? null : name.trim();
	}

	public int getRowCount() {
		return rows;
	}

	public void setRowCount(int rows) {
		this.rows = rows;
	}

	public int getColumnCount() {
		return cols;
	}

	public void setColumnCount(int cols) {
		this.cols = cols;
	}

	public List<TemplateItem> getItems() {
		return items;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Template))
			return false;

		return ((Template) o).id == this.id;
	}

	@Override
	public String toString() {
		return this.name;
	}

}