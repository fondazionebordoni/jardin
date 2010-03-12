/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007, 2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package it.fub.jardin.client;

import it.fub.jardin.client.exception.HiddenException;
import it.fub.jardin.client.exception.VisibleException;
import it.fub.jardin.client.model.Credentials;
import it.fub.jardin.client.model.EventTypeSerializable;
import it.fub.jardin.client.model.FieldsMatrix;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.IncomingForeignKeyInformation;
import it.fub.jardin.client.model.Message;
import it.fub.jardin.client.model.Plugin;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.Template;
import it.fub.jardin.client.model.User;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

/**
 * @author gpantanetti
 * 
 */
public interface ManagerService extends RemoteService {

  public String createReport(String file, Template template,
      PagingLoadConfig config, List<BaseModelData> selectedRows,
      List<String> columns, SearchParams searchParams, char fs, char ts) throws VisibleException;

  /**
   * Sits on listening and gets events from server.
   */
  public List<EventTypeSerializable> getEvents();

  public HeaderPreferenceList getGridViews(Integer userId, Integer resultsetId)
      throws HiddenException;

  public List<Integer> getHeaderUserPreference(Integer id,
      Integer userPreferenceHeaderId) throws HiddenException;

  /**
   * Effettua una ricerca su database. La query viene eseguita ritornando un
   * numero di risultati limitato secondo le specifiche contenute nella config
   * (configurazione di paginazione).
   */
  public PagingLoadResult<BaseModelData> getRecords(PagingLoadConfig config,
      SearchParams searchParams) throws HiddenException;

  public List<BaseModelData> getReGroupings(int resultSetId)
      throws HiddenException;

  /**
   * Chiede al server l'ora attuale formattata nel modo HH:MM. La funzione può
   * essere utilizzata in fase di login per controllare l'effettivo collegamento
   * tra server-side application e client-side frontend.
   * 
   * @return la stringa contenente l'ora del server
   */
  public String getServerTime();

  public User getUser(Credentials credentials) throws VisibleException;

  public List<Message> getUserMessages(Integer userId) throws HiddenException;

  /**
   * @param resultsetId
   * @param fieldId
   * @return Lista dei valori possibili per il determinato campo del determinato
   *         resultset. Serve per l'autocompletamento dei combobox
   */
  public List<BaseModelData> getValuesOfAField(int resultsetId, String fieldId)
      throws HiddenException;

  public List<BaseModelData> getValuesOfAFieldFromTableName(String table,
      String field) throws HiddenException;

  public FieldsMatrix getValuesOfFields(Integer resultsetId)
      throws HiddenException;

  public FieldsMatrix getValuesOfForeignKeys(Integer resultsetId)
      throws HiddenException;

  public Integer removeObjects(Integer resultset,
      List<BaseModelData> selectedRows) throws HiddenException;

  // TODO implement USER direct messages
  public void sendMessage(Message message) throws HiddenException,
      VisibleException;

  public Integer setObjects(Integer resultsetId, List<BaseModelData> newItemList)
      throws HiddenException;

  public boolean setUserResultsetHeaderPreferencesNoDefault(Integer userid,
      Integer resultsetId, ArrayList<Integer> listfields, String value)
      throws HiddenException;

  public void updateUserProperties(User user) throws HiddenException;

  public ArrayList<BaseModelData> getPopUpDetailEntry(BaseModelData data)
      throws HiddenException;

  public ArrayList<Plugin> getPlugins(int gid, int rsid) throws HiddenException;

}
