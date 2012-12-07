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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

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
  private ArrayList<IncomingForeignKeyInformation> foreignKeyIn;

  /* Lista delle delle foreignkey */

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
  public ResultsetImproved(final int id, final String name, final String alias,
      final String statement, final boolean readperm, final boolean delete,
      final boolean modify, final boolean insert, final ArrayList<Tool> tools) {
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

    this.fields = new ArrayList<ResultsetField>();
    this.fieldGroupings = new ArrayList<ResultsetFieldGroupings>();
  }

  public int getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getAlias() {
    return this.alias;
  }

  public String getStatement() {
    return this.statement;
  }

  public ArrayList<Tool> getTools() {
    return this.tools;
  }

  public boolean isRead() {
    return this.read;
  }

  public boolean isDelete() {
    return this.delete;
  }

  public boolean isModify() {
    return this.modify;
  }

  public boolean isInsert() {
    return this.insert;
  }

  public List<ResultsetField> getFields() {
    return this.fields;
  }

  public List<ResultsetFieldGroupings> getFieldGroupings() {
    return this.fieldGroupings;
  }

  public FieldsMatrix getForeignKeyList() {
    return this.foreignKeyList;
  }

  public ArrayList<IncomingForeignKeyInformation> getForeignKeyIn() {
    return this.foreignKeyIn;
  }

  public FieldsMatrix getValuesList() {
    return this.valuesList;
  }

  public void setId(final int id) {
    this.id = id;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setAlias(final String alias) {
    this.alias = alias;
  }

  public void setStatement(final String statement) {
    this.statement = statement;
  }

  public void setRead(final boolean read) {
    this.read = read;
  }

  public void setDelete(final boolean delete) {
    this.delete = delete;
  }

  public void setModify(final boolean modify) {
    this.modify = modify;
  }

  public void setInsert(final boolean insert) {
    this.insert = insert;
  }

  public void setToolbar(final ArrayList<Tool> tools) {
    this.tools = tools;
  }

  public void setFields(final List<ResultsetField> fields) {
    this.fields = fields;
  }

  public void setFieldGroupings(
      final List<ResultsetFieldGroupings> fieldGroupings) {
    this.fieldGroupings = fieldGroupings;
  }

  public void setForeignKeyList(final FieldsMatrix foreignKeyList) {
    this.foreignKeyList = foreignKeyList;
  }

  public void setForeignKeyIn(
      final ArrayList<IncomingForeignKeyInformation> foreignKeyIn) {
    this.foreignKeyIn = foreignKeyIn;
  }

  public void setValuesList(final FieldsMatrix valuesList) {
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
  public ResultsetFieldGroupings getFieldGrouping(final Integer fieldGroupingId) {
    for (ResultsetFieldGroupings fg : this.getFieldGroupings()) {
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
  public void addFieldGroupings(final ResultsetFieldGroupings fieldGrouping) {
    this.fieldGroupings.add(fieldGrouping);
  }

  /**
   * Aggiunge un campo alla lista dei campi del Resultset
   * 
   * @param resultField
   */
  public void addField(final ResultsetField resultField) {
    this.fields.add(resultField);
  }

}
