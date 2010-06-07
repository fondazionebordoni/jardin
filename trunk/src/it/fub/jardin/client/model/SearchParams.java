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

public class SearchParams implements IsSerializable {

  private int resultsetId;

  private boolean limit = true;

  private List<BaseModelData> fieldsValuesList;
  private boolean accurate;

  @SuppressWarnings("unused")
  private SearchParams() {
    // As of GWT 1.5, it must have a default (zero argument) constructor
    // (with any access modifier) or no constructor at all.
  }

  public SearchParams(final int resultsetId) {
    super();
    this.resultsetId = resultsetId;
  }

  public int getResultsetId() {
    return this.resultsetId;
  }

  public void setResultsetId(final Integer resultsetId) {
    this.resultsetId = resultsetId;
  }

  /**
   * @param queryFieldList
   *          the fieldsValuesList to set
   * @uml.property name="fieldsValuesList"
   */
  public void setFieldsValuesList(final List<BaseModelData> queryFieldList) {
    this.fieldsValuesList = queryFieldList;
  }

  /**
   * @return the fieldsValuesList
   * @uml.property name="fieldsValuesList"
   */
  public List<BaseModelData> getFieldsValuesList() {
    return this.fieldsValuesList;
  }

  public void setAccurate(final boolean accurate) {
    this.accurate = accurate;
  }

  public boolean getAccurate() {
    return this.accurate;
  }

  /**
   * @param limit
   *          the limit to set
   */
  public void setLimit(final boolean limit) {
    this.limit = limit;
  }

  /**
   * @return the limit
   */
  public boolean isLimit() {
    return this.limit;
  }

}
