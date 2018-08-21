package com.letv.autoapk.ui.player;

import java.io.Serializable;
/**
 * 码率
 * @author pys
 *
 */
class RateTypeItem implements Serializable{
	
	public RateTypeItem(){
	}

	public RateTypeItem(String name, String typeId) {
		this.setName(name);
		this.setTypeId(typeId);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	private String name;
	private String typeId;
}
