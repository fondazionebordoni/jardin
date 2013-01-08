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

public class Credentials implements IsSerializable {

  private static final long serialVersionUID = 1L;
  private String username;
  private String password;
  private String newPassword;

  @SuppressWarnings("unused")
  private Credentials() {
    /*
     * As of GWT 1.5, it must have a default (zero argument) constructor (with
     * any access modifier) or no constructor at all.
     */
  }

  public Credentials(final String username, final String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return this.username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(final String password) {
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

  public static Credentials parseCredentials(final String value) {
    int index = value.indexOf('#');
    String username = value.substring(0, index);
    String password = value.substring(index + 1);
    return new Credentials(username, password);
  }

  /**
   * @return the newPassword
   */
  public String getNewPassword() {
    return newPassword;
  }

  /**
   * @param newPassword the newPassword to set
   */
  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

}
