package com.lincbio.lincxmap.pojo;

import java.io.Serializable;

public class TemplateItem implements Serializable {
	private static final long serialVersionUID = 2302658716404424029L;

	private int id;
	private int templateId;
	private int productId;
	private int x;
	private int y;

	public TemplateItem() {
	}

	public TemplateItem(int id, int templateId, int productId, int x, int y) {
		this.id = id;
		this.templateId = templateId;
		this.productId = productId;
		this.x = x;
		this.y = y;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}
