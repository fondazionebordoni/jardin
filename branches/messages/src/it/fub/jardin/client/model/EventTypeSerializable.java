/**
 * 
 */
package it.fub.jardin.client.model;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Serializable class that extends EventType
 * 
 * @author gpantanetti
 *
 * TODO Eliminate this class upon integrating Ext-GWT version 2.0.2 library
 * see: http://www.extjs.com/products/gxt/CHANGES_extgwt-2.0.2.html
 */
public class EventTypeSerializable
  implements IsSerializable {

  private Integer eventCode = -1;
  
  /**
   * Creates a new serializable event type.
   */
  public EventTypeSerializable() {

  }

  /**
   * Creates a new browser based event type.
   * 
   * @param eventCode additional information about the event
   */
  public EventTypeSerializable(Integer eventCode) {
    this.eventCode = eventCode;
  }

  /**
   * Returns the event code.
   * 
   * @return the event code
   * @see Event
   */
  public int getEventCode() {
    return eventCode;
  }

  /**
   * Returns true if the event type represents a browser event type (GWT event).
   * 
   * @return true for browser event types
   */
  public boolean isBrowserEvent() {
    return eventCode != -1;
  }

}
