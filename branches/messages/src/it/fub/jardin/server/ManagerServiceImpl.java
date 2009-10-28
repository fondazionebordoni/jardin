/**
 * 
 */
package it.fub.jardin.server;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.ManagerService;
import it.fub.jardin.client.exception.HiddenException;
import it.fub.jardin.client.exception.VisibleException;
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
import java.util.Collection;
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
      throws VisibleException {

    if (searchParams == null) {
      throw new VisibleException("Effettuare prima una ricerca");
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
        throw new VisibleException("Impossibile ottenere il template di default");
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
      throw new VisibleException("Impossibile leggere il template");
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
        Log.debug("User " + user.getUsername() + " got events!");
        events = user.getEvents();
        user.cleanEvents();
      }
    }
    return events;
  }

  // TODO get user from thread id
  public HeaderPreferenceList getGridViews(Integer userId, Integer resultsetId)
      throws HiddenException {
    return dbUtils.getHeaderUserPreferenceList(userId, resultsetId);
  }

  // TODO get user from thread id
  public List<Integer> getHeaderUserPreference(Integer idUser,
      Integer userPreferenceHeaderId) throws HiddenException {
    return dbUtils.getHeaderUserPreference(idUser, userPreferenceHeaderId);
  }

  public PagingLoadResult<BaseModelData> getRecords(PagingLoadConfig config,
      SearchParams searchParams) throws HiddenException {
    List<BaseModelData> records = dbUtils.getObjects(config, searchParams);
    int recordSize = dbUtils.countObjects(searchParams);

    if (config != null) {
      return new BasePagingLoadResult<BaseModelData>(records,
          config.getOffset(), recordSize);
    } else {
      return new BasePagingLoadResult<BaseModelData>(records);
    }
  }

  public List<BaseModelData> getReGroupings(int resultSetId) throws HiddenException {
    try {
      return dbUtils.getReGroupings(resultSetId);
    } catch (SQLException e) {
      throw new HiddenException(e.getLocalizedMessage());
    }
  }

  public String getServerTime() {
    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
    return formatter.format(new Date());
  }

  public User getUser(Credentials credentials) throws VisibleException {
    User user = dbUtils.getUser(credentials);

    String id = getThreadLocalRequest().getSession().getId();
    synchronized (this) {
      users.put(id, user);
    }

    log("LOGIN");
    return user;
  }

  // TODO get user from thread id
  public List<Message> getUserMessages(Integer userId) throws HiddenException {
    return dbUtils.getUserMessages(userId);
  }

  public List<BaseModelData> getValuesOfAField(int resultsetId, String fieldId)
      throws HiddenException {
    return dbUtils.getValuesOfAField(resultsetId, fieldId);
  }

  public List<BaseModelData> getValuesOfAFieldFromTableName(String table,
      String field) throws HiddenException {

    return dbUtils.getValuesOfAFieldFromTableName(table, field);
  }

  public FieldsMatrix getValuesOfFields(Integer resultsetId) throws HiddenException {
    return dbUtils.getValuesOfFields(resultsetId);
  }

  public FieldsMatrix getValuesOfForeignKeys(Integer resultsetId)
      throws HiddenException {
    return dbUtils.getValuesOfForeignKeys(resultsetId);
  }

  public ArrayList<BaseModelData> getForeignKeyInForATable(Integer resultsetId)
      throws DbException {
    return dbUtils.getForeignKeyInForATable(resultsetId);
  }
  
  public Integer removeObjects(Integer resultset,
      List<BaseModelData> selectedRows) throws HiddenException {
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
  public void sendMessage(Message message) throws HiddenException, VisibleException {

    MessageType type = message.getType();
    User sender = getUserByUid(message.getSender());
    dbUtils.sendMessage(message);
    Collection<User> users = null;
    
    switch (type) {
    case GROUP:
      users = getGroupUsers(sender.getGid());
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
        // TODO throws java.lang.IllegalMonitorStateException: current thread not owner 
        user.addEvent(EventList.NewMessage);
        user.notifyAll();
      }
    }

  }

  public Integer setObjects(Integer resultsetId, List<BaseModelData> newItemList)
      throws HiddenException {

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
      throws HiddenException {
    return dbUtils.setUserResultsetHeaderPreferencesNoDefault(userid,
        resultsetId, listfields, value);
  }

  // TODO get user from thread id
  public void updateUserProperties(User user) throws HiddenException {
    dbUtils.updateUserProperties(user);
  }

}
