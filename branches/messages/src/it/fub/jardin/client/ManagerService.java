/*
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007, 2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package it.fub.jardin.client;

import it.fub.jardin.client.model.Credentials;
import it.fub.jardin.client.model.EventTypeSerializable;
import it.fub.jardin.client.model.FieldsMatrix;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.MessageType;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.Template;
import it.fub.jardin.client.model.User;
import it.fub.jardin.client.model.Message;

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
      PagingLoadConfig config, List<String> columns, SearchParams searchParams)
      throws UserException;

  /**
   * Sits on listening and gets events from server.
   * 
   * @gwt.typeArgs <it.fub.jardin.client.model.EventTypeSerializable>
   */
  public List<EventTypeSerializable> getEvents();

  public HeaderPreferenceList getGridViews(Integer userId, Integer resultsetId)
      throws DbException;

  public List<Integer> getHeaderUserPreference(Integer id,
      Integer userPreferenceHeaderId) throws DbException;

  /**
   * Effettua una ricerca su database. La query viene eseguita ritornando un
   * numero di risultati limitato secondo le specifiche contenute nella config
   * (configurazione di paginazione).
   */
  public PagingLoadResult<BaseModelData> getRecords(PagingLoadConfig config,
      SearchParams searchParams) throws DbException;

  public List<BaseModelData> getReGroupings(int resultSetId);

  /**
   * Chiede al server l'ora attuale formattata nel modo HH:MM. La funzione può
   * essere utilizzata in fase di login per controllare l'effettivo collegamento
   * tra server-side application e client-side frontend.
   * 
   * @return la stringa contenente l'ora del server
   */
  public String getServerTime();

  public User getUser(Credentials credentials) throws UserException;

  public List<Message> getUserMessages(Integer userId) throws DbException;

  /**
   * @param resultsetId
   * @param fieldId
   * @return Lista dei valori possibili per il determinato campo del determinato
   *         resultset. Serve per l'autocompletamento dei combobox
   */
  public List<BaseModelData> getValuesOfAField(int resultsetId, String fieldId)
      throws DbException;

  public List<BaseModelData> getValuesOfAFieldFromTableName(String table,
      String field) throws DbException;

  public FieldsMatrix getValuesOfFields(Integer resultsetId) throws DbException;

  public FieldsMatrix getValuesOfForeignKeys(Integer resultsetId)
      throws DbException;

  public Integer removeObjects(Integer resultset,
      List<BaseModelData> selectedRows) throws DbException;

  // TODO implement USER direct messages
  public void sendMessage(MessageType type, String title, String body);

  public Integer setObjects(Integer resultsetId, List<BaseModelData> newItemList)
      throws DbException;

  public boolean setUserResultsetHeaderPreferencesNoDefault(Integer userid,
      Integer resultsetId, ArrayList<Integer> listfields, String value)
      throws DbException;

  public void updateUserProperties(User user) throws DbException;

}
