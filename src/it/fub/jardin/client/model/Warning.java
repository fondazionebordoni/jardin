/**
 * 
 */
package it.fub.jardin.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author acozzolino
 * 
 */
public class Warning implements IsSerializable {

  /**
	 * 
	 */
  private static final long serialVersionUID = -6542980077484895267L;

  private Integer id;
  private String date;
  private String title;
  private String body;
  private String type;

  @SuppressWarnings("unused")
  private Warning() {
    // As of GWT 1.5, it must have a default (zero argument) constructor
    // (with any access modifier) or no constructor at all.
  }

  /**
   * @param id
   * @param date
   * @param title
   * @param body
   * @param type
   */
  public Warning(Integer id, String date, String title, String body, String type) {
    this.id = id;
    this.date = date;
    this.title = title;
    this.body = body;
    this.type = type;
  }

  /**
   * @return the id
   */
  public Integer getId() {
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
  public String getDate() {
    return date;
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

}
