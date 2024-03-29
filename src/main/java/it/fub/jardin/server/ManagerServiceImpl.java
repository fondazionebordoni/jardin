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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Jardin. If not, see <http://www.gnu.org/licenses/>.
 */

package it.fub.jardin.server;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.ManagerService;
import it.fub.jardin.client.exception.HiddenException;
import it.fub.jardin.client.exception.VisibleException;
import it.fub.jardin.client.model.Credentials;
import it.fub.jardin.client.model.FieldsMatrix;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.MassiveUpdateObject;
import it.fub.jardin.client.model.Message;
import it.fub.jardin.client.model.MessageType;
import it.fub.jardin.client.model.Plugin;
import it.fub.jardin.client.model.RegistrationInfo;
import it.fub.jardin.client.model.Resultset;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.ResultsetPlus;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.Template;
import it.fub.jardin.client.model.User;
import it.fub.jardin.server.tools.JardinLogger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.event.EventType;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

//import com.allen_sauer.gwt.log.client.Log;

public class ManagerServiceImpl extends RemoteServiceServlet implements
    ManagerService {

  private static final long serialVersionUID = 1L;
  private final DbUtils dbUtils;
  private final Map<Integer, User> users = new HashMap<Integer, User>();
  private String subSystem = "JARDiN";
  private String log4jConfPath = "conf/";
  private DbConnectionHandler dbConnectionHandler;
  private DbProperties dbProperties;
  private MailUtility mailUtility;
  private static boolean logInitialized = false;

  public ManagerServiceImpl() throws VisibleException {
    super();
    this.dbProperties = new DbProperties();
    this.dbConnectionHandler = this.dbProperties.getConnectionHandler();
    this.dbUtils = new DbUtils(dbProperties, dbConnectionHandler);
//    this.users = dbUtils.getJardinUsers();
    this.setMailUtility(new MailUtility(
        dbConnectionHandler.getDbConnectionParameters().getMailSmtpHost(),
        dbConnectionHandler.getDbConnectionParameters().getMailSmtpAuth(),
        dbConnectionHandler.getDbConnectionParameters().getMailSmtpUser(),
        dbConnectionHandler.getDbConnectionParameters().getMailSmtpPass(),
        dbConnectionHandler.getDbConnectionParameters().getMailSmtpSender(),
        dbConnectionHandler.getDbConnectionParameters().getMailSmtpSysadmin()));

    subSystem = dbConnectionHandler.getDbConnectionParameters().getSubSystem();

  }

  public Integer massiveUpdate(MassiveUpdateObject muo, int userId)
      throws VisibleException, HiddenException {

    User user = getUserByUid(userId);
    String logtext = "";
    for (String pkvalue : muo.getPrimaryKeyValues()) {
      logtext += muo.getFieldName() + "=" + pkvalue + ";";
    }
    JardinLogger.info(user.getUsername(), "avviata modifica massiva per: "
        + logtext);

    int result = dbUtils.massiveUpdate(muo, user.getUsername());
    if (result != 1) {
      JardinLogger.error(user.getUsername(),
          "impossibile completare update massivo " + logtext);
      return 0;
    } else
      JardinLogger.info(user.getUsername(), "COMPLETATA modifica massiva per: "
          + logtext);
    return result;
  }

  public String createReport(final String file, final Template template,
      final PagingLoadConfig config, final List<BaseModelData> selectedRows,
      final List<String> columns, final SearchParams searchParams,
      final char fs, final char ts) throws VisibleException, HiddenException {
    if (searchParams == null) {
      throw new VisibleException("Effettuare prima una ricerca");
    } else if ((template.getInfo().compareTo(Template.CSV.getInfo()) == 0)
        && (ts == '\0')) {
      throw new VisibleException(
          "Il separatore di testo deve essere composto da un carattere");
    } else if ((template.getInfo().compareTo(Template.CSV.getInfo()) == 0)
        && (fs == '\0')) {
      throw new VisibleException(
          "Il separatore di campo deve essere composto da un carattere");
    } else if ((template.getInfo().compareTo(Template.CSV.getInfo()) == 0)
        && (fs == ts)) {
      throw new VisibleException(
          "Il separatore di campo e il separatore di testo non possono essere uguali");
    }
    List<BaseModelData> records = null;
    if (selectedRows == null) {
      records =
          this.dbUtils.getObjects(config, searchParams,
              getUserByUid(searchParams.getUserId()).getUsername());
    } else {
      records = selectedRows;
    }
    String xsl =
        this.getServletContext().getRealPath(
            Template.TEMPLATE_DIR + template.getXsl());

    /* Gestione del template di default */
    User user = getUserByUid(searchParams.getUserId());
    if (template.getInfo().compareTo(Template.DEFAULT.getInfo()) == 0) {
      ResultsetImproved resultset =
          dbUtils.getResultsetImproved(searchParams.getResultsetId(),
              searchParams.getGroupId());
      // user.getResultsetImprovedFromId(searchParams.getResultsetId());
      try {
        FileUtils.prepareDefaultTemplate(resultset, xsl, columns);
      } catch (IOException e) {
        JardinLogger.error(user.getUsername(),
            "template per l'export non trovato");
        // Log.error("Impossibile ottenere il template di default", e);
        e.printStackTrace();

        throw new VisibleException(
            "Impossibile ottenere il template di default");
      }
    }

    if (template != null) {
      String context = this.getServletContext().getRealPath("/");
      String realpath = this.getServletContext().getRealPath(file);
      String result =
          FileUtils.createReport(realpath, xsl, template, records, columns, fs,
              ts);
      JardinLogger.info(user.getUsername(), "File esportato: " + result);
      // JardinLogger.debug("Servlet context path: " + context);
      return result.substring(context.length());
    } else {
      JardinLogger.error(user.getUsername(),
          "template per l'export non leggibile");
      throw new VisibleException("Impossibile leggere il template");
    }
  }

//  private synchronized User getCurrentUser() {
//    String id = this.getThreadLocalRequest().getSession().getId();
//    return this.users.get(id);
//  }

  private synchronized List<User> getGroupUsers(final int gid) {
    ArrayList<User> group = new ArrayList<User>();
    for (User user : this.users.values()) {
      if (user.getGid() == gid) {
        group.add(user);
      }
    }
    return group;
  }

  public List<EventType> getEvents(int userId) {

    List<EventType> events = null;
    // User user = this.getCurrentUser();
    User user = this.getUserByUid(userId);
    if (user != null) {
      while (user.getEvents().size() == 0) {
        try {
          synchronized (user) {
            user.wait(30 * 1000);
          }
        } catch (InterruptedException ignored) {
          ;
        }
      }

      synchronized (user) {
        // Log.debug("User " + user.getUsername() + " got events!");
        events = user.getEvents();
        user.cleanEvents();
      }
    }
    return events;
  }

  // TODO get user from thread id
  public HeaderPreferenceList getGridViews(final Integer userId,
      final Integer resultsetId) throws HiddenException {
    return this.dbUtils.getHeaderUserPreferenceList(userId, resultsetId);
  }

  // TODO get user from thread id
  public List<Integer> getHeaderUserPreference(final Integer idUser,
      final Integer userPreferenceHeaderId) throws HiddenException {
    return this.dbUtils.getHeaderUserPreference(idUser, userPreferenceHeaderId);
  }

  public PagingLoadResult<BaseModelData> getRecords(
      final PagingLoadConfig config, final SearchParams searchParams)
      throws HiddenException {
//    System.out.println("utente " + searchParams.getUserId());
    User user = getUserByUid(searchParams.getUserId());
//    System.out.println("RS id: " + searchParams.getResultsetId());
//    System.out.println("user id: " + user.getUsername());
    JardinLogger.info(user.getUsername(), "avviata query di ricerca su RS "
        + searchParams.getResultsetId());
    List<BaseModelData> records =
        this.dbUtils.getObjects(config, searchParams, user.getUsername());

    if (config != null) {
      int recordSize = this.dbUtils.countObjects(searchParams);
      return new BasePagingLoadResult<BaseModelData>(records,
          config.getOffset(), recordSize);
    } else {
      return new BasePagingLoadResult<BaseModelData>(records);
    }
  }

  public List<BaseModelData> getReGroupings(final int resultSetId)
      throws HiddenException {
    try {
      return this.dbUtils.getReGroupings(resultSetId);
    } catch (SQLException e) {
      throw new HiddenException(e.getLocalizedMessage());
    }
  }

  public String getServerTime() {
    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
    return formatter.format(new Date());
  }

  public User getUser(final Credentials credentials) throws VisibleException {

    User user = this.dbUtils.getUser(credentials);
//    String id = this.getThreadLocalRequest().getSession().getId();
    synchronized (this) {
//      this.users.put(Integer.valueOf(id), user);
      this.users.put(user.getUid(), user);
    }

    JardinLogger.info(user.getUsername(), "LOGIN utente " + user.getName());
    return user;
  }

  public User getSimpleUser(final Credentials credentials)
      throws VisibleException {

    User user = this.dbUtils.getSimpleUser(credentials);

    // String id = this.getThreadLocalRequest().getSession().getId();
//    synchronized (this) {
      this.users.put(user.getUid(), user);
//    }

    if (user != null) {
      if (logInitialized == false) {

        String confDir =
            this.getClass().getClassLoader().getResource("/").getPath()
                + log4jConfPath;
        // System.out.println("dir: " + confDir);
        // JardinLogger.init(confDir, subSystem, getCurrentUser());
        JardinLogger.init(confDir, subSystem);

        logInitialized = true;
      }

    }

    JardinLogger.info(user.getUsername(),
        "LOGIN utente effettuato (accesso numero " + user.getLogin() + ")");
    return user;
  }

  // TODO get user from thread id
  public List<Message> getUserMessages(final Integer userId)
      throws HiddenException {
    return this.dbUtils.getUserMessages(userId);
  }

  public List<BaseModelData> getValuesOfAField(final int resultsetId,
      final String fieldId) throws HiddenException {
    return this.dbUtils.getValuesOfAField(resultsetId, fieldId);
  }

  public List<BaseModelData> getValuesOfAFieldFromTableName(final String table,
      final String field) throws HiddenException {

    return this.dbUtils.getValuesOfAFieldFromTableName(table, field);
  }

  public FieldsMatrix getValuesOfFields(final Integer resultsetId)
      throws HiddenException {
    return this.dbUtils.getValuesOfFields(resultsetId);
  }

  public FieldsMatrix getValuesOfForeignKeys(final Integer resultsetId)
      throws HiddenException {
    return this.dbUtils.getValuesOfForeignKeys(resultsetId);
  }

  public Integer removeObjects(final Integer resultsetId,
      final List<BaseModelData> selectedRows, Integer userId)
      throws HiddenException, VisibleException {

    User user = getUserByUid(userId);
    JardinLogger.info(user.getUsername(), "Removing records...");
    try {
      this.dbUtils.notifyChanges(mailUtility, resultsetId, selectedRows,
          "cancellazione", user.getUsername());
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    int success =
        this.dbUtils.removeObjects(resultsetId, selectedRows,
            user.getUsername());
    if (success > 0) {
      JardinLogger.info(user.getUsername(),
          "Objects successfull removed for resultset " + resultsetId);

    } else
      JardinLogger.error(user.getUsername(),
          "Error in setting objects for resultset " + resultsetId + "!");
    return success;
  }

  protected synchronized User getUserByUid(final int uid) {
    for (User user : this.users.values()) {
      if (user.getUid() == uid) {
        return user;
      }
    }
    return null;
  }

  // TODO implement USER direct messages
  public void sendMessage(final Message message) throws HiddenException,
      VisibleException {

    MessageType type = message.getType();
    User sender = this.getUserByUid(message.getSender());
    this.dbUtils.sendMessage(message);
    Collection<User> users = null;

    switch (type) {
    case GROUP:
      users = this.getGroupUsers(sender.getGid());
      break;
    case ALL:
      users = this.users.values();
    case USER:
      // TODO implement user direct messages
      // dbUtils.sendMessage(message);
      break;
    default:
      break;
    }

    for (User user : users) {
      synchronized (user) {
        user.addEvent(EventList.NewMessage);
        user.notifyAll();
      }
    }

  }

  public Integer setObjects(final Integer resultsetId,
      final List<BaseModelData> newItemList, Integer userId)
      throws HiddenException {

    User user = getUserByUid(userId);
    JardinLogger.info(user.getUsername(), "Setting objects for resultset "
        + resultsetId + "...");
    // recupero dei vecchi parametri
    // e passaggio a notifyCanges
    List<BaseModelData> newItemListTest = newItemList;
    int success =
        this.dbUtils.setObjects(resultsetId, newItemList, user.getUsername());
    if (success > 0) {
      JardinLogger.info(user.getUsername(),
          "Objects successfull setted for resultset " + resultsetId);
      try {
        this.dbUtils.notifyChanges(mailUtility, resultsetId, newItemListTest,
            "inserimento", user.getUsername());
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else
      JardinLogger.error(user.getUsername(),
          "Error in setting objects for resultset " + resultsetId + "!");
    return success;
  }

  // TODO get user from thread id
  public boolean setUserResultsetHeaderPreferencesNoDefault(
      final Integer userid, final Integer resultsetId,
      final ArrayList<Integer> listfields, final String value)
      throws HiddenException {
    return this.dbUtils.setUserResultsetHeaderPreferencesNoDefault(userid,
        resultsetId, listfields, value);
  }

  // TODO get user from thread id
  public void updateUserProperties(final User user) throws HiddenException {
    this.dbUtils.updateUserProperties(user);
  }

  public ArrayList<BaseModelData> getPopUpDetailEntry(final BaseModelData data)
      throws HiddenException {
    return this.dbUtils.getPopUpDetailEntry(data);
  }

  public List<Plugin> getPlugins(final int gid, final int rsid)
      throws HiddenException {
    return this.dbUtils.getPlugin(gid, rsid);
  }

  public Integer updateObjects(final Integer resultsetId,
      final List<BaseModelData> newItemList, final String condition,
      Integer userId) throws HiddenException {
    User user = getUserByUid(userId);
    JardinLogger.info(user.getUsername(), "Updating records for resultset "
        + resultsetId + "...");
    // recupero dei vecchi parametri
    // e passaggio a notifyCanges
    List<BaseModelData> newItemListTest = newItemList;
    int success =
        this.dbUtils.updateObjects(resultsetId, newItemList, condition,
            user.getUsername());
    if (success > 0) {
      JardinLogger.info(user.getUsername(), "Records for resultset "
          + resultsetId + " UPDATED!");
      try {
        this.dbUtils.notifyChanges(mailUtility, resultsetId, newItemListTest,
            "aggiornamento", user.getUsername());
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else
      JardinLogger.error(user.getUsername(),
          "Cannot update records for resultset " + resultsetId);
    return success;
  }

  public List<Resultset> getUserResultsetList(int uid) throws HiddenException {
    // List<BaseModelData> resultsetList = new ArrayList<BaseModelData>();
    User user = getUserByUid(uid);
    JardinLogger.info(user.getUsername(), "avviata interfaccia per l'utente");
    return this.dbUtils.getUserResultSetList(uid);
  }

  @Override
  public String testServerPresence() {
    return "Hello!";
  }

  public ResultsetImproved getResultsetImproved(int resultsetId, int uid)
      throws HiddenException {
    User user = getUserByUid(uid);
    ResultsetImproved resultset =
        this.dbUtils.getResultsetImproved(resultsetId, user.getGid());

    if (resultset != null) {
      JardinLogger.info(user.getUsername(),
          "apertura resultset " + resultset.getAlias());
    } else
      JardinLogger.error(user.getUsername(), "apertura resultset "
          + resultsetId + " non riuscita!");

    return resultset;

  }

  public ResultsetPlus getResultsetPlus(int resultsetId, int gid)
      throws HiddenException {
    return this.dbUtils.getResultsetPlus(resultsetId, gid);

  }

  @Override
  public User changePassword(Credentials credentials) throws VisibleException,
      HiddenException {
    // TODO Auto-generated method stub
    User newUser = this.dbUtils.changePassword(credentials);
    if (newUser != null) {
      JardinLogger.info(newUser.getUsername(), "Password successfull changed!");
    } else
      JardinLogger.error(newUser.getUsername(),
          "Cannot change password for user" + credentials.getUsername() + "!");
    return newUser;
  }

  /**
   * @return the dbConnectionHandler
   */
  public DbConnectionHandler getDbConnectionHandler() {
    return dbConnectionHandler;
  }

  /**
   * @param dbConnectionHandler
   *          the dbConnectionHandler to set
   */
  public void setDbConnectionHandler(DbConnectionHandler dbConnectionHandler) {
    this.dbConnectionHandler = dbConnectionHandler;
  }

  /**
   * @return the mailUtility
   */
  public MailUtility getMailUtility() {
    return mailUtility;
  }

  /**
   * @param mailUtility
   *          the mailUtility to set
   */
  public void setMailUtility(MailUtility mailUtility) {
    this.mailUtility = mailUtility;
  }

  public Integer checkRegistrationInfo(RegistrationInfo regInfo)
      throws VisibleException, HiddenException {
    // TODO Auto-generated method stub
    if (logInitialized == false) {

      String confDir =
          this.getClass().getClassLoader().getResource("/").getPath()
              + log4jConfPath;
      // System.out.println("dir: " + confDir);
      JardinLogger.init(confDir, subSystem);

      logInitialized = true;
    }

    Integer output = dbUtils.checkRegistrationInfo(regInfo);

    if (output == 0 || output == 1) {
      JardinLogger.info("REGISTRAZIONE", "impossibile registrare l'utente "
          + regInfo.getUsername());
    } else if (output == 2 || output == 3) {
      JardinLogger.info("REGISTRAZIONE", "utente " + regInfo.getUsername()
          + " registrato con successo");
      JardinLogger.info(
          "REGISTRAZIONE",
          "invio email con password primo accesso all'utente "
              + regInfo.getUsername());
      this.dbUtils.sendRegistrationMail(mailUtility, regInfo, output);
      JardinLogger.info(
          "REGISTRAZIONE",
          "email con password primo accesso all'utente "
              + regInfo.getUsername() + " inviata");
    }
    return output;
  }

  //
  // public User getSimpleUserAndChangePassword(Credentials credentials)
  // throws VisibleException {
  // // TODO Auto-generated method stub
  // return this.dbUtils.getSimpleUserAndChangePassword(credentials);
  // }

}
