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

package it.fub.jardin.client;

import it.fub.jardin.client.model.Credentials;
import it.fub.jardin.client.model.FieldsMatrix;
import it.fub.jardin.client.model.HeaderPreferenceList;
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
import com.extjs.gxt.ui.client.event.EventType;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ManagerServiceAsync {

  public void createReport(String file, Template template,
      PagingLoadConfig config, List<BaseModelData> selectedRows,
      List<String> columns, SearchParams searchParams, char fs, char ts,
      AsyncCallback<String> callback);

  void getEvents(AsyncCallback<List<EventType>> callback);

  public void getGridViews(Integer userId, Integer resultsetId,
      AsyncCallback<HeaderPreferenceList> callback);

  public void getHeaderUserPreference(Integer id,
      Integer userPreferenceHeaderId, AsyncCallback<List<Integer>> callback);

  public void getRecords(PagingLoadConfig loadConfig,
      SearchParams searchParams,
      AsyncCallback<PagingLoadResult<BaseModelData>> callback);

  public void getReGroupings(int resultSetId,
      AsyncCallback<List<BaseModelData>> callback);

  public void getServerTime(AsyncCallback<String> callback);

  public void getUser(Credentials credentials, AsyncCallback<User> callback);

  public void getUserMessages(Integer userId,
      AsyncCallback<List<Message>> callback);

  public void getValuesOfAField(int resultsetId, String fieldId,
      AsyncCallback<List<BaseModelData>> callback);

  public void getValuesOfAFieldFromTableName(String table, String field,
      AsyncCallback<List<BaseModelData>> callback);

  public void getValuesOfFields(Integer resultsetId,
      AsyncCallback<FieldsMatrix> callback);

  public void getValuesOfForeignKeys(Integer resultsetId,
      AsyncCallback<FieldsMatrix> callback);

  public void removeObjects(Integer resultset,
      List<BaseModelData> selectedRows, AsyncCallback<Integer> asyncCallback);

  // TODO implement USER direct messages
  public void sendMessage(Message message, AsyncCallback callback);

  public void setObjects(Integer resultset, List<BaseModelData> newItemList,
      AsyncCallback<Integer> asyncCallback);

  public void setUserResultsetHeaderPreferencesNoDefault(Integer integer,
      Integer resultsetId, ArrayList<Integer> headerFields, String value,
      AsyncCallback<Boolean> callback);

  public void updateUserProperties(User user, AsyncCallback callback);

  public void getPopUpDetailEntry(BaseModelData data,
      AsyncCallback<ArrayList<BaseModelData>> callbackPopUpDetailEntry);

  public void getPlugins(int gid, int rsid,
      AsyncCallback<ArrayList<Plugin>> callback);

  public void updateObjects(Integer resultsetId,
      List<BaseModelData> newItemList, String condition,
      AsyncCallback<Integer> callback);
}
