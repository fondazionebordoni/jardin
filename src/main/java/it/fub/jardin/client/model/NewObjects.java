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
public class NewObjects implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 4848272039358007277L;
  private int resultsetId;
  private List<BaseModelData> newObjectList;
  /**
   * 
   */
  public NewObjects() {
    // TODO Auto-generated constructor stub
  }
  public NewObjects(int id, List<BaseModelData> newItemList) {
    setNewObjectList(newItemList);
    setResultsetId(id);
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
   * @return the newObjectList
   */
  public List<BaseModelData> getNewObjectList() {
    return newObjectList;
  }
  /**
   * @param newObjectList the newObjectList to set
   */
  public void setNewObjectList(List<BaseModelData> newObjectList) {
    this.newObjectList = newObjectList;
  }

}
