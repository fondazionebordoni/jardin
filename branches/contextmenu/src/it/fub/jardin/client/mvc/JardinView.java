/**
 * 
 */
package it.fub.jardin.client.mvc;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.Jardin;
import it.fub.jardin.client.ManagerServiceAsync;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.IncomingForeignKeyInformation;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.widget.HeaderArea;
import it.fub.jardin.client.widget.JardinColumnModel;
import it.fub.jardin.client.widget.JardinDetail;
import it.fub.jardin.client.widget.JardinGrid;
import it.fub.jardin.client.widget.JardinTabItem;
import it.fub.jardin.client.widget.LoginDialog;
import it.fub.jardin.client.widget.SearchAreaAdvanced;
import it.fub.jardin.client.widget.SearchAreaBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author gpantanetti
 */
public class JardinView extends View {

  public static final String HEADER_AREA = "header-area";
  public static final String MAIN_AREA = "main-area";
  public static final String DETAIL_AREA = "detail-area";
  public static final String SEARCH_AREA = "search-area";
  public static final String SEARCH_AREA_ADVANCED = "search-area-advanced";
  private static final String ITEM_PREFIX = "item-id-";

  private JardinController controller;
  private Viewport viewport;
  // private ContentPanel search;
  private TabPanel main;
  private HeaderArea header;
  private LoginDialog dialog;

  public JardinView(Controller controller) {
    super(controller);
    if (controller instanceof JardinController) {
      this.controller = (JardinController) controller;
    }
  }

  @Override
  protected void handleEvent(AppEvent event) {
    EventType t = event.getType();
    if (t == EventList.Login) {
      if (event.getData() instanceof String) {
        login((String) event.getData());
      } else {
        login(null);
      }
    } else if (t == EventList.Init) {
      dialog.hide();
      initUI();
    } else if (t == EventList.Refresh) {
      this.viewport.removeAll();
      Dispatcher.forwardEvent(new AppEvent(EventList.Login));
    } else if (t == EventList.LoginError) {
      dialog.hide();
      String message;
      if (event.getData() instanceof String) {
        message = (String) event.getData();
      } else {
        message = "Errore durante l'accesso";
      }
      loginError(message);
    } else if (t == EventList.NewResultset) {
      if (event.getData() instanceof Integer) {
        newResultset((Integer) event.getData());
      }
    } else if (t == EventList.GotValuesOfFields) {
      if (event.getData() instanceof Integer) {
        gotValuesOfFields((Integer) event.getData());
      }
    } else if (t == EventList.GotValuesOfForeignKeys) {
      if (event.getData() instanceof Integer) {
        gotValuesOfForeignKeys((Integer) event.getData());
      }
    } else if (t == EventList.GotValuesOfForeignKeysIn) {
      if (event.getData() instanceof Integer) {
        gotForeignKeyInForATable((Integer) event.getData());
      }
    } else if (t == EventList.Search) {
      if (event.getData() instanceof SearchParams) {
        onSearch((SearchParams) event.getData());
      }
      /*
       * -----------------------------------------------------------------------
       * Gestione eventi della toolbar
       * -----------------------------------------------------------------------
       */
    } else if (t == EventList.AddRow) {
      if (event.getData() instanceof Integer) {
        onAddRow((Integer) event.getData());
      }
    } else if (t == EventList.ShowAllColumns) {
      if (event.getData() instanceof Integer) {
        onShowAllColumns((Integer) event.getData());
      }
    } else if (t == EventList.SaveGridView) {
      if (event.getData() instanceof Integer) {
        onSaveGridView((Integer) event.getData());
      }
    } else if (t == EventList.GotHeaderPreference) {
      updatePreferenceListMenu((HeaderPreferenceList) event.getData());
      /*
       * -----------------------------------------------------------------------
       * Altri eventi
       * -----------------------------------------------------------------------
       */
    }
  }

  private void onShowAllColumns(int resultset) {
    JardinTabItem item = getItemByResultsetId(resultset);
    item.getGrid().showAllColumns();
  }

  private void onAddRow(int resultset) {
    JardinTabItem item = getItemByResultsetId(resultset);
    item.getGrid().addRow();
  }

  private void initUI() {
    viewport = new Viewport();
    viewport.setLayout(new RowLayout(Orientation.VERTICAL));

    createHeader();
    createMain();

    RootPanel.get().add(viewport);
    Dispatcher.forwardEvent(EventList.CreateUI);
  }

  private void createHeader() {
    this.header = new HeaderArea(this.controller.getUser());
    this.header.setId(JardinView.HEADER_AREA);

    RowData rd = new RowData(1, 32);
    rd.setMargins(new Margins(0));
    this.viewport.add(this.header, rd);
  }

  private void createMain() {
    this.main = new TabPanel();
    this.main.setId(JardinView.MAIN_AREA);
    this.main.setAnimScroll(true);
    this.main.setTabScroll(true);

    RowData rd = new RowData(1, 1);
    this.viewport.add(this.main, rd);
  }

  /**
   * Restituisce il tabItem che rappresenta il resultset in base all'id o null
   * se non esiste
   * 
   * @param resultsetId
   *          l'id del resultset
   * @return un TabItem o null se non esiste
   */
  public synchronized JardinTabItem getItemByResultsetId(Integer resultsetId) {
    if (this.main.getItemByItemId(ITEM_PREFIX + resultsetId) instanceof JardinTabItem) {
      JardinTabItem item =
          (JardinTabItem) this.main.getItemByItemId(ITEM_PREFIX + resultsetId);
      // setSelection per agganciare correttamente gli oggetti al tabItem
      // TODO Verificare se è possibile togliere questa chiamata
      this.main.setSelection(item);
      return item;
    } else {
      return null;
    }
  }

