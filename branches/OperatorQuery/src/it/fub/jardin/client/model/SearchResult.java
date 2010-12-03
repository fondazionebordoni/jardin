/*
 * Copyright (c) 2010 Jardin Development Group <jardin.project@gmail.com>.
 * 
 * Jardin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Jardin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Jardin.  If not, see <http://www.gnu.org/licenses/>.
 */

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
   * @param resultsetId
   *          the resultsetId to set
   */
  public void setResultsetId(final int resultsetId) {
    this.resultsetId = resultsetId;
  }

  /**
   * @return the resultsetId
   */
  public int getResultsetId() {
    return this.resultsetId;
  }

  /**
   * @param store
   *          the store to set
   */
  public void setStore(final ListStore<BaseModelData> store) {
    this.store = store;
  }

  /**
   * @return the store
   */
  public ListStore<BaseModelData> getStore() {
    return this.store;
  }

}
