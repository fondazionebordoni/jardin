package it.fub.jardin.client.mvc;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.Jardin;
import it.fub.jardin.client.ManagerServiceAsync;
import it.fub.jardin.client.SearchStringParser;
import it.fub.jardin.client.model.Credentials;
import it.fub.jardin.client.model.EventTypeSerializable;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.IncomingForeignKeyInformation;
import it.fub.jardin.client.model.Message;
import it.fub.jardin.client.model.Plugin;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.Template;
import it.fub.jardin.client.model.User;
import it.fub.jardin.client.model.SearchResult;
import it.fub.jardin.client.testLayoutGWTPKG.ResultSetGui;
import it.fub.jardin.client.testLayoutGWTPKG.RsIdAndParentRsId;
import it.fub.jardin.client.widget.JardinGrid;
import it.fub.jardin.client.widget.JardinGridToolBar;
import it.fub.jardin.client.widget.JardinSelectColumnsForChartPopUp;
import it.fub.jardin.client.widget.Jungle;
import it.fub.jardin.client.widget.UploadDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.BarDataProvider;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.PieDataProvider;
import com.extjs.gxt.charts.client.model.charts.BarChart;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.charts.client.model.charts.BarChart.BarStyle;
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
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author acozzolino
 */
public class JardinController extends Controller {

  private static String webKitSuggest =
      "Jardin è ottimizzato per <i>WebKit</i>."
          + " Per ottenere prestazioni migliori si consiglia di usare"
          + " <a href=\"http://www.google.com/chrome/\">Chrome</a></li>"
          + " o Safari";

  private User user;
  private JardinView view;

  private enum ChartType {
    PIE, BAR;
  }

  private static final String[] chartColors =
      { "#204a87", "#4e9a06", "#cc0000", "#75507b", "#f57900", "#edd400" };

  /**
   * Creazione del controller generale per l'applicazione. Vengono registrati
   * gli eventi che saranno gestiti da questo controller.
   */
  public JardinController() {
    registerEventTypes(EventList.Login);
    registerEventTypes(EventList.Error);
    registerEventTypes(EventList.CheckUser);
    registerEventTypes(EventList.LoginError);
    registerEventTypes(EventList.Refresh);
    registerEventTypes(EventList.Init);
    //registerEventTypes(EventList.CreateUI);
    registerEventTypes(EventList.Search);
    registerEventTypes(EventList.CommitChanges);
    registerEventTypes(EventList.AddRow);
    registerEventTypes(EventList.RemoveRows);
    registerEventTypes(EventList.ExportAllStoreAllColumns);
    registerEventTypes(EventList.ExportAllStoreSomeColumns);
    registerEventTypes(EventList.ExportSomeStoreAllColumns);
    registerEventTypes(EventList.ExportSomeStoreSomeColumns);
    registerEventTypes(EventList.ExportSomeRowsAllColumns);
    registerEventTypes(EventList.ExportSomeRowsSomeColumns);
    registerEventTypes(EventList.ShowAllColumns);
    registerEventTypes(EventList.SaveGridView);
    registerEventTypes(EventList.GetGridViews);
    registerEventTypes(EventList.UploadTemplate);
    registerEventTypes(EventList.UploadImport);
    registerEventTypes(EventList.UploadInsert);
    registerEventTypes(EventList.UpdateColumnModel);
    registerEventTypes(EventList.Jungle);
    registerEventTypes(EventList.ShowPieChart);
    registerEventTypes(EventList.ShowBarChart);
    registerEventTypes(EventList.ShowChart);
    registerEventTypes(EventList.SendMessage);
    registerEventTypes(EventList.NewMessage);
    registerEventTypes(EventList.ViewLinkedTable);
    registerEventTypes(EventList.ViewPopUpDetail);
    registerEventTypes(EventList.GetPlugins);
    registerEventTypes(EventList.GotPlugins);
    registerEventTypes(EventList.ViewPlugin);
	registerEventTypes(EventList.UpdateCorrelatedResultset);
 }

  public void initialize() {
    view = new JardinView(this);
  }

