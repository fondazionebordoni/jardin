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

import it.fub.jardin.client.exception.HiddenException;
import it.fub.jardin.client.exception.VisibleException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//import com.allen_sauer.gwt.log.client.Log;

public class DbConnectionHandler {

  /**
   * Funzione per effettuare il collegamento al database
   * 
   * @return
   */
  DbConnectionParameters dbConnectionParameters; 

  String url;
  String db;
  String dbInformationSchema;
  String driver;
  String user;
  String pass;
  String view;

  public DbConnectionHandler() throws VisibleException {
    this.dbConnectionParameters = new DbConnectionParameters();
    this.url = this.dbConnectionParameters.getUrl();
    this.db = this.dbConnectionParameters.getDb();
    this.dbInformationSchema =
        this.dbConnectionParameters.getDbInformationSchema();
    this.driver = this.dbConnectionParameters.getDriver();
    this.user = this.dbConnectionParameters.getUser();
    this.pass = this.dbConnectionParameters.getPass();
    this.view = this.dbConnectionParameters.getView();
  }

  public Connection getConn() throws HiddenException {
    Connection connection = null;
    try {
      Class.forName(this.driver).newInstance();
      connection =
          DriverManager.getConnection(this.url + this.db, this.user, this.pass);
    } catch (Exception e) {
//      Log.error("Errore durante la creazione della connesione a database", e);
      e.printStackTrace();
      throw new HiddenException(
          "Errore durante la creazione della connessione a database");
    }
    return connection;
  }

  public Connection getConnDbInformationSchema() throws HiddenException {
    Connection connection = null;
    try {
      Class.forName(this.driver).newInstance();
      connection =
          DriverManager.getConnection(this.url + this.dbInformationSchema,
              this.user, this.pass);
    } catch (Exception e) {
//      Log.warn("Errore durante la connesione a database", e);
      throw new HiddenException(
          "Errore durante la creazione della connessione a database informationschema");
    }
    return connection;
  }

  public String getDB() {
    return this.db;
  }

  public String getDbInformationSchema() {
    return this.dbInformationSchema;
  }

  public String getView() {
    return this.view;
  }

  public void closeConn(final Connection connection) throws HiddenException {
    try {
      if (connection != null) {
        connection.close();
      }
    } catch (SQLException e) {
//      Log.warn("Errore durante la chiusura della connessione", e);
      throw new HiddenException(
          "Errore durante la chiusura della connessione a database");
    }
  }
  
  /**
   * @return the dbConnectionParameters
   */
  public DbConnectionParameters getDbConnectionParameters() {
    return dbConnectionParameters;
  }

  /**
   * @param dbConnectionParameters the dbConnectionParameters to set
   */
  public void setDbConnectionParameters(
      DbConnectionParameters dbConnectionParameters) {
    this.dbConnectionParameters = dbConnectionParameters;
  }

}
