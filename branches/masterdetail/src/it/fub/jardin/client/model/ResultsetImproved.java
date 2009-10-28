package it.fub.jardin.client.model;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author acozzolino
 */
public class ResultsetImproved implements IsSerializable {

  private static final long serialVersionUID = 3478598676129553498L;

  private int id;
  private String name;
  private String alias;
  private String statement;
  private boolean read;
  private boolean delete;
  private boolean modify;
  private boolean insert;
  private ArrayList<Tool> tools;

  /* Lista dei campi del resultset */
  private List<ResultsetField> fields;
  /* Lista delle proprietà di raggruppamento */
  private List<ResultsetFieldGroupings> fieldGroupings;
  /* Lista dei vincoli di integrità */
  private FieldsMatrix foreignKeyList;
  /* Lista dei valori dei campi */
  private FieldsMatrix valuesList;
  /* Lista delle foreignKey entranti */
  private ArrayList<BaseModelData> foreignKeyIn;

  @SuppressWarnings("unused")
  private ResultsetImproved() {
    // As of GWT 1.5, it must have a default (zero argument) constructor
    // (with any access modifier) or no constructor at all.
  }

  /**
   * @param id
   * @param name
   * @param alias
   * @param statement
   * @param readperm
   * @param delete
   * @param modify
   * @param insert
   * @param toolbar
   */
  public ResultsetImproved(int id, String name, String alias, String statement,
      boolean readperm, boolean delete, boolean modify, boolean insert,
      ArrayList<Tool> tools) {
    super();
    this.id = id;
    this.name = name;
    this.alias = alias;
    this.statement = statement;
    this.read = readperm;
    this.delete = delete;
    this.modify = modify;
    this.insert = insert;
    this.tools = tools;

    fields = new ArrayList<ResultsetField>();
    fieldGroupings = new ArrayList<ResultsetFieldGroupings>();
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAlias() {
    return alias;
  }

  public String getStatement() {
    return statement;
  }

  public ArrayList<Tool> getTools() {
    return this.tools;
  }

  public boolean isRead() {
    return read;
  }

  public boolean isDelete() {
    return delete;
  }

  public boolean isModify() {
    return modify;
  }

  public boolean isInsert() {
    return insert;
  }

  public List<ResultsetField> getFields() {
    return fields;
  }

  public List<ResultsetFieldGroupings> getFieldGroupings() {
    return fieldGroupings;
  }

  public FieldsMatrix getForeignKeyList() {
    return foreignKeyList;
  }
  
  public ArrayList<BaseModelData>  getForeignKeyIn() {
    return foreignKeyIn;
  }

  public FieldsMatrix getValuesList() {
    return valuesList;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setStatement(String statement) {
    this.statement = statement;
  }

  public void setRead(boolean read) {
    this.read = read;
  }

  public void setDelete(boolean delete) {
    this.delete = delete;
  }

  public void setModify(boolean modify) {
    this.modify = modify;
  }

  public void setInsert(boolean insert) {
    this.insert = insert;
  }

  public void setToolbar(ArrayList<Tool> tools) {
    this.tools = tools;
  }

  public void setFields(List<ResultsetField> fields) {
    this.fields = fields;
  }

  public void setFieldGroupings(List<ResultsetFieldGroupings> fieldGroupings) {
    this.fieldGroupings = fieldGroupings;
  }

  public void setForeignKeyList(FieldsMatrix foreignKeyList) {
    this.foreignKeyList = foreignKeyList;
  }
  
  public void setForeignKeyIn(ArrayList<BaseModelData> foreignKeyIn) {
    this.foreignKeyIn = foreignKeyIn;
  }

  public void setValuesList(FieldsMatrix valuesList) {
    this.valuesList = valuesList;
  }

  /**
   * Esamina l'elenco dei raggruppamenti dei campi del resultset e restituisce
   * il raggruppamento richiesto
   * 
   * @param fieldGroupingId
   *          l'id del raggruppamento desiderato
   * @return l'oggetto raggruppamento di campi desiderato o null se non presente
   *         nell'elenco dei raggruppamenti
   */
  public ResultsetFieldGroupings getFieldGrouping(Integer fieldGroupingId) {
    for (ResultsetFieldGroupings fg : getFieldGroupings()) {
      if (fg.getId() == fieldGroupingId) {
        return fg;
      }
    }
    return null;
  }

  /**
   * Aggiunge un FieldGrouping alla lista dei FieldGrouping
   * 
   * @param fieldGrouping
   */
  public void addFieldGroupings(ResultsetFieldGroupings fieldGrouping) {
    this.fieldGroupings.add(fieldGrouping);
  }

  /**
   * Aggiunge un campo alla lista dei campi del Resultset
   * 
   * @param resultField
   */
  public void addField(ResultsetField resultField) {
    this.fields.add(resultField);
  }

}
