package it.fub.jardin.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class IncomingForeignKeyInformation implements IsSerializable{

	private String linkingTable;
	private String linkingField;
	private String field;
	private String fieldValue;
	private Integer resultsetId;
	private ResultsetImproved interestedResultset;

	@SuppressWarnings("unused")
	private IncomingForeignKeyInformation() {
	}
	
	public IncomingForeignKeyInformation(String linkingTable, String linkingField, String field) {
		this.linkingTable = linkingTable;
		this.linkingField = linkingField;
		this.field = field;
		this.fieldValue = null;
		this.resultsetId = null;
	}
	
	public String getLinkingTable() {
		return linkingTable;
	}

	public void setLinkingTable(String linkingTable) {
		this.linkingTable = linkingTable;
	}

	public String getLinkingField() {
		return linkingField;
	}

	public void setLinkingField(String linkingField) {
		this.linkingField = linkingField;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
	
	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	
	public Integer getResultsetId() {
		return resultsetId;
	}

	public void setResultsetId(Integer resultSetId) {
		this.resultsetId = resultSetId;
	}

	public ResultsetImproved getInterestedResultset() {
		return interestedResultset;
	}

	public void setInterestedResultset(ResultsetImproved interestedResultset) {
		this.interestedResultset = interestedResultset;
	}
}