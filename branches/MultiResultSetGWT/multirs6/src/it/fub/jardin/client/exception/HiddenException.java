/**
 * 
 */
package it.fub.jardin.client.exception;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Implements serializable client exception that should not be showed to
 * client.
 * 
 * @author acozzolino
 */
public class HiddenException extends Exception implements IsSerializable {

  /**
	 * 
	 */
  private static final long serialVersionUID = 9217470123399242438L;

  /**
	 * 
	 */
  public HiddenException() {
    super();
  }

  /**
   * Create a new hidden exception. Message should not be revealed to client.
   * 
   * @param message
   */
  public HiddenException(String message) {
    super(message);
  }
}
