/**
 * 
 */
package it.fub.jardin.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author gpantanetti
 * 
 */
public class Credentials implements IsSerializable {

  private static final long serialVersionUID = 1L;
  private String username;
  private String password;

  @SuppressWarnings("unused")
  private Credentials() {
    /*
     * As of GWT 1.5, it must have a default (zero argument) constructor (with
     * any access modifier) or no constructor at all.
     */
  }

  public Credentials(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return this.username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Codifica le credenziali e restituisce una stringa codificata
   * 
   * @return
   */
  public String encode() {
    // TODO Trovare un sistema migliore per la codifica (e decodifica)
    return this.username + '#' + this.password;
  }

  public static Credentials parseCredentials(String value) {
    int index = value.indexOf('#');
    String username = value.substring(0, index);
    String password = value.substring(index + 1);
    return new Credentials(username, password);
  }

}
