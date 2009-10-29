/**
 * 
 */
package it.fub.jardin.client.model;

import com.google.gwt.i18n.client.Messages;

/**
 * @author acozzolino
 * 
 */
public interface RnfMessages extends Messages {

  // @DefaultMessage("Dati {1}: {0}")
  // String gridHeaderColumnsMessage(String groupName, int groupNumber);

  @DefaultMessage(" {0}:")
  String gridHeaderColumnsMessage(String groupName);

}
