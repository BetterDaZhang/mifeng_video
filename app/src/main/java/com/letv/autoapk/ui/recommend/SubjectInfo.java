package com.letv.autoapk.ui.recommend;

import java.util.ArrayList;
import java.util.List;

public class SubjectInfo {
	private String subjectId;
	private String subjectName;
	private String subjectBrief;
	private String subjectImg;
	private List<DisplayBlockInfo> subjectBlocks;

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getSubjectBrief() {
		return subjectBrief;
	}

	public void setSubjectBrief(String subjectBrief) {
		this.subjectBrief = subjectBrief;
	}

	public String getSubjectImg() {
		return subjectImg;
	}

	public void setSubjectImg(String subjectImg) {
		this.subjectImg = subjectImg;
	}

	public List<DisplayBlockInfo> getSubjectBlocks() {
		if(subjectBlocks == null){
			subjectBlocks = new ArrayList<DisplayBlockInfo>();
		}
		return subjectBlocks;
	}

	public void setSubjectBlocks(List<DisplayBlockInfo> subjectBlocks) {
		this.subjectBlocks = subjectBlocks;
	}

}
