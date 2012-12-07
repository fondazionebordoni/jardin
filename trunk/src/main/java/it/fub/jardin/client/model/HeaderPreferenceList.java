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

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;

public class HeaderPreferenceList implements IsSerializable {

  private List<BaseModelData> userPref;
  private Integer resultsetId;

  public HeaderPreferenceList() {

  }

  /**
   * @param resultsetId
   *          the resultsetId to set
   */
  public void setResultsetId(final Integer resultsetId) {
    this.resultsetId = resultsetId;
  }

  /**
   * @return the resultsetId
   */
  public Integer getResultsetId() {
    return this.resultsetId;
  }

  /**
   * @param userPref
   *          the userPref to set
   */
  public void setUserPref(final List<BaseModelData> userPref) {
    this.userPref = userPref;
  }

  /**
   * @return the userPref
   */
  public List<BaseModelData> getUserPref() {
    return this.userPref;
  }

}
