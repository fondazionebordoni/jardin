/**
 * 
 */
package it.fub.jardin.client.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author acozzolino
 *
 */
public class ForeignKey implements Serializable {

  
  private static final long serialVersionUID = 2706077982741334974L;
  private int pointingResultsetId;
  private String pointingFieldName;
  private String pointedTableName;
  private String pointedFieldName;
  
  private List values;
  
  public ForeignKey() {  
    // TODO Auto-generated constructor stub
  }
  
  /**
   * @return the pointingResultsetId
   */
  public int getPointingResultsetId() {
    return pointingResultsetId;
  }
  /**
   * @param pointingResultsetId the pointingResultsetId to set
   */
  public void setPointingResultsetId(int pointingResultsetId) {
    this.pointingResultsetId = pointingResultsetId;
  }
  /**
   * @return the pointingFieldName
   */
  public String getPointingFieldName() {
    return pointingFieldName;
  }
  /**
   * @param pointingFieldName the pointingFieldName to set
   */
  public void setPointingFieldName(String pointingFieldName) {
    this.pointingFieldName = pointingFieldName;
  }
  /**
   * @return the pointedTableName
   */
  public String getPointedTableName() {
    return pointedTableName;
  }
  /**
   * @param pointedTableName the pointedTableName to set
   */
  public void setPointedTableName(String pointedTableName) {
    this.pointedTableName = pointedTableName;
  }
  /**
   * @return the pointedFieldName
   */
  public String getPointedFieldName() {
    return pointedFieldName;
  }
  /**
   * @param pointedFieldName the pointedFieldName to set
   */
  public void setPointedFieldName(String pointedFieldName) {
    this.pointedFieldName = pointedFieldName;
  }

  /**
   * @return the values
   */
  public List getValues() {
    return values;
  }

  /**
   * @param values the values to set
   */
  public void setValues(List values) {
    this.values = values;
  }



}
