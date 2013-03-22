/**
 * 
 */
package it.fub.jardin.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author acozzolino
 *
 */
public class RegistrationInfo implements IsSerializable {

  private static final long serialVersionUID = 1L;
  private String nome;
  private String cognome;
  private String email;
  private String telefono;
  private String username;
//  private String password;
  
  public RegistrationInfo() {
    
  }
//  public RegistrationInfo(String nome, String cognome, String email, String username, String password) {
  public RegistrationInfo(String nome, String cognome, String email, String username) {
    // TODO Auto-generated constructor stub
    setNome(nome);
    setCognome(cognome);
    setEmail(email);
    setUsername(username);
  }
  
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
   * @return the cognome
   */
  public String getCognome() {
    return cognome;
  }
  /**
   * @param cognome the cognome to set
   */
  public void setCognome(String cognome) {
    this.cognome = cognome;
  }
  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }
  /**
   * @param email the email to set
   */
  public void setEmail(String email) {
    this.email = email;
  }
  /**
   * @return the telefono
   */
  public String getTelefono() {
    return telefono;
  }
  /**
   * @param telefono the telefono to set
   */
  public void setTelefono(String telefono) {
    this.telefono = telefono;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return the password
   */
//  public String getPassword() {
//    return password;
//  }
//
//  /**
//   * @param password the password to set
//   */
//  public void setPassword(String password) {
//    this.password = password;
//  }
}
