/**
 * 
 */
package it.fub.jardin.client.exception;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Implements serializable client exception that contains messages that should
 * be shown in client browser.
 * 
 * @author gpantanetti
 */
public class VisibleException extends Exception implements IsSerializable {

  private static final long serialVersionUID = 1L;

  /**
	 * 
	 */
  public VisibleException() {
    super();
  }

  /**
   * Create a new visible exception. Message is shown in client browser.
   * 
   * @param message
   */
  public VisibleException(String message) {
    super(message);
  }

}
