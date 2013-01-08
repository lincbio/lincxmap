package com.lincbio.lincxmap.pojo;

import java.io.Serializable;

/**
 * Detection history
 * 
 * @author Johnson Lee
 * 
 */
public class History implements Serializable {
	private static final long serialVersionUID = 3167860739963308096L;

	private long id;
	private long resultId;
	private String owner;
	private String label;
	private String time;

	public History() {
	}

	public History(long id, long resultId, String owner, String label, String time) {
		this.id = id;
		this.resultId = resultId;
		this.owner = owner;
		this.label = label;
		this.time = time;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getResultId() {
		return resultId;
	}

	public void setResultId(long resultId) {
		this.resultId = resultId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
