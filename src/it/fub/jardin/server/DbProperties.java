/**
 * 
 */
package it.fub.jardin.server;

import it.fub.jardin.client.exception.HiddenException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author acozzolino
 * 
 */
public class DbProperties {

  private DbConnectionHandler dbConnectionHandler;

  public DbProperties() {
    this.dbConnectionHandler = new DbConnectionHandler();
  }

  public DbConnectionHandler getConnectionHandler() {
    return dbConnectionHandler;
  }

  /**
   * @param table
   * @return lista delle chiavi primarie della tabella <i>table</i>
   * @throws SQLException
   */
  public List<BaseModelData> getPrimaryKeys(String table) throws SQLException {
    List<BaseModelData> primaryKeys = new ArrayList<BaseModelData>();

    Connection connection = null;
    try {
      connection = dbConnectionHandler.getConn();
    } catch (HiddenException e) {
      // TODO re-throw HiddenException to be caught by caller
      Log.error("Error con database connection", e);
    }

    try {
      DatabaseMetaData dbmt = connection.getMetaData();
      ResultSet infoPrimaryKey =
          dbmt.getPrimaryKeys(connection.getCatalog(), null, table);

      while (infoPrimaryKey.next()) {
        BaseModelData pk = new BaseModelData();
        pk.set("TABLE_NAME", infoPrimaryKey.getString("TABLE_NAME"));
        pk.set("PK_NAME", infoPrimaryKey.getString("COLUMN_NAME"));
        Log.debug("Primary key per: " + table + " -->"
            + infoPrimaryKey.getString("TABLE_NAME") + "."
            + infoPrimaryKey.getString("COLUMN_NAME"));
        primaryKeys.add(pk);
      }
    } catch (SQLException e) {
      Log.warn("Errore durante il lookup per le chiavi primarie");
      throw e;
    } finally {
      dbConnectionHandler.closeConn(connection);
    }
    return primaryKeys;
  }

  public List<BaseModelData> getForeignKeys(String table) throws SQLException {
    List<BaseModelData> foreignKeys = new ArrayList<BaseModelData>();

    Connection connection = null;
    try {
      connection = dbConnectionHandler.getConn();
    } catch (HiddenException e) {
      // TODO re-throw HiddenException to be caught by caller
      Log.error("Error con database connection", e);
    }

    try {
      DatabaseMetaData dbmt = connection.getMetaData();
      ResultSet infoForeignKey =
          dbmt.getImportedKeys(connection.getCatalog(), null, table);

      while (infoForeignKey.next()) {
        BaseModelData fk = new BaseModelData();

        fk.set("FIELD", infoForeignKey.getString("FKCOLUMN_NAME"));
        fk.set("FOREIGN_KEY", infoForeignKey.getString("PKTABLE_NAME") + "."
            + infoForeignKey.getString("PKCOLUMN_NAME"));

        foreignKeys.add(fk);
      }

    } catch (SQLException e) {
      Log.warn("Errore durante il lookup per i vincoli di integrità");
      throw e;
    } finally {
      dbConnectionHandler.closeConn(connection);
    }
    return foreignKeys;
  }

