/**
 * 
 */
package it.fub.jardin.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author acozzolino
 * 
 */
public class HeaderPreferenceList implements IsSerializable {

  private List<BaseModelData> userPref;
  private Integer resultsetId;

  public HeaderPreferenceList() {

  }

  /**
   * @param resultsetId
   *          the resultsetId to set
   */
  public void setResultsetId(Integer resultsetId) {
    this.resultsetId = resultsetId;
  }

  /**
   * @return the resultsetId
   */
  public Integer getResultsetId() {
    return resultsetId;
  }

  /**
   * @param userPref
   *          the userPref to set
   */
  public void setUserPref(List<BaseModelData> userPref) {
    this.userPref = userPref;
  }

  /**
   * @return the userPref
   */
  public List<BaseModelData> getUserPref() {
    return userPref;
  }

}
