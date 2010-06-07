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

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ResultsetField extends BaseTreeModel implements IsSerializable {

  private static final long serialVersionUID = 3099400302542129019L;

  @SuppressWarnings("unused")
  private ResultsetField() {
    // As of GWT 1.5, it must have a default (zero argument) constructor
    // (with any access modifier) or no constructor at all.
  }

  public ResultsetField(final Integer id, final String name,
      final String alias, final Integer resultsetid,
      final boolean defaultheader, final Integer searchgrouping,
      final Integer idgrouping, final boolean readperm,
      final boolean deleteperm, final boolean modifyperm,
      final boolean insertperm, final boolean visible) {
    this.set("id", id);
    this.set("name", name);
    this.set("alias", alias);
    this.set("resultsetid", resultsetid);
    this.set("defaultheader", defaultheader);
    this.set("searchgrouping", searchgrouping);
    this.set("idgrouping", idgrouping);
    this.set("readperm", readperm);
    this.set("deleteperm", deleteperm);
    this.set("modifyperm", modifyperm);
    this.set("insertperm", insertperm);
    this.set("visible", visible);
  }

  public Integer getId() {
    return this.get("id");
  }

  public void setId(final Integer id) {
    this.set("id", id);
  }

  public String getName() {
    return this.get("name");
  }

  public void setName(final String name) {
    this.set("name", name);
  }

  public String getAlias() {
    return this.get("alias");
  }

  public void setAlias(final String alias) {
    this.set("alias", alias);
  }

  public Integer getResultsetid() {
    return this.get("resultsetid");
  }

  public void setResultsetid(final Integer resultsetId) {
    this.set("resultsetid", resultsetId);
  }

  public boolean getDefaultheader() {
    return Boolean.parseBoolean(this.get("defaultheader").toString());
  }

  public void setDefaultheader(final boolean defaultheader) {
    this.set("defaultheader", defaultheader);
  }

  /**
   * Restituisce informazioni sull'appartenenza del campo alla ricerca semplice
   * o avanzata
   * 
   * @return 0 se il campo appartiene alla ricerca semplice, 1 se appartiene al
   *         gruppo di ricerca avanzato
   */
  public Integer getSearchgrouping() {
    return this.get("searchgrouping");
  }

  public void setSearchgrouping(final Integer searchgrouping) {
    this.set("searchgrouping", searchgrouping);
  }

  public Integer getIdgrouping() {
    return this.get("idgrouping");
  }

  public void setIdrouping(final Integer idgrouping) {
    this.set("idgrouping", idgrouping);
  }

  public boolean getReadperm() {
    return Boolean.parseBoolean(this.get("readperm").toString());
  }

  public void setReadperm(final boolean perm) {
    this.set("readperm", perm);
  }

  public boolean getDeleteperm() {
    return Boolean.parseBoolean(this.get("deleteperm").toString());
  }

  public void setDeleteperm(final boolean perm) {
    this.set("deleteperm", perm);
  }

  public boolean getModifyperm() {
    return Boolean.parseBoolean(this.get("modifyperm").toString());
  }

  public void setModifyperm(final boolean perm) {
    this.set("modifyperm", perm);
  }

  public boolean getInsertperm() {
    return Boolean.parseBoolean(this.get("insertperm").toString());
  }

  public void setInsertperm(final boolean perm) {
    this.set("insertperm", perm);
  }

  public boolean getVisible() {
    return Boolean.parseBoolean(this.get("visible").toString());
  }

  public void setVisible(final boolean perm) {
    this.set("visible", perm);
  }

  public String getType() {
    return this.get("fieldtype");
  }

  public void setType(final String type) {
    this.set("fieldtype", type);
  }

  public String getDefaultVAlue() {
    return this.get("fielddefaultvalue");
  }

  public void setDefaultValue(final String value) {
    this.set("fielddefaultvalue", value);
  }

  public String getForeignKey() {
    return this.get("foreignkey");
  }

  public void setForeignKey(final String FK) {
    this.set("foreignkey", FK);
  }

  public void setIsPK(final boolean ispk) {
    this.set("ispk", ispk);
  }

  public boolean getIsPK() {
    return Boolean.parseBoolean(this.get("ispk").toString());
  }

  public void setUnique(final boolean unique) {
    this.set("unique", unique);
  }

  public boolean isUnique() {
    return Boolean.parseBoolean(this.get("unique").toString());
  }

}