  private void login(String loginMessage) {

    dialog = new LoginDialog();
    dialog.setClosable(false);
    dialog.show();

    if (loginMessage != null) {
      MessageBox.alert("Attenzione", loginMessage, null);
    }
  }

  private void loginError(String message) {
    final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
      public void handleEvent(MessageBoxEvent we) {
        Dispatcher.forwardEvent(new AppEvent(EventList.Login));
      }
    };
    MessageBox.alert("Errore", message, l);
  }

  private void newResultset(Integer resultsetId) {
    /* Prendi le proprietà del resultset in base all'id dall'utente */
    ResultsetImproved resultset =
        controller.getUser().getResultsetFromId(resultsetId);
    JardinTabItem item = new JardinTabItem(resultset);
    item.setId(ITEM_PREFIX + resultsetId);

    this.main.add(item);
  }

  private synchronized void gotValuesOfFields(Integer resultsetId) {
    ResultsetImproved resultset =
        controller.getUser().getResultsetFromId(resultsetId);

    /* Creazione dell'area di ricerca avanzata */
    SearchAreaAdvanced searchAreaAdvanced = new SearchAreaAdvanced(resultset);

    JardinTabItem item = getItemByResultsetId(resultsetId);
    if (item != null) {
      /* Aggiungere la ricerca avanzata al tabitem */
      item.addSearchAreaAdvanced(searchAreaAdvanced);
    }
  }

  private synchronized void gotValuesOfForeignKeys(Integer resultsetId) {
    ResultsetImproved resultset =
        controller.getUser().getResultsetFromId(resultsetId);

    /* Creazione della griglia */
    ListStore<BaseModelData> store = new ListStore<BaseModelData>();
    JardinColumnModel cm = new JardinColumnModel(resultset);
    JardinGrid grid = new JardinGrid(store, cm, resultset);

    /* Creazione dell'area di ricerca semplice */
    SearchAreaBase searchAreaBase = new SearchAreaBase(resultset);

    /* Creazione del dettaglio */
    JardinDetail detail = new JardinDetail(resultset);

    JardinTabItem item = getItemByResultsetId(resultsetId);
    if (item != null) {
      /* Aggiungere la griglia al tabItem */
      item.setGrid(grid);

      /* Aggiungere la ricerca semplice al tabItem */
      item.addSearchAreaBase(searchAreaBase);

      /* Aggiungere il dettaglio al tabitem */
      item.addDetail(detail);
      
      /* Eseguo una ricerca per riempire la il resultset
      SearchParams searchParams = new SearchParams(resultsetId);  
      List<BaseModelData> queryFieldList = new ArrayList<BaseModelData>();
      searchParams.setFieldsValuesList(queryFieldList);
      Dispatcher.forwardEvent(EventList.Search, searchParams);*/ 
    }
  }
  
  private synchronized void gotForeignKeyInForATable(Integer resultsetId) {
 // recuperiamo le fk entranti del resultset attuale
    ResultsetImproved resultset =
      controller.getUser().getResultsetFromId(resultsetId);
    
    ArrayList<IncomingForeignKeyInformation> foreignKeyIn = new ArrayList<IncomingForeignKeyInformation>();
    foreignKeyIn = resultset.getForeignKeyIn();
    System.out.println("Foreign Key Entranti");
    for (IncomingForeignKeyInformation fk : foreignKeyIn) {
      System.out.println(fk.getField()+"<-"+fk.getLinkedTable()+"."+fk.getLinkedField());
    }
  }

  private void onGotExport(String url) {
    Window.open(url, "Download", null);
  }

  private void onSearch(SearchParams searchParams) {
    JardinTabItem item = getItemByResultsetId(searchParams.getResultsetId());

    if (item != null) {
      if (item.getGrid() != null) {
        /* Aggiornamento dello store della griglia del tabItem */
        item.updateStore(this.getStore(searchParams));
        item.getGrid().setSearchparams(searchParams);
      }
    }
  }

  private ListStore<BaseModelData> getStore(final SearchParams searchParams) {

    final ManagerServiceAsync service =
        (ManagerServiceAsync) Registry.get(Jardin.SERVICE);

    RpcProxy<PagingLoadResult<BaseModelData>> proxy =
        new RpcProxy<PagingLoadResult<BaseModelData>>() {
          @Override
          public void load(Object loadConfig,
              AsyncCallback<PagingLoadResult<BaseModelData>> callback) {
            service.getRecords((PagingLoadConfig) loadConfig, searchParams,
                callback);
          }
        };

    PagingLoader<PagingLoadResult<BaseModelData>> loader =
        new BasePagingLoader<PagingLoadResult<BaseModelData>>(proxy);
    loader.setRemoteSort(true);
    ListStore<BaseModelData> store = new ListStore<BaseModelData>(loader);
    return store;
  }

  private void onSaveGridView(int resultset) {
    final JardinGrid grid = getItemByResultsetId(resultset).getGrid();
    final MessageBox box = MessageBox.prompt("Nome", "Salva visualizzazione");
    box.addCallback(new Listener<MessageBoxEvent>() {
      public void handleEvent(MessageBoxEvent be) {
        grid.saveGridView(controller.getUser(), be.getValue());
      }
    });
  }

  private void updatePreferenceListMenu(HeaderPreferenceList data) {
    JardinTabItem item = getItemByResultsetId(data.getResultsetId());
    if (item != null) {
      /* Aggiungere la ricerca avanzata al tabitem */
      item.updatePreference(data);
    }
  }
}
