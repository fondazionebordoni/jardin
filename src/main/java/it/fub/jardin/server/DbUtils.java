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
import it.fub.jardin.client.model.Credentials;
import it.fub.jardin.client.model.FieldsMatrix;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.IncomingForeignKeyInformation;
import it.fub.jardin.client.model.MassiveUpdateObject;
import it.fub.jardin.client.model.Message;
import it.fub.jardin.client.model.MessageType;
import it.fub.jardin.client.model.Plugin;
import it.fub.jardin.client.model.RegistrationInfo;
import it.fub.jardin.client.model.ResourcePermissions;
import it.fub.jardin.client.model.Resultset;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetFieldGroupings;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.ResultsetPlus;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.Tool;
import it.fub.jardin.client.model.User;
import it.fub.jardin.client.widget.UploadDialog;
import it.fub.jardin.server.tools.JardinLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.mail.MessagingException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.Ostermiller.util.CSVParser;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

//import com.allen_sauer.gwt.log.client.Log;

public class DbUtils {

  DbConnectionHandler dbConnectionHandler;
  DbProperties dbProperties;
  private User user;

  public DbUtils(DbProperties dbProperties,
      DbConnectionHandler dbConnectionHandler) throws VisibleException {
    this.dbProperties = dbProperties;
    this.dbConnectionHandler = dbConnectionHandler;
  }

  private static final String SPECIAL_FIELD = "searchField";

  private static final String SYSTEM_PREFIX = "__system_";
  private static final char WRAP = '`';
  public static final String T_RESULTSET = WRAP + SYSTEM_PREFIX + "resultset"
      + WRAP;
  public static final String T_NOTIFY = WRAP + SYSTEM_PREFIX + "notify" + WRAP;
  public static final String T_TOOLBAR = WRAP + SYSTEM_PREFIX + "toolbar"
      + WRAP;
  public static final String T_PLUGIN = WRAP + SYSTEM_PREFIX + "plugin" + WRAP;
  public static final String T_PLUGINASSOCIATION = WRAP + SYSTEM_PREFIX
      + "pluginassociation" + WRAP;
  public static final String T_USER = WRAP + SYSTEM_PREFIX + "user" + WRAP;
  public static final String T_GROUP = WRAP + SYSTEM_PREFIX + "group" + WRAP;
  public static final String T_MANAGEMENT = WRAP + SYSTEM_PREFIX + "management"
      + WRAP;
  public static final String T_RESOURCE = WRAP + SYSTEM_PREFIX + "resource"
      + WRAP;
  public static final String T_FIELD = WRAP + SYSTEM_PREFIX + "field" + WRAP;
  public static final String T_HEADERPREFERENCE = WRAP + SYSTEM_PREFIX
      + "headerpreference" + WRAP;
  public static final String T_FIELDINPREFERENCE = WRAP + SYSTEM_PREFIX
      + "fieldinpreference" + WRAP;
  public static final String T_GROUPING = WRAP + SYSTEM_PREFIX + "grouping"
      + WRAP;
  public static final String T_USERMESSAGES = WRAP + SYSTEM_PREFIX
      + "userwarnings" + WRAP;
  public static final String T_GROUPMESSAGES = WRAP + SYSTEM_PREFIX
      + "groupwarnings" + WRAP;
  public static final String T_MESSAGES = WRAP + SYSTEM_PREFIX + "messages"
      + WRAP;

  /**
   * Logga le operazioni su database dell'utente. Ogni messaggio sarà preceduto
   * dall'identificativo dell'utente
   * 
   * @param message
   *          il messaggio da loggare
   */
  // private void log(final String message) {
  // // Log.info("[" + this.user.getUsername() + "] " + message);
  // }

  public static ResultSet doQuery(final Connection connection,
      final String query) throws SQLException {

    PreparedStatement ps =
        (PreparedStatement) connection.prepareStatement(query);
    return ps.executeQuery();
  }

