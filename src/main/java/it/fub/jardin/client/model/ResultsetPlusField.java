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

import java.io.Serializable;

public class ResultsetPlusField implements Serializable {

  private static final long serialVersionUID = 3099400302542129019L;

  private int id;
  private String name;
  private String alias;
  private int resultsetId;
  private boolean defaultheader;
  private int searchGrouping;
  private int idGrouping;
  private boolean readperm;
  private boolean deleteperm;
  private boolean modifyperm;
  private boolean insertperm;
  private boolean visible;

  private String fieldtype;

  private String fielddefaultvalue;

  private String foreignKey;

  private boolean ispk;
  
  private boolean isUnique;

  @SuppressWarnings("unused")
  private ResultsetPlusField() {
    // As of GWT 1.5, it must have a default (zero argument) constructor
    // (with any access modifier) or no constructor at all.
  }

  public ResultsetPlusField(final Integer id, final String name,
      final String alias, final Integer resultsetid,
      final boolean defaultheader, final Integer searchgrouping,
      final Integer idgrouping, final boolean readperm,
      final boolean deleteperm, final boolean modifyperm,
      final boolean insertperm, final boolean visible) {
    this.id = id;
    this.name = name;
    this.alias = alias;
    this.resultsetId = resultsetid;
    this.defaultheader = defaultheader;
    this.searchGrouping = searchgrouping;
    this.idGrouping = idgrouping;
    this.readperm = readperm;
    this.deleteperm = deleteperm;
    this.modifyperm = modifyperm;
    this.insertperm = insertperm;
    this.visible = visible;
  }

  public Integer getId() {
    return id;
  }

  public void setId(final Integer id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getAlias() {
    return this.alias;
  }

  public void setAlias(final String alias) {
    this.alias = alias;
  }

  public Integer getResultsetid() {
    return this.resultsetId;
  }

  public void setResultsetid(final Integer resultsetId) {
    this.resultsetId = resultsetId;
  }

  public boolean getDefaultheader() {
    return this.defaultheader;
  }

  public void setDefaultheader(final boolean defaultheader) {
    this.defaultheader = defaultheader;
  }

  /**
   * Restituisce informazioni sull'appartenenza del campo alla ricerca semplice
   * o avanzata
   * 
   * @return 0 se il campo appartiene alla ricerca semplice, 1 se appartiene al
   *         gruppo di ricerca avanzato
   */
  public Integer getSearchgrouping() {
    return this.searchGrouping;
  }

  public void setSearchgrouping(final Integer searchgrouping) {
    this.searchGrouping = searchgrouping;
  }

  public Integer getIdgrouping() {
    return this.idGrouping;
  }

  public void setIdrouping(final Integer idgrouping) {
    this.idGrouping = idgrouping;
  }

  public boolean getReadperm() {
    return this.readperm;
  }

  public void setReadperm(final boolean perm) {
    this.readperm = perm;
  }

  public boolean getDeleteperm() {
    return this.deleteperm;
  }

  public void setDeleteperm(final boolean perm) {
    this.deleteperm = perm;
  }

  public boolean getModifyperm() {
    return this.modifyperm;
  }

  public void setModifyperm(final boolean perm) {
    this.modifyperm = perm;
  }

  public boolean getInsertperm() {
    return this.insertperm;
  }

  public void setInsertperm(final boolean perm) {
    this.insertperm = perm;
  }

  public boolean getVisible() {
    return this.visible;
  }

  public void setVisible(final boolean perm) {
    this.visible = perm;
  }

  public String getType() {
    return this.fieldtype;
  }

  public void setType(final String type) {
    this.fieldtype = type;
  }

  public String getDefaultVAlue() {
    return this.fielddefaultvalue;
  }

  public void setDefaultValue(final String value) {
    this.fielddefaultvalue = value;
  }

  public String getForeignKey() {
    return this.foreignKey;
  }

  public void setForeignKey(final String FK) {
    this.foreignKey = FK;
  }

  public void setIsPK(final boolean ispk) {
    this.ispk = ispk;
  }

  public boolean getIsPK() {
    return this.ispk;
  }

  public void setIsUnique(final boolean unique) {
    this.isUnique = unique;
  }

  public boolean isUnique() {
    return this.isUnique;
  }

}
