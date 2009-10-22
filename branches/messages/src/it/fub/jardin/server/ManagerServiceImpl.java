/**
 * 
 */
package it.fub.jardin.server;

import it.fub.jardin.client.DbException;
import it.fub.jardin.client.EventList;
import it.fub.jardin.client.ManagerService;
import it.fub.jardin.client.UserException;
import it.fub.jardin.client.model.Credentials;
import it.fub.jardin.client.model.EventTypeSerializable;
import it.fub.jardin.client.model.FieldsMatrix;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.Message;
import it.fub.jardin.client.model.MessageType;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.Template;
import it.fub.jardin.client.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author gpantanetti
 * 
 */
public class ManagerServiceImpl extends RemoteServiceServlet implements
    ManagerService {

  private static final long serialVersionUID = 1L;
  private DbUtils dbUtils;
  private Map<String, User> users = new HashMap<String, User>();

  public ManagerServiceImpl() {
    super();
    this.dbUtils = new DbUtils();
  }

  public synchronized void log(String message) {
    User user = getCurrentUser();
    Log.info("[" + user.getUsername() + "] " + message);
  }

  public String createReport(String file, Template template,
      PagingLoadConfig config, List<String> columns, SearchParams searchParams)
      throws UserException {

    if (searchParams == null) {
      throw new UserException("Effettuare prima una ricerca");
    }

    List<BaseModelData> records = dbUtils.getObjects(config, searchParams);

    String xsl =
        this.getServletContext().getRealPath(
            Template.TEMPLATE_DIR + template.getXsl());

    /* Gestione del template di default */
    User user = getCurrentUser();
    if (template.getInfo().compareTo(Template.DEFAULT.getInfo()) == 0) {
      ResultsetImproved resultset =
          user.getResultsetFromId(searchParams.getResultsetId());
      try {
        FileUtils.prepareDefaultTemplate(resultset, xsl);
      } catch (IOException e) {
        Log.error("Impossibile ottenere il template di default", e);
        throw new UserException("Impossibile ottenere il template di default");
      }
    }

    if (template != null) {
      String context = this.getServletContext().getRealPath("/");
      String realpath = this.getServletContext().getRealPath(file);
      String result =
          FileUtils.createReport(realpath, xsl, template, records, columns);
      Log.debug("File esportato: " + result);
      Log.debug("Servlet context path: " + context);
      return result.substring(context.length());
    } else {
      throw new UserException("Impossibile leggere il template");
    }
  }

  private synchronized User getCurrentUser() {
    String id = getThreadLocalRequest().getSession().getId();
    return (User) users.get(id);
  }

  private synchronized List<User> getGroupUsers(int gid) {
    ArrayList<User> group = new ArrayList<User>();
    for (User user : users.values()) {
      if (user.getGid() == gid) {
        group.add(user);
      }
    }
    return group;
  }

  public List<EventTypeSerializable> getEvents() {

    List<EventTypeSerializable> events = null;
    User user = getCurrentUser();
    if (user != null) {
      if (user.getEvents().size() == 0) {
        try {
          synchronized (user) {
            user.wait(30 * 1000);
          }
        } catch (InterruptedException ignored) {
          ;
        }
      }
      synchronized (user) {
        events = user.getEvents();
        user.cleanEvents();
      }
    }
    return events;
  }

  // TODO get user from thread id
  public HeaderPreferenceList getGridViews(Integer userId, Integer resultsetId)
      throws DbException {
    return dbUtils.getHeaderUserPreferenceList(userId, resultsetId);
  }

  // TODO get user from thread id
  public List<Integer> getHeaderUserPreference(Integer idUser,
      Integer userPreferenceHeaderId) throws DbException {
    return dbUtils.getHeaderUserPreference(idUser, userPreferenceHeaderId);
  }

  public PagingLoadResult<BaseModelData> getRecords(PagingLoadConfig config,
      SearchParams searchParams) throws DbException {
    List<BaseModelData> records = dbUtils.getObjects(config, searchParams);
    int recordSize = dbUtils.countObjects(searchParams);

    if (config != null) {
      return new BasePagingLoadResult<BaseModelData>(records,
          config.getOffset(), recordSize);
    } else {
      return new BasePagingLoadResult<BaseModelData>(records);
    }
  }

  public List<BaseModelData> getReGroupings(int resultSetId) {
    return dbUtils.getReGroupings(resultSetId);
  }

  public String getServerTime() {
    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
    return formatter.format(new Date());
  }

  public User getUser(Credentials credentials) throws UserException {
    User user = dbUtils.getUser(credentials);

    String id = getThreadLocalRequest().getSession().getId();
    synchronized (this) {
      users.put(id, user);
    }

    log("LOGIN");
    return user;
  }

  // TODO get user from thread id
  public List<Message> getUserMessages(Integer userId) throws DbException {
    return dbUtils.getUserMessages(userId);
  }

  public List<BaseModelData> getValuesOfAField(int resultsetId, String fieldId)
      throws DbException {
    return dbUtils.getValuesOfAField(resultsetId, fieldId);
  }

  public List<BaseModelData> getValuesOfAFieldFromTableName(String table,
      String field) throws DbException {

    return dbUtils.getValuesOfAFieldFromTableName(table, field);
  }

  public FieldsMatrix getValuesOfFields(Integer resultsetId) throws DbException {
    return dbUtils.getValuesOfFields(resultsetId);
  }

  public FieldsMatrix getValuesOfForeignKeys(Integer resultsetId)
      throws DbException {
    return dbUtils.getValuesOfForeignKeys(resultsetId);
  }

  public Integer removeObjects(Integer resultset,
      List<BaseModelData> selectedRows) throws DbException {
    log("Removing records...");
    return dbUtils.removeObjects(resultset, selectedRows);
  }

  protected synchronized User getUserByUid(int uid) {
    for (User user : users.values()) {
      if (user.getUid() == uid) {
        return user;
      }
    }
    return null;
  }

  // TODO implement USER direct messages
  public void sendMessage(MessageType type, String title, String body) {

    User sender = getCurrentUser();

    switch (type) {
    case GROUP:
      dbUtils.sendMessage(sender.getUid(), title, body, type);
      for (User user : getGroupUsers(sender.getGid())) {
        user.addEvent(EventList.SendMessage);
        user.notifyAll();
      }
      break;
    case ALL:
      dbUtils.sendMessage(sender.getUid(), title, body, type);
      for (User user : this.users.values()) {
        user.addEvent(EventList.SendMessage);
        user.notifyAll();
      }
      break;
    case USER:
      // TODO implent user direct messages
      // dbUtils.sendMessage(sender.getUid(), title, body, type, recipient);
      break;
    default:
      break;
    }

  }

  public Integer setObjects(Integer resultsetId, List<BaseModelData> newItemList)
      throws DbException {

    log("Setting records...");
    // recupero dei vecchi parametri
    // e passaggio a notifyCanges
    List<BaseModelData> newItemListTest = newItemList;
    int success = dbUtils.setObjects(resultsetId, newItemList);
    if (success > 0) {
      try {
        dbUtils.notifyChanges(resultsetId, newItemListTest);
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return success;
  }

  // TODO get user from thread id
  public boolean setUserResultsetHeaderPreferencesNoDefault(Integer userid,
      Integer resultsetId, ArrayList<Integer> listfields, String value)
      throws DbException {
    return dbUtils.setUserResultsetHeaderPreferencesNoDefault(userid,
        resultsetId, listfields, value);
  }

  // TODO get user from thread id
  public void updateUserProperties(User user) throws DbException {
    dbUtils.updateUserProperties(user);
  }

}
