/**
 * 
 */
package it.fub.jardin.client.exception;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Classe per la codifica delle eccezioni delle operazioni d'utente via RPC
 * 
 * @author seppe
 */
public class UserException extends Exception implements IsSerializable {

  private static final long serialVersionUID = 1L;

  /**
	 * 
	 */
  public UserException() {
    super();
  }

  /**
   * @param message
   */
  public UserException(String message) {
    super(message);
  }

}