  private ResultSet doUpdate(final Connection connection, final String query)
      throws SQLException {
    Statement update =
        connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
            ResultSet.CONCUR_READ_ONLY);
    update.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
    return update.getGeneratedKeys();
  }

  private void updateLoginCount(final int userId, final int loginCount)
      throws SQLException, HiddenException {
    Connection connection = this.dbConnectionHandler.getConn();
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
      JardinLogger.error("impossibile aggiornare conto login");
      throw new HiddenException("impossibile aggiornare conto login");
    } finally {
      this.dbConnectionHandler.closeConn(connection);
    }
  }

  public void updateUserProperties(final User user) throws HiddenException {
    Connection connection = this.dbConnectionHandler.getConn();
    String query =
        "UPDATE " + T_USER + " SET" + " password=PASSWORD('"
            + user.getPassword() + "'), name='" + user.getName()
            + "', surname='" + user.getSurname() + "', email='"
            + user.getEmail() + "', office='" + user.getOffice()
            + "', telephone='" + user.getTelephone() + "', id_group='"
            + user.getGid() + "' WHERE id = '" + user.getUid() + "'";
    try {
      this.doUpdate(connection, query);
    } catch (SQLException e) {
      // Log.warn("Errore SQL", e);
      JardinLogger.error("Errore SQL: impossibile aggiornare conto login");
      throw new HiddenException(
          "Errore durante il salvataggio delle preferenze");
    } finally {
      this.dbConnectionHandler.closeConn(connection);
    }
  }

  public void updateUserCreds(RegistrationInfo regInfo, String password)
      throws HiddenException {
    Connection connection = this.dbConnectionHandler.getConn();
    String query =
        "UPDATE " + T_USER + " SET" + " password=PASSWORD('" + password
            + "') ,username = '" + regInfo.getUsername()
            + "', status='1' WHERE name='" + regInfo.getNome()
            + "' AND surname='" + regInfo.getCognome() + "' AND email='"
            + regInfo.getEmail() + "'";
    if (regInfo.getTelefono() != null
        && regInfo.getTelefono().compareToIgnoreCase("") != 0) {
      query = query + " AND telephone='" + regInfo.getTelefono() + "'";
    }
    try {
      this.doUpdate(connection, query);
      JardinLogger.info("REGISTRAZIONE: password aggiornata per l'utente "
          + regInfo.getUsername());
    } catch (SQLException e) {
      // Log.warn("Errore SQL", e);
      JardinLogger.error("Errore SQL: impossibile aggiornare password per l'utente "
          + regInfo.getUsername());
      throw new HiddenException(
          "Errore durante il salvataggio della password per l'utente "
              + regInfo.getUsername());
    } finally {
      this.dbConnectionHandler.closeConn(connection);
    }
  }

  public List<Integer> getUserResultsetHeaderPrefereces(final Integer uid,
      final Integer rsid) {
    String query =
        "SELECT fip.id_field as fieldid FROM " + "(((" + T_FIELD + " f JOIN "
            + T_FIELDINPREFERENCE + " fip " + "ON (fip.id_field=f.id)) "
            + "JOIN " + T_HEADERPREFERENCE + " hp ON "
            + "(hp.id=fip.id_headerpreference)) " + "JOIN " + T_USER + " u "
            + "ON (hp.id_user=u.id)) " + "WHERE u.id = '" + uid
            + "' AND f.id_resultset='" + rsid + "'";

    Connection connection = null;
    try {
      connection = this.dbConnectionHandler.getConn();
    } catch (HiddenException e) {
      e.printStackTrace();
      JardinLogger.error("Errore SQL: impossibile connettersi al db");
      // TODO re-throw HiddenException to be caught by caller
      // Log.error("Error con database connection", e);
    }

    List<Integer> hp = new ArrayList<Integer>();
    try {
      ResultSet resultset = doQuery(connection, query);
      while (resultset.next()) {
        hp.add(resultset.getInt("fieldid"));
      }
    } catch (SQLException e) {
      JardinLogger.error("Errore SQL: impossibile recuperare preferenze header");
      e.printStackTrace();
      // TODO throw a HiddenException to be caught by caller
      // Log.error("Error on loading user resultset preferences", e);
    }

    try {
      this.dbConnectionHandler.closeConn(connection);
    } catch (HiddenException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return hp;
  }

  private String createSearchQuery(final PagingLoadConfig config,
      final SearchParams searchParams) throws SQLException {

    // TODO like può essere recuperato, se necessario, da searchParams;

    boolean like = !(searchParams.getAccurate());

    Integer id = searchParams.getResultsetId();
    List<BaseModelData> fieldList = searchParams.getFieldsValuesList();

    /*
     * Trasformazione di List<BaseModelData> in Map<String, String>
     */

    Map<String, String> fields = this.getMapFromListModelData(fieldList);
    String query = null;
    try {
      query = this.dbProperties.getStatement(id);
    } catch (HiddenException e) {
      // TODO Auto-generated catch block
      JardinLogger.error("Errore SQL: impossibile recuperare statement resultset da id "
          + id);
      e.printStackTrace();
    }
    // query = "SELECT * FROM " + query + " WHERE 1";

    /*
     * Gestione parte WHERE della query
     */

    for (String keyValue : fields.keySet()) {
      // System.out.println("kv:" + keyValue);
      String key = "";
      String comparer = " LIKE ";
      String value = fields.get(keyValue);
      int indexOr = value.indexOf("|");
      if (indexOr == -1) {
        String[] aKeyValue = keyValue.split("_operator_");
        key = aKeyValue[0];
        if (!key.equals(SPECIAL_FIELD) && aKeyValue.length > 1)
          comparer = aKeyValue[1];
      } else {
        String[] aKeyValueOR = keyValue.split("_operatorOr_");
        key = aKeyValueOR[0];
        if (!key.equals(SPECIAL_FIELD) && aKeyValueOR.length > 1) {
          comparer = aKeyValueOR[1];
          // System.out.println("Valore di comparer  " + comparer);
        }
      }
      if (value.length() > 0) {
        StringTokenizer stringTokenizer = new StringTokenizer(value, "|");

        if (key.compareTo(SPECIAL_FIELD) != 0) {
          /* Gestione campo normale */
          query += " AND (0";
          while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            // int iLenToken = token.length();
            String sValue[] = token.split("_operatorOr_");
            if (sValue.length > 1) {
              String sValueToken = sValue[0];
              String sValueOperator = sValue[1];
              token = sValueToken;
              comparer = sValueOperator;
            }
            if (like)
              comparer = " LIKE ";
            query += this.fieldTest(key, "OR", token, like, comparer);
          }
          query += ")";
        } else {
          /* Gestione campo speciale */
          while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            try {
              query +=
                  this.fieldTest(this.dbProperties.getFieldList(id), "OR",
                      token, like, comparer);
            } catch (HiddenException e) {
              // TODO Auto-generated catch block
              JardinLogger.error("Errore: impossibile costruire stringa di ricerca");
              e.printStackTrace();
            }
          }
        }

      }
    }

    /*
     * Gestione configurazione di ricerca (SORT e LIMIT)
     */
    if (config != null) {
      if (config.getSortInfo().getSortField() != null) {
        if (query.toUpperCase().indexOf("ORDER BY") != -1) {
          String sottostringa =
              query.substring(0, query.toUpperCase().indexOf("ORDER BY"));
          query =
              sottostringa + " ORDER BY `"
                  + config.getSortInfo().getSortField() + "` "
                  + config.getSortInfo().getSortDir();
        } else {
          query +=
              " ORDER BY `" + config.getSortInfo().getSortField() + "` "
                  + config.getSortInfo().getSortDir();

        }
      }

      if (config.getLimit() != -1) {
        query +=
            " LIMIT " + ((PagingLoadConfig) config).getOffset() + ","
                + ((PagingLoadConfig) config).getLimit();
      }
    }

    // Log.debug("Search Query: " + query);
    //
    return query;
  }

  private String createSearchQueryForCount(final PagingLoadConfig config,
      final SearchParams searchParams) throws SQLException {

    String query = this.createSearchQuery(config, searchParams);

    int startPartialQuery = query.toLowerCase().indexOf("from");
    String partialQuery = query.substring(startPartialQuery);
    query = "SELECT COUNT(*) " + partialQuery;

    // Log.debug("Count Query: " + query);
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
  private String fieldTest(final List<String> fields, final String operation,
      final String value, final boolean like, String comparer) {
    String result = "";

    if (operation.compareToIgnoreCase("OR") == 0) {
      result += " AND (0";
    } else if (operation.compareToIgnoreCase("AND") == 0) {
      result += " OR (1";
    } else {
      return "";
    }

    for (String field : fields) {
      result += this.fieldTest(field, operation, value, like, comparer);
    }
    result += ")";

    return result;
  }

  private String fieldTest(String field, final String operation, String value,
      final boolean like, String comparer) {
    String operator = comparer;
    String wrapper = "";
    if (like) {
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
  public List<BaseModelData> getObjects(final PagingLoadConfig config,
      final SearchParams searchParams) {

    List<BaseModelData> records = new ArrayList<BaseModelData>();
    Connection connection = null;
    try {
      String query = this.createSearchQuery(config, searchParams);
      ResultsetImproved resultSet =
          getResultsetImproved(searchParams.getResultsetId(),
              searchParams.getGroupId());
      connection = this.dbConnectionHandler.getConn();
      // System.out.println(query);
      ResultSet result = doQuery(connection, query);
      int resultWidth = result.getMetaData().getColumnCount();
      JardinLogger.debug("INFO SQL: query di ricerca: " + query);
      // this.log(query);
      ResultSet res =
          connection.getMetaData().getColumns(null, null,
              result.getMetaData().getTableName(1), null);

      // HashTable types = new HashTable();
      // while (res.next()) {
      // types.put(res.getString("COLUMN_NAME"), res.getString("TYPE_NAME"));
      // }

      while (result.next()) {
        BaseModelData map = new BaseModelData();
        // WARNING la prima colonna di una tabella ha indice 1 (non 0)
        // for (int i = 1; i <= resultWidth; i++) {
        for (ResultsetField field : resultSet.getFields()) {
          // String key = result.getMetaData().getColumnLabel(i);//
          // getColumnClassName(i);
          Object value;
          // if (value != null) {
          // System.out.println("colonna: " + field.getName() + " del tipo "
          // + field.getSpecificType());
          if (field.getSpecificType().compareToIgnoreCase("varchar") == 0
              || field.getSpecificType().compareToIgnoreCase("char") == 0
              || field.getSpecificType().compareToIgnoreCase("enum") == 0
              || field.getSpecificType().compareToIgnoreCase("text") == 0) {
            value = result.getString(field.getName());
          } else if (field.getSpecificType().compareToIgnoreCase("int") == 0) {
            // value = ((BigDecimal)
            // result.getObject(i)).floatValue();
            value = result.getInt(field.getName());
          } else if (field.getSpecificType().compareToIgnoreCase("float") == 0) {
            value = result.getFloat(field.getName());
          } else if (field.getSpecificType().compareToIgnoreCase("double") == 0) {
            value = result.getDouble(field.getName());
          } else if (field.getSpecificType().compareToIgnoreCase("DATE") == 0
              || field.getSpecificType().compareToIgnoreCase("DATETIME") == 0) {
            value = result.getDate(field.getName());
          } else if (field.getSpecificType().compareToIgnoreCase("TIMESTAMP") == 0) {
            try {
              value = result.getTimestamp(field.getName());
            } catch (Exception e) {
              value = result.getObject(field.getName());
            }

          } else {
            value = result.getString(field.getName());
          }
          // }
          // TODO Inserire un controllo di compatibilità di
          // conversione dati
          // SQL->JDBC
          // Eg. se DATE non è una data ammissibile (eg. 0000-00-00)
          // viene
          // sollevata un'eccezione e la query non prosegue

          // Object value = result.getObject(i);
          // if (value != null) {
          // if (value instanceof BigDecimal) {
          // value = ((BigDecimal) value).floatValue();
          // } else if
          // (value.getClass().toString().contains("class [B")) {
          // // TODO trovare un modo migliore per accorgersi che il
          // l'oggetto
          // // recuperato sia un byte[]
          // value = new String(result.getBytes(i));
          // } else {
          // value = value.toString();
          // }
          // }
          // System.out.println(key + ": " + value.getClass());
          map.set(field.getName(), value);
        }
        records.add(map);
      }
    } catch (Exception e) {
      // Log.warn("Errore SQL", e);
      JardinLogger.error("Errore SQL: recuperare dati dal db");
      e.printStackTrace();
    } finally {
      try {
        this.dbConnectionHandler.closeConn(connection);
      } catch (HiddenException e) {
        JardinLogger.error("Errore SQL: impossibile chiudere la connessione con il db");
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return records;
  }

  /**
   * Fa diventare una matrice a tre dimensioni una di due sovrascrivendo i
   * valori delle chiavi ripetute. Questo va bene se le chiavi sono sempre
   * univoche (è il caso dei parametri di ricerca)
   */
  private Map<String, String> getMapFromListModelData(
      final List<BaseModelData> fieldList) {

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

  public int countObjects(final SearchParams searchParams)
      throws HiddenException {
    int recordSize = 0;
    Connection connection = this.dbConnectionHandler.getConn();

    try {
      String query = this.createSearchQueryForCount(null, searchParams);

      ResultSet result = doQuery(connection, query);
      result.next();
      recordSize = result.getInt(1);
    } catch (SQLException e) {
      // Log.warn("Errore SQL", e);
      throw new HiddenException("Errore durante l'interrogazione del database");
    } finally {
      this.dbConnectionHandler.closeConn(connection);
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
  public List<BaseModelData> getReGroupings(final int resultSetId)
      throws HiddenException, SQLException {
    // TODO modificare la funzione: creare un oggetto per i raggruppamenti
    Connection connection = this.dbConnectionHandler.getConn();
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
        m.set("name", result.getString("name"));
        m.set("alias", result.getString("alias"));
        groups.add(m);
      }
    } catch (SQLException e) {
      throw e;
    } finally {
      this.dbConnectionHandler.closeConn(connection);
    }
    return groups;
  }

  public HashMap<Integer, ResultsetFieldGroupings> getGroupingsListForResultset(
      final int resultSetId) throws HiddenException, SQLException {
    // TODO modificare la funzione: creare un oggetto per i raggruppamenti
    Connection connection = this.dbConnectionHandler.getConn();
    String groupingQuery =
        "SELECT g.* FROM `__system_field` f join `__system_resource` "
            + "res on res.id=f.id join __system_grouping g on g.id=f.id_grouping "
            + "where f.id_resultset = " + resultSetId
            + " group by f.id_grouping";

    HashMap<Integer, ResultsetFieldGroupings> groups =
        new HashMap<Integer, ResultsetFieldGroupings>();
    try {
      ResultSet result = doQuery(connection, groupingQuery);
      while (result.next()) {
        ResultsetFieldGroupings m = new ResultsetFieldGroupings();
        m.setId(result.getInt("id"));
        m.setName(result.getString("name"));
        m.setAlias(result.getString("alias"));

        // System.out.println("id :" + m.getId() + " m: " + m.getName());
        JardinLogger.debug("id :" + m.getId() + " m: " + m.getName());
        groups.put(m.getId(), m);
      }
    } catch (SQLException e) {
      throw e;
    } finally {
      this.dbConnectionHandler.closeConn(connection);
    }
    return groups;
  }

  /**
   * @param resultset
   * @param fieldName
   * @return Lista dei valori per quel determinato campo di quel determinato
   *         resultset. Serve a riempire i combobox per l'autocompletamento
   * @throws SQLException
   * @throws HiddenException
   */
  private List<BaseModelData> getValuesOfAField(final String resultset,
      final String fieldName) throws SQLException, HiddenException {
    List<BaseModelData> autoCompleteList = new ArrayList<BaseModelData>();
    Connection connection = this.dbConnectionHandler.getConn();
    try {
      String query =
          "SELECT DISTINCT `" + fieldName + "` FROM " + resultset
              + " ORDER BY `" + fieldName + "` ASC";
      // System.out.println("query valori possibili per " + resultset + "."
      // + fieldName + ": " + query);
      JardinLogger.debug(query);
      ResultSet res = doQuery(connection, query);
      while (res.next()) {
        BaseModelData m = new BaseModelData();
        m.set(fieldName, res.getString(fieldName));
        autoCompleteList.add(m);
      }
    } catch (SQLException e) {
      throw e;
    } finally {
      this.dbConnectionHandler.closeConn(connection);
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
  public List<BaseModelData> getValuesOfAField(final int resultsetId,
      final String fieldName) throws HiddenException {
    try {
      return this.getValuesOfAField(
          this.dbProperties.getResultSetName(resultsetId), fieldName);
      // return getValuesOfAField(dbProperties.getStatement(resultsetId),
      // fieldName);
    } catch (SQLException e) {
      // Log.warn("Errore SQL", e);
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
  public List<BaseModelData> getValuesOfAFieldFromTableName(
      final String tableName, final String fieldName) throws HiddenException {
    try {
      return this.getValuesOfAField("`" + tableName + "`", fieldName);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new HiddenException("Errore con i valori della chiave primaria "
          + e.getLocalizedMessage());
    }
  }

  private MessageType getMessageType(final String type) {

    for (MessageType m : MessageType.values()) {
      if (type.compareToIgnoreCase(m.toString()) == 0) {
        return m;
      }
    }

    return null;
  }

  public List<Message> getUserMessages(final int uid) throws HiddenException {
    List<Message> messages = new ArrayList<Message>();
    Connection connection = this.dbConnectionHandler.getConn();

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
        MessageType type = this.getMessageType(result.getString("type"));
        int sender = result.getInt("sender");
        int recipient = result.getInt("recipient");
        Message w = new Message(id, title, body, date, type, sender, recipient);
        messages.add(w);
      }
    } catch (SQLException e) {
      // Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero dei messaggi di utente");
    } finally {
      this.dbConnectionHandler.closeConn(connection);
    }
    return messages;
  }

  public int setObjects(final Integer resultsetId,
      final List<BaseModelData> records) throws HiddenException {

    // JardinLogger.info("Setting records...");

    int result = 0;
    Connection connection = this.dbConnectionHandler.getConn();
    final String sep = ",";

    String tableName = null;
    // String set = "";
    try {
      ResultSetMetaData metadata =
          this.dbProperties.getResultsetMetadata(connection, resultsetId);
      tableName = metadata.getTableName(1);
      connection.setAutoCommit(false);
      for (BaseModelData record : records) {
        String set = "";
        int columns = record.getPropertyNames().size();
        for (String property : record.getPropertyNames()) {
          set += "`" + property + "`=?" + sep;
        }
        set = set.substring(0, set.length() - sep.length());

        // String query =
        // "INSERT INTO `" + tableName + "` SET " + set
        // + " ON DUPLICATE KEY UPDATE " + set;
        String query = "INSERT INTO `" + tableName + "` SET " + set;

        PreparedStatement ps =
            (PreparedStatement) connection.prepareStatement(query);
        int i = 1;
        for (String property : record.getPropertyNames()) {
          Object value = record.get(property);
          if ((value != null) && (String.valueOf(value).length() > 0)) {
            ps.setObject(i, record.get(property));
            // ps.setObject(i + columns, record.get(property));
          } else {
            ps.setNull(i, java.sql.Types.NULL);
            // ps.setNull(i + columns, java.sql.Types.NULL);
          }
          i++;
        }
        // System.out.println(ps.toString());
        int num = ps.executeUpdate();
        if (num > 0) {
          String toLog = "INSERT (" + ps.toString() + ")";
          // Log.debug(toLog);
          JardinLogger.debug(toLog);
        }
        result += num;
      }
      connection.commit();
      connection.setAutoCommit(true);
    } catch (MySQLIntegrityConstraintViolationException ex) {
      try {
        connection.rollback();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        JardinLogger.debug("Errore SQL: impossibile eseguire rollback transazione");
        e.printStackTrace();
      }
      String message = ex.getLocalizedMessage();
      String newMess = "";
      // Log.warn("Errore SQL", ex);
      if (ex.getErrorCode() == 1062) {
        // updateObjects(resultsetId, records);
        newMess =
            newMess.concat(ex.getErrorCode()
                + " - Errore!!! \n PRIMARY KEY DUPLICATA :\n" + message);
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
      JardinLogger.debug("Errore SQL: " + newMess);
      throw new HiddenException(newMess);

    } catch (Exception e) {
      try {
        JardinLogger.error("Errore SQL: impossibile eseguire rollback transazione");
        connection.rollback();
      } catch (Exception e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      // Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il salvataggio delle modifiche:\n"
              + e.getLocalizedMessage());
    } finally {
      JardinLogger.info("Records setted");
      this.dbConnectionHandler.closeConn(connection);
    }
    return result;
  }

  // Cancella una riga dalla tabella
  public Integer removeObjects(final Integer resultsetId,
      final List<BaseModelData> records) throws HiddenException {

    JardinLogger.info("Removing objects");

    int resCode = 0;

    Connection connection = this.dbConnectionHandler.getConn();

    String query = new String(""), appChiavePrimaria = "";
    PreparedStatement ps = null;
    try {

      ResultSetMetaData metadata =
          this.dbProperties.getResultsetMetadata(connection, resultsetId);
      String tableName = metadata.getTableName(1);
      // Ciclo per gestire più cancellazioni nella stessa invocazione
      List<BaseModelData> primaryKeyList =
          this.dbProperties.getPrimaryKeys(tableName);
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

        // Log.debug("Query DELETE: " + ps);
        int num = ps.executeUpdate();
        if (num > 0) {
          // this.log("DELETE (" + ps.toString() + ")");
          JardinLogger.debug("DEBUG SQL: DELETE (" + ps.toString() + ")");
        }
        resCode += num;
      }
    } catch (Exception e) {
      // Log.warn("Errore SQL", e);
      throw new HiddenException("Errore durante l'eliminazione dei record");
    } finally {
      JardinLogger.info("Objects removed");
      this.dbConnectionHandler.closeConn(connection);
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
  private String getForeignKeyForAField(final String fieldName,
      final ResultSet result) throws SQLException {

    String foreignKey = null;
    String tableName = null;

    for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
      if (result.getMetaData().getColumnLabel(i).compareToIgnoreCase(fieldName) == 0) {
        tableName = result.getMetaData().getTableName(i);
        break;
      }
    }

    if ((tableName == null) || (tableName.length() <= 0)) {
      return null;
    }

    try {
      for (BaseModelData fk : this.dbProperties.getForeignKeys(tableName)) {
        if (fk.get("FIELD").toString().compareTo(fieldName) == 0) {
          foreignKey = fk.get("FOREIGN_KEY");
          break;
        }
      }
    } catch (HiddenException e) {
      // TODO Auto-generated catch block
      JardinLogger.error("Errore SQL: Cannot retrieve foreign keys info");
      e.printStackTrace();
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

  public User getUser(final Credentials credentials) throws VisibleException {

    String username = credentials.getUsername();
    String password = credentials.getPassword();

    ResultSet result;

    Connection connection;
    try {
      connection = this.dbConnectionHandler.getConn();
    } catch (HiddenException e) {
      JardinLogger.error("Errore SQL: impossibile connettersi al db");
      throw new VisibleException(e.getLocalizedMessage());
    }

    String query =
        "SELECT u.id, u.name, u.surname, u.email, u.office, "
            + "u.telephone, u.status AS userstatus, u.lastlogintime, "
            + "u.logincount, g.id AS groupid, g.name AS groupname " + "FROM "
            + T_USER + " u JOIN " + T_GROUP + " g ON g.id = u.id_group "
            + "WHERE username = ? and password = PASSWORD(?) and u.status='1'";

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
      JardinLogger.info("LOGIN: tentativo di login utente "
          + credentials.getUsername());
      result = ps.executeQuery();
    } catch (SQLException e) {
      e.printStackTrace();
      JardinLogger.error("Errore SQL: Errore durante l'interrogazione su database");
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

        JardinLogger.info("Login: login successfull!");
        // String lastlogintime = result.getString("lastlogintime");
        DateFormat df =
            DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        String last = df.format(new Date());

        /* Carica le preferenze dell'utente */
        List<ResultsetImproved> resultsets =
            this.getUserResultsetImproved(uid, gid);

        List<Message> messages = new ArrayList<Message>();

        this.updateLoginCount(uid, ++login);

        User user =
            new User(uid, gid, new Credentials(username, password), name,
                surname, group, email, office, telephone, status, login, last,
                resultsets, messages);
        this.user = user;
        return user;
      }
    } catch (Exception e) {
      // Log.warn("Errore SQL", e);
      throw new VisibleException("Errore di accesso "
          + "al risultato dell'interrogazione su database");
    } finally {
      try {
        this.dbConnectionHandler.closeConn(connection);
      } catch (HiddenException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
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
  public FieldsMatrix getValuesOfFields(final Integer resultsetId)
      throws HiddenException {
    FieldsMatrix matrix = new FieldsMatrix();

    try {
      HashMap<Integer, String> rsf = this.getResultsetFields(resultsetId);
      for (Integer fieldId : rsf.keySet()) {
        List<BaseModelData> autoCompleteList =
            this.getValuesOfAField(resultsetId, rsf.get(fieldId));
        List<String> values = new ArrayList<String>();
        for (BaseModelData fieldValue : autoCompleteList) {
          values.add((String) fieldValue.get(rsf.get(fieldId)));
        }
        matrix.addField(fieldId, values);
      }
    } catch (SQLException e) {
      // Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero dei valori dei campi");
    }

    return matrix;
  }

  private HashMap<Integer, String> getResultsetFields(final int resultsetId)
      throws SQLException, HiddenException {
    HashMap<Integer, String> fieldList = new HashMap<Integer, String>();

    Connection connection = this.dbConnectionHandler.getConn();
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
      this.dbConnectionHandler.closeConn(connection);
    }
    return fieldList;
  }

  private List<ResultsetField> getResultsetFieldList(final int resultsetId,
      String resultsetName, int groupId) throws SQLException, HiddenException {
    List<ResultsetField> fieldList = new ArrayList<ResultsetField>();

    Connection connection = this.dbConnectionHandler.getConn();
    String query =
        "SELECT res.id as id, res.name as name, res.alias, f.id_resultset, "
            + "m.readperm, m.deleteperm, m.modifyperm, m.insertperm, "
            + "f.search_grouping, f.default_header, f.id_grouping, f.type, f.defaultvalue FROM "
            + T_RESOURCE + " res JOIN " + T_FIELD + " f ON res.id = f.id JOIN "
            + T_MANAGEMENT
            + " m on res.id=m.id_resource WHERE f.id_resultset = "
            + resultsetId + " AND m.id_group = " + groupId;

    try {
      ResultSet result = doQuery(connection, query);
      while (result.next()) {
        boolean visible = result.getInt("default_header") == 1;

        ResultsetField resField =
            new ResultsetField(result.getInt("id"), result.getString("name"),
                result.getString("alias"), resultsetId,
                result.getBoolean("search_grouping"),
                result.getInt("default_header"), result.getInt("id_grouping"),
                result.getBoolean("readperm"), result.getBoolean("deleteperm"),
                result.getBoolean("modifyperm"),
                result.getBoolean("insertperm"), visible);

        resField.setType(result.getString("type"));
        resField.setDefaultValue(result.getString("defaultvalue"));
        resField.setForeignKey(dbProperties.getForeignKey(resultsetName,
            resField.getName()));
        fieldList.add(resField);
      }
    } catch (SQLException e) {
      throw e;
    } finally {
      this.dbConnectionHandler.closeConn(connection);
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
  public FieldsMatrix getValuesOfForeignKeys(final Integer resultsetId)
      throws HiddenException {

    FieldsMatrix matrix = new FieldsMatrix();
    Connection connection = this.dbConnectionHandler.getConn();

    try {
      HashMap<Integer, String> rsf = this.getResultsetFields(resultsetId);
      // String query =
      // "SELECT * FROM " + dbProperties.getStatement(resultsetId)
      // + " WHERE 0";
      String query = this.dbProperties.getStatement(resultsetId);
      ResultSet resultset = doQuery(connection, query);

      for (Integer fieldId : rsf.keySet()) {
        String foreignKey =
            this.getForeignKeyForAField(rsf.get(fieldId), resultset);
        String fkTName;
        String fkFName;
        List<BaseModelData> autoCompleteList = new ArrayList<BaseModelData>();
        if ((foreignKey != null) && (foreignKey.length() > 0)) {
          fkTName = foreignKey.split("\\.")[0];
          fkFName = foreignKey.split("\\.")[1];

          autoCompleteList =
              this.getValuesOfAFieldFromTableName(fkTName, fkFName);

          List<String> values = new ArrayList<String>();
          for (BaseModelData fieldValue : autoCompleteList) {
            values.add((String) fieldValue.get(fkFName));
          }
          matrix.addField(fieldId, values);
          // System.out.println(fieldId + "->" + values.toString());
        }
      }
    } catch (SQLException e) {
      // Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero dei valori dei vincoli");
    } finally {
      this.dbConnectionHandler.closeConn(connection);
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
      final Integer resultsetId, final List<ResultsetImproved> resultSetList)
      throws HiddenException {
    ArrayList<IncomingForeignKeyInformation> listaIfki;
    String tableName = null;
    Connection connection = this.dbConnectionHandler.getConn();
    String queryStatement = null;
    String query =
        "SELECT statement FROM " + T_RESULTSET + " WHERE id = '" + resultsetId
            + "' ";
    try {
      ResultSet res = doQuery(connection, query);
      while (res.next()) {
        queryStatement = res.getString("statement");
      }

      // TO-DO: bisogna evitare di eseguire lo statement per recuperare info
      // sulle foreignkey entranti
      queryStatement = queryStatement + " LIMIT 0,1";
      ResultSet result = doQuery(connection, queryStatement);
      if ((result != null) && (result.getMetaData().getColumnCount() > 1)) {
        tableName = result.getMetaData().getTableName(1);
        // System.out.println("que: " + queryStatement);
        // System.out.println("t-name: " + tableName);
      }

      // è una vista
      if (tableName == null) {
        return null;
      }

      if (tableName.length() <= 0) {
        return null;
      }

      String db = this.dbConnectionHandler.getDB();
      Connection connectionInformationSchema =
          this.dbConnectionHandler.getConnDbInformationSchema();
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
      // Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero delle foreign keys entranti");
    } finally {
      this.dbConnectionHandler.closeConn(connection);
    }

    return listaIfki;
  }

  /**
   * @param userId
   * @return resultSetList
   * 
   *         Ritorna le foreign key entranti
   * @throws HiddenException
   */

  public ArrayList<IncomingForeignKeyInformation> getForeignKeyInForATable(
      int gid, String statement) throws HiddenException {
    ArrayList<IncomingForeignKeyInformation> listaIfki;
    String tableName = null;
    Connection connection = this.dbConnectionHandler.getConn();
    String queryStatement = null;

    try {

      // TO-DO: bisogna evitare di eseguire lo statement per recuperare info
      // sulle foreignkey entranti
      queryStatement = statement + " LIMIT 0,1";
      ResultSet result = doQuery(connection, queryStatement);
      if ((result != null) && (result.getMetaData().getColumnCount() > 1)) {
        tableName = result.getMetaData().getTableName(1);
        // System.out.println("que: " + queryStatement);
        // System.out.println("t-name: " + tableName);
      }

      // è una vista
      if (tableName == null) {
        return null;
      }

      if (tableName.length() <= 0) {
        return null;
      }

      String db = this.dbConnectionHandler.getDB();
      Connection connectionInformationSchema =
          this.dbConnectionHandler.getConnDbInformationSchema();
      String queryFKIn =
          "SELECT TABLE_NAME, COLUMN_NAME, REFERENCED_COLUMN_NAME FROM KEY_COLUMN_USAGE where TABLE_SCHEMA = '"
              + db + "' AND REFERENCED_TABLE_NAME = '" + tableName + "'";
      ResultSet resultFKIn = doQuery(connectionInformationSchema, queryFKIn);

      listaIfki = new ArrayList<IncomingForeignKeyInformation>();

      // List<ResultsetImproved> resultSetList =
      // getCompleteResultsetImprovedList();
      List<Resultset> resultSetList = getCompleteResultsetList(gid);
      while (resultFKIn.next()) {
        String linkingTable = resultFKIn.getString("TABLE_NAME");
        String linkingField = resultFKIn.getString("COLUMN_NAME");
        String field = resultFKIn.getString("REFERENCED_COLUMN_NAME");

        // trasformare la linkingTable in un rsimproved
        // dal nome recupero l'id e dall'id recupero l'rs

        // recuperare lista dei NOMI delle tabelle possibili
        // se il nome == linkingtable --> creare rs
        for (final Resultset rs : resultSetList) {

          if (rs.getName().compareTo(linkingTable) == 0) {
            JardinLogger.debug("aggiunta FK entrante per il campo " + field
                + ": " + linkingTable + "." + linkingField);
            // System.out.println("aggiunta FK entrante per il campo " + field
            // + ": " + linkingTable + "." + linkingField);

            IncomingForeignKeyInformation ifki =
                new IncomingForeignKeyInformation(linkingTable, linkingField,
                    field);

            ResultsetImproved rsImp = getResultsetImproved(rs.getId(), gid);

            ifki.setInterestedResultset(rsImp);
            ifki.setResultsetId(rs.getId());
            listaIfki.add(ifki);
          }
        }

      }
    } catch (SQLException e) {
      // Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero delle foreign keys entranti");
    } finally {
      this.dbConnectionHandler.closeConn(connection);
    }

    return listaIfki;
  }

  private List<Boolean> getResultsetPermissions(int resId)
      throws HiddenException {
    // TODO Auto-generated method stub
    List<Boolean> permissionsList = new ArrayList<Boolean>();
    Connection connection = this.dbConnectionHandler.getConn();

    String query =
        "SELECT readperm,deleteperm,modifyperm,insertperm FROM " + T_MANAGEMENT
            + " WHERE id_resource=" + resId;

    try {
      ResultSet result = doQuery(connection, query);
      while (result.next()) {
        permissionsList.add(result.getBoolean("readperm"));
        permissionsList.add(result.getBoolean("deleteperm"));
        permissionsList.add(result.getBoolean("modifyperm"));
        permissionsList.add(result.getBoolean("insertperm"));
      }
    } catch (SQLException e) {
      // Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero della lista dei resultset semplici");
    } finally {
      this.dbConnectionHandler.closeConn(connection);
    }
    return permissionsList;
  }

  private List<Resultset> getCompleteResultsetList(int gid)
      throws HiddenException {
    // TODO Auto-generated method stub
    List<Resultset> resultSetList = new ArrayList<Resultset>();
    Connection connection = this.dbConnectionHandler.getConn();

    String query =
        "SELECT * FROM " + T_RESOURCE + " r JOIN " + T_RESULTSET
            + " res ON res.id=r.id JOIN " + T_MANAGEMENT
            + " m ON res.id=m.id_resource WHERE m.id_group=" + gid;
    JardinLogger.debug("res list query: " + query);

    try {
      ResultSet result = doQuery(connection, query);
      while (result.next()) {
        Resultset res = new Resultset();
        res.setId(result.getInt("id"));
        res.setName(result.getString("name"));
        res.setAlias(result.getString("alias"));
        res.setGestible(result.getBoolean("gestible"));
        res.setNote(result.getString("note"));
        res.setStatement(result.getString("statement"));
        res.setPermissions(new ResourcePermissions(
            result.getBoolean("readperm"), result.getBoolean("deleteperm"),
            result.getBoolean("modifyperm"), result.getBoolean("insertperm")));
        resultSetList.add(res);
      }
    } catch (SQLException e) {
      // Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero della lista dei resultset semplici");
    } finally {
      this.dbConnectionHandler.closeConn(connection);
    }
    return resultSetList;
  }

  private List<ResultsetImproved> getCompleteResultsetImprovedList()
      throws HiddenException {

    List<ResultsetImproved> resultSetList = new ArrayList<ResultsetImproved>();
    Connection connection = this.dbConnectionHandler.getConn();

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
            + " WHERE readperm = '1' "
            + " ORDER BY r.id ASC";
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

          ArrayList<Tool> tools = this.getToolbar(rsid, groupid);

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

          List<BaseModelData> groupings = this.getReGroupings(id);
          for (BaseModelData grouping : groupings) {
            ResultsetFieldGroupings rfg =
                new ResultsetFieldGroupings((Integer) grouping.get("id"),
                    (String) grouping.get("name"),
                    (String) grouping.get("alias"));
            res.addFieldGroupings(rfg);
          }

          PKs = this.dbProperties.getPrimaryKeys(name);
          UKs = this.dbProperties.getUniqueKeys(name);

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
                this.dbProperties.getForeignKey(resultSetList.get(i).getName(),
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
      // Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero delle viste su database");
    } finally {
      this.dbConnectionHandler.closeConn(connection);
    }

    return resultSetList;
  }

  /**
   * @param userId
   * @return resultSetList
   * 
   *         Ritorna i resultset (id, alias e statement SQL) per i quali
   *         l'utente passato come parametro ha permesso 'read' uguale a 1
   * @throws HiddenException
   */
  public List<ResultsetImproved> getUserResultsetImproved(final Integer uid,
      final Integer gid) throws HiddenException {

    List<ResultsetImproved> resultSetList = new ArrayList<ResultsetImproved>();
    Connection connection = this.dbConnectionHandler.getConn();

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

          ArrayList<Tool> tools = this.getToolbar(rsid, groupid);

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

          List<BaseModelData> groupings = this.getReGroupings(id);
          for (BaseModelData grouping : groupings) {
            ResultsetFieldGroupings rfg =
                new ResultsetFieldGroupings((Integer) grouping.get("id"),
                    (String) grouping.get("name"),
                    (String) grouping.get("alias"));
            res.addFieldGroupings(rfg);
          }

          PKs = this.dbProperties.getPrimaryKeys(name);
          UKs = this.dbProperties.getUniqueKeys(name);

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
                this.dbProperties.getForeignKey(resultSetList.get(i).getName(),
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
      // Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero delle viste su database");
    } finally {
      this.dbConnectionHandler.closeConn(connection);
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
  private ArrayList<Tool> getToolbar(final Integer rsid, final Integer groupid)
      throws HiddenException, SQLException {

    ArrayList<Tool> tools = new ArrayList<Tool>();
    String toolbar = null;

    Connection connection = this.dbConnectionHandler.getConn();
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
        tools.add(this.getTool(t));
      }
    }

    return tools;

  }

  private Tool getTool(final String t) {
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

  public boolean setUserResultsetHeaderPreferencesNoDefault(
      final Integer userid, final Integer resultsetId,
      final ArrayList<Integer> listfields, final String value)
      throws HiddenException {
    boolean esito = true;
    Connection connection = this.dbConnectionHandler.getConn();

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    String creationDate = formatter.format(new Date());
    String createHeaderPreference =
        "INSERT INTO " + T_HEADERPREFERENCE + " VALUES  (NULL , '" + value
            + "-" + creationDate.hashCode() + "', '" + userid + "', '"
            + creationDate + "')";

    try {
      ResultSet newRes = this.doUpdate(connection, createHeaderPreference);
      newRes.next();
      Integer headerPrefId = newRes.getInt(1);

      /* Crea i riferimenti in fieldinpreferences */
      for (int i = 0; i < listfields.size(); i++) {
        String newPreferences =
            "INSERT INTO " + T_FIELDINPREFERENCE + " VALUES ('" + headerPrefId
                + "', '" + listfields.get(i) + "')";
        this.doUpdate(connection, newPreferences);
      }
    } catch (SQLException e) {
      esito = false;
      // Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero delle viste su database");
    } finally {
      this.dbConnectionHandler.closeConn(connection);
    }

    return esito;
  }

  public HeaderPreferenceList getHeaderUserPreferenceList(final Integer idUser,
      final Integer idResultset) throws HiddenException {

    HeaderPreferenceList hp = new HeaderPreferenceList();
    Connection connection = this.dbConnectionHandler.getConn();
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
      // Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero delle preferenze utente");
    } finally {
      this.dbConnectionHandler.closeConn(connection);
    }
    return hp;
  }

  public List<Integer> getHeaderUserPreference(final Integer idUser,
      final Integer userPreferenceHeaderId) throws HiddenException {
    List<Integer> fieldInPref = new ArrayList<Integer>();
    Connection connection = this.dbConnectionHandler.getConn();
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
      // Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero delle preferenze utente");
    } finally {
      this.dbConnectionHandler.closeConn(connection);
    }

    return fieldInPref;
  }

  public int importFile(final Credentials credentials, final int resultsetId,
      final File importFile, final String ts, final String fs,
      final String tipologia, final String type, final String condition)
      throws HiddenException, VisibleException, SQLException {

    // this.getUser(credentials);

    int opCode = 0;
    BufferedReader in = null;

    try {
      in = new BufferedReader(new FileReader(importFile));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new HiddenException("file " + importFile.getName() + " non trovato");
    }

    String recordLine;
    String[] columns = null;
    List<BaseModelData> recordList = new ArrayList<BaseModelData>();
    ArrayList<String> regExSpecialChars = new ArrayList<String>();
    regExSpecialChars.add("\\");
    regExSpecialChars.add("^");
    regExSpecialChars.add("$");
    regExSpecialChars.add("{");
    regExSpecialChars.add("}");
    regExSpecialChars.add("[");
    regExSpecialChars.add("}");
    regExSpecialChars.add("(");
    regExSpecialChars.add(")");
    regExSpecialChars.add(".");
    regExSpecialChars.add("*");
    regExSpecialChars.add("+");
    regExSpecialChars.add("?");
    regExSpecialChars.add("|");
    regExSpecialChars.add("<");
    regExSpecialChars.add(">");
    regExSpecialChars.add("-");
    regExSpecialChars.add("&");

    try {
      // recordLine = in.readLine();
      if (tipologia.compareToIgnoreCase("fix") == 0) {
        // TODO gestione campo a lunghezza fissa da db!
      } else {

      }

      // recordLine = in.readLine();
      CSVParser csvp = new CSVParser(in);
      String delim = new String(fs);
      String quote = new String(ts);
      String commentDelims = new String("#");
      csvp.changeDelimiter(delim.charAt(0));
      csvp.changeQuote(quote.charAt(0));
      csvp.setCommentStart(commentDelims);

      String[] t = null;
      columns = csvp.getLine(); // la prima riga deve contenere i nomi delle
      // colonne

      ArrayList<String> colsCheck = new ArrayList<String>();
      for (String col : columns) {
        boolean present = false;
        HashMap<Integer, String> rsFieldList = getResultsetFields(resultsetId);

        if (rsFieldList.containsValue(col)) {
          present = true;
        }
        if (!present) {
          colsCheck.add(col);
        }
      }

      if (colsCheck.size() != 0) {
        throw new VisibleException("Attenzione!!! Colonna '" + colsCheck.get(0)
            + "' non riconosciuta");
      }

      int lineFailed = 0;
      try {
        while ((t = csvp.getLine()) != null) {
          lineFailed++;
          BaseModelData bm = new BaseModelData();
          // System.out.println("lunghezza riga: " + t.length);
          // System.out.print("" + csvp.lastLineNumber() + ":");
          for (int i = 0; i < t.length; i++) {
            // System.out.println("valorizzazione campo: " +
            // columns[i] + " = "
            // + t[i]);
            bm.set(columns[i], t[i]);
            // System.out.println("\"" + t[i] + "\";");
          }
          recordList.add(bm);
        }
      } catch (ArrayIndexOutOfBoundsException ex) {
        // Log.warn("Troppi campi nel file: " + t.length + " alla riga "
        // + (lineFailed + 1), ex);
        throw new VisibleException("Troppi campi nel file: " + t.length
            + " alla riga " + (lineFailed + 1));
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      throw new HiddenException("impossibile leggere il file: "
          + importFile.getName());
    }
    if (type.compareToIgnoreCase(UploadDialog.TYPE_INSERT) == 0) {
      opCode = this.setObjects(resultsetId, recordList);
    } else {
      opCode = this.updateObjects(resultsetId, recordList, condition);
    }
    return opCode;
  }

  /**
   * Put a Java Object in a PreparedStatement. Return the number of transferred
   * Objects.
   * 
   * @param ps
   *          The PreparedStatement to modify
   * @param i
   *          The index in which to put the Java Object
   * @param value
   *          The Object to put in the PreparedStatement
   * @return Number of transferred Objects (1 if all went OK, 0 otherwise). Tip:
   *         you can use this value to increment index pointer.
   * @throws SQLException
   */
  private Integer putJavaObjectInPs(final PreparedStatement ps,
      final Integer i, final Object value) throws SQLException {

    // TODO Warning!
    if ((value != null) && (value.toString().length() > 0)) {
      ps.setObject(i, value);
    } else {
      ps.setNull(i, Types.NULL);
    }

    return 1;
  }

  /*
   * invocata solo in caso di duplicate key entry per la setObject: si suppone
   * quindi che la chiave primaria non sia stata alterata
   */
  public Integer updateObjects(final Integer resultsetId,
      final List<BaseModelData> newItemList, final String condition)
      throws HiddenException {

    // JardinLogger.info("Updating records...");

    int result = 0;
    Connection connection = this.dbConnectionHandler.getConn();
    final String sep = ",";
    boolean defaultPrimaryKeys = condition.equalsIgnoreCase("$-notspec-$");

    try {
      ResultSetMetaData metadata =
          this.dbProperties.getResultsetMetadata(connection, resultsetId);
      String tableName = metadata.getTableName(1);

      // TODO Creare un oggetto per la memorizzazione colonna->valore
      List<BaseModelData> PKs =
          this.dbProperties.getResultsetPrimaryKeys(resultsetId);

      String PKset = "";
      connection.setAutoCommit(false);
      for (BaseModelData record : newItemList) {

        boolean conditionFounded = false;
        if (defaultPrimaryKeys) {
          conditionFounded = true;

          // richiesta di update da griglia o dettaglio
          for (BaseModelData pk : PKs) {
            PKset += (String) pk.get("PK_NAME") + "=? AND ";
          }
          PKset = PKset.substring(0, PKset.length() - 5); // Strips
          // " AND "

        } else {
          PKset = condition + "=? ";
        }

        String set = "";
        Collection<String> properties = record.getPropertyNames();
        for (String property : properties) {
          if (property.equalsIgnoreCase(condition)) {
            conditionFounded = true;
          } else {
            set += "`" + property + "`=? " + sep;
          }
        }

        if (!conditionFounded) {
          throw new VisibleException(
              "condizione di UPDATE non trovata nel file");
        }

        set = set.substring(0, set.length() - sep.length());

        String query =
            "UPDATE `" + tableName + "` SET " + set + " WHERE " + PKset;

        PreparedStatement ps =
            (PreparedStatement) connection.prepareStatement(query);
        int i = 1;

        /* Set prepared statement values for changing fields */
        for (String property : properties) {
          if (!property.equalsIgnoreCase(condition)) {
            i += this.putJavaObjectInPs(ps, i, record.get(property));
          }
        }

        /* Set prepared statement values for where condition fields */
        if (defaultPrimaryKeys) {
          for (BaseModelData pk : PKs) {
            Object value = record.get((String) pk.get("PK_NAME"));
            i += this.putJavaObjectInPs(ps, i, value);
          }
        } else {
          Object value = record.get(condition);
          i += this.putJavaObjectInPs(ps, i, value);
        }

        // Log.debug("Query UPDATE: " + ps);
        int num = ps.executeUpdate();
        if (num > 0) {
          JardinLogger.debug(("UPDATE (" + ps.toString() + ")"));
        }
        result += num;
      }
      connection.commit();
      connection.setAutoCommit(true);
      // JardinLogger.info("Records updated");
    } catch (Exception e) {
      try {
        connection.rollback();
      } catch (Exception e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      // Log.warn("Errore SQL", e);
      e.printStackTrace();
      throw new HiddenException("Errore durante l'aggiornamento del record:\n"
          + e.getLocalizedMessage());
    } finally {

      this.dbConnectionHandler.closeConn(connection);
    }
    return result;
  }

  public void notifyChanges(MailUtility mailUtility, final Integer resultsetId,
      final List<BaseModelData> newItemList) throws SQLException,
      HiddenException {
    Integer id_table = 0;
    String mitt = mailUtility.getMailSmtpSender();

    Connection connection = this.dbConnectionHandler.getConn();

    String query =
        "SELECT address_statement, data_statement, link_id, name FROM "
            + T_NOTIFY + " WHERE id_resultset = '" + resultsetId + "'";

    JardinLogger.debug("query: " + query);

    ResultSet result = doQuery(connection, query);
    while (result.next()) {
      String testo = "";
      String address_statement = result.getString(1);
      String data_statement = result.getString(2);
      String bmdid = result.getString(3);
      String oggetto = result.getString(4);
      JardinLogger.debug("bmdid " + bmdid);

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
            JardinLogger.debug("\nmessaggio:\n" + testo);
            testo += "\n";
          }

          PreparedStatement ps =
              (PreparedStatement) connection.prepareStatement(address_statement);
          // ps.setInt(1, id_table);
          ResultSet resultAddress = ps.executeQuery();
          while (resultAddress.next()) {
            JardinLogger.info("Sending notification mail to: "
                + resultAddress.getString(1));
            if (!(resultAddress.getString(1) == null)) {
              try {
                mailUtility.sendMail(resultAddress.getString(1), mitt, oggetto,
                    testo);

              } catch (MessagingException e) {
                e.printStackTrace();
                JardinLogger.error("Invio non riuscito!");
                JardinLogger.error("MessagingException: " + e.toString());
                // Log.info(e.toString());
              }
              JardinLogger.info("Invio riuscito!");
            } else {
              JardinLogger.error("Errore invio mail: Mail non valida!");
            }
          }
        } else {
          JardinLogger.error("Notifica non inviata perchè è un inserimento!");
        }
      }
    }
  }

  public void sendMessage(final Message message) throws HiddenException,
      VisibleException {

    String query =
        "INSERT INTO "
            + T_MESSAGES
            + " (title, body, date, type, sender, recipient) VALUES (?, ?, ?, ?, ?, ?)";

    Connection connection = this.dbConnectionHandler.getConn();

    PreparedStatement ps;
    try {
      ps = (PreparedStatement) connection.prepareStatement(query);
      // TODO Title could be NULL -> check sql
      ps.setString(1, message.getTitle());
      ps.setString(2, message.getBody());
      // Log.debug(message.toString());

      DateFormat df =
          new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
      String date = df.format(message.getDate());
      ps.setString(3, date);

      ps.setString(4, message.getType().type());
      ps.setInt(5, message.getSender());
      ps.setInt(6, message.getRecipient());
      ps.executeUpdate();
    } catch (SQLException e) {
      // Log.error("Error during new message insertion", e);
      throw new VisibleException("Impossibile salvare il messaggio");
    } finally {
      this.dbConnectionHandler.closeConn(connection);
    }
  }

  public ArrayList<BaseModelData> getPopUpDetailEntry(final BaseModelData data)
      throws HiddenException {

    String linkingField = data.get("FK");
    String linkingValue = "" + data.get("VALUE");
    String queryStatement = ((Resultset) data.get("RSLINKED")).getStatement();
    int groupId = data.get("GID");
    String query =
        "SELECT * FROM (" + queryStatement + ") AS entry WHERE " + linkingField
            + " = '" + linkingValue + "' LIMIT 1";
    Connection connection = this.dbConnectionHandler.getConn();
    ResultSet result;
    BaseModelData row = new BaseModelData();
    try {
      List<ResultsetField> resultSetFieldList =
          getResultsetFieldList(((Resultset) data.get("RSLINKED")).getId(),
              ((Resultset) data.get("RSLINKED")).getName(), groupId);
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
      ((Resultset) data.get("RSLINKED")).setResultsetListField(resultSetFieldList);
      ((Resultset) data.get("RSLINKED")).setFieldGroupings(getGroupingsListForResultset(((Resultset) data.get("RSLINKED")).getId()));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    this.dbConnectionHandler.closeConn(connection);
    ArrayList<BaseModelData> infoToView = new ArrayList<BaseModelData>();
    infoToView.add(data);
    infoToView.add(row);
    return infoToView;
  }

  public String getStatementByResultsetId(final Integer rsid)
      throws HiddenException {
    String query =
        "SELECT statement FROM " + T_RESULTSET + " WHERE id = " + rsid;
    Connection connection = this.dbConnectionHandler.getConn();
    ResultSet result;
    String queryStatement = null;
    try {
      result = doQuery(connection, query);
      result.next();
      queryStatement = result.getString(1);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    this.dbConnectionHandler.closeConn(connection);
    return queryStatement;
  }

  public List<Plugin> getPlugin(final int gid, final int rsid)
      throws HiddenException {
    List<Plugin> plugins = new ArrayList<Plugin>();
    String query =
        "SELECT id_group, id_resultset, name, configurationfile, type, note, id FROM "
            + T_PLUGIN + " INNER JOIN " + T_PLUGINASSOCIATION
            + " ON (id_plugin = id) WHERE id_resultset = '" + rsid
            + "' AND id_group = '" + gid + "'";
    Connection connection = this.dbConnectionHandler.getConn();
    // System.out.println("DBUTILS: recupero plugin");
    try {
      ResultSet result;
      result = doQuery(connection, query);
      while (result.next()) {
        Plugin plugin =
            new Plugin(result.getString("name"),
                result.getString("configurationfile"),
                result.getString("type"), result.getString("note"),
                result.getInt("id"), result.getInt("id_resultset"),
                result.getInt("id_group"));
        plugins.add(plugin);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    this.dbConnectionHandler.closeConn(connection);
    return plugins;
  }

  // ritorna la lista dei resultSet con almeno il permesso di lettura
  public List<Resultset> getUserResultSetList(int uid) throws HiddenException {
    // TODO Auto-generated method stub
    List<Resultset> resultsets = new ArrayList<Resultset>();
    String query =
        "SELECT s.*, r.statement, m.readperm, m.deleteperm, m.modifyperm, m.insertperm "
            + "FROM "
            + T_RESOURCE
            + " s JOIN "
            + T_RESULTSET
            + " r on s.id=r.id JOIN "
            + T_MANAGEMENT
            + " m on  m.id_resource=r.id JOIN "
            + T_GROUP
            + " g on m.id_group=g.id JOIN "
            + T_USER
            + " u on g.id=u.id_group WHERE u.id=" + uid + " and m.readperm=1";
    // System.out.println("query firsttabb: " + query);
    Connection connection = this.dbConnectionHandler.getConn();
    try {
      ResultSet result;
      result = doQuery(connection, query);

      while (result.next()) {
        Resultset res = new Resultset();
        res.setId(result.getInt("id"));
        res.setAlias(result.getString("alias"));
        res.setGestible(result.getBoolean("gestible"));
        res.setName(result.getString("name"));
        res.setNote(result.getString("note"));
        res.setStatement(result.getString("statement"));
        res.setPermissions(new ResourcePermissions(
            result.getBoolean("readperm"), result.getBoolean("deleteperm"),
            result.getBoolean("modifyperm"), result.getBoolean("insertperm")));
        resultsets.add(res);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    this.dbConnectionHandler.closeConn(connection);
    return resultsets;
  }

  // recupera info su rs: lista campi, plugins, tool, FK, FKin, PK e UK
  public ResultsetImproved getResultsetImproved(int resultsetId, int gid)
      throws HiddenException {
    // TODO Auto-generated method stub
    Connection connection = this.dbConnectionHandler.getConn();
    ResultsetImproved res = null;
    String resultsetName = null;

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

    // getFieldsForResultset(int resultsetId, int gid)
    // getPluginsForResultset(int resultsetId, int gid)
    // getForeignKeyForResultset(int resultsetId, int gid)
    // getForeignKeyINforResultset(int resultsetId, int gid)
    // getToolsResultset(int resultsetId, int gid)

    try {
      String queryResultset =
          "SELECT res.statement as statement, " + "r.id as resourceid, "
              + "g.id AS groupid, " + "r.name as resourcename, "
              + "r.alias as resourcealias, " + "m.readperm as readperm, "
              + "m.deleteperm as deleteperm, " + "m.modifyperm as modifyperm, "
              + "m.insertperm as insertperm " + "FROM " + T_GROUP + " g JOIN "
              + T_MANAGEMENT + " m ON g.id = m.id_group " + "JOIN "
              + T_RESOURCE + " r ON r.id = m.id_resource " + "LEFT JOIN "
              + T_RESULTSET + " res ON res.id=r.id " + " WHERE r.id = '"
              + resultsetId + "' AND g.id = '" + gid + "'";

      JardinLogger.debug("costruzione resultset:" + queryResultset);

      List<ResultsetField> resultFieldList = new ArrayList<ResultsetField>();
      List<BaseModelData> PKs = null;
      ArrayList<String> UKs = new ArrayList<String>();

      ResultSet result = doQuery(connection, queryResultset);

      if (result.next()) {
        // System.out.println("RESULT SET RECUPERATO");
        String statement = result.getString("statement");
        Integer id = Integer.valueOf(result.getInt("resourceid"));
        resultsetName = result.getString("resourcename");
        String alias = result.getString("resourcealias");
        boolean readperm = result.getBoolean("readperm");
        boolean deleteperm = result.getBoolean("deleteperm");
        boolean modifyperm = result.getBoolean("modifyperm");
        boolean insertperm = result.getBoolean("insertperm");
        Integer groupid = Integer.valueOf(result.getInt("groupid"));

        /* Gestione di un RESULTSET */

        ArrayList<Tool> tools = this.getToolbar(id, groupid);

        if (views.contains(resultsetName)) {
          // System.out.println(name + " è una view");
          res =
              new ResultsetImproved(id, resultsetName, alias, statement,
                  readperm, false, false, false, tools);
        } else {
          // System.out.println(name + " è una tabella");
          res =
              new ResultsetImproved(id, resultsetName, alias, statement,
                  readperm, deleteperm, modifyperm, insertperm, tools);
        }

        // System.out.println("STATEMENT:" + res.getStatement());
        List<BaseModelData> groupings = this.getReGroupings(id);
        for (BaseModelData grouping : groupings) {
          ResultsetFieldGroupings rfg =
              new ResultsetFieldGroupings((Integer) grouping.get("id"),
                  (String) grouping.get("name"), (String) grouping.get("alias"));
          res.addFieldGroupings(rfg);
          JardinLogger.debug("aggiunto raggruppamento: " + rfg.getId()
              + rfg.getAlias());
        }

        PKs = this.dbProperties.getPrimaryKeys(resultsetName);
        UKs = this.dbProperties.getUniqueKeys(resultsetName);
      }

      String filedsQuery =
          "SELECT res.*, m.readperm, m.deleteperm, m.modifyperm,"
              + "m.insertperm, f.default_header , f.search_grouping,"
              + "f.id_grouping, f.type, f.defaultvalue "
              + "FROM `__system_resource` res join "
              + "__system_field f on res.id=f.id  join "
              + "__system_management m on res.id=m.id_resource join "
              + "__system_group g on g.id=m.id_group  "
              + "WHERE f.id_resultset=" + resultsetId + " and g.id=" + gid;

      JardinLogger.debug("costruzione campi:" + filedsQuery);
      ResultSet resultFields = doQuery(connection, filedsQuery);

      /* Gestione di un CAMPO di un resultset */
      while (resultFields.next()) {
        boolean visible = resultFields.getInt("default_header") == 1;

        ResultsetField resField =
            new ResultsetField(resultFields.getInt("id"),
                resultFields.getString("name"),
                resultFields.getString("alias"), resultsetId,
                resultFields.getBoolean("default_header"),
                resultFields.getInt("search_grouping"),
                resultFields.getInt("id_grouping"),
                resultFields.getBoolean("readperm"),
                resultFields.getBoolean("deleteperm"),
                resultFields.getBoolean("modifyperm"),
                resultFields.getBoolean("insertperm"), visible);

        resField.setType(resultFields.getString("type"));
        resField.setDefaultValue(resultFields.getString("defaultvalue"));
        resField.setIsPK(false);
        resField.setUnique(false);
        resField.setForeignKey(dbProperties.getForeignKey(resultsetName,
            resField.getName()));

        resultFieldList.add(resField);

        res.addField(resField);
        // System.out.println("aggiunto campo " + resField.getName() + " la rs "
        // + res.getName());

        if (PKs != null) {
          for (BaseModelData pk : PKs) {
            if (((String) pk.get("PK_NAME")).compareToIgnoreCase(resultFields.getString("name")) == 0) {
              resField.setIsPK(true);
            }
          }
        }

        if (UKs != null) {
          if (UKs.contains(resultFields.getString("name"))) {
            resField.setUnique(true);
          }
        }
      }

      // FOREIGNEKEY

      // PKs = null;

      // aggiunta delle eventuali foreignKEY entranti
      // res.setForeignKeyIn(getForeignKeyInForATable(resultsetId));
      res.setForeignKeyIn(getForeignKeyInForATable(gid, res.getStatement()));
      // }
    } catch (SQLException e) {
      // Log.warn("Errore SQL", e);
      throw new HiddenException(
          "Errore durante il recupero delle viste su database");
    } finally {
      this.dbConnectionHandler.closeConn(connection);
    }

    return res;

  }

  public User getSimpleUser(Credentials credentials) throws VisibleException {
    String username = credentials.getUsername();
    String password = credentials.getPassword();

    ResultSet result;

    Connection connection;
    try {
      connection = this.dbConnectionHandler.getConn();
    } catch (HiddenException e) {
      throw new VisibleException(e.getLocalizedMessage());
    }

    String query =
        "SELECT u.id, u.name, u.surname, u.email, u.office, "
            + "u.telephone, u.status AS userstatus, u.lastlogintime, "
            + "u.logincount, g.id AS groupid, g.name AS groupname "
            + "FROM "
            + T_USER
            + " u JOIN "
            + T_GROUP
            + " g ON g.id = u.id_group "
            + "WHERE username = ? and password = PASSWORD(?) AND u.status = '1'";
    // JardinLogger.debug("query getuser:"
    // + "SELECT u.id, u.name, u.surname, u.email, u.office, "
    // + "u.telephone, u.status AS userstatus, u.lastlogintime, "
    // + "u.logincount, g.id AS groupid, g.name AS groupname " + "FROM "
    // + T_USER + " u JOIN " + T_GROUP + " g ON g.id = u.id_group "
    // + "WHERE username = " + username + " and password = PASSWORD("
    // + password + ") AND status = '1'");
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
      // Log.debug("User validation query: " + ps.toString());
      e.printStackTrace();
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

        // JardinLogger.info("LOGIN: login utente " + credentials.getUsername()
        // + " RIUSCITO!");
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

        DateFormat df =
            DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        String last = df.format(new Date());

        /* Carica le preferenze dell'utente */
        
        List<Message> messages = new ArrayList<Message>();


        User user =
            new User(uid, gid, new Credentials(username, password), name,
                surname, group, email, office, telephone, status, login, last);
        this.user = user;

        if (login > 0)
          this.updateLoginCount(uid, ++login);

        return user;
      }
    } catch (Exception e) {
      // Log.warn("Errore SQL", e);
      throw new VisibleException("Errore di accesso "
          + "al risultato dell'interrogazione su database");
    } finally {
      try {
        this.dbConnectionHandler.closeConn(connection);
      } catch (HiddenException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    // JardinLogger.info("Errore LOGIN: tentativo di login utente "
    // + credentials.getUsername() + " FALLITO!");
    throw new VisibleException("Errore di accesso: username o password errati");
  }

  public ResultsetPlus getResultsetPlus(int resultsetId, int gid) {
    // TODO Auto-generated method stub
    return null;
  }

  public User changePassword(Credentials credentials) throws VisibleException,
      HiddenException {
    Connection connection = this.dbConnectionHandler.getConn();
    String query =
        "UPDATE " + T_USER + " SET password=PASSWORD('"
            + credentials.getNewPassword()
            + "'), lastlogintime=NOW(), logincount=1 WHERE username=?";
    JardinLogger.debug("UPDATE " + T_USER + " SET password=PASSWORD('"
        + credentials.getNewPassword()
        + "'), lastlogintime=NOW(), logincount='1' WHERE username="
        + credentials.getUsername());
    User newuser = null;
    try {
      PreparedStatement ps =
          (PreparedStatement) connection.prepareStatement(query);
      ps.setString(1, credentials.getUsername());
      ps.executeUpdate();
      // if (execCode > 0) {
      // System.out.println("PASSWORD AGGIORNATA: " +
      // credentials.getNewPassword());
      credentials.setPassword(credentials.getNewPassword());
      newuser = getSimpleUser(credentials);
      // System.out.println("pass nuovo utente:" + newuser.getPassword());
      // } else {
      // throw new VisibleException(
      // "utente inesistente! contattare supporto tecnico");
      // }
      return newuser;
    } catch (SQLException e) {
      throw new HiddenException(
          "errore nell'aggiornamento password per l'utente: "
              + credentials.getUsername());
    } finally {
      this.dbConnectionHandler.closeConn(connection);
    }

  }

  public Integer massiveUpdate(MassiveUpdateObject muo) throws HiddenException,
      VisibleException {
    // TODO Auto-generated method stub
    Connection connection = this.dbConnectionHandler.getConn();

    int result = -1;
    String tableName = null;

    ResultSetMetaData metadata;
    String transQueries = "";
    try {
      metadata =
          this.dbProperties.getResultsetMetadata(connection,
              muo.getResultsetId());
      tableName = metadata.getTableName(1);
      connection.setAutoCommit(false);

      // String[] transQueries = null;

      // int i = 0;
      for (String pkValue : muo.getPrimaryKeyValues()) {
        transQueries = "UPDATE `" + tableName + "` SET ";
        BaseModelData newValues = muo.getNewValues();
        for (String tableField : newValues.getPropertyNames()) {
          transQueries +=
              tableField + " = '" + newValues.get(tableField) + "', ";
        }
        transQueries = transQueries.substring(0, transQueries.length() - 2);

        transQueries +=
            " WHERE " + muo.getFieldName() + " = '" + pkValue + "'; ";
        // i++;
        Statement stmt = connection.createStatement();
        // System.out.println("query update massivo: " + transQueries);
        JardinLogger.debug("query update massivo: " + transQueries);
        stmt.executeUpdate(transQueries);
      }

      connection.commit();
      connection.setAutoCommit(true);
      connection.close();

      return 1;

    } catch (SQLException e) {
      // TODO Auto-generated catch block
      try {
        connection.rollback();
      } catch (SQLException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }

      JardinLogger.debug("query update massivo: " + transQueries);
      e.printStackTrace();
      throw new VisibleException("Impossibile eseguire update massivo");
    } finally {
      try {
        connection.close();
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public Integer checkRegistrationInfo(RegistrationInfo regInfo)
      throws VisibleException {
    // TODO Auto-generated method stub
    ResultSet result;

    Connection connection;
    try {
      connection = this.dbConnectionHandler.getConn();
    } catch (HiddenException e) {
      throw new VisibleException(e.getLocalizedMessage());
    }

    String query =
        "SELECT status FROM " + T_USER
            + " WHERE name = ? AND surname = ? AND email = ? ";
    if (regInfo.getTelefono() != null) {
      query = query + " AND telephone = ? ";
    }
    // System.out.println("nome:" + regInfo.getNome());
    // System.out.println("cognome:" + regInfo.getCognome());
    // System.out.println("email:" + regInfo.getEmail());
    // System.out.println(query);

    PreparedStatement ps;
    try {
      ps = connection.prepareStatement(query);
      ps.setString(1, regInfo.getNome());
      ps.setString(2, regInfo.getCognome());
      ps.setString(3, regInfo.getEmail());
      if (regInfo.getTelefono() != null) {
        // System.out.println("tel:" + regInfo.getTelefono());
        ps.setString(4, regInfo.getTelefono());
      }
    } catch (SQLException e) {
      throw new VisibleException(
          "Errore nella query per il check della registrazione");
    }

    try {
      JardinLogger.info("REGISTRAZIONE: tentativo di registrazione per l'utente "
          + regInfo.getUsername());
      JardinLogger.info("REGISTRAZIONE: sottomessi i dati [nome="
          + regInfo.getNome() + ",cognome=" + regInfo.getCognome() + ",email="
          + regInfo.getEmail() + ",telefono=" + regInfo.getTelefono() + "]");
      result = ps.executeQuery();
    } catch (SQLException e) {
      e.printStackTrace();
      JardinLogger.error("Errore SQL: Errore durante l'interrogazione su database");
      throw new VisibleException("Errore durante l'interrogazione su database");
    }

    int rows = 0;

    try {
      while (result.next()) {
        rows++;
        if (rows > 1) {
          throw new VisibleException("Errore nel database degli utenti: "
              + "due account con username e password uguali");
        } else {
          // System.out.println("status: " + result.getInt("status"));
          return result.getInt("status");

        }
      }
      if (rows == 0)
        return 0;
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      throw new VisibleException("Errore di accesso "
          + "al risultato dell'interrogazione su database");
    } finally {
      try {
        this.dbConnectionHandler.closeConn(connection);
      } catch (HiddenException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    throw new VisibleException("Errore: impossibile registrare l'utente "
        + regInfo.getUsername());
  }

  public void sendRegistrationMail(MailUtility mailUtility,
      RegistrationInfo regInfo, Integer output) throws HiddenException {

    String mitt = mailUtility.getMailSmtpSender();
    String oggetto = "Jardin Manager - conferma registrazione";
    String password =
        RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(13) + 8);
    String testo =
        "Jardin Manager\n\n Conferma della registrazione al portale per l'utente "
            + regInfo.getNome() + " " + regInfo.getCognome() + "\n\n"
            + "Credenziali primo accesso:\n" + "Username: "
            + regInfo.getUsername() + "\n";

    if (output == 2) {
      testo = testo + "Password: " + password;
      updateUserCreds(regInfo, password);
    } else if (output == 3) {
      testo = testo + "Prima parte della password: " + password;
      // la seconda parte della password è inviata tramite sms o telefonata
      String password2 =
          RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(13) + 8);

      JardinLogger.info("REGISTRAZIONE: all'utente " + regInfo.getUsername()
          + " deve essere fornita la seconda parte della password: "
          + password2 + " (la prima parte è " + password + ")");

      updateUserCreds(regInfo, password + password2);

      testo =
          testo
              + "\n\n La seconda parte della password verrà fornita tramite il numero di telefono indicato";
    }

    try {
      mailUtility.sendMail(regInfo.getEmail(), mitt, oggetto, testo);

    } catch (MessagingException e) {
      e.printStackTrace();
      JardinLogger.error("Invio non riuscito!");
      JardinLogger.error("MessagingException: " + e.toString());
      // Log.info(e.toString());
    }
  }

}
