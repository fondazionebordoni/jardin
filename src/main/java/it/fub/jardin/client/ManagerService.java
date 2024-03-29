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

package it.fub.jardin.client;

import it.fub.jardin.client.exception.HiddenException;
import it.fub.jardin.client.exception.VisibleException;
import it.fub.jardin.client.model.Credentials;
import it.fub.jardin.client.model.FieldsMatrix;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.MassiveUpdateObject;
import it.fub.jardin.client.model.Message;
import it.fub.jardin.client.model.NewObjects;
import it.fub.jardin.client.model.Plugin;
import it.fub.jardin.client.model.RegistrationInfo;
import it.fub.jardin.client.model.Resultset;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.ResultsetPlus;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.Template;
import it.fub.jardin.client.model.User;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.event.EventType;
import com.google.gwt.user.client.rpc.RemoteService;

public interface ManagerService extends RemoteService {
  
  public Integer checkRegistrationInfo(RegistrationInfo regInfo) throws VisibleException, HiddenException ;
  
  public Integer massiveUpdate(MassiveUpdateObject muo, int userId) throws VisibleException, HiddenException;

  public User changePassword(Credentials credentials) throws VisibleException, HiddenException;
  
  public ResultsetImproved getResultsetImproved(int resultsetId, int gid) throws HiddenException;
  
  public ResultsetPlus getResultsetPlus(int resultsetId, int gid) throws HiddenException;
  
  public String createReport(String file, Template template,
    PagingLoadConfig config, List<BaseModelData> selectedRows,
    List<String> columns, SearchParams searchParams, char fs, char ts)
    throws VisibleException, HiddenException;

  /**
   * Sits on listening and gets events from server.
   */
  public List<EventType> getEvents(int userId);

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

  public User getSimpleUser(final Credentials credentials) throws VisibleException;
  
//  public User getSimpleUserAndChangePassword(final Credentials credentials) throws VisibleException;
  
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
    List<BaseModelData> selectedRows, Integer userId) throws HiddenException, VisibleException;

  // TODO implement USER direct messages
  public void sendMessage(Message message) throws HiddenException,
    VisibleException;

  public Integer setObjects(Integer resultsetId, List<BaseModelData> newItemList, Integer userId)
    throws HiddenException;

  public Integer updateObjects(Integer resultsetId,
    List<BaseModelData> newItemList, String condition, Integer userId) throws HiddenException;

  public boolean setUserResultsetHeaderPreferencesNoDefault(Integer userid,
    Integer resultsetId, ArrayList<Integer> listfields, String value)
    throws HiddenException;

  public void updateUserProperties(User user) throws HiddenException;

  public ArrayList<BaseModelData> getPopUpDetailEntry(BaseModelData data)
    throws HiddenException;

  public List<Plugin> getPlugins(int gid, int rsid) throws HiddenException;

  public List<Resultset> getUserResultsetList(int uid) throws HiddenException;

  public String testServerPresence();

}
