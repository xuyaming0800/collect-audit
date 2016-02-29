package com.autonavi.audit.entity;

import java.util.List;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;

public class HistoricalRecords {
	private HistoricActivityInstance historicActivityInstance;
	private List<Comment> comments;
	private List<Attachment> attachments;

	public HistoricActivityInstance getHistoricActivityInstance() {
		return historicActivityInstance;
	}

	public void setHistoricActivityInstance(
			HistoricActivityInstance historicActivityInstance) {
		this.historicActivityInstance = historicActivityInstance;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	@Override
	public String toString() {
		return "HistoricalRecords [historicActivityInstance="
				+ historicActivityInstance + ", comments=" + comments
				+ ", attachments=" + attachments + "]";
	}

}
