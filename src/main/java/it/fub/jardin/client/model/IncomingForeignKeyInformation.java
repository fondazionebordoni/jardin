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

import com.google.gwt.user.client.rpc.IsSerializable;

public class IncomingForeignKeyInformation implements IsSerializable {

  private String linkingTable;
  private String linkingField;
  private String field;
  private String fieldValue;
  private Integer resultsetId;
  private ResultsetImproved interestedResultset;

  @SuppressWarnings("unused")
  private IncomingForeignKeyInformation() {
  }

  public IncomingForeignKeyInformation(final String linkingTable,
      final String linkingField, final String field) {
    this.linkingTable = linkingTable;
    this.linkingField = linkingField;
    this.field = field;
    this.fieldValue = null;
    this.resultsetId = null;
  }

  public String getLinkingTable() {
    return this.linkingTable;
  }

  public void setLinkingTable(final String linkingTable) {
    this.linkingTable = linkingTable;
  }

  public String getLinkingField() {
    return this.linkingField;
  }

  public void setLinkingField(final String linkingField) {
    this.linkingField = linkingField;
  }

  public String getField() {
    return this.field;
  }

  public void setField(final String field) {
    this.field = field;
  }

  public String getFieldValue() {
    return this.fieldValue;
  }

  public void setFieldValue(final String fieldValue) {
    this.fieldValue = fieldValue;
  }

  public Integer getResultsetId() {
    return this.resultsetId;
  }

  public void setResultsetId(final Integer resultSetId) {
    this.resultsetId = resultSetId;
  }

  public ResultsetImproved getInterestedResultset() {
    return this.interestedResultset;
  }

  public void setInterestedResultset(final ResultsetImproved interestedResultset) {
    this.interestedResultset = interestedResultset;
  }
}
