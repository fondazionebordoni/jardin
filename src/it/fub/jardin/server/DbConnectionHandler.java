/**
 * 
 */
package it.fub.jardin.server;

import it.fub.jardin.client.exception.HiddenException;
import it.fub.jardin.server.AdvancedSqlConnection.ConnStatus;
import it.fub.jardin.server.AdvancedSqlConnection.ConnType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
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
  String dbInformationSchema;
  String driver;
  String user;
  String pass;
  String view;
  ArrayList<AdvancedSqlConnection> normalConnectionArrayList ; 
  ArrayList<AdvancedSqlConnection> infoConnectionArrayList ; 
  
  public DbConnectionHandler() {
    dbConnectionParameters = new DbConnectionParameters();
    url = dbConnectionParameters.getUrl();
    db = dbConnectionParameters.getDb();
    dbInformationSchema = dbConnectionParameters.getDbInformationSchema();
    driver = dbConnectionParameters.getDriver();
    user = dbConnectionParameters.getUser();
    pass = dbConnectionParameters.getPass();
    view = dbConnectionParameters.getView();
    normalConnectionArrayList = new ArrayList<AdvancedSqlConnection>();
    infoConnectionArrayList = new ArrayList<AdvancedSqlConnection>();
 }

	private void createConnections( ConnType connType) throws HiddenException{
		if (connType.equals(ConnType.normal)) {
			if (normalConnectionArrayList.size() < 30){
				for (int i = 0; i < 2; i++) {
					Connection newConn = createConnection(connType);
					AdvancedSqlConnection newAdvancedSqlConnection = new AdvancedSqlConnection (newConn, connType);
					normalConnectionArrayList.add(newAdvancedSqlConnection);
				}
			} 
		} else {
			if (normalConnectionArrayList.size() < 30){
				for (int i = 0; i < 2; i++) {
					Connection newConn = createConnection(connType);
					AdvancedSqlConnection newAdvancedSqlConnection = new AdvancedSqlConnection (newConn, connType);
					infoConnectionArrayList.add(newAdvancedSqlConnection);
				}
			}			
		}
	}

	public Connection getFreeExclusiveNormalConnection ( ) throws HiddenException {
		boolean found = false;
		while (!found){ 
			int size =  normalConnectionArrayList.size(); 
			for (int i = 0; i < size; i++) {
				AdvancedSqlConnection currConn = normalConnectionArrayList.get(i);
				if (currConn.getStatus() == ConnStatus.free) {
					found = true;
					currConn.setStatus(ConnStatus.busy);
					return currConn.getConn();
				}
			}
			createConnections(ConnType.normal);
		}
		   throw new HiddenException(
		    "Errore durante la creazione della connessione a database");
	  }
 
  //to be sincro 
  private Connection getFreeConnection (ConnType connType ) throws HiddenException {
	if (connType.equals(ConnType.normal)) {
		boolean found = false;
		while (!found){ 
			int size =  normalConnectionArrayList.size(); 
			for (int i = 0; i < size; i++) {
				AdvancedSqlConnection currConn = normalConnectionArrayList.get(i);
				if (currConn.getStatus() == ConnStatus.free) {
					found = true;
					//currConn.setStatus(ConnStatus.busy);
					return currConn.getConn();
				}
			}
			createConnections(connType);
		}
	} else {
		boolean found = false;
		while (!found){ 
			int size =  infoConnectionArrayList.size(); 
			for (int i = 0; i < size; i++) {
				AdvancedSqlConnection currConn = infoConnectionArrayList.get(i);
				if (currConn.getStatus() == ConnStatus.free) {
					found = true;
					//currConn.setStatus(ConnStatus.busy);
					return currConn.getConn();
				}
			}
			createConnections(connType);
		}		
	}
	   throw new HiddenException(
	    "Errore durante la creazione della connessione a database");
  }

  private Connection createConnection(ConnType connType) throws HiddenException {
	  System.out.println("createConnection " + connType + " " +  normalConnectionArrayList.size());
	  Connection connection = null;
    try {
      Class.forName(driver).newInstance();
		String dbString = db ;
		if (connType.equals(ConnType.info)) {
			dbString = dbInformationSchema;
		}
		connection =
          (Connection) DriverManager.getConnection(url + dbString, user, pass);
		return connection;
    } catch (Exception e) {
      Log.error("Errore durante la creazione della connesione a database", e);
      throw new HiddenException(
          "Errore durante la creazione della connessione a database");
    }
   } 

  public Connection getConn() throws HiddenException {
	System.out.println("getConn" + " " +  normalConnectionArrayList.size());
	Connection connection = getFreeConnection(ConnType.normal);
    return connection;
  }

  public Connection getConnDbInformationSchema() throws HiddenException {
		System.out.println("getConnDbInformationSchema" + " " +  infoConnectionArrayList.size());
		Connection connection = getFreeConnection(ConnType.info);
	    return connection;	 
  }

  public String getDB() {
    return db;
  }

  public String getDbInformationSchema() {
    return dbInformationSchema;
  }

  public String getView() {
    return view;
  }

  private AdvancedSqlConnection findExistentAdvancedConnection (Connection conn, ConnType connType){
	if (connType.equals(ConnType.normal)) {
		int size = normalConnectionArrayList.size(); 
		for (int i = 0; i < size; i++) {
			AdvancedSqlConnection currAdvConn = normalConnectionArrayList.get(i);
			if (currAdvConn.getConn() == conn ) {
				return currAdvConn ;
			}
		}
		return null;
	} else {
			int size = infoConnectionArrayList.size(); 
			for (int i = 0; i < size; i++) {
				AdvancedSqlConnection currAdvConn = infoConnectionArrayList.get(i);
				if (currAdvConn.getConn() == conn ) {
					return currAdvConn ;
				}
			}
		return null;		
	}
  }

  public void freeConn(Connection connection, ConnType connType) {
	  Log.warn("dummyFree conn");
  } 
  	
  public void freeExclusiveNormalConnection(Connection connection, ConnType connType) {
    try {
      if (connection != null) {
    	  AdvancedSqlConnection advConn = findExistentAdvancedConnection (connection,connType);
    	  if (advConn != null) {
    		  advConn.setStatus(ConnStatus.free);
    	  }
      }
    } catch (Exception e) {
      Log.warn("Errore durante la chiusura della connessione", e);
    }
  }

    public void closeConnOld(Connection connection) {
        try {
          if (connection != null) {
            connection.close();
          }
        } catch (SQLException e) {
          Log.warn("Errore durante la chiusura della connessione", e);
        }
  }

}
