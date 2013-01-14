/**
 * 
 */
package it.fub.jardin.client.model;

import java.io.Serializable;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author acozzolino
 *
 */
public class MassiveUpdateObject implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -2381099343243194496L;
  private List<String> primaryKeyValues;
  private int resultsetId;
  private String fieldName;
  private BaseModelData newValues;
  /**
   * 
   */
  public MassiveUpdateObject() {
    // TODO Auto-generated constructor stub
    newValues = new BaseModelData();
  }
  /**
   * @return the values
   */
  public List<String> getPrimaryKeyValues() {
    return primaryKeyValues;
  }
  /**
   * @param values the values to set
   */
  public void setPrimaryKeyValues(List<String> values) {
    this.primaryKeyValues = values;
  }
  /**
   * @return the resultsetId
   */
  public int getResultsetId() {
    return resultsetId;
  }
  /**
   * @param resultsetId the resultsetId to set
   */
  public void setResultsetId(int resultsetId) {
    this.resultsetId = resultsetId;
  }
  /**
   * @return the fieldName
   */
  public String getFieldName() {
    return fieldName;
  }
  /**
   * @param fieldName the fieldName to set
   */
  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }
  /**
   * @return the newValues
   */
  public BaseModelData getNewValues() {
    return newValues;
  }
  /**
   * @param newValues the newValues to set
   */
  public void setNewValues(BaseModelData newValues) {
    this.newValues = newValues;
  }
  
}
