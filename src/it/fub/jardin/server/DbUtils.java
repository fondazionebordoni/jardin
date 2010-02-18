/**
 * 
 */
package it.fub.jardin.server;

import it.fub.jardin.client.exception.HiddenException;
import it.fub.jardin.client.exception.VisibleException;
import it.fub.jardin.client.model.Credentials;
import it.fub.jardin.client.model.FieldsMatrix;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.IncomingForeignKeyInformation;
import it.fub.jardin.client.model.Message;
import it.fub.jardin.client.model.MessageType;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetFieldGroupings;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.Tool;
import it.fub.jardin.client.model.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.mail.MessagingException;

import org.apache.batik.apps.svgbrowser.JSVGViewerFrame.ViewSourceAction;
import org.apache.batik.dom.util.HashTable;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

/**
 * @author acozzolino
 * 
 */
public class DbUtils {

  DbConnectionHandler dbConnectionHandler;
  DbProperties dbProperties;
  private User user;

  public DbUtils() {
    this.dbProperties = new DbProperties();
    this.dbConnectionHandler = dbProperties.getConnectionHandler();
  }

  private static final String SPECIAL_FIELD = "searchField";

  private static final String SYSTEM_PREFIX = "__system_";
  private static final char WRAP = '`';
  public static final String T_RESULTSET =
      WRAP + SYSTEM_PREFIX + "resultset" + WRAP;
  public static final String T_NOTIFY = WRAP + SYSTEM_PREFIX + "notify" + WRAP;
  public static final String T_TOOLBAR =
      WRAP + SYSTEM_PREFIX + "toolbar" + WRAP;
  public static final String T_USER = WRAP + SYSTEM_PREFIX + "user" + WRAP;
  public static final String T_GROUP = WRAP + SYSTEM_PREFIX + "group" + WRAP;
  public static final String T_MANAGEMENT =
      WRAP + SYSTEM_PREFIX + "management" + WRAP;
  public static final String T_RESOURCE =
      WRAP + SYSTEM_PREFIX + "resource" + WRAP;
  public static final String T_FIELD = WRAP + SYSTEM_PREFIX + "field" + WRAP;
  public static final String T_HEADERPREFERENCE =
      WRAP + SYSTEM_PREFIX + "headerpreference" + WRAP;
  public static final String T_FIELDINPREFERENCE =
      WRAP + SYSTEM_PREFIX + "fieldinpreference" + WRAP;
  public static final String T_GROUPING =
      WRAP + SYSTEM_PREFIX + "grouping" + WRAP;
  public static final String T_USERMESSAGES =
      WRAP + SYSTEM_PREFIX + "userwarnings" + WRAP;
  public static final String T_GROUPMESSAGES =
      WRAP + SYSTEM_PREFIX + "groupwarnings" + WRAP;
  public static final String T_MESSAGES =
      WRAP + SYSTEM_PREFIX + "messages" + WRAP;

  /**
   * Logga le operazioni su database dell'utente. Ogni messaggio sarà preceduto
   * dall'identificativo dell'utente
   * 
   * @param message
   *          il messaggio da loggare
   */
  private void log(String message) {
    Log.info("[" + this.user.getUsername() + "] " + message);
  }

  public static ResultSet doQuery(Connection connection, String query)
      throws SQLException {

    PreparedStatement ps =
        (PreparedStatement) connection.prepareStatement(query);
    return ps.executeQuery();
  }

