package it.fub.jardin.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Resultset implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 5570519625877579910L;
  
  private String name;
  private String note;
  private int id;
  private boolean gestible;
  private String alias;
  private String statement;
  private ResourcePermissions permissions;
  private List<ResultsetField> resultsetListField;
  private HashMap<Integer, ResultsetFieldGroupings> fieldGroupings;

  /**
   * @return the nome
   */
  public String getName() {
    return name;
  }
  /**
   * @param nome the nome to set
   */
  public void setName(String nome) {
    this.name = nome;
  }
  /**
   * @return the note
   */
  public String getNote() {
    return note;
  }
  /**
   * @param note the note to set
   */
  public void setNote(String note) {
    this.note = note;
  }
  /**
   * @return the id
   */
  public int getId() {
    return id;
  }
  /**
   * @param id the id to set
   */
  public void setId(int id) {
    this.id = id;
  }
  /**
   * @return the gestible
   */
  public boolean isGestible() {
    return gestible;
  }
  /**
   * @param gestible the gestible to set
   */
  public void setGestible(boolean gestible) {
    this.gestible = gestible;
  }
  /**
   * @return the alias
   */
  public String getAlias() {
    return alias;
  }
  /**
   * @param alias the alias to set
   */
  public void setAlias(String alias) {
    this.alias = alias;
  }
  /**
   * @return the statement
   */
  public String getStatement() {
    return statement;
  }
  /**
   * @param statement the statement to set
   */
  public void setStatement(String statement) {
    this.statement = statement;
  }
  /**
   * @return the permissions
   */
  public ResourcePermissions getPermissions() {
    return permissions;
  }
  /**
   * @param permissions the permissions to set
   */
  public void setPermissions(ResourcePermissions permissions) {
    this.permissions = permissions;
  }
  /**
   * @return the resultsetListField
   */
  public List<ResultsetField> getResultsetListField() {
    return resultsetListField;
  }
  /**
   * @param resultsetListField the resultsetListField to set
   */
  public void setResultsetListField(List<ResultsetField> resultsetListField) {
    this.resultsetListField = resultsetListField;
  }
  /**
   * @return the fieldGroupings
   */
  public HashMap<Integer,ResultsetFieldGroupings> getFieldGroupings() {
    return fieldGroupings;
  }
  /**
   * @param fieldGroupings the fieldGroupings to set
   */
  public void setFieldGroupings(HashMap<Integer, ResultsetFieldGroupings> fieldGroupings) {
    this.fieldGroupings = fieldGroupings;
  }

}
