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

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.ManagerService;
import it.fub.jardin.client.exception.HiddenException;
import it.fub.jardin.client.exception.VisibleException;
import it.fub.jardin.client.model.Credentials;
import it.fub.jardin.client.model.FieldsMatrix;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.Message;
import it.fub.jardin.client.model.MessageType;
import it.fub.jardin.client.model.Plugin;
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
import com.extjs.gxt.ui.client.event.EventType;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ManagerServiceImpl extends RemoteServiceServlet implements
    ManagerService {

  private static final long serialVersionUID = 1L;
  private final DbUtils dbUtils;
  private final Map<String, User> users = new HashMap<String, User>();

  public ManagerServiceImpl() {
    super();
    this.dbUtils = new DbUtils();
  }

  @Override
  public synchronized void log(final String message) {
    User user = this.getCurrentUser();
    Log.info("[" + user.getUsername() + "] " + message);
  }

  public String createReport(final String file, final Template template,
      final PagingLoadConfig config, final List<BaseModelData> selectedRows,
      final List<String> columns, final SearchParams searchParams,
      final char fs, final char ts) throws VisibleException {
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
      records = this.dbUtils.getObjects(config, searchParams);
    } else {
      records = selectedRows;
    }
    String xsl =
        this.getServletContext().getRealPath(
            Template.TEMPLATE_DIR + template.getXsl());

    /* Gestione del template di default */
    User user = this.getCurrentUser();
    if (template.getInfo().compareTo(Template.DEFAULT.getInfo()) == 0) {
      ResultsetImproved resultset =
          user.getResultsetFromId(searchParams.getResultsetId());
      try {
        FileUtils.prepareDefaultTemplate(resultset, xsl, columns);
      } catch (IOException e) {
        Log.error("Impossibile ottenere il template di default", e);
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
      Log.debug("File esportato: " + result);
      Log.debug("Servlet context path: " + context);
      return result.substring(context.length());
    } else {
      throw new VisibleException("Impossibile leggere il template");
    }
  }

  private synchronized User getCurrentUser() {
    String id = this.getThreadLocalRequest().getSession().getId();
    return this.users.get(id);
  }

  private synchronized List<User> getGroupUsers(final int gid) {
    ArrayList<User> group = new ArrayList<User>();
    for (User user : this.users.values()) {
      if (user.getGid() == gid) {
        group.add(user);
      }
    }
    return group;
  }

  public List<EventType> getEvents() {

    List<EventType> events = null;
    User user = this.getCurrentUser();
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
    List<BaseModelData> records = this.dbUtils.getObjects(config, searchParams);

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
    String id = this.getThreadLocalRequest().getSession().getId();
    synchronized (this) {
      this.users.put(id, user);
    }

    this.log("LOGIN");
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

  public Integer removeObjects(final Integer resultset,
      final List<BaseModelData> selectedRows) throws HiddenException {
    this.log("Removing records...");
    return this.dbUtils.removeObjects(resultset, selectedRows);
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
      final List<BaseModelData> newItemList) throws HiddenException {

    this.log("Setting records...");
    // recupero dei vecchi parametri
    // e passaggio a notifyCanges
    List<BaseModelData> newItemListTest = newItemList;
    int success = this.dbUtils.setObjects(resultsetId, newItemList);
    if (success > 0) {
      try {
        this.dbUtils.notifyChanges(resultsetId, newItemListTest);
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
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

  public ArrayList<Plugin> getPlugins(final int gid, final int rsid)
      throws HiddenException {
    return this.dbUtils.getPlugin(gid, rsid);
  }

  public Integer updateObjects(final Integer resultsetId,
      final List<BaseModelData> newItemList, final String condition)
      throws HiddenException {
    this.log("Setting records...");
    // recupero dei vecchi parametri
    // e passaggio a notifyCanges
    List<BaseModelData> newItemListTest = newItemList;
    int success =
        this.dbUtils.updateObjects(resultsetId, newItemList, condition);
    if (success > 0) {
      try {
        this.dbUtils.notifyChanges(resultsetId, newItemListTest);
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return success;
  }

}