  private ResultSet doUpdate(Connection connection, String query)
      throws SQLException {
    Statement update =
        connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
            ResultSet.CONCUR_READ_ONLY);
    update.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
    return update.getGeneratedKeys();
  }

  private void updateLoginCount(int userId, int loginCount)
      throws SQLException, HiddenException {
    Connection connection = dbConnectionHandler.getConn();
    String query =
        "UPDATE " + T_USER
            + " SET logincount=?, lastlogintime=NOW() WHERE id=?";
    try {
      PreparedStatement ps =
          (PreparedStatement) connection.prepareStatement(query);
      ps.setInt(1, loginCount);
      ps.setInt(2, userId);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw e;
    } finally {
      dbConnectionHandler.closeConn(connection);
    }
  }

  public void updateUserProperties(User user) throws HiddenException {
    Connection connection = dbConnectionHandler.getConn();
    String query =
        "UPDATE " + T_USER + " SET" + " password=PASSWORD('"
            + user.getPassword() + "'), name='" + user.getName()
            + "', surname='" + user.getSurname() + "', email='"
            + user.getEmail() + "', office='" + user.getOffice()
            + "', telephone='" + user.getTelephone() + "', id_group='"
            + user.getGid() + "' WHERE id = '" + user.getUid() + "'";
    try {
      doUpdate(connection, query);
    } catch (SQLException e) {
      Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il salvataggio delle preferenze");
    } finally {
      dbConnectionHandler.closeConn(connection);
    }
  }

  public List<Integer> getUserResultsetHeaderPrefereces(Integer uid,
      Integer rsid) {
    String query =
        "SELECT fip.id_field as fieldid FROM " + "(((" + T_FIELD + " f JOIN "
            + T_FIELDINPREFERENCE + " fip " + "ON (fip.id_field=f.id)) "
            + "JOIN " + T_HEADERPREFERENCE + " hp ON "
            + "(hp.id=fip.id_headerpreference)) " + "JOIN " + T_USER + " u "
            + "ON (hp.id_user=u.id)) " + "WHERE u.id = '" + uid
            + "' AND f.id_resultset='" + rsid + "'";

    Connection connection = null;
    try {
      connection = dbConnectionHandler.getConn();
    } catch (HiddenException e) {
      // TODO re-throw HiddenException to be caught by caller
      Log.error("Error con database connection", e);
    }

    List<Integer> hp = new ArrayList<Integer>();
    try {
      ResultSet resultset = doQuery(connection, query);
      while (resultset.next()) {
        hp.add(resultset.getInt("fieldid"));
      }
    } catch (SQLException e) {
      // TODO throw a HiddenException to be caught by caller
      Log.error("Error on loading user resultset preferences", e);
    }

    dbConnectionHandler.closeConn(connection);
    return hp;
  }

  private String createSearchQuery(PagingLoadConfig config,
      SearchParams searchParams) throws SQLException {

    // TODO like può essere recuperato, se necessario, da searchParams;

    boolean like = !(searchParams.getAccurate());

    Integer id = searchParams.getResultsetId();
    List<BaseModelData> fieldList = searchParams.getFieldsValuesList();

    /*
     * Trasformazione di List<BaseModelData> in Map<String, String>
     */

    Map<String, String> fields = getMapFromListModelData(fieldList);

    String query = dbProperties.getStatement(id);
    // query = "SELECT * FROM " + query + " WHERE 1";

    /*
     * Gestione parte WHERE della query
     */

    for (String key : fields.keySet()) {
      String value = fields.get(key);

      if (value.length() > 0) {
        StringTokenizer stringTokenizer = new StringTokenizer(value, "|");

        if (key.compareTo(SPECIAL_FIELD) != 0) {
          /* Gestione campo normale */
          query += " AND (0";
          while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            query += fieldTest(key, "OR", token, like);
          }
          query += ")";
        } else {
          /* Gestione campo speciale */
          while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            query +=
                fieldTest(dbProperties.getFieldList(id), "OR", token, like);
          }
        }

      }
    }

    /*
     * Gestione configurazione di ricerca (SORT e LIMIT)
     */
    if (config != null) {
      if (config.getSortInfo().getSortField() != null) {
        query +=
            " ORDER BY `" + config.getSortInfo().getSortField() + "` "
                + config.getSortInfo().getSortDir();
      }

      if (config.getLimit() != -1) {
        query +=
            " LIMIT " + ((PagingLoadConfig) config).getOffset() + ","
                + ((PagingLoadConfig) config).getLimit();
      }
    }

    Log.debug("Search Query: " + query);
    return query;
  }

  private String createSearchQueryForCount(PagingLoadConfig config,
      SearchParams searchParams) throws SQLException {

    // TODO like può essere recuperato, se necessario, da searchParams;

    boolean like = !(searchParams.getAccurate());

    Integer id = searchParams.getResultsetId();
    List<BaseModelData> fieldList = searchParams.getFieldsValuesList();

    /*
     * Trasformazione di List<BaseModelData> in Map<String, String>
     */

    Map<String, String> fields = getMapFromListModelData(fieldList);

    String query = dbProperties.getStatement(id);
    // query = "SELECT * FROM " + query + " WHERE 1";

    int startPartialQuery = query.toLowerCase().indexOf("from");
    String partialQuery = query.substring(startPartialQuery);
    query = "SELECT COUNT(*) " + partialQuery;
    /*
     * Gestione parte WHERE della query
     */

    for (String key : fields.keySet()) {
      String value = fields.get(key);

      if (value.length() > 0) {
        StringTokenizer stringTokenizer = new StringTokenizer(value, "|");

        if (key.compareTo(SPECIAL_FIELD) != 0) {
          /* Gestione campo normale */
          query += " AND (0";
          while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            query += fieldTest(key, "OR", token, like);
          }
          query += ")";
        } else {
          /* Gestione campo speciale */
          while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            query +=
                fieldTest(dbProperties.getFieldList(id), "OR", token, like);
          }
        }

      }
    }

    /*
     * Gestione configurazione di ricerca (SORT e LIMIT)
     */
    if (config != null) {
      if (config.getSortInfo().getSortField() != null) {
        query +=
            " ORDER BY `" + config.getSortInfo().getSortField() + "` "
                + config.getSortInfo().getSortDir();
      }

      if (config.getLimit() != -1) {
        query +=
            " LIMIT " + ((PagingLoadConfig) config).getOffset() + ","
                + ((PagingLoadConfig) config).getLimit();
      }
    }

    Log.debug("Search Query: " + query);
    return query;
  }

  /**
   * @param fields
   * @param operation
   * @param value
   * @param like
   * @return Restituisce una stringa compresa tra parentesi che rappresenta la
   *         ricerca del valore <i>value</i> in tutti i campi desiderati
   *         combinati con l'operazione <i>operation</i>
   */
  private String fieldTest(List<String> fields, String operation, String value,
      boolean like) {
    String result = "";

    if (operation.compareToIgnoreCase("OR") == 0) {
      result += " AND (0";
    } else if (operation.compareToIgnoreCase("AND") == 0) {
      result += " OR (1";
    } else {
      return "";
    }

    for (String field : fields) {
      result += fieldTest(field, operation, value, like);
    }
    result += ")";

    return result;
  }

  private String fieldTest(String field, String operation, String value,
      boolean like) {
    String operator = " = ";
    String wrapper = "";
    if (like) {
      operator = " LIKE ";
      wrapper = "%";
    }
    value = '"' + wrapper + value + wrapper + '"';
    field = "`" + field + "`";

    return " " + operation + " " + field + operator + value;
  }

  /**
   * Esegue una query su database e restituisce la lista dei record sotto forma
   * di List<BaseModelData>. Le proprietà del ModelData sono inviate tramite
   * BaseModelData vedi: http://extjs.com/deploy/gxtdocs/com/extjs/gxt/ui/client
   * /data/BaseModelData.html#BaseModelData(java.util.Map) se config == null ->
   * restituisce l'intero store, altrimenti rispetta la paginazione
   * 
   * @param config
   * @param searchParams
   * @return
   */
  // TODO Spostare la gestione dell'eccezione al chiamante della funzione
  public List<BaseModelData> getObjects(PagingLoadConfig config,
      SearchParams searchParams) {

    List<BaseModelData> records = new ArrayList<BaseModelData>();
    Connection connection = null;
    try {
      String query = createSearchQuery(config, searchParams);
      connection = dbConnectionHandler.getConn();
      ResultSet result = doQuery(connection, query);
      int resultWidth = result.getMetaData().getColumnCount();
      log(query);
      ResultSet res =
          connection.getMetaData().getColumns(null, null,
              result.getMetaData().getTableName(1), null);

      HashTable types = new HashTable();
      while (res.next()) {
        // System.out.println(
        // "  "+res.getString("TABLE_SCHEM")
        // + ", "+res.getString("TABLE_NAME")
        // + ", "+res.getString("COLUMN_NAME")
        // + ", "+res.getString("TYPE_NAME")
        // + ", "+res.getInt("COLUMN_SIZE")
        // + ", "+res.getString("NULLABLE"));
        types.put(res.getString("COLUMN_NAME"), res.getString("TYPE_NAME"));
      }

      while (result.next()) {
        BaseModelData map = new BaseModelData();
        // WARNING la prima colonna di una tabella ha indice 1 (non 0)
        for (int i = 1; i <= resultWidth; i++) {
          String key = result.getMetaData().getColumnLabel(i);
          Object value = result.getObject(i);
          if (value != null) {
            if (((String) types.get(key)).compareToIgnoreCase("VARBINARY") == 0) {
              value = new String(result.getBytes(i));
            } else if (((String) types.get(key)).compareToIgnoreCase("bigdecimal") == 0) {
              value = ((BigDecimal) value).floatValue();
            } else {
              value = value.toString();
            }
          }
          // TODO Inserire un controllo di compatibilità di conversione dati
          // SQL->JDBC
          // Eg. se DATE non è una data ammissibile (eg. 0000-00-00) viene
          // sollevata un'eccezione e la query non prosegue

          // Object value = result.getObject(i);
          // if (value != null) {
          // if (value instanceof BigDecimal) {
          // value = ((BigDecimal) value).floatValue();
          // } else if (value.getClass().toString().contains("class [B")) {
          // // TODO trovare un modo migliore per accorgersi che il l'oggetto
          // // recuperato sia un byte[]
          // value = new String(result.getBytes(i));
          // } else {
          // value = value.toString();
          // }
          // }
          // System.out.println(key + ": " + value.getClass());
          map.set(key, value);
        }
        records.add(map);
      }
    } catch (Exception e) {
      Log.warn("Errore SQL", e);
    } finally {
      dbConnectionHandler.closeConn(connection);
    }
    return records;
  }

  /**
   * Fa diventare una matrice a tre dimensioni una di due sovrascrivendo i
   * valori delle chiavi ripetute. Questo va bene se le chiavi sono sempre
   * univoche (è il caso dei parametri di ricerca)
   */
  private Map<String, String> getMapFromListModelData(
      List<BaseModelData> fieldList) {

    Map<String, String> map = new HashMap<String, String>();

    for (BaseModelData md : fieldList) {
      for (String key : md.getPropertyNames()) {
        if (md.get(key) instanceof String) {
          map.put(key, (String) md.get(key));
        }
      }
    }

    return map;
  }

  public int countObjects(SearchParams searchParams) throws HiddenException {
    int recordSize = 0;
    Connection connection = dbConnectionHandler.getConn();

    try {
      String query = createSearchQueryForCount(null, searchParams);

      ResultSet result = doQuery(connection, query);
      result.next();
      recordSize = result.getInt(1);
    } catch (SQLException e) {
      Log.warn("Errore SQL", e);
      throw new HiddenException("Errore durante l'interrogazione del database");
    } finally {
      dbConnectionHandler.closeConn(connection);
    }

    return recordSize;
  }

  /**
   * @param resultSetId
   * @return groups
   * 
   *         Ritorna una lista di record formata da id e alias per i
   *         raggruppamenti disponibili sui campi del resultset passato come
   *         parametro
   * @throws HiddenException
   * @throws SQLException
   */
  public List<BaseModelData> getReGroupings(int resultSetId)
      throws HiddenException, SQLException {
    // TODO modificare la funzione: creare un oggetto per i raggruppamenti
    Connection connection = dbConnectionHandler.getConn();
    String groupingQuery =
        "SELECT DISTINCT " + T_GROUPING + ".id as id, " + T_GROUPING
            + ".name as name, " + T_GROUPING + ".alias as alias " + "FROM ("
            + T_GROUPING + " JOIN " + T_FIELD + " ON " + T_GROUPING + ".id = "
            + T_FIELD + ".id_grouping) " + "WHERE " + T_FIELD
            + ".id_resultset = '" + resultSetId + "'";

    List<BaseModelData> groups = new ArrayList<BaseModelData>();
    try {
      ResultSet result = doQuery(connection, groupingQuery);
      while (result.next()) {
        BaseModelData m = new BaseModelData();
        m.set("id", result.getInt("id"));
        m.set("alias", result.getString("alias"));
        groups.add(m);
      }
    } catch (SQLException e) {
      throw e;
    } finally {
      dbConnectionHandler.closeConn(connection);
    }
    return groups;
  }

  /**
   * @param resultsetId
   * @param fieldName
   * @return Lista dei valori per quel determinato campo di quel determinato
   *         resultset. Serve a riempire i combobox per l'autocompletamento
   * @throws SQLException
   * @throws HiddenException
   */
  private List<BaseModelData> getValuesOfAField(String resultset,
      String fieldName) throws SQLException, HiddenException {
    List<BaseModelData> autoCompleteList = new ArrayList<BaseModelData>();
    Connection connection = dbConnectionHandler.getConn();
    try {
      String query =
          "SELECT DISTINCT `" + fieldName + "` FROM " + resultset
              + " ORDER BY `" + fieldName + "` ASC";
      ResultSet res = doQuery(connection, query);
      while (res.next()) {
        BaseModelData m = new BaseModelData();
        m.set(fieldName, res.getString(fieldName));
        autoCompleteList.add(m);
      }
    } catch (SQLException e) {
      throw e;
    } finally {
      dbConnectionHandler.closeConn(connection);
    }
    return autoCompleteList;
  }

  /**
   * @param resultsetId
   * @param fieldName
   * @return Lista dei valori per quel determinato campo di quel determinato
   *         resultset. Serve a riempire i combobox per l'autocompletamento
   * @throws HiddenException
   */
  public List<BaseModelData> getValuesOfAField(int resultsetId, String fieldName)
      throws HiddenException {
    try {
      return getValuesOfAField(dbProperties.getStatement(resultsetId),
          fieldName);
    } catch (SQLException e) {
      Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero dei valori di campo");
    }
  }

  /**
   * @param tableName
   * @param fieldName
   * @return Lista dei valori per quel determinato campo di quell determinata
   *         tabella. Serve a riempire i combobox per la modifica della griglia
   * @throws HiddenException
   * @throws SQLException
   */
  public List<BaseModelData> getValuesOfAFieldFromTableName(String tableName,
      String fieldName) throws HiddenException {
    try {
      return getValuesOfAField("`" + tableName + "`", fieldName);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new HiddenException("Errore con i valori della chiave primaria "
          + e.getLocalizedMessage());
    }
  }

  private MessageType getMessageType(String type) {

    for (MessageType m : MessageType.values()) {
      if (type.compareToIgnoreCase(m.toString()) == 0) {
        return m;
      }
    }

    return null;
  }

  public List<Message> getUserMessages(int uid) throws HiddenException {
    List<Message> messages = new ArrayList<Message>();
    Connection connection = dbConnectionHandler.getConn();

    String query =
        "(SELECT m.* FROM " + T_MESSAGES + " m JOIN " + T_USER + " u "
            + " ON (u.id = m.recipient OR u.id_group = m.recipient)"
            + " WHERE u.id = " + uid + " ) UNION ( SELECT m.* FROM "
            + T_MESSAGES + " m WHERE m.type = 'ALL' ) ORDER BY date DESC";

    try {
      ResultSet result = doQuery(connection, query);
      while (result.next()) {
        int id = result.getInt("id");
        String title = result.getString("title");
        String body = result.getString("body");
        Date date = result.getDate("date");
        MessageType type = getMessageType(result.getString("type"));
        int sender = result.getInt("sender");
        int recipient = result.getInt("recipient");
        Message w = new Message(id, title, body, date, type, sender, recipient);
        messages.add(w);
      }
    } catch (SQLException e) {
      Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero dei messaggi di utente");
    } finally {
      dbConnectionHandler.closeConn(connection);
    }
    return messages;
  }

  public int setObjects(Integer resultsetId, List<BaseModelData> records)
      throws HiddenException {

    log("<START> Setting records");

    int result = 0;
    Connection connection = dbConnectionHandler.getConn();
    final String sep = ",";

    String tableName = null;
    // String set = "";
    try {
      ResultSetMetaData metadata =
          dbProperties.getResultsetMetadata(connection, resultsetId);
      tableName = metadata.getTableName(1);
      for (BaseModelData record : records) {
        String set = "";
        int columns = record.getPropertyNames().size();
        for (String property : record.getPropertyNames()) {
          set += "`" + property + "`=?" + sep;
        }
        set = set.substring(0, set.length() - sep.length());

        String query =
            "INSERT INTO `" + tableName + "` SET " + set
                + " ON DUPLICATE KEY UPDATE " + set;

        Log.debug("Query INSERT: " + query);
        PreparedStatement ps =
            (PreparedStatement) connection.prepareStatement(query);
        int i = 1;
        for (String property : record.getPropertyNames()) {
          Object value = record.get(property);
          if (value != null && String.valueOf(value).length() > 0) {
            ps.setObject(i, record.get(property));
            ps.setObject(i + columns, record.get(property));
          } else {
            ps.setNull(i, java.sql.Types.NULL);
            ps.setNull(i + columns, java.sql.Types.NULL);
          }
          i++;
        }

        int num = ps.executeUpdate();
        if (num > 0) {
          String toLog = "INSERT (" + ps.toString() + ")";
          Log.debug(toLog);
          log(toLog);
        }
        result += num;
      }
    } catch (MySQLIntegrityConstraintViolationException ex) {
      String message = ex.getLocalizedMessage();
      String newMess = "";
      Log.warn("Errore SQL", ex);
      if (ex.getErrorCode() == 1062) {

        updateObjects(resultsetId, records);

      } else if (ex.getErrorCode() == 1048) {
        newMess =
            newMess.concat(ex.getErrorCode()
                + " - Errore!!! \n VINCOLO DI INTEGRITA' VIOLATO :\n" + message);
      } else if (ex.getErrorCode() == 1452) {
        newMess =
            newMess.concat(ex.getErrorCode()
                + " - Errore!!! \n VINCOLO DI FOREIGN KEY VIOLATO :\n"
                + message);
      } else {
        newMess =
            ex.getErrorCode()
                + " - Errore!!! \n Problemi sui dati da salvare :\n" + message;
      }
      throw new HiddenException(newMess);

    } catch (Exception e) {
      Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il salvataggio delle modifiche:\n"
              + e.getLocalizedMessage());
    } finally {
      log("<END> Setting records");
      dbConnectionHandler.closeConn(connection);
    }
    return result;
  }

  /*
   * public int removeObjects(int resultsetId, List<BaseModelData> records)
   * throws HiddenException {
   * 
   * log("<START> Removing objects");
   * 
   * int result = 0; Connection connection = dbConnectionHandler.getConn();
   * final String sep = " AND ";
   * 
   * try { ResultSetMetaData metadata =
   * dbProperties.getResultsetMetadata(connection, resultsetId); String
   * tableName = metadata.getTableName(1);
   * 
   * for (BaseModelData record : records) { String query = "DELETE FROM `" +
   * tableName + "` WHERE ";
   * 
   * for (String property : record.getPropertyNames()) { query += "`" + property
   * + "`=?" + sep; } query = query.substring(0, query.length() - sep.length());
   * 
   * PreparedStatement ps = (PreparedStatement)
   * connection.prepareStatement(query); int i = 1; for (String property :
   * record.getPropertyNames()) { ps.setObject(i++, record.get(property)); }
   * 
   * Log.debug("Query DELETE: " + ps); int num = ps.executeUpdate(); if (num >
   * 0) { log("DELETE (" + ps.toString() + ")"); } result += num; } } catch
   * (Exception e) { Log.warn("Errore SQL", e); throw new
   * HiddenException("Errore durante l'eliminazione dei record"); } finally {
   * log("<END> Removing objects"); dbConnectionHandler.closeConn(connection); }
   * return result; }
   */

  // Cancella una riga dalla tabella
  public Integer removeObjects(Integer resultsetId, List<BaseModelData> records)
      throws HiddenException {

    int resCode = 0;

    Connection connection = dbConnectionHandler.getConn();

    String query = new String(""), appChiavePrimaria = "";
    PreparedStatement ps = null;
    try {

      ResultSetMetaData metadata =
          dbProperties.getResultsetMetadata(connection, resultsetId);
      String tableName = metadata.getTableName(1);
      // Ciclo per gestire più cancellazioni nella stessa invocazione
      List<BaseModelData> primaryKeyList =
          dbProperties.getPrimaryKeys(tableName);
      if (primaryKeyList.size() <= 0) {
        throw new HiddenException(
            "La tabella non contiene chiavi primarie: impossibile operare!");
      }
      for (BaseModelData record : records) {
        query = "";
        // Preparazione della query
        query = query.concat("DELETE FROM `" + tableName + "` WHERE `");

        String separator = "AND `";
        for (BaseModelData pk : primaryKeyList) {
          appChiavePrimaria = pk.get("PK_NAME");
          query = query.concat(appChiavePrimaria);
          if (record.get(appChiavePrimaria) == null) {
            query = query.concat("` IS NULL ");
          } else {
            query = query.concat("` = ? ");
          }

          query = query.concat(separator);
        }

        query = query.substring(0, query.length() - separator.length());

        ps = (PreparedStatement) connection.prepareStatement(query);
        int i = 1;
        // for (String property : record.getPropertyNames()) {
        for (BaseModelData pk : primaryKeyList) {
          ps.setObject(i, record.get((String) pk.get("PK_NAME")));
          i++;
        }

        Log.debug("Query DELETE: " + ps);
        int num = ps.executeUpdate();
        if (num > 0) {
          log("DELETE (" + ps.toString() + ")");
        }
        resCode += num;
      }
    } catch (Exception e) {
      Log.warn("Errore SQL", e);
      throw new HiddenException("Errore durante l'eliminazione dei record");
    } finally {
      log("<END> Removing objects");
      dbConnectionHandler.closeConn(connection);
    }

    return (new Integer(resCode));
  }

  /**
   * @param fieldName
   * @param result
   * @return ritorna la Foreign Key per il campo il cui nome è passato come
   *         parametro, se esiste. Se non esiste, ritorna una stringa vuota.
   * @throws SQLException
   */
  private String getForeignKeyForAField(String fieldName, ResultSet result)
      throws SQLException {

    String foreignKey = null;
    String tableName = null;

    for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
      if (result.getMetaData().getColumnLabel(i).compareToIgnoreCase(fieldName) == 0) {
        tableName = result.getMetaData().getTableName(i);
        break;
      }
    }

    if (tableName == null || tableName.length() <= 0) {
      return null;
    }

    for (BaseModelData fk : dbProperties.getForeignKeys(tableName)) {
      if (fk.get("FIELD").toString().compareTo(fieldName) == 0) {
        foreignKey = fk.get("FOREIGN_KEY");
        break;
      }
    }

    return foreignKey;
  }

  /**
   * @param fieldName
   * @param result
   * @return ritorna la Foreign Key per il campo il cui nome è passato come
   *         parametro, se esiste. Se non esiste, ritorna una stringa vuota.
   * @throws SQLException
   */
  // private List<BaseModelData> getForeignKeyInfoForAResultset(String
  // resultsetName)
  // throws SQLException {
  // return dbProperties.getForeignKeys(resultsetName);
  // }

  public User getUser(Credentials credentials) throws VisibleException {

    String username = credentials.getUsername();
    String password = credentials.getPassword();

    ResultSet result;

    Connection connection;
    try {
      connection = dbConnectionHandler.getConn();
    } catch (HiddenException e) {
      throw new VisibleException(e.getLocalizedMessage());
    }

    String query =
        "SELECT u.id, u.name, u.surname, u.email, u.office, "
            + "u.telephone, u.status AS userstatus, u.lastlogintime, "
            + "u.logincount, g.id AS groupid, g.name AS groupname " + "FROM "
            + T_USER + " u JOIN " + T_GROUP + " g ON g.id = u.id_group "
            + "WHERE username = ? and password = PASSWORD(?)";

    PreparedStatement ps;
    try {
      ps = connection.prepareStatement(query);
      ps.setString(1, username);
      ps.setString(2, password);
    } catch (SQLException e) {
      throw new VisibleException("Errore nella query "
          + "per la verifica di username e password");
    }

    try {
      result = ps.executeQuery();
    } catch (SQLException e) {
      Log.debug("User validation query: " + ps.toString());
      throw new VisibleException("Errore durante l'interrogazione su database");
    }

    int rows = 0;
    try {
      while (result.next()) {
        rows++;
        if (rows > 1) {
          throw new VisibleException("Errore nel database degli utenti: "
              + "due account con username e password uguali");
        }

        /* Creazione dell'utente con i dati del database */
        int uid = result.getInt("id");
        int gid = result.getInt("groupid");
        String name = result.getString("name");
        String surname = result.getString("surname");
        String group = result.getString("groupname");
        String email = result.getString("email");
        String office = result.getString("office");
        String telephone = result.getString("telephone");
        int status = result.getInt("userstatus");
        int login = result.getInt("logincount");

        // String lastlogintime = result.getString("lastlogintime");
        DateFormat df =
            DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        String last = df.format(new Date());

        /* Carica le preferenze dell'utente */
        List<ResultsetImproved> resultsets = getUserResultsetImproved(uid, gid);

        List<Message> messages = new ArrayList<Message>();

        updateLoginCount(uid, ++login);

        User user =
            new User(uid, gid, new Credentials(username, password), name,
                surname, group, email, office, telephone, status, login, last,
                resultsets, messages);
        this.user = user;
        return user;
      }
    } catch (Exception e) {
      Log.warn("Errore SQL", e);
      throw new VisibleException("Errore di accesso "
          + "al risultato dell'interrogazione su database");
    } finally {
      dbConnectionHandler.closeConn(connection);
    }
    throw new VisibleException("Errore di accesso: username o password errati");
  }

  /**
   * @param resultsetId
   * @return Crea una fieldMatrix che contiene i valori di autocompletamento per
   *         ogni campo del resultset passato per parametro. I valori di
   *         autocompletamento, sono quelli già presenti in tabella per ognuno
   *         dei campi
   * @throws HiddenException
   */
  public FieldsMatrix getValuesOfFields(Integer resultsetId)
      throws HiddenException {
    FieldsMatrix matrix = new FieldsMatrix();

    try {
      HashMap<Integer, String> rsf = getResultsetFields(resultsetId);
      for (Integer fieldId : rsf.keySet()) {
        List<BaseModelData> autoCompleteList =
            getValuesOfAField(resultsetId, rsf.get(fieldId));
        List<String> values = new ArrayList<String>();
        for (BaseModelData fieldValue : autoCompleteList) {
          values.add((String) fieldValue.get(rsf.get(fieldId)));
        }
        matrix.addField(fieldId, values);
      }
    } catch (SQLException e) {
      Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero dei valori dei campi");
    }

    return matrix;
  }

  private HashMap<Integer, String> getResultsetFields(int resultsetId)
      throws SQLException, HiddenException {
    HashMap<Integer, String> fieldList = new HashMap<Integer, String>();

    Connection connection = dbConnectionHandler.getConn();
    String query =
        "SELECT res.id as id, res.name as name FROM " + T_RESOURCE
            + " res JOIN " + T_FIELD
            + " f ON res.id = f.id WHERE f.id_resultset = " + resultsetId;

    try {
      ResultSet result = doQuery(connection, query);
      while (result.next()) {
        fieldList.put(result.getInt("id"), result.getString("name"));
      }
    } catch (SQLException e) {
      throw e;
    } finally {
      dbConnectionHandler.closeConn(connection);
    }
    return fieldList;
  }

  /**
   * @param resultsetId
   * @return Crea una fieldMatrix che contiene i valori di autocompletamento per
   *         ogni campo del resultset passato per parametro. I valori di
   *         autocompletamento, sono quelli già presenti in tabella per ognuno
   *         dei campi
   * @throws HiddenException
   */
  public FieldsMatrix getValuesOfForeignKeys(Integer resultsetId)
      throws HiddenException {

    FieldsMatrix matrix = new FieldsMatrix();
    Connection connection = dbConnectionHandler.getConn();

    try {
      HashMap<Integer, String> rsf = getResultsetFields(resultsetId);
      // String query =
      // "SELECT * FROM " + dbProperties.getStatement(resultsetId)
      // + " WHERE 0";
      String query = dbProperties.getStatement(resultsetId);
      ResultSet resultset = doQuery(connection, query);

      for (Integer fieldId : rsf.keySet()) {
        String foreignKey = getForeignKeyForAField(rsf.get(fieldId), resultset);
        String fkTName;
        String fkFName;
        List<BaseModelData> autoCompleteList = new ArrayList<BaseModelData>();
        if (foreignKey != null && foreignKey.length() > 0) {
          fkTName = foreignKey.split("\\.")[0];
          fkFName = foreignKey.split("\\.")[1];

          autoCompleteList = getValuesOfAFieldFromTableName(fkTName, fkFName);

          List<String> values = new ArrayList<String>();
          for (BaseModelData fieldValue : autoCompleteList) {
            values.add((String) fieldValue.get(fkFName));
          }
          matrix.addField(fieldId, values);
          // System.out.println(fieldId + "->" + values.toString());
        }
      }
    } catch (SQLException e) {
      Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero dei valori dei vincoli");
    } finally {
      dbConnectionHandler.closeConn(connection);
    }

    return matrix;
  }

  /**
   * @param resultSetList
   * @param userId
   * @return resultSetList
   * 
   *         Ritorna le foreign key entranti
   * @throws HiddenException
   */

  public ArrayList<IncomingForeignKeyInformation> getForeignKeyInForATable(
      Integer resultsetId, List<ResultsetImproved> resultSetList)
      throws HiddenException {
    ArrayList<IncomingForeignKeyInformation> listaIfki;
    String tableName = null;
    Connection connection = dbConnectionHandler.getConn();
    String queryStatement = null;
    String query =
        "SELECT statement FROM " + T_RESULTSET + " WHERE id = '" + resultsetId
            + "' ";
    try {
      ResultSet res = doQuery(connection, query);
      while (res.next()) {
        queryStatement = res.getString("statement");
      }
      ResultSet result = doQuery(connection, queryStatement);
      if ((result != null) && (result.getMetaData().getColumnCount() > 1)) {
        tableName = result.getMetaData().getTableName(1);
      }
      if (tableName.length() <= 0) {
        return null;
      }
      String db = dbConnectionHandler.getDB();
      Connection connectionInformationSchema =
          dbConnectionHandler.getConnDbInformationSchema();
      String queryFKIn =
          "SELECT TABLE_NAME, COLUMN_NAME, REFERENCED_COLUMN_NAME FROM KEY_COLUMN_USAGE where TABLE_SCHEMA = '"
              + db + "' AND REFERENCED_TABLE_NAME = '" + tableName + "'";
      ResultSet resultFKIn = doQuery(connectionInformationSchema, queryFKIn);

      listaIfki = new ArrayList<IncomingForeignKeyInformation>();

      while (resultFKIn.next()) {
        String linkingTable = resultFKIn.getString("TABLE_NAME");
        String linkingField = resultFKIn.getString("COLUMN_NAME");
        String field = resultFKIn.getString("REFERENCED_COLUMN_NAME");
        // trasformare la linkingTable in un rsimproved
        // dal nome recupero l'id e dall'id recupero l'rs
        for (final ResultsetImproved rs : resultSetList) {
          IncomingForeignKeyInformation ifki =
              new IncomingForeignKeyInformation(linkingTable, linkingField,
                  field);
          if (rs.getName().compareTo(ifki.getLinkingTable()) == 0) {
            ifki.setInterestedResultset(rs);
            ifki.setResultsetId(rs.getId());
            listaIfki.add(ifki);
          }
        }

      }
    } catch (SQLException e) {
      Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero delle foreign keys entranti");
    } finally {
      dbConnectionHandler.closeConn(connection);
    }

    return listaIfki;
  }

  /**
   * @param userId
   * @return resultSetList
   * 
   *         Ritorna i resultset (id, alias e statement SQL) per i quali
   *         l'utente passato come parametro ha permesso 'read' uguale a 1
   * @throws HiddenException
   */
  public List<ResultsetImproved> getUserResultsetImproved(Integer uid,
      Integer gid) throws HiddenException {

    List<ResultsetImproved> resultSetList = new ArrayList<ResultsetImproved>();
    Connection connection = dbConnectionHandler.getConn();

    // recupero i nomi delle view
    ArrayList<String> views = new ArrayList<String>();
    try {
      ResultSet rs = null;
      DatabaseMetaData meta = connection.getMetaData();
      rs = meta.getTables(null, null, null, new String[] { "VIEW" });
      while (rs.next()) {
        views.add(rs.getString("TABLE_NAME"));
      }
    } catch (SQLException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    String query =
        "SELECT res.statement as statement, r.id as resourceid, g.id AS groupid, res.id AS rsid, "
            + "f.id_resultset as resultsetid, "
            + "f.default_header as defaultheader, "
            + "f.search_grouping as searchgrouping, "
            + "f.id_grouping as idgrouping,"
            + "r.name as resourcename, "
            + "r.alias as resourcealias, m.readperm as readperm, "
            + "m.deleteperm as deleteperm, m.modifyperm as modifyperm, "
            + "m.insertperm as insertperm, f.defaultvalue as defaultvalue, f.type as type "
            + "FROM ((((("
            + T_USER
            + " u JOIN "
            + T_GROUP
            + " g ON (u.id_group=g.id)) "
            + "JOIN "
            + T_MANAGEMENT
            + " m ON (g.id = m.id_group)) "
            + "JOIN "
            + T_RESOURCE
            + " r ON r.id = m.id_resource) "
            + "LEFT JOIN "
            + T_RESULTSET
            + " res ON res.id=r.id) LEFT JOIN "
            + T_FIELD
            + " f ON  (r.id=f.id))"
            + " WHERE u.id = '"
            + uid
            + "' AND readperm = '1' " + " ORDER BY r.id ASC";
    try {

      List<ResultsetField> resultFieldList = new ArrayList<ResultsetField>();
      List<BaseModelData> PKs = null;
      ArrayList<String> UKs = new ArrayList<String>();

      ResultSet result = doQuery(connection, query);
      while (result.next()) {
        String statement = result.getString("statement");
        Integer id = Integer.valueOf(result.getInt("resourceid"));
        Integer resultsetid = Integer.valueOf(result.getInt("resultsetid"));
        boolean defaultheader = result.getBoolean("defaultheader");
        Integer searchgrouping =
            Integer.valueOf(result.getInt("searchgrouping"));
        Integer idgrouping = Integer.valueOf(result.getInt("idgrouping"));
        String name = result.getString("resourcename");
        String alias = result.getString("resourcealias");
        boolean readperm = result.getBoolean("readperm");
        boolean deleteperm = result.getBoolean("deleteperm");
        boolean modifyperm = result.getBoolean("modifyperm");
        boolean insertperm = result.getBoolean("insertperm");
        Integer rsid = Integer.valueOf(result.getInt("rsid"));
        Integer groupid = Integer.valueOf(result.getInt("groupid"));

        if (statement != null) {
          /* Gestione di un RESULTSET */

          ArrayList<Tool> tools = getToolbar(rsid, groupid);

          ResultsetImproved res = null;
          if (views.contains(name)) {
            // System.out.println(name + " è una view");
            res =
                new ResultsetImproved(id, name, alias, statement, readperm,
                    false, false, false, tools);
          } else {
            res =
                new ResultsetImproved(id, name, alias, statement, readperm,
                    deleteperm, modifyperm, insertperm, tools);
          }
          resultSetList.add(res);

          List<BaseModelData> groupings = getReGroupings(id);
          for (BaseModelData grouping : groupings) {
            ResultsetFieldGroupings rfg =
                new ResultsetFieldGroupings((Integer) grouping.get("id"),
                    (String) grouping.get("name"),
                    (String) grouping.get("alias"));
            res.addFieldGroupings(rfg);
          }

          PKs = dbProperties.getPrimaryKeys(name);
          UKs = dbProperties.getUniqueKeys(name);

        } else {
          /* Gestione di un CAMPO di un resultset */

          boolean visible = result.getInt("defaultheader") == 1;

          ResultsetField resField =
              new ResultsetField(id, name, alias, resultsetid, defaultheader,
                  searchgrouping, idgrouping, readperm, deleteperm, modifyperm,
                  insertperm, visible);

          resField.setType(result.getString("type"));
          resField.setDefaultValue(result.getString("defaultvalue"));
          resField.setIsPK(false);
          resField.setUnique(false);

          resultFieldList.add(resField);

          if (PKs != null) {
            for (BaseModelData pk : PKs) {
              if (((String) pk.get("PK_NAME")).compareToIgnoreCase(name) == 0) {
                resField.setIsPK(true);
              }
            }
          }

          if (UKs != null) {
            if (UKs.contains(name)) {
              resField.setUnique(true);
            }
          }

        }
      }

      PKs = null;

      for (int i = 0; i < resultSetList.size(); i++) {
        for (int j = 0; j < resultFieldList.size(); j++) {
          if (resultFieldList.get(j).getResultsetid() == resultSetList.get(i).getId()) {

            // aggiunta dell'eventuale foreignKEY
            resultFieldList.get(j).setForeignKey(
                dbProperties.getForeignKey(resultSetList.get(i).getName(),
                    resultFieldList.get(j).getName()));
            resultSetList.get(i).addField(resultFieldList.get(j));
          }
        }

        // aggiunta delle eventuali foreignKEY entranti
        resultSetList.get(i).setForeignKeyIn(
            this.getForeignKeyInForATable(resultSetList.get(i).getId(),
                resultSetList));
      }
    } catch (SQLException e) {
      Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero delle viste su database");
    } finally {
      dbConnectionHandler.closeConn(connection);
    }

    return resultSetList;
  }

  /**
   * Restituisce i tool associati al resultset e al gruppo di utenza
   * 
   * @param rsid
   * @param groupid
   * @return un array vuoto se non ci sono tool associati
   * @throws HiddenException
   * @throws SQLException
   */
  private ArrayList<Tool> getToolbar(Integer rsid, Integer groupid)
      throws HiddenException, SQLException {

    ArrayList<Tool> tools = new ArrayList<Tool>();
    String toolbar = null;

    Connection connection = dbConnectionHandler.getConn();
    String querytoolbar =
        "SELECT tools FROM " + T_TOOLBAR + " WHERE " + " id_resultset = "
            + rsid + " AND id_group = " + groupid + " LIMIT 0,1";
    ResultSet resulttoolbar = doQuery(connection, querytoolbar);

    while (resulttoolbar.next()) {
      toolbar = resulttoolbar.getString("tools");
    }

    if (toolbar != null) {
      StringTokenizer st = new StringTokenizer(toolbar);
      while (st.hasMoreTokens()) {
        String t = st.nextToken();
        tools.add(getTool(t));
      }
    }

    return tools;

  }

  private Tool getTool(String t) {
    if (t.compareTo(Tool.MODIFY.toString()) == 0) {
      return Tool.MODIFY;
    } else if (t.compareTo(Tool.ANALISYS.toString()) == 0) {
      return Tool.ANALISYS;
    } else if (t.compareTo(Tool.EXPORT.toString()) == 0) {
      return Tool.EXPORT;
    } else if (t.compareTo(Tool.IMPORT.toString()) == 0) {
      return Tool.IMPORT;
    } else if (t.compareTo(Tool.PREFERENCE.toString()) == 0) {
      return Tool.PREFERENCE;
    } else if (t.compareTo(Tool.ALL.toString()) == 0) {
      return Tool.ALL;
    }
    return null;
  }

  public boolean setUserResultsetHeaderPreferencesNoDefault(Integer userid,
      Integer resultsetId, ArrayList<Integer> listfields, String value)
      throws HiddenException {
    boolean esito = true;
    Connection connection = dbConnectionHandler.getConn();

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    String creationDate = formatter.format(new Date());
    String createHeaderPreference =
        "INSERT INTO " + T_HEADERPREFERENCE + " VALUES  (NULL , '" + value
            + "-" + creationDate.hashCode() + "', '" + userid + "', '"
            + creationDate + "')";

    try {
      ResultSet newRes = doUpdate(connection, createHeaderPreference);
      newRes.next();
      Integer headerPrefId = newRes.getInt(1);

      /* Crea i riferimenti in fieldinpreferences */
      for (int i = 0; i < listfields.size(); i++) {
        String newPreferences =
            "INSERT INTO " + T_FIELDINPREFERENCE + " VALUES ('" + headerPrefId
                + "', '" + listfields.get(i) + "')";
        doUpdate(connection, newPreferences);
      }
    } catch (SQLException e) {
      esito = false;
      Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero delle viste su database");
    } finally {
      dbConnectionHandler.closeConn(connection);
    }

    return esito;
  }

  public HeaderPreferenceList getHeaderUserPreferenceList(Integer idUser,
      Integer idResultset) throws HiddenException {

    HeaderPreferenceList hp = new HeaderPreferenceList();
    Connection connection = dbConnectionHandler.getConn();
    String query =
        "SELECT hp.id as idpref, hp.name as namepref FROM " + "("
            + T_HEADERPREFERENCE + " hp JOIN " + T_FIELDINPREFERENCE + " fip "
            + "ON hp.id=fip.id_headerpreference) JOIN " + T_FIELD
            + " f ON fip.id_field=f.id WHERE hp.id_user='" + idUser
            + "' AND f.id_resultset='" + idResultset + "' GROUP BY namepref";

    try {
      ResultSet result = doQuery(connection, query);
      List<BaseModelData> userPref = new ArrayList<BaseModelData>();

      while (result.next()) {
        BaseModelData bm = new BaseModelData();
        bm.set(result.getString("namepref"), result.getInt("idpref"));
        userPref.add(bm);
      }

      hp.setUserPref(userPref);
      hp.setResultsetId(idResultset);

    } catch (SQLException e) {
      Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero delle preferenze utente");
    } finally {
      dbConnectionHandler.closeConn(connection);
    }
    return hp;
  }

  public List<Integer> getHeaderUserPreference(Integer idUser,
      Integer userPreferenceHeaderId) throws HiddenException {
    List<Integer> fieldInPref = new ArrayList<Integer>();
    Connection connection = dbConnectionHandler.getConn();
    String query =
        "SELECT fip.id_field as fieldid  " + "FROM " + T_HEADERPREFERENCE
            + " hp JOIN " + T_FIELDINPREFERENCE + " fip "
            + "ON hp.id=fip.id_headerpreference WHERE hp.id = '"
            + userPreferenceHeaderId + "' AND hp.id_user='" + idUser + "'";

    try {
      ResultSet result = doQuery(connection, query);
      while (result.next()) {
        fieldInPref.add(result.getInt("fieldid"));
      }
    } catch (SQLException e) {
      Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero delle preferenze utente");
    } finally {
      dbConnectionHandler.closeConn(connection);
    }

    return fieldInPref;
  }

  public int importFile(Credentials credentials, int resultsetId,
      File importFile) throws HiddenException, VisibleException, SQLException {

    getUser(credentials);

    int opCode = 0;
    BufferedReader in = null;

    try {
      in = new BufferedReader(new FileReader(importFile));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new HiddenException("file " + importFile.getName() + " non trovato");
    }

    Connection connection = dbConnectionHandler.getConn();
    ResultSetMetaData rsmd = null;

    try {
      rsmd = dbProperties.getResultsetMetadata(connection, resultsetId);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new HiddenException(
          "impossibile recuperare metadati per resultset: " + resultsetId);
    }

    String recordLine;
    String[] columns;
    List<BaseModelData> recordList = new ArrayList<BaseModelData>();

    try {
      recordLine = in.readLine();
      columns = recordLine.replaceAll("\"", "").split("\\|");
      /*
       * check nomi dei campi corretti
       * 
       * ArrayList<Boolean> colsCheck = new ArrayList<Boolean>(); for (String
       * col : columns) { boolean present = false; for (int i = 0; i <
       * rsmd.getColumnCount(); i++) { if
       * (col.compareToIgnoreCase(rsmd.getColumnName(i)) == 0) { present = true;
       * } } colsCheck.add(present); }
       * 
       * if (colsCheck.contains(false)) { recordLine = null;
       * Log.debug("Una delle colonne non è stata riconosciuta!"); }
       */
      recordLine = in.readLine();
      while (recordLine != null) {
        Log.debug(recordLine);
        // if (validateLine(rsmd, recordLine)) {
        recordList.add(createRecord(rsmd, recordLine, columns));
        // } else
        // throw new HiddenException("record: " + recordLine +
        // " non valido!");
        recordLine = in.readLine();
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new HiddenException("impossibile leggere il file: "
          + importFile.getName());
    }

    opCode = setObjects(resultsetId, recordList);

    return opCode;
  }

  private BaseModelData createRecord(ResultSetMetaData rsmd, String recordLine,
      String[] columns) throws HiddenException {

    BaseModelData bm = new BaseModelData();
    for (int i = 0; i < columns.length; i++) {
      if (i < recordLine.split("\\|").length) {
        String value = recordLine.split("\\|")[i].replaceAll("^\"|\"$", "");
        bm.set(columns[i], value);
      }
    }
    return bm;
  }

  // private boolean validateLine(ResultSetMetaData rsmd, String recordLine)
  // throws HiddenException {
  //
  // boolean valid = false;
  // try {
  //
  // if (recordLine.split("\\|").length == rsmd.getColumnCount()) {
  // valid = true;
  // }
  // } catch (SQLException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // throw new HiddenException("problemi nella lettura dei metadata per il rs");
  // }
  //
  // return true;
  // }

  /*
   * invocata solo in caso di duplicate key entry per la setObject: si suppone
   * quindi che la chiave primaria non sia stata alterata
   */
  public Integer updateObjects(Integer resultsetId,
      List<BaseModelData> newItemList) throws HiddenException {

    log("<START> Setting records");

    int result = 0;
    Connection connection = dbConnectionHandler.getConn();
    final String sep = ",";

    try {
      ResultSetMetaData metadata =
          dbProperties.getResultsetMetadata(connection, resultsetId);
      String tableName = metadata.getTableName(1);

      List<BaseModelData> PKs = dbProperties.getPrimaryKeys(tableName);
      String PKset = "";

      for (BaseModelData record : newItemList) {

        for (BaseModelData pk : PKs) {
          PKset =
              PKset.concat((String) pk.get("PK_NAME") + "="
                  + record.get((String) pk.get("PK_NAME")) + " AND ");
        }

        PKset = PKset.substring(0, PKset.length() - 5);

        String set = "";

        for (String property : record.getPropertyNames()) {
          set += "`" + property + "`=?'" + sep;
        }

        set = set.substring(0, set.length() - sep.length());

        String query =
            "UPDATE `" + tableName + "` SET " + set + " WHERE " + PKset;

        PreparedStatement ps =
            (PreparedStatement) connection.prepareStatement(query);
        int i = 1;
        for (String property : record.getPropertyNames()) {
          ps.setObject(i, record.get(property));
          // ps.setObject(i + columns, record.get(property));
          if (i < record.getPropertyNames().size()) {
            i++;
          }
        }

        Log.debug("Query UPDATE: " + ps);

        int num = ps.executeUpdate();

        if (num > 0) {
          log("UPDATE (" + ps.toString() + ")");
        }
        result += num;

      }
    } catch (Exception e) {
      Log.warn("Errore SQL", e);
      throw new HiddenException("Errore durante l'aggiornamento del record:\n"
          + e.getLocalizedMessage());
    } finally {
      log("<END> Setting records");
      dbConnectionHandler.closeConn(connection);
    }
    return result;
  }

  public void notifyChanges(Integer resultsetId, List<BaseModelData> newItemList)
      throws SQLException, HiddenException {
    Integer id_table = 0;
    String mitt = "notReplyJardin@fub.it";
    String oggetto = "Situazione pratica";
    Connection connection = dbConnectionHandler.getConn();

    String query =
        "SELECT address_statement, data_statement, link_id FROM " + T_NOTIFY
            + " WHERE id_resultset = '" + resultsetId + "'";

    Log.debug("query: " + query);

    ResultSet result = doQuery(connection, query);
    while (result.next()) {
      String testo = "";
      String address_statement = result.getString(1);
      String data_statement = result.getString(2);
      String bmdid = result.getString(3);
      // Log.debug("bmdid"+bmdid);

      for (BaseModelData record : newItemList) {
        if (record.get(bmdid) != null) {
          id_table = Integer.valueOf(record.get(bmdid).toString());
          PreparedStatement psData =
              (PreparedStatement) connection.prepareStatement(data_statement);
          psData.setInt(1, id_table);
          ResultSet resultData = psData.executeQuery();
          while (resultData.next()) {
            ResultSetMetaData md = resultData.getMetaData();
            int count = md.getColumnCount();
            for (int i = 1; i <= count; i++) {
              testo +=
                  md.getColumnLabel(i) + ": " + resultData.getString(i) + "\n";
            }
            Log.debug("\nmessaggio:\n" + testo);
            testo += "\n";
          }

          PreparedStatement ps =
              (PreparedStatement) connection.prepareStatement(address_statement);
          ps.setInt(1, id_table);
          ResultSet resultAddress = ps.executeQuery();
          while (resultAddress.next()) {
            Log.debug("mailto: " + resultAddress.getString(1));
            if (!(resultAddress.getString(1) == null)) {
              try {
                MailUtility.sendMail(resultAddress.getString(1), mitt, oggetto,
                    testo);

              } catch (MessagingException e) {
                Log.info("Invio non riuscito!");
                Log.info("MessagingException: ");
                Log.info(e.toString());
              }
              Log.info("Invio riuscito!");
            } else {
              Log.info("Mail non valida!");
            }
          }
        } else {
          Log.error("Notifica non inviata perchè è un inserimento!");
        }
      }
    }
  }

  public void sendMessage(Message message) throws HiddenException,
      VisibleException {

    String query =
        "INSERT INTO "
            + T_MESSAGES
            + " (title, body, date, type, sender, recipient) VALUES (?, ?, ?, ?, ?, ?)";

    Connection connection = dbConnectionHandler.getConn();

    PreparedStatement ps;
    try {
      ps = (PreparedStatement) connection.prepareStatement(query);
      // TODO Title could be NULL -> check sql
      ps.setString(1, message.getTitle());
      ps.setString(2, message.getBody());
      Log.debug(message.toString());

      DateFormat df =
          new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
      String date = df.format(message.getDate());
      ps.setString(3, date);

      ps.setString(4, message.getType().type());
      ps.setInt(5, message.getSender());
      ps.setInt(6, message.getRecipient());
      ps.executeUpdate();
    } catch (SQLException e) {
      Log.error("Error during new message insertion", e);
      throw new VisibleException("Impossibile salvare il messaggio");
    } finally {
      dbConnectionHandler.closeConn(connection);
    }
  }

  public ArrayList<BaseModelData> getPopUpDetailEntry(BaseModelData data)
      throws HiddenException {
    ResultsetImproved rs = data.get("RSLINKED");
    String linkingField = data.get("FK");
    String linkingValue = "" + data.get("VALUE");
    String queryStatement = getStatementByResultsetId(rs.getId());
    String query =
        "SELECT * FROM (" + queryStatement + ") AS entry WHERE " + linkingField
            + " = '" + linkingValue + "' LIMIT 1";
    Connection connection = dbConnectionHandler.getConn();
    ResultSet result;
    BaseModelData row = new BaseModelData();
    try {
      result = doQuery(connection, query);
      int resultWidth = result.getMetaData().getColumnCount();
      while (result.next()) {
        // WARNING la prima colonna di una tabella ha indice 1 (non 0)
        for (int i = 1; i <= resultWidth; i++) {
          String key = result.getMetaData().getColumnLabel(i);
          row.set(key, result.getObject(i));
          // Log.debug(key + "=" + result.getObject(i));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    dbConnectionHandler.closeConn(connection);
    ArrayList<BaseModelData> infoToView = new ArrayList<BaseModelData>();
    infoToView.add(data);
    infoToView.add(row);
    return infoToView;
  }

  public String getStatementByResultsetId(Integer rsid) throws HiddenException {
    String query =
        "SELECT statement FROM " + T_RESULTSET + " WHERE id = " + rsid;
    Connection connection = dbConnectionHandler.getConn();
    ResultSet result;
    String queryStatement = null;
    try {
      result = doQuery(connection, query);
      result.next();
      queryStatement = result.getString(1);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    dbConnectionHandler.closeConn(connection);
    return queryStatement;
  }
}