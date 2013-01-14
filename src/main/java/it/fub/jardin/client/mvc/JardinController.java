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

package it.fub.jardin.client.mvc;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.Jardin;
import it.fub.jardin.client.ManagerServiceAsync;
import it.fub.jardin.client.SearchStringParser;
import it.fub.jardin.client.model.Credentials;
import it.fub.jardin.client.model.ForeignKey;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.IncomingForeignKeyInformation;
import it.fub.jardin.client.model.MassiveUpdateObject;
import it.fub.jardin.client.model.Message;
import it.fub.jardin.client.model.Plugin;
import it.fub.jardin.client.model.Resultset;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.SearchResult;
import it.fub.jardin.client.model.Template;
import it.fub.jardin.client.model.User;
import it.fub.jardin.client.widget.JardinGrid;
import it.fub.jardin.client.widget.JardinGridToolBar;
import it.fub.jardin.client.widget.JardinSelectColumnsForChartPopUp;
import it.fub.jardin.client.widget.JardinTabItem;
import it.fub.jardin.client.widget.Jungle;
import it.fub.jardin.client.widget.UploadDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.BarDataProvider;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.PieDataProvider;
import com.extjs.gxt.charts.client.model.charts.BarChart;
import com.extjs.gxt.charts.client.model.charts.BarChart.BarStyle;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class JardinController extends Controller {

  private static String webKitSuggest =
      "Jardin è ottimizzato per <i>WebKit</i>."
          + " Per ottenere prestazioni migliori si consiglia di usare"
          + " <a href=\"http://www.google.com/chrome/\">Chrome</a></li>"
          + " o Safari";

  private User user;
  private JardinView view;
  private ManagerServiceAsync service;

  private enum ChartType {
    PIE, BAR;
  }

  private static final String[] chartColors = { "#204a87", "#4e9a06",
      "#cc0000", "#75507b", "#f57900", "#edd400" };

  /**
   * Creazione del controller generale per l'applicazione. Vengono registrati
   * gli eventi che saranno gestiti da questo controller.
   */
  public JardinController() {
    this.registerEventTypes(EventList.Login);
    this.registerEventTypes(EventList.Error);
    this.registerEventTypes(EventList.CheckUser);
    this.registerEventTypes(EventList.CheckCredential);
    this.registerEventTypes(EventList.LoginError);
    this.registerEventTypes(EventList.Refresh);
    this.registerEventTypes(EventList.Init);
    this.registerEventTypes(EventList.CreateUI);
    this.registerEventTypes(EventList.CreateFirstTab);
    this.registerEventTypes(EventList.Search);
    this.registerEventTypes(EventList.CommitChanges);
    this.registerEventTypes(EventList.AddRow);
    this.registerEventTypes(EventList.RemoveRows);
    this.registerEventTypes(EventList.ExportAllStoreAllColumns);
    this.registerEventTypes(EventList.ExportAllStoreSomeColumns);
    this.registerEventTypes(EventList.ExportSomeStoreAllColumns);
    this.registerEventTypes(EventList.ExportSomeStoreSomeColumns);
    this.registerEventTypes(EventList.ExportSomeRowsAllColumns);
    this.registerEventTypes(EventList.ExportSomeRowsSomeColumns);
    this.registerEventTypes(EventList.ShowAllColumns);
    this.registerEventTypes(EventList.SaveGridView);
    this.registerEventTypes(EventList.GetGridViews);
    this.registerEventTypes(EventList.UploadTemplate);
    this.registerEventTypes(EventList.UploadImport);
    this.registerEventTypes(EventList.UploadInsert);
    this.registerEventTypes(EventList.UpdateColumnModel);
    this.registerEventTypes(EventList.Jungle);
    this.registerEventTypes(EventList.ShowPieChart);
    this.registerEventTypes(EventList.ShowBarChart);
    this.registerEventTypes(EventList.ShowChart);
    this.registerEventTypes(EventList.SendMessage);
    this.registerEventTypes(EventList.NewMessage);
    this.registerEventTypes(EventList.ViewLinkedTable);
    this.registerEventTypes(EventList.ViewPopUpDetail);
    this.registerEventTypes(EventList.GetPlugins);
    this.registerEventTypes(EventList.GotPlugins);
    this.registerEventTypes(EventList.ViewPlugin);
    this.registerEventTypes(EventList.getResultsetImproved);
    this.registerEventTypes(EventList.getResultsetPlus);
    this.registerEventTypes(EventList.gotResultsetImproved);
    this.registerEventTypes(EventList.gotResultsetPlus);
    this.registerEventTypes(EventList.initialChangePassword);
    this.registerEventTypes(EventList.CheckCredentialAndChangePassword);
    this.registerEventTypes(EventList.changePasswordError);
    this.registerEventTypes(EventList.getResultsetImprovedFromContextMenu);
    this.registerEventTypes(EventList.ViewAddingPopup);
    this.registerEventTypes(EventList.saveNewRecord);
    this.registerEventTypes(EventList.GetValuesOfAField);
    this.registerEventTypes(EventList.GotValuesOfAField);
    this.registerEventTypes(EventList.MassUpdate);

    service = (ManagerServiceAsync) Registry.get(Jardin.SERVICE);
  }

  @Override
  public void initialize() {
    this.view = new JardinView(this);
  }

  /**
   * Gestione degli eventi. (non-Javadoc)
   * 
   * @see com.extjs.gxt.ui.client.mvc.Controller#handleEvent(com.extjs.gxt.ui.client.mvc.AppEvent)
   */
  @Override
  public void handleEvent(final AppEvent event) {
    EventType t = event.getType();
    // System.out.println("CONTROLLER catturato evento: " + t.toString());
    // if (t == EventList.GetPlugins) {
    // System.out.println("CONTROLLER: catturato plugins " + t.toString());
    // }
    if (t == EventList.Login) {
      this.forwardToView(this.view, EventList.Login, this.loginMessage());
    } else if (t == EventList.CheckUser) {
      if (event.getData() instanceof Credentials) {
        Credentials credentials = (Credentials) event.getData();
        this.onCheckUser(credentials);
      } else {
        // TODO Gestire errore nei dati di EventList.CheckUser
      }
    } else if (t == EventList.CheckCredential) {
      if (event.getData() instanceof Credentials) {
        Credentials credentials = (Credentials) event.getData();
        this.onCheckCredential(credentials);
      } else {
        // TODO Gestire errore nei dati di EventList.CheckUser
      }
    } else if (t == EventList.CheckCredentialAndChangePassword) {
      if (event.getData() instanceof Credentials) {
        Credentials credentials = (Credentials) event.getData();
        this.onCheckCredentialAndChangePassword(credentials);
      } else {
        // TODO Gestire errore nei dati di EventList.CheckUser
        // System.out.println("ERRORE!!!!");
        forwardToView(view, new AppEvent(EventList.Login));
      }
    } else if (t == EventList.Init) {
      if (event.getData() instanceof User) {
        User user = (User) event.getData();
        this.onInit(user);
      } else {
        // TODO Gestire errore nei dati di EventList.Init
      }
    } else if (t == EventList.Refresh) {
      this.onRefresh(event);
    } else if (t == EventList.Error) {
      if (event.getData() instanceof String) {
        this.onError((String) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.Error
      }
    } else if (t == EventList.LoginError) {
      this.onLoginError(event);
    } else if (t == EventList.changePasswordError) {
      this.onChangePasswordError(event);
    } else if (t == EventList.CreateFirstTab) {
      this.onCreateFirstTab();
    } else if (t == EventList.CreateUI) {
      this.onCreateUI();
    } else if (t == EventList.Search) {
      if (event.getData() instanceof SearchParams) {
        SearchParams searchParams = (SearchParams) event.getData();
        this.onSearch(searchParams);
      } else {
        // TODO Gestire errore nei dati di EventList.Search
      }
    } else if (t == EventList.saveNewRecord) {
      if (event.getData() instanceof List<?>) {
        this.onSaveNewRecord((List<BaseModelData>) event.getData());
      }
    } else if (t == EventList.CommitChanges) {

      if (event.getData() instanceof JardinGrid) {
        JardinGrid grid = (JardinGrid) event.getData();
        this.onCommitChanges(grid);
      } else {
        // TODO Gestire errore nei dati di EventList.CommitChanges
      }
      /*
       * ------------------------------------------------------------------
       * ----- Gestione eventi della toolbar ------------------------------
       * -----------------------------------------
       */
    } else if (t == EventList.AddRow) {
      if (event.getData() instanceof Integer) {
        this.onAddRow((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.AddRow
        // Log.error("Errore nei dati di EventList.AddRow");
      }
    } else if (t == EventList.ViewPopUpDetail) {
      if (event.getData() instanceof BaseModelData) {
        this.onViewPopUpDetail((BaseModelData) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.ViewPopUpDetail
        // Log.error("Errore nei dati di EventList.ViewPopUpDetail");
      }
    } else if (t == EventList.RemoveRows) {
      if (event.getData() instanceof Integer) {
        this.onRemoveRows((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.RemoveRows
        // Log.error("Errore nei dati di EventList.RemoveRows");
      }
    } else if (t == EventList.ExportAllStoreAllColumns) {
      if (event.getData() instanceof Integer) {
        this.onExport((Integer) event.getData(), false, true, true);
      } else {
        // TODO Gestire errore nei dati di
        // EventList.ExportAllStoreAllColumns
        // Log.error("Errore nei dati di EventList.ExportAllStoreAllColumns");
      }
    } else if (t == EventList.ExportAllStoreSomeColumns) {
      if (event.getData() instanceof Integer) {
        this.onExport((Integer) event.getData(), false, true, false);
      } else {
        // TODO Gestire errore nei dati di
        // EventList.ExportAllStoreSomeColumns
        // Log.error("Errore nei dati di EventList.ExportAllStoreSomeColumns");
      }
    } else if (t == EventList.ExportSomeStoreAllColumns) {
      if (event.getData() instanceof Integer) {
        this.onExport((Integer) event.getData(), false, false, true);
      } else {
        // TODO Gestire errore nei dati di
        // EventList.ExportAllStoreAllColumns
        // Log.error("Errore nei dati di EventList.ExportAllStoreAllColumns");
      }
    } else if (t == EventList.ExportSomeStoreSomeColumns) {
      if (event.getData() instanceof Integer) {
        this.onExport((Integer) event.getData(), false, false, false);
      } else {
        // TODO Gestire errore nei dati di
        // EventList.ExportSomeStoreSomeColumns
        // Log.error("Errore nei dati di EventList.ExportAllStoreSomeColumns");
      }
    } else if (t == EventList.ExportSomeRowsAllColumns) {
      if (event.getData() instanceof Integer) {
        this.onExport((Integer) event.getData(), true, true, true);
      } else {
        // TODO Gestire errore nei dati di EventList.ExportAllStoreAllColumns
        // Log.error("Errore nei dati di EventList.ExportSomeRowAllColumns");
      }
    } else if (t == EventList.ExportSomeRowsSomeColumns) {
      if (event.getData() instanceof Integer) {
        this.onExport((Integer) event.getData(), true, true, false);
      } else {
        // TODO Gestire errore nei dati di EventList.ExportSomeStoreSomeColumns
        // Log.error("Errore nei dati di EventList.ExportSomeRowSomeColumns");
      }
    } else if (t == EventList.ShowAllColumns) {
      if (event.getData() instanceof Integer) {
        this.onShowAllColumns((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.ShowAllColumns
        // Log.error("Errore nei dati di EventList.ShowAllColumns");
      }
    } else if (t == EventList.SaveGridView) {
      if (event.getData() instanceof Integer) {
        this.onSaveGridView((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.SaveGridView
        // Log.error("Errore nei dati di EventList.SaveGridView");
      }
    } else if (t == EventList.GetGridViews) {
      if (event.getData() instanceof Integer) {
        this.onGetGridViews((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.GetGridViews
        // Log.error("Errore nei dati di EventList.GetGridViews");
      }
    } else if (t == EventList.UploadTemplate) {
      if (event.getData() instanceof Integer) {
        this.onUploadTemplate((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.UploadTemplate
        // Log.error("Errore nei dati di EventList.UploadTemplate");
      }
    } else if (t == EventList.UploadImport) {
      if (event.getData() instanceof Integer) {
        this.onUploadImport((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.UploadImport
        // Log.error("Errore nei dati di EventList.UploadImport");
      }
    } else if (t == EventList.UploadInsert) {
      if (event.getData() instanceof Integer) {
        this.onUploadInsert((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.UploadInsert
        // Log.error("Errore nei dati di EventList.UploadInsert");
      }
    } else if (t == EventList.UpdateColumnModel) {
      // TODO CAMBIARE!!!
      if (event.getData() instanceof JardinGrid) {
        this.onUpdateColumnModel((JardinGrid) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.UpdateColumnModel
        // Log.error("Errore nei dati di EventList.UpdateColumnModel");
      }
    } else if (t == EventList.Jungle) {
      if (event.getData() instanceof Integer) {
        this.onJungle((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.Jungle
        // Log.error("Errore nei dati di EventList.Jungle");
      }
    } else if (t == EventList.ShowChart) {
      if (event.getData() instanceof ArrayList) {
        this.onShowChart((ArrayList<String>) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.SelectColumnsForChart
        // Log.error("Errore nei dati di EventList.ShowChart");
      }
    } else if (t == EventList.ShowPieChart) {
      if (event.getData() instanceof Integer) {
        this.onSelectColumnsForChart(ChartType.PIE, (Integer) event.getData());
        // onShowChart(ChartType.PIE, (Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.ShowPieChart
        // Log.error("Errore nei dati di EventList.ShowPieChart");
      }
    } else if (t == EventList.ShowBarChart) {
      if (event.getData() instanceof Integer) {
        this.onSelectColumnsForChart(ChartType.BAR, (Integer) event.getData());
        // onShowChart(ChartType.BAR, (Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.ShowBarChart
        // Log.error("Errore nei dati di EventList.ShowBarChart");
      }
      /*
       * ------------------------------------------------------------------
       * ----- Altri eventi ------------------------------------------------
       * -----------------------
       */
    } else if (t == EventList.UpdateTemplates) {
      if (event.getData() instanceof Integer) {
        this.onUpdateTemplates((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.UpdateTemplates
        // Log.error("Errore nei dati di EventList.UpdateTemplates");
      }
    } else if (t == EventList.SendMessage) {
      if (event.getData() instanceof Message) {
        this.onSendMessage((Message) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.SendMessage
        // Log.error("Errore nei dati di EventList.SendMessage");
      }
    } else if (t == EventList.NewMessage) {
      this.onNewMessage();
    } else if (t == EventList.ViewLinkedTable) {
      if (event.getData() instanceof IncomingForeignKeyInformation) {
        this.onViewLinkedResultset((IncomingForeignKeyInformation) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.ViewLinkedTable
        // Log.error("Errore nei dati di EventList.ViewLinkedTable");
      }
    } else if (t == EventList.ViewAddingPopup) {
      if (event.getData() instanceof IncomingForeignKeyInformation) {
        this.onViewLinkedResultset((IncomingForeignKeyInformation) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.ViewLinkedTable
        // Log.error("Errore nei dati di EventList.ViewLinkedTable");
      }
    } else if (t == EventList.GetPlugins) {
      // System.out.println("CONTROLLER: richiesto menù plugins per "
      // +event.getData().toString()+ "!!!");
      if (event.getData() instanceof Integer) {
        this.onGetPlugins((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.GetPlugins
        // Log.error("Errore nei dati di EventList.GetPlugins");
      }
    } else if (t == EventList.ViewPlugin) {
      if (event.getData() instanceof String) {
        this.forwardToView(this.view, EventList.ViewPlugin, event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.ViewPlugin
        // Log.error("Errore nei dati di EventList.ViewPlugin");
      }
    } else if (t == EventList.getResultsetImproved) {
      if (event.getData() instanceof Integer) {
        this.onGetResultsetImproved((Integer) event.getData());
      } else if (event.getData() instanceof SearchParams) {
        // VUOL DIRE CHE IL RESULTSET NON È ANCORA APERTO!!!!!!!
      }
      // else Log.error("ERRORE recupero resultset cliccato");
    } else if (t == EventList.GetValuesOfAField) {
      if (event.getData() instanceof ForeignKey) {
        this.onGetValuesOfaField((ForeignKey) event.getData());
      } else
        System.out.println("ERRORE!!! che è??");
    } else if (t == EventList.MassUpdate) {
      if (event.getData() instanceof MassiveUpdateObject) {
        this.onMassiveUpdate((MassiveUpdateObject) event.getData());
      } else
        System.out.println("ERRORE!!! che è??");
    } 

  }

  private void onMassiveUpdate(MassiveUpdateObject data) {
    // TODO Auto-generated method stub
    AsyncCallback<Integer> callback =
        new AsyncCallback<Integer>() {

          @Override
          public void onFailure(Throwable arg0) {
            // TODO Auto-generated method stub
            MessageBox.alert("ERRORE interno", "Impossibile eseguire update massivo!", null);
          }

          @Override
          public void onSuccess(Integer result) {
            // TODO Auto-generated method stub
            if (result > 0) {
              MessageBox.info("SUCCESS", "Update massivo riuscito!", null);
            } else MessageBox.alert("ERRORE", "Impossibile eseguire update massivo!", null);
          }
        };

    service.massiveUpdate(data, callback);
  }

  private void onGetValuesOfaField(final ForeignKey data) {
    // TODO Auto-generated method stub
    AsyncCallback<List<BaseModelData>> callback =
        new AsyncCallback<List<BaseModelData>>() {

          @Override
          public void onFailure(Throwable arg0) {
            // TODO Auto-generated method stub

          }

          @Override
          public void onSuccess(List<BaseModelData> values) {
            // TODO Auto-generated method stub
            ForeignKey newData = new ForeignKey();
            newData.setPointingFieldName(data.getPointingFieldName());
            newData.setPointingResultsetId(data.getPointingResultsetId());
            newData.setPointedTableName(data.getPointedTableName());
            newData.setPointedFieldName(data.getPointedFieldName());
            List<String> newValues = new ArrayList<String>();
            for (BaseModelData m : values) {              
              newValues.add((String) m.get(data.getPointedFieldName()));
            }
            newData.setValues(newValues);
//            System.out.println("lunghezza lista sul controller: "
//                + newValues.size());
            forwardToView(view, EventList.GotValuesOfAField, newData);
          }
        };

    service.getValuesOfAFieldFromTableName(
        data.getPointedTableName().toString(),
        data.getPointedFieldName().toString(), callback);
  }

  private void onSaveNewRecord(List<BaseModelData> data) {
    final MessageBox waitbox =
        MessageBox.wait("Attendere", "Salvataggio in corso...", "");

    List<BaseModelData> appList = new ArrayList<BaseModelData>();
    appList.add(data.get(1));
    /* Set up the callback */
    AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
      public void onFailure(final Throwable caught) {
        waitbox.close();
        Dispatcher.forwardEvent(EventList.Error, caught.getLocalizedMessage());
      }

      public void onSuccess(final Integer result) {
        waitbox.close();
        if (result.intValue() > 0) {
          Info.display("Informazione", "Dati salvati", "");

        } else {
          Dispatcher.forwardEvent(EventList.Error,
              "Impossibile salvare il nuovo record");
        }
      }
    };

    /* Make the call */
    service.setObjects((Integer) data.get(0).get("RSID"), appList, callback);

  }

  private void onCheckCredential(Credentials credentials) {
    // TODO Auto-generated method stub
    AsyncCallback<User> callback = new AsyncCallback<User>() {
      public void onFailure(final Throwable caught) {
        Dispatcher.forwardEvent(EventList.LoginError,
            caught.getLocalizedMessage());
      }

      public void onSuccess(final User user) {
        if (user.getLogin() == 1) {
          forwardToView(view, EventList.initialChangePassword, user);
        } else
          Dispatcher.forwardEvent(EventList.Init, user);
      }
    };

    /* Make the call */
    service.getSimpleUser(credentials, callback);
  }

  private void onCheckCredentialAndChangePassword(Credentials credentials) {
    // TODO Auto-generated method stub
    AsyncCallback<User> callback = new AsyncCallback<User>() {
      public void onFailure(final Throwable caught) {
        Dispatcher.forwardEvent(EventList.changePasswordError,
            caught.getLocalizedMessage());
      }

      public void onSuccess(final User user) {
        if (user == null) {
          System.out.println("problema nel cambio password...utente nullo");
          forwardToView(view, EventList.changePasswordError, user);
        } else {
          Info.display("Aggiornamento Riuscito",
              "password aggiornata correttamente");
          Dispatcher.forwardEvent(EventList.Init, user);
        }
      }
    };

    /* Make the call */
    service.changePassword(credentials, callback);
  }

  private void onGetResultsetImproved(final int resultsetId) {
    final MessageBox box =
        MessageBox.progress("Please wait", "Loading items...",
            "Initializing...");
    final ProgressBar bar = box.getProgressBar();
    bar.auto();
    // TODO Auto-generated method stub
    AsyncCallback<ResultsetImproved> callback =
        new AsyncCallback<ResultsetImproved>() {

          @Override
          public void onFailure(Throwable caught) {
            // TODO Auto-generated method stub
            box.close();
            Info.display("ERRORE", "Impossibile caricare resultset");
            // Log.error("Errore nel caricamento della lista del resultSet "
            // + resultsetId);
          }

          @Override
          public void onSuccess(ResultsetImproved result) {
            // TODO Auto-generated method stub
            box.close();
            Info.display("OK", "Resultset caricato");
            onGotResultsetImproved(result);

          }

        };

    service.getResultsetImproved(resultsetId, user.getGid(), callback);
  }

  private void onCreateFirstTab() {
    // TODO Auto-generated method stub
    final MessageBox box =
        MessageBox.progress("Please wait", "Loading main menù...",
            "Initializing...");
    final ProgressBar bar = box.getProgressBar();
    bar.auto();
    AsyncCallback<List<Resultset>> callback =
        new AsyncCallback<List<Resultset>>() {

          @Override
          public void onFailure(Throwable caught) {
            // TODO Auto-generated method stub
            box.close();
            // Log.error("Errore nel caricamento della lista dei resultSet per l'utente "
            // + user.getName());
          }

          @Override
          public void onSuccess(List<Resultset> resultsetList) {
            // TODO Auto-generated method stub
            box.close();
            Info.display("Informazione", "Lista ResultSet caricata");
            user.setResultsetList(resultsetList);
            onGotFirstTab(resultsetList);
          }

        };

    service.getUserResultsetList(this.user.getUid(), callback);
  }

  private void onGotResultsetImproved(ResultsetImproved result) {
    // TODO Auto-generated method stub

    if (this.user.getResultsetImprovedFromId(result.getId()) == null) {
      // System.out.println("resultsetIMPROVED " + result.getName() +
      // " aggiunto alla lista");
      this.user.addResultsetToList(result);
    }
    this.forwardToView(this.view, EventList.gotResultsetImproved, result);
  }

  private void onGotFirstTab(List<Resultset> resultsetList) {
    // TODO Auto-generated method stub
    this.user.setResultsetList(resultsetList);
    this.forwardToView(this.view, EventList.GotResultsetList, user);
  }

  private void onNewMessage() {
    // final ManagerServiceAsync service =
    // (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    /* Set up the callback */
    AsyncCallback<List<Message>> callback = new AsyncCallback<List<Message>>() {

      public void onFailure(final Throwable caught) {
        // TODO Auto-generated method stub

      }

      public void onSuccess(final List<Message> messages) {
        JardinController.this.user.setMessages(messages);
        Info.display("Informazione", "Hai ricevuto un nuovo messaggio");
        // forwardToView(view, EventList.NewMessage, null);
      }
    };

    /* Make the call */
    service.getUserMessages(this.user.getUid(), callback);
  }

  private void onSendMessage(final Message message) {

    /* Fill message with sender id */
    if (message.getSender() < 0) {
      message.setSender(this.user.getUid());
    }

    // final ManagerServiceAsync service =
    // (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    /* Set up the callback */
    AsyncCallback callback = new AsyncCallback() {

      public void onFailure(final Throwable caught) {
        // TODO Auto-generated method stub

      }

      public void onSuccess(final Object result) {
        Info.display("Informazione", "Messaggio inviato e memorizzato.");
        // TODO Auto-generated method stub

      }
    };

    /* Make the call */
    service.sendMessage(message, callback);
  }

  public User getUser() {
    return this.user;
  }

  private String loginMessage() {
    if (GXT.isWebKit) {
      return null;
    } else {
      return webKitSuggest;
    }
  }

  private void onError(final String error) {
    MessageBox.alert("Errore", error, null);
  }

  private void onCheckUser(final Credentials credentials) {

    // final ManagerServiceAsync service =
    // (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    /* Set up the callback */
    AsyncCallback<User> callback = new AsyncCallback<User>() {
      public void onFailure(final Throwable caught) {
        Dispatcher.forwardEvent(EventList.LoginError,
            caught.getLocalizedMessage());
      }

      public void onSuccess(final User user) {
        Dispatcher.forwardEvent(EventList.Init, user);
      }
    };

    /* Make the call */
    service.getUser(credentials, callback);
  }

  private void onLoginError(final AppEvent event) {
    this.forwardToView(this.view, event);
  }

  private void onChangePasswordError(final AppEvent event) {
    this.forwardToView(this.view, event);
  }

  private void onRefresh(final AppEvent event) {
    this.forwardToView(this.view, event);
  }

  private void onInit(final User user) {
    this.user = user;
    user.addEvent(EventList.NewMessage);

    // final ManagerServiceAsync service =
    // (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    AsyncCallback<List<EventType>> callback =
        new AsyncCallback<List<EventType>>() {

          public void onSuccess(final List<EventType> eventTypes) {

            for (EventType eventType : eventTypes) {
              JardinController.this.handleEvent(new AppEvent(eventType));
            }
            service.getEvents(this);
          }

          public void onFailure(final Throwable caught) {
            // TODO Auto-generated method stub

          }
        };

    service.getEvents(callback);

    this.forwardToView(this.view, EventList.Init, user);
  }

  private void onCreateUI() {
    /* Vedi sequence diagram init_sd.pic */

    /* Per ogni resultset carica da service le sue proprietà */
    for (ResultsetImproved resultset : this.user.getResultsets()) {
      final Integer resultsetId = resultset.getId();
      /* Avvisa la view che si sta creando un nuovo resultset */
      this.forwardToView(this.view, EventList.NewResultset, resultsetId);
      this.forwardToView(this.view, EventList.GotValuesOfFields, resultsetId);
      // forwardToView(view, EventList.gotValuesOfForeignKeys,
      // resultsetId);

      // final ManagerServiceAsync service =
      // (ManagerServiceAsync) Registry.get(Jardin.SERVICE);
      //
      // /*
      // * Carica i valori dei vincoli di integrità referenziale attualmente
      // * presenti: sever per il binding dei campi del dettaglio e per il row
      // * editor
      // */
      //
      // AsyncCallback<FieldsMatrix> callbackValuesOfForeignKeys =
      // new AsyncCallback<FieldsMatrix>() {
      // public void onFailure(Throwable caught) {
      // Dispatcher.forwardEvent(EventList.Error,
      // caught.getLocalizedMessage());
      // }
      //
      // public void onSuccess(FieldsMatrix fieldsMatrix) {
      // ResultsetImproved rs = user.getResultsetFromId(resultsetId);
      // rs.setForeignKeyList(fieldsMatrix);
      // forwardToView(view, EventList.GotValuesOfForeignKeys, resultsetId);
      // }
      // };
      //
      // service.getValuesOfForeignKeys(resultsetId,
      // callbackValuesOfForeignKeys);
      this.forwardToView(this.view, EventList.GotValuesOfForeignKeys,
          resultsetId);
    }
  }

  private void onSearch(final SearchParams searchParams) {
    new MessageBox();
    final MessageBox waitBox =
        MessageBox.wait("Caricamento dati", "Attendere prego...", "Loading...");
    // TODO Modificare per gestire il solo resultsetId come parametro
    // if
    // (this.user.getResultsetImprovedFromId(searchParams.getResultsetId()).isRead())
    // {
    if (this.user.getResultsetImprovedFromId(searchParams.getResultsetId()) != null) {
      if (this.user.getResultsetFromId(searchParams.getResultsetId()).getPermissions().isReadperm()) {

        // ///////////////////////////////////////////
        // alla griglia servono i searchParams

        this.forwardToView(this.view, EventList.Search, searchParams);

        final boolean limit = searchParams.isLimit();
        // final ManagerServiceAsync service =
        // (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

        RpcProxy<PagingLoadResult<BaseModelData>> proxy =
            new RpcProxy<PagingLoadResult<BaseModelData>>() {
              @Override
              public void load(final Object loadConfig,
                  final AsyncCallback<PagingLoadResult<BaseModelData>> callback) {
                PagingLoadConfig plc = (PagingLoadConfig) loadConfig;
                if (!limit) {
                  plc.setLimit(-1);
                }
                service.getRecords(plc, searchParams, callback);
              }
            };

        PagingLoader<PagingLoadResult<BaseModelData>> loader =
            new BasePagingLoader<PagingLoadResult<BaseModelData>>(proxy);

        loader.setRemoteSort(true);
        ListStore<BaseModelData> store = new ListStore<BaseModelData>(loader);

        loader.addLoadListener(new LoadListener() {
          @Override
          public void loaderLoad(final LoadEvent le) {
            waitBox.close();
          }

          @Override
          public void loaderLoadException(final LoadEvent le) {
            waitBox.close();
            Dispatcher.forwardEvent(EventList.Error, le.exception);
          }
        });

        SearchResult searchResult = new SearchResult();
        searchResult.setResultsetId(searchParams.getResultsetId());
        searchResult.setStore(store);
        this.forwardToView(this.view, EventList.Searched, searchResult);
        // ///////////////////////////////////////////

        // forwardToView(view, EventList.Search, searchParams);
      } else {
        Dispatcher.forwardEvent(EventList.Error,
            "L'utente non dispone dei permessi di lettura");
      }
    } else { // questo resultsetimproved non è stato ancora caricato-->niente
             // tab sulla griglia
      // Dispatcher.forwardEvent(EventList.getResultsetImprovedFromContextMenu,
      // searchParams.getResultsetId());
    }
  }

  private void onCommitChanges(final JardinGrid grid) {

    // TODO Modificare per gestire il solo resultsetID come parametro
    ResultsetImproved resultset = grid.getResultset();
    if (resultset.isInsert() || resultset.isModify()) {

      List<BaseModelData> newItemList = new ArrayList<BaseModelData>();
      for (Record rec : grid.getStore().getModifiedRecords()) {
        newItemList.add((BaseModelData) rec.getModel());
      }

      if (newItemList.size() > 0) {
        final MessageBox waitbox =
            MessageBox.wait("Attendere", "Salvataggio modifiche in corso...",
                "");

        /* Create the service proxy class */
        // final ManagerServiceAsync service =
        // (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

        /* Set up the callback */
        AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
          public void onFailure(final Throwable caught) {
            waitbox.close();
            grid.getStore().rejectChanges();
            Dispatcher.forwardEvent(EventList.Error,
                caught.getLocalizedMessage());
          }

          public void onSuccess(final Integer retCode) {
            waitbox.close();
            grid.getStore().commitChanges();
            Info.display("Informazione", "Modifiche salvate sul Database");
          }
        };

        /* Make the call */
        // service.setObjects(resultset.getId(), newItemList, callback);
        service.updateObjects(resultset.getId(), newItemList, new String(
            "$-notspec-$"), callback);
      } else {
        Info.display("Informazione", "Nessuna modifica da salvare", "");
      }
    } else {
      Dispatcher.forwardEvent(EventList.Error,
          "L'utente non dispone dei permessi di modifica");
    }
  }

  private void onAddRow(final int resultset) {
    if (this.user.getResultsetImprovedFromId(resultset).isInsert()) {
      this.forwardToView(this.view, EventList.AddRow, resultset);
    } else {
      Dispatcher.forwardEvent(EventList.Error,
          "L'utente non dispone dei permessi di inserimento");
    }
  }

  private void onViewPopUpDetail(final BaseModelData data) {
    // if (((Resultset) data.get("RSLINKED")).getPermissions().isReadperm()) {
    // restituisce il BaseModelData con la riga interessata

    // final ManagerServiceAsync service =
    // (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    AsyncCallback<ArrayList<BaseModelData>> callbackPopUpDetailEntry =
        new AsyncCallback<ArrayList<BaseModelData>>() {
          public void onFailure(final Throwable caught) {
            Dispatcher.forwardEvent(EventList.Error,
                caught.getLocalizedMessage());
          }

          public void onSuccess(final ArrayList<BaseModelData> infoToView) {
            forwardToView(view, EventList.ViewPopUpDetail, infoToView);
          }
        };

    service.getPopUpDetailEntry(data, callbackPopUpDetailEntry);

    // } else {
    // Dispatcher.forwardEvent(EventList.Error,
    // "L'utente non dispone dei permessi di visualizzazione");
    // }
  }

  private void onRemoveRows(final int resultset) {
    if (this.user.getResultsetImprovedFromId(resultset).isDelete()) {
      final JardinGrid grid =
          this.view.getItemByResultsetId(resultset).getGrid();

      final List<BaseModelData> selectedRows =
          grid.getSelectionModel().getSelection();

      int numrows = selectedRows.size();
      if (numrows > 0) {

        String prompt = "Vuoi davvero eliminare ";
        if (numrows > 1) {
          prompt += numrows + " righe dal database?";
        } else {
          prompt += "la riga dal database?";
        }

        final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
          public void handleEvent(final MessageBoxEvent ce) {
            Button btn = ce.getButtonClicked();
            if (btn.getText().equalsIgnoreCase("yes")) {
              final MessageBox waitbox =
                  MessageBox.wait("Attendere", "Eliminazione in corso...", "");

              /* Create the service proxy class */
              // final ManagerServiceAsync service =
              // (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

              /* Set up the callback */
              AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
                public void onFailure(final Throwable caught) {
                  waitbox.close();
                  Dispatcher.forwardEvent(EventList.Error,
                      caught.getLocalizedMessage());
                }

                public void onSuccess(final Integer result) {
                  waitbox.close();
                  if (result.intValue() <= 0) {
                    Dispatcher.forwardEvent(EventList.Error,
                        "Nessuna riga eliminata");
                  } else {
                    ListStore<BaseModelData> store = grid.getStore();
                    for (BaseModelData row : selectedRows) {
                      store.remove(row);
                    }
                    store.commitChanges();
                    Info.display("Informazione", "Dati cancellati", "");
                  }
                }
              };

              /* Make the call */
              service.removeObjects(resultset, selectedRows, callback);
            }
          }
        };

        MessageBox.confirm("Attenzione", prompt, l);

      } else {
        Info.display("Informazione", "Selezionare almeno una riga", "");
      }
    } else {
      Dispatcher.forwardEvent(EventList.Error,
          "L'utente non dispone dei permessi di eliminazione");
    }
  }

  /**
   * Esporta tutti i dati contenuti nella griglia in formato CSV
   * 
   * @param grid
   *          la griglia che contiene i dati da esportare
   * @param allStore
   *          se esportare o no tutti i record dello store o solo quelli
   *          visualizzati nella griglia
   * @param allColumns
   *          se esportare o no tutte le colonne dello store o solo quelle
   *          visualizate nella griglia
   */
  // private void onExport(BaseModelData dataForExport) {
  // int resultset = dataForExport.get("resultsetId");
  // boolean allRows = dataForExport.get("allRows");
  // boolean allColumns = dataForExport.get("allColumns");
  // boolean allStore = dataForExport.get("allStore");
  // String fs = dataForExport.get("fs");
  // String ts = dataForExport.get("ts");
  private void onExport(final int resultset, final boolean allRows,
      final boolean allStore, final boolean allColumns) {

    new MessageBox();
    final MessageBox waitBox =
        MessageBox.wait("Caricamento dati", "Attendere prego...", "Loading...");

    /* Nome del file da creare */
    String filename =
        this.user.getResultsetImprovedFromId(resultset).getAlias().replace(" ",
            "_");

    /*
     * Prendi il tabItem per recuperare la toolbar (formato d'esportazione) e la
     * grid (config dei record da esportare, colonne visibili e criteri di
     * ricerca)
     */
    JardinTabItem item = this.view.getItemByResultsetId(resultset);

    /* Prendi il formato di esportazione */
    JardinGridToolBar toolbar = item.getToolbar();
    Template template = toolbar.getTemplate();

    // Prendi i separatori per l'export csv
    // if toolbar
    char fs = toolbar.getFieldSeparator();
    char ts = toolbar.getTextSeparator();

    /* Prendi la griglia */
    JardinGrid grid = item.getGrid();

    /* Config dei record da esportare (tutti se allStore = true) */
    PagingLoadConfig config = null;
    List<BaseModelData> selectedRows = null;
    if (!allStore) {
      config = (PagingLoadConfig) grid.getStore().getLoadConfig();
    } else if (allRows) {
      selectedRows = grid.getSelectionModel().getSelection();
    }

    /* Colonne visibili (tutte se allColumns = true) */
    ColumnModel cm = grid.getColumnModel();
    List<String> columns = new ArrayList<String>();
    for (int i = 0; i < cm.getColumnCount(); i++) {
      if (allColumns || !cm.getColumn(i).isHidden()) {
        columns.add(cm.getColumn(i).getId());
      }
    }

    /* Criteri di ricerca */
    SearchParams searchParams = grid.getSearchparams();

    /* Effettua la chiamata RPC */
    // final ManagerServiceAsync service =
    // (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    AsyncCallback<String> callback = new AsyncCallback<String>() {
      public void onFailure(final Throwable caught) {
        waitBox.close();
        Dispatcher.forwardEvent(EventList.Error, caught.getLocalizedMessage());
      }

      public void onSuccess(final String result) {
        waitBox.close();
        if ((result != null) && (result.length() > 0)) {
          // Log.debug("Export file: " + result);
          String url = GWT.getModuleBaseURL() + "download?file=" + result;
          Window.open(url, "Download", null);
        } else {
          // Log.warn("File d'esportazione vuoto");
        }
      }
    };

    service.createReport("/tmp/" + filename, template, config, selectedRows,
        columns, searchParams, fs, ts, callback);
  }

  private void onShowAllColumns(final int resultset) {
    this.forwardToView(this.view, EventList.ShowAllColumns, resultset);
  }

  private void onSaveGridView(final int resultset) {
    this.forwardToView(this.view, EventList.SaveGridView, resultset);
  }

  private void onGetGridViews(final int resultset) {
    // final ManagerServiceAsync service =
    // (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    AsyncCallback<HeaderPreferenceList> callback =
        new AsyncCallback<HeaderPreferenceList>() {
          public void onFailure(final Throwable caught) {
            Dispatcher.forwardEvent(EventList.Error,
                caught.getLocalizedMessage());
          }

          public void onSuccess(final HeaderPreferenceList result) {
            Info.display("Informazione", "Lista delle preferenze caricata");
            JardinController.this.forwardToView(JardinController.this.view,
                EventList.GotHeaderPreference, result);
          }
        };

    service.getGridViews(this.user.getUid(), resultset, callback);
  }

  private void onUploadTemplate(final int resultset) {
    UploadDialog d =
        new UploadDialog(this.user, UploadDialog.TYPE_TEMPLATE, resultset);
    d.show();
  }

  private void onUploadImport(final int resultset) {
    UploadDialog d =
        new UploadDialog(this.user, UploadDialog.TYPE_IMPORT, resultset);
    d.show();
  }

  private void onUploadInsert(final int resultset) {
    UploadDialog d =
        new UploadDialog(this.user, UploadDialog.TYPE_INSERT, resultset);
    d.show();
  }

  private void onUpdateColumnModel(final JardinGrid grid) {
    // final ManagerServiceAsync service =
    // (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    AsyncCallback<List<Integer>> callback = new AsyncCallback<List<Integer>>() {
      public void onFailure(final Throwable caught) {
        Dispatcher.forwardEvent(EventList.Error, caught.getLocalizedMessage());
      }

      public void onSuccess(final List<Integer> result) {
        grid.updateGridHeader(result);
      }
    };

    service.getHeaderUserPreference(this.user.getUid(),
        grid.getUserPreferenceHeaderId(), callback);
  }

  private void onJungle(final int resultset) {
    /* Nome del file da creare */
    String filename =
        this.user.getResultsetImprovedFromId(resultset).getAlias();

    /*
     * Prendi il tabItem per recuperare la toolbar (formato d'esportazione) e la
     * grid (config dei record da esportare, colonne visibili e criteri di
     * ricerca)
     */
    JardinTabItem item = this.view.getItemByResultsetId(resultset);

    /* Prendi la griglia */
    JardinGrid grid = item.getGrid();

    /* Colonne */
    ColumnModel cm = grid.getColumnModel();
    final List<String> columns = new ArrayList<String>();
    for (int i = 0; i < cm.getColumnCount(); i++) {
      if (!(cm.getColumn(i).isHidden())) {
        columns.add(cm.getColumn(i).getId());
      }
    }

    /* Criteri di ricerca */
    SearchParams searchParams = grid.getSearchparams();

    /* Effettua la chiamata RPC */
    // final ManagerServiceAsync service =
    // (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    AsyncCallback<String> callback = new AsyncCallback<String>() {
      public void onFailure(final Throwable caught) {
        Dispatcher.forwardEvent(EventList.Error, caught.getLocalizedMessage());
      }

      public void onSuccess(final String result) {
        if ((result != null) && (result.length() > 0)) {
          Jungle j =
              new Jungle(
                  JardinController.this.user.getResultsetImprovedFromId(resultset),
                  columns, result);
          j.show();
        } else {
          // Log.warn("File d'esportazione vuoto");
        }
      }
    };
    char noChar = 0;
    service.createReport("/tmp/" + filename, Template.XML, null, null, columns,
        searchParams, noChar, noChar, callback);

  }

  /**
   * Recupera i 2 campi da utilizzare come testo - valore nel grafico
   * 
   * @param ct
   * 
   * @param type
   * @param resultset
   */
  private void onSelectColumnsForChart(final ChartType ct,
      final Integer resultset) {
    JardinTabItem item = this.view.getItemByResultsetId(resultset);
    JardinGrid grid = item.getGrid();

    JardinSelectColumnsForChartPopUp popup =
        new JardinSelectColumnsForChartPopUp(grid, ct.toString());

    popup.show();

  }

  /**
   * Genera un grafico prendendo le prime due colonne visibili della griglia. La
   * prima deve essere di tipo stringa o numerico, la seconda deve essere di
   * tipo numerico
   * 
   * @param type
   * @param resultset
   */
  private void onShowChart(final ArrayList<String> dataToChart) {

    /*
     * Prendi il tabItem per recuperare la toolbar (formato d'esportazione) e la
     * grid (config dei record da esportare, colonne visibili e criteri di
     * ricerca)
     */
    Integer resultset = Integer.valueOf(dataToChart.get(1));
    String title = dataToChart.get(2);
    String value = dataToChart.get(3);
    ChartType type = ChartType.valueOf(dataToChart.get(0));

    JardinTabItem item = this.view.getItemByResultsetId(resultset);

    /* Prendi la griglia */
    JardinGrid grid = item.getGrid();

    /* Prendi gli ID delle prime due colonne visibili */
    // ColumnModel columnModel = grid.getColumnModel();
    String cx = title;
    String cy = value;

    String url = "resources/chart/open-flash-chart.swf";

    Chart chart = new Chart(url);
    chart.setBorders(false);

    String resultsetAlias =
        this.user.getResultsetImprovedFromId(resultset).getAlias();
    ChartModel cm = new ChartModel(resultsetAlias);
    cm.setBackgroundColour("#ffffff");

    // SearchParams searchParams = grid.getSearchparams();
    // ListStore<BaseModelData> store = view.getStore(searchParams, false);
    ListStore<BaseModelData> store = grid.getStore();
    store.getLoader().load();

    switch (type) {
    case BAR:
      BarChart bar = new BarChart(BarStyle.GLASS);
      bar.setAnimateOnShow(true);
      bar.setTooltip("#val#");
      BarDataProvider bdp = new BarDataProvider(cy, cx);
      bdp.bind(store);
      System.out.println(store);
      bar.setDataProvider(bdp);
      // cm.setScaleProvider(ScaleProvider.ROUNDED_NEAREST_SCALE_PROVIDER);
      cm.addChartConfig(bar);
      break;
    case PIE:
    default:
      PieChart pie = new PieChart();
      pie.setAlpha(0.5f);
      pie.setNoLabels(false);
      pie.setTooltip("#label# #val#<br>#percent#");
      pie.setGradientFill(true);
      pie.setColours(chartColors);
      PieDataProvider pdp = new PieDataProvider(cy, cx);
      pdp.bind(store);
      pie.setDataProvider(pdp);
      cm.addChartConfig(pie);
      break;
    }
    chart.setChartModel(cm);

    Dialog d = new Dialog();
    d.getButtonBar().removeAll();
    d.setMaximizable(true);
    d.setHeading("Grafico " + resultsetAlias);
    d.setIconStyle("icon-chart");
    d.setLayout(new FitLayout());
    d.setSize(500, 500);
    d.add(chart);
    d.show();
  }

  private void onUpdateTemplates(final int resultset) {
    // TODO Auto-generated method stub
  }

  private void onViewLinkedResultset(final IncomingForeignKeyInformation ifki) {
    ResultsetImproved rs = ifki.getInterestedResultset();
    SearchParams searchParams = new SearchParams(rs.getId());
    searchParams.setAccurate(true);
    List<BaseModelData> queryFieldList = new ArrayList<BaseModelData>();
    SearchStringParser parser =
        new SearchStringParser(ifki.getLinkingField() + "=\""
            + ifki.getFieldValue() + "\"");

    Map<String, String> searchMap = parser.getSearchMap();
    for (String key : parser.getSearchMap().keySet()) {
      BaseModelData m = new BaseModelData();
      m.set(key, searchMap.get(key));
      queryFieldList.add(m);
    }
    searchParams.setFieldsValuesList(queryFieldList);
    Dispatcher.forwardEvent(EventList.Search, searchParams);
  }

  private void onGetPlugins(final Integer data) {
    ResultsetImproved rs = this.user.getResultsetImprovedFromId(data);
    // final JardinGrid grid = view.getItemByResultsetId(data).getGrid();

    // final ManagerServiceAsync service =
    // (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    AsyncCallback<List<Plugin>> callback = new AsyncCallback<List<Plugin>>() {
      public void onFailure(final Throwable caught) {
        Dispatcher.forwardEvent(EventList.Error, caught.getLocalizedMessage());
      }

      public void onSuccess(final List<Plugin> result) {
        JardinController.this.forwardToView(JardinController.this.view,
            EventList.GotPlugins, result);
        // System.out.println("CONTROLLER: fine recupero plugin");
      }
    };

    service.getPlugins(this.user.getGid(), rs.getId(), callback);

  }
}
