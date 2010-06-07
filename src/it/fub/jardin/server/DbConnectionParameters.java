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

package it.fub.jardin.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.allen_sauer.gwt.log.client.Log;

public class DbConnectionParameters {

  private final String path = "/conf/db.conf";
  private String url;
  private String db;
  private String dbInformationSchema;
  private String driver;
  private String user;
  private String pass;
  private String view;

  public DbConnectionParameters() {

    InputStream in =
        this.getClass().getClassLoader().getResourceAsStream(this.path);
    Log.debug("File " + this.path + " found");

    Properties myProps = new Properties();

    try {
      myProps.load(in);
      this.setUrl(myProps.getProperty("url"));
      this.setDb(myProps.getProperty("db"));
      this.setDbInformationSchema(myProps.getProperty("dbInformationSchema"));
      this.setDriver(myProps.getProperty("driver"));
      this.setUser(myProps.getProperty("user"));
      this.setPass(myProps.getProperty("pass"));
      this.setView(myProps.getProperty("view"));
    } catch (IOException e) {
      Log.warn("Impossibile leggere dal file " + this.path);
    }
  }

  /**
   * @return
   * @uml.property name="url"
   */
  public String getUrl() {
    return this.url;
  }

  /**
   * @return
   * @uml.property name="db"
   */
  public String getDb() {
    return this.db;
  }

  public String getDbInformationSchema() {
    return this.dbInformationSchema;
  }

  /**
   * @return
   * @uml.property name="driver"
   */
  public String getDriver() {
    return this.driver;
  }

  /**
   * @return
   * @uml.property name="user"
   */
  public String getUser() {
    return this.user;
  }

  /**
   * @return
   * @uml.property name="pass"
   */
  public String getPass() {
    return this.pass;
  }

  public String getView() {
    // TODO Auto-generated method stub
    return this.view;
  }

  /**
   * @param db
   * @uml.property name="db"
   */
  private void setDb(final String db) {
    this.db = db;
  }

  private void setDbInformationSchema(final String dbInformationSchema) {
    this.dbInformationSchema = dbInformationSchema;
  }

  /**
   * @param url
   * @uml.property name="url"
   */
  private void setUrl(final String url) {
    this.url = url;
  }

  /**
   * @param driver
   * @uml.property name="driver"
   */
  private void setDriver(final String driver) {
    this.driver = driver;
  }

  /**
   * @param user
   * @uml.property name="user"
   */
  private void setUser(final String user) {
    this.user = user;
  }

  /**
   * @param pass
   * @uml.property name="pass"
   */
  private void setPass(final String pass) {
    // TODO salvare la password sul file di configurazione con una chiave che
    // qui verrà usata per la decodifica
    this.pass = pass;
  }

  /**
   * @param view
   *          the view to set
   */
  private void setView(final String view) {
    this.view = view;
  }

}
