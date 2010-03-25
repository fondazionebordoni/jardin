package it.fub.jardin.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.google.gwt.user.client.rpc.IsSerializable;

public class SearchResult implements IsSerializable {
  
  private int resultsetId;
  
  public SearchResult() {
    super();
  }

  private ListStore<BaseModelData> store;

  /**
   * @param resultsetId the resultsetId to set
   */
  public void setResultsetId(int resultsetId) {
    this.resultsetId = resultsetId;
  }

  /**
   * @return the resultsetId
   */
  public int getResultsetId() {
    return resultsetId;
  }

  /**
   * @param store the store to set
   */
  public void setStore(ListStore<BaseModelData> store) {
    this.store = store;
  }

  /**
   * @return the store
   */
  public ListStore<BaseModelData> getStore() {
    return store;
  }

}