  /**
   * Gestione degli eventi. (non-Javadoc)
   * 
   * @see com.extjs.gxt.ui.client.mvc.Controller#handleEvent(com.extjs.gxt.ui.client.mvc.AppEvent)
   */
  public void handleEvent(AppEvent event) {
    EventType t = event.getType();

    if (t == EventList.Login) {
      forwardToView(view, EventList.Login, loginMessage());
    } else if (t == EventList.CheckUser) {
      if (event.getData() instanceof Credentials) {
        Credentials credentials = (Credentials) event.getData();
        onCheckUser(credentials);
      } else {
        // TODO Gestire errore nei dati di EventList.CheckUser
      }
    } else if (t == EventList.Init) {
      if (event.getData() instanceof User) {
        User user = (User) event.getData();
        onInit(user);
      } else {
        // TODO Gestire errore nei dati di EventList.Init
      }
    } else if (t == EventList.Refresh) {
      onRefresh(event);
    } else if (t == EventList.Error) {
      if (event.getData() instanceof String) {
        onError((String) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.Error
      }
    } else if (t == EventList.LoginError) {
      onLoginError(event);
//    } else if (t == EventList.CreateUI) {
//      onCreateUI();
    } else if (t == EventList.Search) {
      if (event.getData() instanceof SearchParams) {
        SearchParams searchParams = (SearchParams) event.getData();
        onSearch(searchParams);
      } else {
        // TODO Gestire errore nei dati di EventList.Search
      }
    } else if (t == EventList.CommitChanges) {
      if (event.getData() instanceof JardinGrid) {
        JardinGrid grid = (JardinGrid) event.getData();
        onCommitChanges(grid);
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
        onAddRow((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.AddRow
        Log.error("Errore nei dati di EventList.AddRow");
      }
    } else if (t == EventList.ViewPopUpDetail) {
      if (event.getData() instanceof BaseModelData) {
        onViewPopUpDetail((BaseModelData) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.ViewPopUpDetail
        Log.error("Errore nei dati di EventList.ViewPopUpDetail");
      }
    } else if (t == EventList.RemoveRows) {
      if (event.getData() instanceof Integer) {
        onRemoveRows((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.RemoveRows
        Log.error("Errore nei dati di EventList.RemoveRows");
      }
    } else if (t == EventList.ExportAllStoreAllColumns) {
      if (event.getData() instanceof Integer) {
        onExport((Integer) event.getData(), false, true, true);
      } else {
        // TODO Gestire errore nei dati di
        // EventList.ExportAllStoreAllColumns
        Log.error("Errore nei dati di EventList.ExportAllStoreAllColumns");
      }
    } else if (t == EventList.ExportAllStoreSomeColumns) {
      if (event.getData() instanceof Integer) {
        onExport((Integer) event.getData(), false, true, false);
      } else {
        // TODO Gestire errore nei dati di
        // EventList.ExportAllStoreSomeColumns
        Log.error("Errore nei dati di EventList.ExportAllStoreSomeColumns");
      }
    } else if (t == EventList.ExportSomeStoreAllColumns) {
      if (event.getData() instanceof Integer) {
        onExport((Integer) event.getData(), false, false, true);
      } else {
        // TODO Gestire errore nei dati di
        // EventList.ExportAllStoreAllColumns
        Log.error("Errore nei dati di EventList.ExportAllStoreAllColumns");
      }
    } else if (t == EventList.ExportSomeStoreSomeColumns) {
      if (event.getData() instanceof Integer) {
        onExport((Integer) event.getData(), false, false, false);
      } else {
        // TODO Gestire errore nei dati di
        // EventList.ExportSomeStoreSomeColumns
        Log.error("Errore nei dati di EventList.ExportAllStoreSomeColumns");
      }
    } else if (t == EventList.ExportSomeRowsAllColumns) {
      if (event.getData() instanceof Integer) {
        onExport((Integer) event.getData(), true, true, true);
      } else {
        // TODO Gestire errore nei dati di EventList.ExportAllStoreAllColumns
        Log.error("Errore nei dati di EventList.ExportSomeRowAllColumns");
      }
    } else if (t == EventList.ExportSomeRowsSomeColumns) {
      if (event.getData() instanceof Integer) {
        onExport((Integer) event.getData(), true, true, false);
      } else {
        // TODO Gestire errore nei dati di EventList.ExportSomeStoreSomeColumns
        Log.error("Errore nei dati di EventList.ExportSomeRowSomeColumns");
      }
    } else if (t == EventList.ShowAllColumns) {
      if (event.getData() instanceof RsIdAndParentRsId) {
        onShowAllColumns((RsIdAndParentRsId) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.ShowAllColumns
        Log.error("Errore nei dati di EventList.ShowAllColumns");
      }
    } else if (t == EventList.SaveGridView) {
      if (event.getData() instanceof RsIdAndParentRsId) {
        onSaveGridView((RsIdAndParentRsId) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.SaveGridView
        Log.error("Errore nei dati di EventList.SaveGridView");
      }
    } else if (t == EventList.GetGridViews) {
      if (event.getData() instanceof RsIdAndParentRsId) {
        onGetGridViews((RsIdAndParentRsId) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.GetGridViews
        Log.error("Errore nei dati di EventList.GetGridViews");
      }
    } else if (t == EventList.UploadTemplate) {
      if (event.getData() instanceof Integer) {
        onUploadTemplate((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.UploadTemplate
        Log.error("Errore nei dati di EventList.UploadTemplate");
      }
    } else if (t == EventList.UploadImport) {
      if (event.getData() instanceof Integer) {
        onUploadImport((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.UploadImport
        Log.error("Errore nei dati di EventList.UploadImport");
      }
    } else if (t == EventList.UploadInsert) {
      if (event.getData() instanceof Integer) {
        onUploadInsert((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.UploadInsert
        Log.error("Errore nei dati di EventList.UploadInsert");
      }
    } else if (t == EventList.UpdateColumnModel) {
      // TODO CAMBIARE!!!
      if (event.getData() instanceof JardinGrid) {
        onUpdateColumnModel((JardinGrid) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.UpdateColumnModel
        Log.error("Errore nei dati di EventList.UpdateColumnModel");
      }
    } else if (t == EventList.Jungle) {
      if (event.getData() instanceof Integer) {
        onJungle((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.Jungle
        Log.error("Errore nei dati di EventList.Jungle");
      }
    } else if (t == EventList.ShowChart) {
      if (event.getData() instanceof ArrayList) {
        onShowChart((ArrayList<String>) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.SelectColumnsForChart
        Log.error("Errore nei dati di EventList.ShowChart");
      }
    } else if (t == EventList.ShowPieChart) {
      if (event.getData() instanceof Integer) {
        onSelectColumnsForChart(ChartType.PIE, (Integer) event.getData());
        // onShowChart(ChartType.PIE, (Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.ShowPieChart
        Log.error("Errore nei dati di EventList.ShowPieChart");
      }
    } else if (t == EventList.ShowBarChart) {
      if (event.getData() instanceof Integer) {
        onSelectColumnsForChart(ChartType.BAR, (Integer) event.getData());
        // onShowChart(ChartType.BAR, (Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.ShowBarChart
        Log.error("Errore nei dati di EventList.ShowBarChart");
      }
      /*
       * ------------------------------------------------------------------
       * ----- Altri eventi ------------------------------------------------
       * -----------------------
       */
    } else if (t == EventList.UpdateTemplates) {
      if (event.getData() instanceof Integer) {
        onUpdateTemplates((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.UpdateTemplates
        Log.error("Errore nei dati di EventList.UpdateTemplates");
      }
    } else if (t == EventList.SendMessage) {
      if (event.getData() instanceof Message) {
        onSendMessage((Message) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.SendMessage
        Log.error("Errore nei dati di EventList.SendMessage");
      }
    } else if (t.getEventCode() == EventList.NewMessage.getEventCode()) {
      onNewMessage();
    } else if (t == EventList.ViewLinkedTable) {
      if (event.getData() instanceof IncomingForeignKeyInformation) {
        onViewLinkedResultset((IncomingForeignKeyInformation) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.ViewLinkedTable
        Log.error("Errore nei dati di EventList.ViewLinkedTable");
      }
    } else if (t == EventList.GetPlugins) {
      if (event.getData() instanceof Integer) {
        onGetPlugins((Integer) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.GetPlugins
        Log.error("Errore nei dati di EventList.GetPlugins");
      }
    } else if (t == EventList.ViewPlugin) {
      if (event.getData() instanceof String) {
        forwardToView(view, EventList.ViewPlugin, (String) event.getData());
      } else {
        // TODO Gestire errore nei dati di EventList.ViewPlugin
        Log.error("Errore nei dati di EventList.ViewPlugin");
      } 
    } else if (t == EventList.UpdateCorrelatedResultset) {
		onViewLinkedResultsetForCorrelatedResultset((IncomingForeignKeyInformation) event
				.getData());
	}  
  }

  private void onNewMessage() {
    final ManagerServiceAsync service =
        (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    /* Set up the callback */
    AsyncCallback<List<Message>> callback = new AsyncCallback<List<Message>>() {

      public void onFailure(Throwable caught) {
        // TODO Auto-generated method stub

      }

      public void onSuccess(List<Message> messages) {
        user.setMessages(messages);
        Info.display("Informazione", "Hai ricevuto un nuovo messaggio");
        // forwardToView(view, EventList.NewMessage, null);
      }
    };

    /* Make the call */
    service.getUserMessages(user.getUid(), callback);
  }

  private void onSendMessage(Message message) {

    /* Fill message with sender id */
    if (message.getSender() < 0) {
      message.setSender(user.getUid());
    }

    final ManagerServiceAsync service =
        (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    /* Set up the callback */
    AsyncCallback callback = new AsyncCallback() {

      public void onFailure(Throwable caught) {
        // TODO Auto-generated method stub

      }

      public void onSuccess(Object result) {
        Info.display("Informazione", "Messaggio inviato e memorizzato.");
        // TODO Auto-generated method stub

      }
    };

    /* Make the call */
    service.sendMessage(message, callback);
  }

  public User getUser() {
    return user;
  }

  private String loginMessage() {
    if (GXT.isWebKit) {
      return null;
    } else {
      return webKitSuggest;
    }
  }

  private void onError(String error) {
    MessageBox.alert("Errore", error, null);
  }

  private void onCheckUser(Credentials credentials) {

    final ManagerServiceAsync service =
        (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    /* Set up the callback */
    AsyncCallback<User> callback = new AsyncCallback<User>() {
      public void onFailure(Throwable caught) {
        Dispatcher.forwardEvent(EventList.LoginError,
            caught.getLocalizedMessage());
      }

      public void onSuccess(User user) {
        Dispatcher.forwardEvent(EventList.Init, user);
      }
    };

    /* Make the call */
    service.getUser(credentials, callback);
  }

  private void onLoginError(AppEvent event) {
    forwardToView(view, event);
  }

  private void onRefresh(AppEvent event) {
    forwardToView(view, event);
  }

  private void onInit(User user) {
    this.user = user;
    user.addEvent(EventList.NewMessage);

    final ManagerServiceAsync service =
        (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    AsyncCallback<List<EventTypeSerializable>> callback =
        new AsyncCallback<List<EventTypeSerializable>>() {

          public void onSuccess(List<EventTypeSerializable> eventTypes) {

            for (EventTypeSerializable eventType : eventTypes) {
              handleEvent(new AppEvent(eventType));
            }
            service.getEvents(this);
          }

          public void onFailure(Throwable caught) {
            // TODO Auto-generated method stub

          }
        };

    service.getEvents(callback);

    forwardToView(view, EventList.Init, user);
  }

//  private void onCreateUI() {
//    /* Vedi sequence diagram init_sd.pic */
//
//    /* Per ogni resultset carica da service le sue proprietà */
//    for (ResultsetImproved resultset : this.user.getResultsets()) {
//      final Integer resultsetId = resultset.getId();
//      /* Avvisa la view che si sta creando un nuovo resultset */
//      forwardToView(view, EventList.NewResultset, resultsetId);
//      //forwardToView(view, EventList.GotValuesOfFields, resultsetId);
//      // forwardToView(view, EventList.gotValuesOfForeignKeys,
//      // resultsetId);
//
//      // final ManagerServiceAsync service =
//      // (ManagerServiceAsync) Registry.get(Jardin.SERVICE);
//      //
//      // /*
//      // * Carica i valori dei vincoli di integrità referenziale attualmente
//      // * presenti: sever per il binding dei campi del dettaglio e per il row
//      // * editor
//      // */
//      //
//      // AsyncCallback<FieldsMatrix> callbackValuesOfForeignKeys =
//      // new AsyncCallback<FieldsMatrix>() {
//      // public void onFailure(Throwable caught) {
//      // Dispatcher.forwardEvent(EventList.Error,
//      // caught.getLocalizedMessage());
//      // }
//      //
//      // public void onSuccess(FieldsMatrix fieldsMatrix) {
//      // ResultsetImproved rs = user.getResultsetFromId(resultsetId);
//      // rs.setForeignKeyList(fieldsMatrix);
//      // forwardToView(view, EventList.GotValuesOfForeignKeys, resultsetId);
//      // }
//      // };
//      //
//      // service.getValuesOfForeignKeys(resultsetId,
//      // callbackValuesOfForeignKeys);
//      forwardToView(view, EventList.GotValuesOfForeignKeys, resultsetId);
//    }
//  }

  private void onSearch(final SearchParams searchParams) {
    final MessageBox waitBox =
        new MessageBox().wait("Caricamento dati", "Attendere prego...",
            "Loading...");
    // TODO Modificare per gestire il solo resultsetId come parametro
    // TODO Modificare per gestire anche i diritti di accesso alpadre
    if (user.getResultsetFromId(searchParams.getResultSetId()).isRead()) {

      // ///////////////////////////////////////////
      // alla griglia servono i searchParams

      forwardToView(view, EventList.Search, searchParams);

      final boolean limit = searchParams.isLimit();
      final ManagerServiceAsync service =
          (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

      RpcProxy<PagingLoadResult<BaseModelData>> proxy =
          new RpcProxy<PagingLoadResult<BaseModelData>>() {
            @Override
            public void load(Object loadConfig,
                AsyncCallback<PagingLoadResult<BaseModelData>> callback) {
              PagingLoadConfig plc = (PagingLoadConfig) loadConfig;
              if (!limit) {
                plc.setLimit(-1);
              }
              service.getRecords((PagingLoadConfig) plc, searchParams, callback);
            }
          };

      PagingLoader<PagingLoadResult<BaseModelData>> loader =
          new BasePagingLoader<PagingLoadResult<BaseModelData>>(proxy);

      loader.setRemoteSort(true);
      ListStore<BaseModelData> store = new ListStore<BaseModelData>(loader);

      loader.addLoadListener(new LoadListener() {
        @Override
        public void loaderLoad(LoadEvent le) {
          waitBox.close();
        }

        @Override
        public void loaderLoadException(LoadEvent le) {
          waitBox.close();
          Dispatcher.forwardEvent(EventList.Error, le.exception);
        }
      });

      SearchResult searchResult = new SearchResult();
      searchResult.setResultSetId(searchParams.getResultSetId());
      searchResult.setParentResultSetId(searchParams.getParentResultSetId());
      searchResult.setStore(store);
      forwardToView(view, EventList.Searched, searchResult);
      // ///////////////////////////////////////////

      // forwardToView(view, EventList.Search, searchParams);
    } else {
      Dispatcher.forwardEvent(EventList.Error,
          "L'utente non dispone dei permessi di lettura");
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
        final ManagerServiceAsync service =
            (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

        /* Set up the callback */
        AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
          public void onFailure(Throwable caught) {
            waitbox.close();
            grid.getStore().rejectChanges();
            Dispatcher.forwardEvent(EventList.Error,
                caught.getLocalizedMessage());
          }

          public void onSuccess(Integer retCode) {
            waitbox.close();
            grid.getStore().commitChanges();
            Info.display("Informazione", "Modifiche salvate sul Database");
          }
        };

        /* Make the call */
//        service.setObjects(resultset.getId(), newItemList, callback);
        service.updateObjects(resultset.getId(), newItemList, new String("$-notspec-$"), callback);
      } else {
        Info.display("Informazione", "Nessuna modifica da salvare", "");
      }
    } else {
      Dispatcher.forwardEvent(EventList.Error,
          "L'utente non dispone dei permessi di modifica");
    }
  }

  private void onAddRow(int resultset) {
    if (user.getResultsetFromId(resultset).isInsert()) {
      forwardToView(view, EventList.AddRow, resultset);
    } else {
      Dispatcher.forwardEvent(EventList.Error,
          "L'utente non dispone dei permessi di inserimento");
    }
  }

  private void onViewPopUpDetail(final BaseModelData data) {
    if (((ResultsetImproved) data.get("RSLINKED")).isRead()) {
      // restituisce il BaseModelData con la riga interessata

      final ManagerServiceAsync service =
          (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

      AsyncCallback<ArrayList<BaseModelData>> callbackPopUpDetailEntry =
          new AsyncCallback<ArrayList<BaseModelData>>() {
            public void onFailure(Throwable caught) {
              Dispatcher.forwardEvent(EventList.Error,
                  caught.getLocalizedMessage());
            }

            public void onSuccess(ArrayList<BaseModelData> infoToView) {
              forwardToView(view, EventList.ViewPopUpDetail, infoToView);
            }
          };

      service.getPopUpDetailEntry(data, callbackPopUpDetailEntry);

    } else {
      Dispatcher.forwardEvent(EventList.Error,
          "L'utente non dispone dei permessi di visualizzazione");
    }
  }

  private void onRemoveRows(int resultSetId) {
    if (user.getResultsetFromId(resultSetId).isDelete()) {
      final JardinGrid grid = view.getResultSetGui(resultSetId).getGrid();

      final List<BaseModelData> selectedRows =
          grid.getSelectionModel().getSelection();

      if (selectedRows.size() > 0) {
        final MessageBox waitbox =
            MessageBox.wait("Attendere", "Eliminazione righe in corso...", "");

        /* Create the service proxy class */
        final ManagerServiceAsync service =
            (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

        /* Set up the callback */
        AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
          public void onFailure(Throwable caught) {
            waitbox.close();
            Dispatcher.forwardEvent(EventList.Error,
                caught.getLocalizedMessage());
          }

          public void onSuccess(Integer result) {
            waitbox.close();
            if (result.intValue() <= 0) {
              Dispatcher.forwardEvent(EventList.Error, "Nessuna riga eliminata");
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
        service.removeObjects(resultSetId, selectedRows, callback);
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
   * @param gridHTML
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
  private void onExport(int resultSetId,  boolean allRows, boolean allStore,
      boolean allColumns) {

    final MessageBox waitBox =
        new MessageBox().wait("Caricamento dati", "Attendere prego...",
            "Loading...");

    /* Nome del file da creare */
    String filename =
        user.getResultsetFromId(resultSetId).getAlias().replace(" ", "_");

    /*
     * Prendi il tabItem per recuperare la toolbar (formato d'esportazione) e la
     * grid (config dei record da esportare, colonne visibili e criteri di
     * ricerca)
     */
    ResultSetGui item = view.getResultSetGui(resultSetId);

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
    final ManagerServiceAsync service =
        (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    AsyncCallback<String> callback = new AsyncCallback<String>() {
      public void onFailure(Throwable caught) {
        waitBox.close();
        Dispatcher.forwardEvent(EventList.Error, caught.getLocalizedMessage());
      }

      public void onSuccess(String result) {
        waitBox.close();
        if (result != null && result.length() > 0) {
          Log.debug("Export file: " + result);
          String url = GWT.getModuleBaseURL() + "download?file=" + result;
          Window.open(url, "Download", null);
        } else {
          Log.warn("File d'esportazione vuoto");
        }
      }
    };

    service.createReport("/tmp/" + filename, template, config, selectedRows,
        columns, searchParams, fs, ts, callback);
  }

  private void onShowAllColumns( RsIdAndParentRsId rsIds ) {
    forwardToView(view, EventList.ShowAllColumns, rsIds );
  }

  private void onSaveGridView(RsIdAndParentRsId rsIds ) {
    forwardToView(view, EventList.SaveGridView, rsIds);
  }

  private void onGetGridViews(RsIdAndParentRsId rsIds) {
    final ManagerServiceAsync service =
        (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    AsyncCallback<HeaderPreferenceList> callback =
        new AsyncCallback<HeaderPreferenceList>() {
          public void onFailure(Throwable caught) {
            Dispatcher.forwardEvent(EventList.Error,
                caught.getLocalizedMessage());
          }

          public void onSuccess(HeaderPreferenceList result) {
            Info.display("Informazione", "Lista delle preferenze caricata");
            forwardToView(view, EventList.GotHeaderPreference, result);
          }
        };

    service.getGridViews(user.getUid(), rsIds, callback);
  }

  private void onUploadTemplate(int resultset) {
    UploadDialog d =
        new UploadDialog(user, UploadDialog.TYPE_TEMPLATE, resultset);
    d.show();
  }

  private void onUploadImport(int resultset) {
    UploadDialog d =
        new UploadDialog(user, UploadDialog.TYPE_IMPORT, resultset);
    d.show();
  }

  private void onUploadInsert(int resultset) {
    UploadDialog d =
        new UploadDialog(user, UploadDialog.TYPE_INSERT, resultset);
    d.show();
  }

  private void onUpdateColumnModel(final JardinGrid grid) {
    final ManagerServiceAsync service =
        (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    AsyncCallback<List<Integer>> callback = new AsyncCallback<List<Integer>>() {
      public void onFailure(Throwable caught) {
        Dispatcher.forwardEvent(EventList.Error, caught.getLocalizedMessage());
      }

      public void onSuccess(List<Integer> result) {
        grid.updateGridHeader(result);
      }
    };

    service.getHeaderUserPreference(user.getUid(),
        grid.getUserPreferenceHeaderId(), callback);
  }

  private void onJungle(final int resultSetId) {
    /* Nome del file da creare */
    String filename = user.getResultsetFromId(resultSetId).getAlias();

    /*
     * Prendi il tabItem per recuperare la toolbar (formato d'esportazione) e la
     * grid (config dei record da esportare, colonne visibili e criteri di
     * ricerca)
     */
    ResultSetGui item = view.getResultSetGui(resultSetId);

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
    final ManagerServiceAsync service =
        (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    AsyncCallback<String> callback = new AsyncCallback<String>() {
      public void onFailure(Throwable caught) {
        Dispatcher.forwardEvent(EventList.Error, caught.getLocalizedMessage());
      }

      public void onSuccess(String result) {
        if (result != null && result.length() > 0) {
          Jungle j =
              new Jungle(user.getResultsetFromId(resultSetId), columns, result);
          j.show();
        } else {
          Log.warn("File d'esportazione vuoto");
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
  private void onSelectColumnsForChart(ChartType ct, Integer resultSetId) {
	 ResultSetGui item = view.getResultSetGui(resultSetId);
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
  private void onShowChart(ArrayList<String> dataToChart) {

    /*
     * Prendi il tabItem per recuperare la toolbar (formato d'esportazione) e la
     * grid (config dei record da esportare, colonne visibili e criteri di
     * ricerca)
     */
    Integer resultSetId = Integer.valueOf(dataToChart.get(1));
    String title = dataToChart.get(2);
    String value = dataToChart.get(3);
    ChartType type = ChartType.valueOf(dataToChart.get(0));

    //ResultSetGui item = view.getMainResultSetGuiByResultsetId(resultset);
    ResultSetGui item = view.getResultSetGui(resultSetId);

    /* Prendi la griglia */
    JardinGrid grid = item.getGrid();

    /* Prendi gli ID delle prime due colonne visibili */
    // ColumnModel columnModel = grid.getColumnModel();
    String cx = title;
    String cy = value;

    String url = "resources/chart/open-flash-chart.swf";

    Chart chart = new Chart(url);
    chart.setBorders(false);

    String resultsetAlias = user.getResultsetFromId(resultSetId).getAlias();
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

  private void onUpdateTemplates(int resultset) {
    // TODO Auto-generated method stub
  }


  private void onGetPlugins(Integer data) {
    ResultsetImproved rs = user.getResultsetFromId(data);
    // final JardinGrid grid = view.getItemByResultsetId(data).getGrid();

    final ManagerServiceAsync service =
        (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    AsyncCallback<ArrayList<Plugin>> callback =
        new AsyncCallback<ArrayList<Plugin>>() {
          public void onFailure(Throwable caught) {
            Dispatcher.forwardEvent(EventList.Error,
                caught.getLocalizedMessage());
          }

          public void onSuccess(ArrayList<Plugin> result) {
            forwardToView(view, EventList.GotPlugins, result);
          }
        };

    service.getPlugins(user.getGid(), rs.getId(), callback);

  }
	private SearchParams onViewLinkedResultset(IncomingForeignKeyInformation ifki) {
		//String linkedTable = ifki.getLinkingTable();
		//ResultsetImproved rs = ifki.getInterestedResultset();
//		ResultsetImproved parentRs = ifki.getInterestedParentResultset();

		SearchParams searchParams = new SearchParams( ifki.getInterestedResultset().getId(), ifki.getResultsetId()  );
		searchParams.setAccurate(true);
		List<BaseModelData> queryFieldList = new ArrayList<BaseModelData>();

		SearchStringParser parser = new SearchStringParser(ifki
				.getLinkingField()
				+ ":" + ifki.getFieldValue());

		Map<String, String> searchMap = parser.getSearchMap();
		for (String key : parser.getSearchMap().keySet()) {
			// TODO migliorare la gestione per il case insensitive
			key = key.toLowerCase();
			BaseModelData m = new BaseModelData();
			m.set(key, searchMap.get(key));
			queryFieldList.add(m);
		}
		searchParams.setFieldsValuesList(queryFieldList);
		return searchParams;
		///Dispatcher.forwardEvent(EventList.Search, searchParams);
	}
	
//	private void onViewLinkedResultsetForNewTab(IncomingForeignKeyInformation ifki) {
//		SearchParams searchParams = onViewLinkedResultset( ifki);
//		Dispatcher.forwardEvent(EventList.Search, searchParams);
//	}

	
	private void onViewLinkedResultsetForCorrelatedResultset(IncomingForeignKeyInformation ifki) {
			SearchParams searchParams = onViewLinkedResultset( ifki);
			Dispatcher.forwardEvent(EventList.Search, searchParams);			
	}

//  private void onViewLinkedResultsetForCorrelatedResultset(IncomingForeignKeyInformation ifki) {
//		SearchParams searchParams = onViewLinkedResultset( ifki);
//		Dispatcher.forwardEvent(EventList.Search, searchParams);			
//  }
//
//  private void onViewLinkedResultset(IncomingForeignKeyInformation ifki) {
//	    ResultsetImproved rs = ifki.getInterestedResultset();
//	    SearchParams searchParams = new SearchParams(rs.getId(), 0);
//	    searchParams.setAccurate(true);
//	    List<BaseModelData> queryFieldList = new ArrayList<BaseModelData>();
//	    SearchStringParser parser =
//	        new SearchStringParser(ifki.getLinkingField() + ":\""
//	            + ifki.getFieldValue() + "\"");
//
//	    Map<String, String> searchMap = parser.getSearchMap();
//	    for (String key : parser.getSearchMap().keySet()) {
//	      BaseModelData m = new BaseModelData();
//	      m.set(key, searchMap.get(key));
//	      queryFieldList.add(m);
//	    }
//	    searchParams.setFieldsValuesList(queryFieldList);
//	    Dispatcher.forwardEvent(EventList.Search, searchParams);
//	  }
  
}
