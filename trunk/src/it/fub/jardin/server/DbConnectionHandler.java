/**
 * 
 */
package it.fub.jardin.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.allen_sauer.gwt.log.client.Log;

/**
 * @author gpantanetti
 * 
 */
public class DbConnectionHandler {

  /**
   * Funzione per effettuare il collegamento al database
   * 
   * @return
   */
  DbConnectionParameters dbConnectionParameters;
  String url;
  String db;
  String driver;
  String user;
  String pass;
  String view;

  public DbConnectionHandler() {
    dbConnectionParameters = new DbConnectionParameters();
    url = dbConnectionParameters.getUrl();
    db = dbConnectionParameters.getDb();
    driver = dbConnectionParameters.getDriver();
    user = dbConnectionParameters.getUser();
    pass = dbConnectionParameters.getPass();
    view = dbConnectionParameters.getView();
  }

  public Connection getConn() {
    Connection connection = null;
    try {
      Class.forName(driver).newInstance();
      connection =
          (Connection) DriverManager.getConnection(url + db, user, pass);
    } catch (Exception e) {
      Log.warn("Errore durante la connesione a database", e);
    }
    return connection;
  }

  public String getDB() {
    return db;
  }

  public String getView() {
    return view;
  }

  public void closeConn(Connection connection) {
    try {
      if (connection != null) {
        connection.close();
      }
    } catch (SQLException e) {
      Log.warn("Errore durante la chiusura della connessione", e);
    }
  }

}
