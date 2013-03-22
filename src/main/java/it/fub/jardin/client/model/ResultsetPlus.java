/**
 * 
 */
package it.fub.jardin.client.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author acozzolino
 * 
 * le foreignKey sono proprietà del campo (ResultSetPlusField)
 *
 */
public class ResultsetPlus extends Resultset implements Serializable {

  /**
   * @return the permissions
   */
  public ResourcePermissions getPermissions() {
    return permissions;
  }
  /**
   * @param permissions the permissions to set
   */
  public void setPermissions(ResourcePermissions permissions) {
    this.permissions = permissions;
  }
  /**
   * @return the tools
   */
  public Tool getTools() {
    return tools;
  }
  /**
   * @param tools the tools to set
   */
  public void setTools(Tool tools) {
    this.tools = tools;
  }
  /**
   * @return the resultsetFieldList
   */
  public ArrayList<ResultsetPlusField> getResultsetFieldList() {
    return resultsetFieldList;
  }
  /**
   * @param resultsetFieldList the resultsetFieldList to set
   */
  public void setResultsetFieldList(
      ArrayList<ResultsetPlusField> resultsetFieldList) {
    this.resultsetFieldList = resultsetFieldList;
  }
  /**
   * @return the hasFKin
   */
  public boolean isHasFKin() {
    return hasFKin;
  }
  /**
   * @param hasFKin the hasFKin to set
   */
  public void setHasFKin(boolean hasFKin) {
    this.hasFKin = hasFKin;
  }
  /**
   * @return the fKinList
   */
  public ArrayList<ForeignKeyInField> getFKinList() {
    return FKinList;
  }
  /**
   * @param fKinList the fKinList to set
   */
  public void setFKinList(ArrayList<ForeignKeyInField> fKinList) {
    FKinList = fKinList;
  }
  private ResourcePermissions permissions;
  private Tool tools;
  private ArrayList<ResultsetPlusField> resultsetFieldList;
  private boolean hasFKin;
  private ArrayList<ForeignKeyInField> FKinList;
  /**
   * 
   */
  public ResultsetPlus() {
    // TODO Auto-generated constructor stub
  }

}
