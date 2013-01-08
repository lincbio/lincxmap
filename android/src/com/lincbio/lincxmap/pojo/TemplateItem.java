package com.lincbio.lincxmap.pojo;

import java.io.Serializable;

public class TemplateItem implements Serializable {
	private static final long serialVersionUID = 2302658716404424029L;

	private long id;
	private long templateId;
	private long productId;
	private int x;
	private int y;

	public TemplateItem() {
	}

	public TemplateItem(long id, long templateId, long productId, int x, int y) {
		this.id = id;
		this.templateId = templateId;
		this.productId = productId;
		this.x = x;
		this.y = y;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
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
