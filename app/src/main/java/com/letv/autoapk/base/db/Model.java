package com.letv.autoapk.base.db;

import java.io.Serializable;

import org.xutils.db.annotation.Column;

public class Model implements Serializable{
	private static final long serialVersionUID = 1L;
	@Column(name = "id", isId = true)
	private int id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Model [id=" + id + "]";
	}
}
