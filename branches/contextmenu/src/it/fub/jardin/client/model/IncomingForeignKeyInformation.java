package it.fub.jardin.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class IncomingForeignKeyInformation implements IsSerializable{

	private String linkedTable;
	private String linkedField;
	private String field;

	@SuppressWarnings("unused")
	private IncomingForeignKeyInformation() {
	}
	
	public IncomingForeignKeyInformation(String linkedTable, String linkedField, String field) {
		this.linkedTable = linkedTable;
		this.linkedField = linkedField;
		this.field = field;
	}
	
	public String getLinkedTable() {
		return linkedTable;
	}

	public void setLinkedTable(String linkedTable) {
		this.linkedTable = linkedTable;
	}

	public String getLinkedField() {
		return linkedField;
	}

	public void setLinkedField(String linkedField) {
		this.linkedField = linkedField;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
	
}