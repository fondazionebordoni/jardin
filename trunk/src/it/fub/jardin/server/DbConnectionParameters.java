/**
 * 
 */
package it.fub.jardin.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.allen_sauer.gwt.log.client.Log;

/**
 * @author acozzolino
 */
public class DbConnectionParameters {

  private String path = "/conf/db.conf";
  private String url;
  private String db;
  private String driver;
  private String user;
  private String pass;
  private String view;

  public DbConnectionParameters() {

    InputStream in = this.getClass().getClassLoader().getResourceAsStream(path);
    Log.debug("File " + path + " found");

    Properties myProps = new Properties();

    try {
      myProps.load(in);
      setUrl(myProps.getProperty("url"));
      setDb(myProps.getProperty("db"));
      setDriver(myProps.getProperty("driver"));
      setUser(myProps.getProperty("user"));
      setPass(myProps.getProperty("pass"));
      setView(myProps.getProperty("view"));
    } catch (IOException e) {
      Log.warn("Impossibile leggere dal file " + path);
    }
  }

  /**
   * @return
   * @uml.property name="url"
   */
  public String getUrl() {
    return url;
  }

  /**
   * @return
   * @uml.property name="db"
   */
  public String getDb() {
    return db;
  }

  /**
   * @return
   * @uml.property name="driver"
   */
  public String getDriver() {
    return driver;
  }

  /**
   * @return
   * @uml.property name="user"
   */
  public String getUser() {
    return user;
  }

  /**
   * @return
   * @uml.property name="pass"
   */
  public String getPass() {
    return pass;
  }

  public String getView() {
    // TODO Auto-generated method stub
    return this.view;
  }

  /**
   * @param db
   * @uml.property name="db"
   */
  private void setDb(String db) {
    this.db = db;
  }

  /**
   * @param url
   * @uml.property name="url"
   */
  private void setUrl(String url) {
    this.url = url;
  }

  /**
   * @param driver
   * @uml.property name="driver"
   */
  private void setDriver(String driver) {
    this.driver = driver;
  }

  /**
   * @param user
   * @uml.property name="user"
   */
  private void setUser(String user) {
    this.user = user;
  }

  /**
   * @param pass
   * @uml.property name="pass"
   */
  private void setPass(String pass) {
    // TODO salvare la password sul file di configurazione con una chiave che
    // qui verrà usata per la decodifica
    this.pass = pass;
  }

  /**
   * @param view
   *          the view to set
   */
  private void setView(String view) {
    this.view = view;
  }

}
