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

import com.extjs.gxt.ui.client.event.EventType;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Serializable class that extends EventType
 * 
 * TODO Eliminate this class upon integrating Ext-GWT version 2.0.2 library see:
 * http://www.extjs.com/products/gxt/CHANGES_extgwt-2.0.2.html
 */
public class EventTypeSerializable extends EventType implements IsSerializable {

  /**
   * 
   */
  private static final long serialVersionUID = -1404472534492806685L;
  private Integer eventCode = -1;

  /**
   * Creates a new serializable event type.
   */
  public EventTypeSerializable() {

  }

  /**
   * Creates a new browser based event type.
   * 
   * @param eventCode
   *          additional information about the event
   */
  public EventTypeSerializable(final Integer eventCode) {
    this.eventCode = eventCode;
  }

  /**
   * Returns the event code.
   * 
   * @return the event code
   * @see Event
   */
  @Override
  public int getEventCode() {
    return this.eventCode;
  }

  /**
   * Returns true if the event type represents a browser event type (GWT event).
   * 
   * @return true for browser event types
   */
  @Override
  public boolean isBrowserEvent() {
    return this.eventCode != -1;
  }

}
