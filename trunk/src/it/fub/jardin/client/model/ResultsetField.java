/**
 * 
 */
package it.fub.jardin.client.model;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author acozzolino
 * 
 */
public class ResultsetField extends BaseTreeModel implements IsSerializable {

  private static final long serialVersionUID = 3099400302542129019L;

  @SuppressWarnings("unused")
  private ResultsetField() {
    // As of GWT 1.5, it must have a default (zero argument) constructor
    // (with any access modifier) or no constructor at all.
  }

  public ResultsetField(Integer id, String name, String alias,
      Integer resultsetid, boolean defaultheader, Integer searchgrouping,
      Integer idgrouping, boolean readperm, boolean deleteperm,
      boolean modifyperm, boolean insertperm, boolean visible) {
    set("id", id);
    set("name", name);
    set("alias", alias);
    set("resultsetid", resultsetid);
    set("defaultheader", defaultheader);
    set("searchgrouping", searchgrouping);
    set("idgrouping", idgrouping);
    set("readperm", readperm);
    set("deleteperm", deleteperm);
    set("modifyperm", modifyperm);
    set("insertperm", insertperm);
    set("visible", visible);
  }

  public Integer getId() {
    return get("id");
  }

  public void setId(Integer id) {
    set("id", id);
  }

  public String getName() {
    return get("name");
  }

  public void setName(String name) {
    set("name", name);
  }

  public String getAlias() {
    return get("alias");
  }

  public void setAlias(String alias) {
    set("alias", alias);
  }

  public Integer getResultsetid() {
    return get("resultsetid");
  }

  public void setResultsetid(Integer resultsetId) {
    set("resultsetid", resultsetId);
  }

  public boolean getDefaultheader() {
    return Boolean.parseBoolean(get("defaultheader").toString());
  }

  public void setDefaultheader(boolean defaultheader) {
    set("defaultheader", defaultheader);
  }

  /**
   * Restituisce informazioni sull'appartenenza del campo alla ricerca semplice
   * o avanzata
   * 
   * @return 0 se il campo appartiene alla ricerca semplice, 1 se appartiene al
   *         gruppo di ricerca avanzato
   */
  public Integer getSearchgrouping() {
    return get("searchgrouping");
  }

  public void setSearchgrouping(Integer searchgrouping) {
    set("searchgrouping", searchgrouping);
  }

  public Integer getIdgrouping() {
    return get("idgrouping");
  }

  public void setIdrouping(Integer idgrouping) {
    set("idgrouping", idgrouping);
  }

  public boolean getReadperm() {
    return Boolean.parseBoolean(get("readperm").toString());
  }

  public void setReadperm(boolean perm) {
    set("readperm", perm);
  }

  public boolean getDeleteperm() {
    return Boolean.parseBoolean(get("deleteperm").toString());
  }

  public void setDeleteperm(boolean perm) {
    set("deleteperm", perm);
  }

  public boolean getModifyperm() {
    return Boolean.parseBoolean(get("modifyperm").toString());
  }

  public void setModifyperm(boolean perm) {
    set("modifyperm", perm);
  }

  public boolean getInsertperm() {
    return Boolean.parseBoolean(get("insertperm").toString());
  }

  public void setInsertperm(boolean perm) {
    set("insertperm", perm);
  }

  public boolean getVisible() {
    return Boolean.parseBoolean(get("visible").toString());
  }

  public void setVisible(boolean perm) {
    set("visible", perm);
  }

  public String getType() {
    return get("fieldtype");
  }

  public void setType(String type) {
    set("fieldtype", type);
  }

  public String getDefaultVAlue() {
    return get("fielddefaultvalue");
  }

  public void setDefaultValue(String value) {
    set("fielddefaultvalue", value);
  }

  public String getForeignKey() {
    return get("foreignkey");
  }

  public void setForeignKey(String FK) {
    set("foreignkey", FK);
  }

  public void setIsPK(boolean ispk) {
    set("ispk", ispk);
  }

  public boolean getIsPK() {
    return Boolean.parseBoolean(get("ispk").toString());
  }
  
  public void setUnique(boolean unique) {
    set("unique", unique);
  }

  public boolean isUnique() {
    return Boolean.parseBoolean(get("unique").toString());
  }

}
