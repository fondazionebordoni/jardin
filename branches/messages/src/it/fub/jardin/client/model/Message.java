/**
 * 
 */
package it.fub.jardin.client.model;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author acozzolino
 * 
 */
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
  public Message(int id, String title, String body, Date date,
      MessageType type, int sender, int recipient) {
    this.id = id;
    this.title = title;
    this.body = body;
    this.date = date;
    this.type = type;
    this.sender = sender;
    this.recipient = recipient;
  }

  public Message(String title, String body, Date date, MessageType type,
      int recipient) {
    this(-1, title, body, date, type, -1, recipient);
  }

  /**
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * @return the text
   */
  public String getBody() {
    return body;
  }

  /**
   * @return the date
   */
  public Date getDate() {
    return date;
  }

  /**
   * @return the type
   */
  public MessageType getType() {
    return type;
  }

  /**
   * @return the sender user uid
   */
  public int getSender() {
    return sender;
  }

  /**
   * @return the recipient user uid
   */
  public int getRecipient() {
    return recipient;
  }

  /**
   * @param sender
   *          the sender to set
   */
  public void setSender(int sender) {
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
            + " [" + this.sender + "->" + this.recipient + "] " + this.date.toString();
    return s;
  }

}