  public String getForeignKey(String table, String field) {
    Connection connection = null;
    try {
      connection = dbConnectionHandler.getConn();
    } catch (HiddenException e) {
      // TODO re-throw HiddenException to be caught by caller
      Log.error("Error con database connection", e);
    }

    DatabaseMetaData dbmt;
    try {
      dbmt = connection.getMetaData();

      ResultSet infoForeignKey =
          dbmt.getImportedKeys(connection.getCatalog(), null, table);
      while (infoForeignKey.next()) {
        if (infoForeignKey.getString("FKCOLUMN_NAME").compareToIgnoreCase(field) == 0) {
          dbConnectionHandler.closeConn(connection);
          return infoForeignKey.getString("PKTABLE_NAME") + "."
              + infoForeignKey.getString("PKCOLUMN_NAME");
        }
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      dbConnectionHandler.closeConn(connection);
      e.printStackTrace();
    }
    dbConnectionHandler.closeConn(connection);
    return "";

  }

  public ArrayList<String> getUniqueKeys(String table) {
    Connection connection = null;
    ArrayList<String> uniqueKeys = new ArrayList<String>();
    try {
      
      connection = dbConnectionHandler.getConn();
    } catch (HiddenException e) {
      // TODO re-throw HiddenException to be caught by caller
      Log.error("Error con database connection", e);
    }

    DatabaseMetaData dbmt;
    try {
      dbmt = connection.getMetaData();

      ResultSet infoUniqueKeys =
          dbmt.getIndexInfo(null, null, table, true, false);
      while (infoUniqueKeys.next()) {
          uniqueKeys.add(infoUniqueKeys.getString("COLUMN_NAME"));
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      dbConnectionHandler.closeConn(connection);
      e.printStackTrace();
    }
    dbConnectionHandler.closeConn(connection);
    return uniqueKeys;

  }
  
  public List<String> getFieldList(int resultset) {

    Connection connection = null;
    try {
      connection = dbConnectionHandler.getConn();
    } catch (HiddenException e) {
      // TODO re-throw HiddenException to be caught by caller
      Log.error("Error con database connection", e);
    }

    List<String> result = new ArrayList<String>();
    try {
      ResultSetMetaData metadata = getResultsetMetadata(connection, resultset);

      for (int i = 1; i <= metadata.getColumnCount(); i++) {
        String column = metadata.getColumnName(i);
        result.add(column);
      }
    } catch (SQLException e) {
      Log.warn("Impossibile recuperare i metadata del resultset " + resultset);
    } finally {
      dbConnectionHandler.closeConn(connection);
    }
    return result;

  }

  /**
   * Restituisce i metadata associati al resultset
   * 
   * @param connection
   * @param resultset
   *          il resultset che definisce una query da effettuare su database
   * @return
   * @throws SQLException
   */
  public ResultSetMetaData getResultsetMetadata(Connection connection,
      int resultset) throws SQLException {
    //String query = "SELECT * FROM " + getStatement(resultset) + " WHERE 0";
    String query = getStatement(resultset);
    ResultSet result = DbUtils.doQuery(connection, query);
    return result.getMetaData();
  }

  /**
   * @param resultset
   * @return ritorna lo statement SQL per il resultSet il cui id è passato come
   *         parametro
   * @throws SQLException
   */
  public String getStatement(int resultset) throws SQLException {
    String statement;
    Connection connection = null;
    try {
      connection = dbConnectionHandler.getConn();
    } catch (HiddenException e) {
      // TODO re-throw HiddenException to be caught by caller
      Log.error("Error con database connection", e);
    }

    String query =
        "SELECT statement FROM " + DbUtils.T_RESULTSET + " WHERE id = "
            + resultset;

    try {
      ResultSet result = DbUtils.doQuery(connection, query);
      result.next();
      /*if (dbConnectionHandler.getView().compareToIgnoreCase("enabled") == 0) {
        statement = "`" + result.getString(1) + "`";
      } else {*/
        //statement = "(" + result.getString(1) + ") AS query";
      statement = result.getString(1);
      if (statement.toLowerCase().indexOf("where")==-1){
        statement = statement+" WHERE 1 ";
      }
      //}
    } catch (SQLException e) {
      throw e;
    } finally {
      dbConnectionHandler.closeConn(connection);
    }

    return statement;
  }

  public int getTableNumber(int resultsetId) throws HiddenException,
      SQLException {
    int tableNumber = 0;

    Connection connection = dbConnectionHandler.getConn();

    ResultSetMetaData metadata = getResultsetMetadata(connection, resultsetId);
    int columns = metadata.getColumnCount();

    String lastName = null;
    for (int i = 1; i <= columns; i++) {
      if (!(metadata.getTableName(i).compareToIgnoreCase(lastName) == 0)) {
        tableNumber++;
        lastName = metadata.getTableName(i);
      }
    }
    dbConnectionHandler.closeConn(connection);
    return tableNumber;

  }

  public List<String> getResultsetTableList(int resultsetId)
      throws SQLException {
    Connection connection = null;
    try {
      connection = dbConnectionHandler.getConn();
    } catch (HiddenException e) {
      // TODO re-throw HiddenException to be caught by caller
      Log.error("Error con database connection", e);
    }

    ResultSetMetaData metadata = getResultsetMetadata(connection, resultsetId);

    String lastName = "";
    List<String> tableList = new ArrayList<String>();
    for (int i = 1; i <= metadata.getColumnCount(); i++) {
      if (!(metadata.getTableName(i).compareToIgnoreCase(lastName) == 0)) {
        tableList.add(metadata.getTableName(i));
        lastName = metadata.getTableName(i);
      }
    }

    dbConnectionHandler.closeConn(connection);
    return tableList;
  }

  public List<BaseModelData> getResultsetForeignKeys(int resultsetId)
      throws SQLException, HiddenException {
    Connection connection = dbConnectionHandler.getConn();

    List<BaseModelData> foreignKeys = new ArrayList<BaseModelData>();
    try {
      DatabaseMetaData dbmt = connection.getMetaData();

      for (String table : getResultsetTableList(resultsetId)) {
        ResultSet infoForeignKey =
            dbmt.getImportedKeys(connection.getCatalog(), null, table);

        while (infoForeignKey.next()) {
          BaseModelData fk = new BaseModelData();

          fk.set("FIELD", infoForeignKey.getString("FKCOLUMN_NAME"));
          fk.set("FOREIGN_KEY", infoForeignKey.getString("PKTABLE_NAME") + "."
              + infoForeignKey.getString("PKCOLUMN_NAME"));

          foreignKeys.add(fk);
        }
      }

    } catch (SQLException e) {
      Log.warn("Errore durante il lookup per i vincoli di integrità");
      throw e;
    } finally {
      dbConnectionHandler.closeConn(connection);
    }
    return foreignKeys;
  }

  public List<BaseModelData> getResultsetPrimaryKeys(int resultsetId)
      throws SQLException, HiddenException {
    List<BaseModelData> primaryKeys = new ArrayList<BaseModelData>();
    Connection connection = dbConnectionHandler.getConn();

    try {
      DatabaseMetaData dbmt = connection.getMetaData();

      for (String table : getResultsetTableList(resultsetId)) {
        ResultSet infoPrimaryKey =
            dbmt.getPrimaryKeys(connection.getCatalog(), null, table);

        while (infoPrimaryKey.next()) {
          BaseModelData pk = new BaseModelData();
          pk.set("TABLE_NAME", infoPrimaryKey.getString("TABLE_NAME"));
          pk.set("PK_NAME", infoPrimaryKey.getString("COLUMN_NAME"));
          Log.debug("Primary key per: " + table + " -->"
              + infoPrimaryKey.getString("TABLE_NAME") + "."
              + infoPrimaryKey.getString("COLUMN_NAME"));
          primaryKeys.add(pk);
        }
      }
    } catch (SQLException e) {
      Log.warn("Errore durante il lookup per le chiavi primarie");
      throw e;
    } finally {
      dbConnectionHandler.closeConn(connection);
    }
    return primaryKeys;
  }

}
