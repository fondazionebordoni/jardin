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

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Message implements IsSerializable {

  private static final long serialVersionUID = -6542980077484895267L;

  public static final int MAX_MESSAGE_LENGTH = 160;

  private int id;
  private String title;
  private String body;
  private Date date;
  private MessageType type;
  private int sender;
  private int recipient;

  @SuppressWarnings("unused")
  private Message() {
    // As of GWT 1.5, it must have a default (zero argument) constructor
    // (with any access modifier) or no constructor at all.
  }

  /**
   * @param id
   *          Message's unique id
   * @param date
   *          Sending date
   * @param title
   *          Message's title
   * @param body
   *          Message's body
   * @param type
   *          Message's type (eg. warning)
   * @param sender
   *          Message's sender uid
   */
  public Message(final int id, final String title, final String body,
      final Date date, final MessageType type, final int sender,
      final int recipient) {
    this.id = id;
    this.title = title;
    this.body = body;
    this.date = date;
    this.type = type;
    this.sender = sender;
    this.recipient = recipient;
  }

  public Message(final String title, final String body, final Date date,
      final MessageType type, final int recipient) {
    this(-1, title, body, date, type, -1, recipient);
  }

  /**
   * @return the id
   */
  public int getId() {
    return this.id;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * @return the text
   */
  public String getBody() {
    return this.body;
  }

  /**
   * @return the date
   */
  public Date getDate() {
    return this.date;
  }

  /**
   * @return the type
   */
  public MessageType getType() {
    return this.type;
  }

  /**
   * @return the sender user uid
   */
  public int getSender() {
    return this.sender;
  }

  /**
   * @return the recipient user uid
   */
  public int getRecipient() {
    return this.recipient;
  }

  /**
   * @param sender
   *          the sender to set
   */
  public void setSender(final int sender) {
    this.sender = sender;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String s =
        this.id + " [" + this.type + "]: " + this.title + " - " + this.body
            + " [" + this.sender + "->" + this.recipient + "] "
            + this.date.toString();
    return s;
  }

}
