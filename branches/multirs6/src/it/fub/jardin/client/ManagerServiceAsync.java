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
import it.fub.jardin.client.model.IncomingForeignKeyInformation;
import it.fub.jardin.client.model.Message;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.Template;
import it.fub.jardin.client.model.User;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ManagerServiceAsync {

  public void createReport(String file, Template template,
      PagingLoadConfig config, List<String> columns, SearchParams searchParams,
      AsyncCallback<String> callback);

  void getEvents(AsyncCallback<List<EventTypeSerializable>> callback);

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

}
