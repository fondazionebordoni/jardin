package it.fub.jardin.client.testLayoutGWTPKG;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RsIdAndParentRsId implements IsSerializable {
	public RsIdAndParentRsId() {
	}
	public RsIdAndParentRsId(int resultSetId, int parentResultSetId) {
		this.resultSetId = resultSetId;
		this.parentResultSetId = parentResultSetId;
	}
	public int getResultSetId() {
		return resultSetId;
	}
	public int getParentResultSetId() {
		return parentResultSetId;
	}
	int resultSetId;
	int parentResultSetId;
}