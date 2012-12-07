package it.fub.jardin.client.model;

import java.io.Serializable;

public class Resultset implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 5570519625877579910L;
  
  private String nome;
  private String note;
  private int id;
  private boolean gestible;
  private String alias;
  private String statement;
  /**
   * @return the nome
   */
  public String getNome() {
    return nome;
  }
  /**
   * @param nome the nome to set
   */
  public void setNome(String nome) {
    this.nome = nome;
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

}
