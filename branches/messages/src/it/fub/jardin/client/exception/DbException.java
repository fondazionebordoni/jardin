/**
 * 
 */
package it.fub.jardin.client.exception;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author acozzolino
 * 
 */
public class DbException extends Exception implements IsSerializable {

  /**
	 * 
	 */
  private static final long serialVersionUID = 9217470123399242438L;

  /**
	 * 
	 */
  public DbException() {
    super();
  }

  /**
   * @param message
   */
  public DbException(String message) {
    super(message);
  }
}
