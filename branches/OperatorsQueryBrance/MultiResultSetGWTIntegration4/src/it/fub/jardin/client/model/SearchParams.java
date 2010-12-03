/**
 * 
 */
package it.fub.jardin.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author acozzolino
 */
public class SearchParams implements IsSerializable {

	private int resultSetId;
	private int parentResultSetId;
	private boolean limit = true;

	private List<BaseModelData> fieldsValuesList;
	private boolean accurate;

	@SuppressWarnings("unused")
	private SearchParams() {
		// As of GWT 1.5, it must have a default (zero argument) constructor
		// (with any access modifier) or no constructor at all.
	}

	public SearchParams(int resultSetId, int parentResultSetId) {
		super();
		this.resultSetId = resultSetId;
		this.parentResultSetId = parentResultSetId;
	}

	public int getResultSetId() {
		return resultSetId;
	}

	public int getParentResultSetId() {
		return parentResultSetId;
	}

	public void setResultSetId(Integer resultSetId) {
		this.resultSetId = resultSetId;
	}

	/**
	 * @param queryFieldList
	 *            the fieldsValuesList to set
	 * @uml.property name="fieldsValuesList"
	 */
	public void setFieldsValuesList(List<BaseModelData> queryFieldList) {
		this.fieldsValuesList = queryFieldList;
	}

	/**
	 * @return the fieldsValuesList
	 * @uml.property name="fieldsValuesList"
	 */
	public List<BaseModelData> getFieldsValuesList() {
		return fieldsValuesList;
	}

	public void setAccurate(boolean accurate) {
		this.accurate = accurate;
	}

	public boolean getAccurate() {
		return this.accurate;
	}

  /**
   * @param limit the limit to set
   */
  public void setLimit(boolean limit) {
    this.limit = limit;
  }

  /**
   * @return the limit
   */
  public boolean isLimit() {
    return limit;
  }

}
