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

package it.fub.jardin.client.exception;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Implements serializable client exception that contains messages that should
 * be shown in client browser.
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
  public VisibleException(final String message) {
    super(message);
  }

}
