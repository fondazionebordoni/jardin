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
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.Plugin;
import it.fub.jardin.client.model.Resultset;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.SearchResult;
import it.fub.jardin.client.widget.HeaderArea;
import it.fub.jardin.client.widget.JardinColumnModel;
import it.fub.jardin.client.widget.JardinDetail;
import it.fub.jardin.client.widget.JardinGrid;
import it.fub.jardin.client.widget.JardinGridToolBar;
import it.fub.jardin.client.widget.JardinTabItem;
import it.fub.jardin.client.widget.LoginDialog;
import it.fub.jardin.client.widget.SearchAreaAdvanced;
import it.fub.jardin.client.widget.SearchAreaBase;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.fx.Draggable;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

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
  private List<Resultset> resultSetList;

  public JardinView(final Controller controller) {
    super(controller);
    if (controller instanceof JardinController) {
      this.controller = (JardinController) controller;
    }
  }

  @Override
  protected void handleEvent(final AppEvent event) {
    EventType t = event.getType();
    if (t == EventList.Login) {
      if (event.getData() instanceof String) {
        this.login((String) event.getData());
      } else {
        this.login(null);
      }
    } else if (t == EventList.Init) {
      this.dialog.hide();
      this.initUI();
    } else if (t == EventList.Refresh) {
      this.viewport.removeAll();
      Dispatcher.forwardEvent(new AppEvent(EventList.Login));
    } else if (t == EventList.LoginError) {
      this.dialog.hide();
      String message;
      if (event.getData() instanceof String) {
        message = (String) event.getData();
      } else {
        message = "Errore durante l'accesso";
      }
      this.loginError(message);
    } else if (t == EventList.GotResultsetList) {
      resultSetList = event.getData();
      createFirstTab(resultSetList);
    } else if (t == EventList.NewResultset) {
      if (event.getData() instanceof Integer) {
        this.newResultset((Integer) event.getData());
      }
    } else if (t == EventList.GotValuesOfFields) {
      if (event.getData() instanceof Integer) {
        this.gotValuesOfFields((Integer) event.getData());
      }
    } else if (t == EventList.GotValuesOfForeignKeys) {
      if (event.getData() instanceof Integer) {
        this.gotValuesOfForeignKeys((Integer) event.getData());
      }
    } else if (t == EventList.Searched) {
      if (event.getData() instanceof SearchResult) {
        this.onSearched((SearchResult) event.getData());
      }
      /*
       * Gestione eventi della toolbar
       */
    } else if (t == EventList.Search) {
      if (event.getData() instanceof SearchParams) {
        this.onSearch((SearchParams) event.getData());
      }
      /*
       * Gestione eventi della toolbar
       */
    } else if (t == EventList.AddRow) {
      if (event.getData() instanceof Integer) {
        this.onAddRow((Integer) event.getData());
      }
    } else if (t == EventList.ViewPopUpDetail) {
      if (event.getData() instanceof ArrayList<?>) {
        this.onViewPopUpDetail((ArrayList<BaseModelData>) event.getData());
      }
    } else if (t == EventList.ShowAllColumns) {
      if (event.getData() instanceof Integer) {
        this.onShowAllColumns((Integer) event.getData());
      }
    } else if (t == EventList.SaveGridView) {
      if (event.getData() instanceof Integer) {
        this.onSaveGridView((Integer) event.getData());
      }
    } else if (t == EventList.GotHeaderPreference) {
      this.updatePreferenceListMenu((HeaderPreferenceList) event.getData());
    } else if (t == EventList.GotPlugins) {
      this.updatePluginsMenu((ArrayList<Plugin>) event.getData());
    } else if (t == EventList.ViewPlugin) {
      this.viewPlugin((String) event.getData());
      /*
       * Altri eventi
       */
    }
  }

  private void createFirstTab(List<Resultset> resultSetList) {
    // TODO Auto-generated method stub
    TabItem firstTab = new TabItem("Menù Principale");
    AbsolutePanel con = new AbsolutePanel();
    
    int i = 0;
    for (Resultset resultset : resultSetList) {
      ContentPanel cp = new ContentPanel();
      cp.setCollapsible(true);      
      cp.setWidth(200);
      cp.setHeading(resultset.getAlias());
      Label body = new Label(resultset.getNote());
      cp.add(body);
      
      Draggable d = new Draggable(cp);     
      
      con.add(cp,10 + (i*200),10);
      i++;
    }
    
    firstTab.add(con);
    this.main.add(firstTab);
  }

  private void viewPlugin(final String data) {
    Window w = new Window();
    w.setHeading("Jardin Plugin");
    w.setModal(false);
    w.setResizable(true);
    w.setSize(800, 600);
    w.setMaximizable(true);
    // w.setToolTip("The Rnf Manager");
    w.setUrl(data);
    w.show();

  }

  private void updatePluginsMenu(final ArrayList<Plugin> plugins) {
    if ((plugins != null) && (plugins.size() > 0)) {
      JardinTabItem item = this.getItemByResultsetId(plugins.get(0).getRsId());
      JardinGridToolBar jgtb = item.getToolbar();
      Menu menuPlugins = new Menu();

      for (final Plugin plugin : plugins) {
        MenuItem mi = new MenuItem(plugin.getPluginName());

        mi.addSelectionListener(new SelectionListener() {
          @Override
          public void componentSelected(final ComponentEvent ce) {
            Dispatcher.forwardEvent(EventList.ViewPlugin,
                plugin.getConfigFile());
          }
        });
        menuPlugins.add(mi);
      }
      jgtb.getButtonMenuPlugins().setMenu(menuPlugins);
      jgtb.getButtonMenuPlugins().showMenu();
    }
  }

  private void onShowAllColumns(final int resultset) {
    JardinTabItem item = this.getItemByResultsetId(resultset);
    item.getGrid().showAllColumns();
  }

  private void onAddRow(final int resultset) {
    JardinTabItem item = this.getItemByResultsetId(resultset);
    item.getGrid().addRow();
  }

  private void onViewPopUpDetail(final ArrayList<BaseModelData> infoToView) {
    BaseModelData data = infoToView.get(0);
    Integer rsId = data.get("RSID");
    JardinTabItem item = this.getItemByResultsetId(rsId);
    item.getGrid().viewDetailPopUp(infoToView);
  }

  private void initUI() {
    this.viewport = new Viewport();
    this.viewport.setLayout(new RowLayout(Orientation.VERTICAL));

    this.createHeader();
    this.createMain();

    RootPanel.get().add(this.viewport);
//    Dispatcher.forwardEvent(EventList.CreateUI);
    Dispatcher.forwardEvent(EventList.CreateFirstTab);
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
  public synchronized JardinTabItem getItemByResultsetId(
      final Integer resultsetId) {
    if (this.main.getItemByItemId(ITEM_PREFIX + resultsetId) instanceof JardinTabItem) {
      JardinTabItem item =
          (JardinTabItem) this.main.getItemByItemId(ITEM_PREFIX + resultsetId);
      // TODO Verificare se è possibile togliere questa chiamata
      this.main.setSelection(item);
      return item;
    } else {
      return null;
    }
  }

  private void login(final String loginMessage) {

    this.dialog = new LoginDialog();
    this.dialog.setClosable(false);
    this.dialog.show();

    if (loginMessage != null) {
      MessageBox.alert("Attenzione", loginMessage, null);
    }
  }

  private void loginError(final String message) {
    final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
      public void handleEvent(final MessageBoxEvent we) {
        Dispatcher.forwardEvent(new AppEvent(EventList.Login));
      }
    };
    MessageBox.alert("Errore", message, l);
  }

  private void newResultset(final Integer resultsetId) {
    /* Prendi le proprietà del resultset in base all'id dall'utente */
    ResultsetImproved resultset =
        this.controller.getUser().getResultsetFromId(resultsetId);
    JardinTabItem item = new JardinTabItem(resultset);
    item.setId(ITEM_PREFIX + resultsetId);
    this.main.add(item);
  }

  private synchronized void gotValuesOfFields(final Integer resultsetId) {
    ResultsetImproved resultset =
        this.controller.getUser().getResultsetFromId(resultsetId);

    /* Creazione dell'area di ricerca avanzata */
    SearchAreaAdvanced searchAreaAdvanced = new SearchAreaAdvanced(resultset);

    JardinTabItem item = this.getItemByResultsetId(resultsetId);
    if (item != null) {
      /* Aggiungere la ricerca avanzata al tabitem */
      item.addSearchAreaAdvanced(searchAreaAdvanced);
    }
  }

  private synchronized void gotValuesOfForeignKeys(final Integer resultsetId) {
    ResultsetImproved resultset =
        this.controller.getUser().getResultsetFromId(resultsetId);

    /* Creazione della griglia */
    ListStore<BaseModelData> store = new ListStore<BaseModelData>();
    JardinColumnModel cm = new JardinColumnModel(resultset);
    JardinGrid grid = new JardinGrid(store, cm, resultset);

    new SearchAreaBase(resultset);

    /* Creazione del dettaglio */
    JardinDetail detail = new JardinDetail(resultset);

    JardinTabItem item = this.getItemByResultsetId(resultsetId);
    if (item != null) {
      /* Aggiungere la griglia al tabItem */
      item.setGrid(grid);

      /* Aggiungere il dettaglio al tabitem */
      item.addDetail(detail);

      /*
       * Eseguo una ricerca per riempire il resultset
       * 
       * SearchParams searchParams = new SearchParams(resultsetId);
       * List<BaseModelData> queryFieldList = new ArrayList<BaseModelData>();
       * searchParams.setFieldsValuesList(queryFieldList);
       * Dispatcher.forwardEvent(EventList.Search, searchParams);
       */
    }
  }

  private void onSearch(final SearchParams searchParams) {
    // TODO Auto-generated method stub
    JardinTabItem item =
        this.getItemByResultsetId(searchParams.getResultsetId());
    if (item != null) {
      if (item.getGrid() != null) {
        item.getGrid().setSearchparams(searchParams);
      }
    }
  }

  private void onSearched(final SearchResult result) {
    JardinTabItem item = this.getItemByResultsetId(result.getResultsetId());

    if (item != null) {
      if (item.getGrid() != null) {
        /* Aggiornamento dello store della griglia del tabItem */
        // item.updateStore(this.getStore(searchParams, true));
        item.updateStore(result.getStore());
        // item.getGrid().setSearchparams(searchParams);
      }
    }
  }

  private void onSaveGridView(final int resultset) {
    final JardinGrid grid = this.getItemByResultsetId(resultset).getGrid();
    final MessageBox box = MessageBox.prompt("Nome", "Salva visualizzazione");
    box.addCallback(new Listener<MessageBoxEvent>() {
      public void handleEvent(final MessageBoxEvent be) {
        grid.saveGridView(JardinView.this.controller.getUser(), be.getValue());
      }
    });
  }

  private void updatePreferenceListMenu(final HeaderPreferenceList data) {
    JardinTabItem item = this.getItemByResultsetId(data.getResultsetId());
    if (item != null) {
      /* Aggiungere la ricerca avanzata al tabitem */
      item.updatePreference(data);
    }
  }
}
