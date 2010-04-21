/**
 * 
 */
package it.fub.jardin.client.mvc;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.Plugin;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.SearchResult;
import it.fub.jardin.client.testLayoutGWTPKG.MainResultSetsArea;
import it.fub.jardin.client.testLayoutGWTPKG.MultiResGui;
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

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

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
	DockLayoutPanel mainDlp;
	DockLayoutPanel allTablesDlp;
	FlowPanel mainTablesButtonBar;	
	MainResultSetsArea vertMainResulSetsArea;

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
    } else if (t == EventList.Searched) {
      if (event.getData() instanceof SearchResult) {
        onSearched((SearchResult) event.getData());
      }
      /*
       * Gestione eventi della toolbar
       */
    } else if (t == EventList.Search) {
      if (event.getData() instanceof SearchParams) {
        onSearch((SearchParams) event.getData());
      }
      /*
       * Gestione eventi della toolbar
       */
    } else if (t == EventList.AddRow) {
      if (event.getData() instanceof Integer) {
        onAddRow((Integer) event.getData());
      }
    } else if (t == EventList.ViewPopUpDetail) {
      if (event.getData() instanceof ArrayList<?>) {
        onViewPopUpDetail((ArrayList<BaseModelData>) event.getData());
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
    } else if (t == EventList.GotPlugins) {
      updatePluginsMenu((ArrayList<Plugin>) event.getData());
    } else if (t == EventList.ViewPlugin) {
      viewPlugin((String) event.getData());
      /*
       * Altri eventi
       */
    }
  }

  private void viewPlugin(String data) {
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

  private void updatePluginsMenu(ArrayList<Plugin> plugins) {
    if (plugins != null && plugins.size() > 0) {
      JardinTabItem item = getItemByResultsetId(plugins.get(0).getRsId());
      JardinGridToolBar jgtb = item.getToolbar();
      Menu menuPlugins = new Menu();

      for (final Plugin plugin : plugins) {
        MenuItem mi = new MenuItem(plugin.getPluginName());

        mi.addSelectionListener(new SelectionListener() {
          @Override
          public void componentSelected(ComponentEvent ce) {
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

  private void onShowAllColumns(int resultset) {
    JardinTabItem item = getItemByResultsetId(resultset);
    item.getGrid().showAllColumns();
  }

  private void onAddRow(int resultset) {
    JardinTabItem item = getItemByResultsetId(resultset);
    item.getGrid().addRow();
  }

  private void onViewPopUpDetail(ArrayList<BaseModelData> infoToView) {
    BaseModelData data = infoToView.get(0);
    Integer rsId = data.get("RSID");
    JardinTabItem item = getItemByResultsetId(rsId);
    item.getGrid().viewDetailPopUp(infoToView);
  }

  private void initUI() {
//    viewport = new Viewport();
//    viewport.setLayout(new RowLayout(Orientation.VERTICAL));

//    createHeader();
//    createMain();

//    RootPanel.get().add(viewport);
	  guiCreate();
  }

//  private void createHeader() {
//    this.header = new HeaderArea(this.controller.getUser());
//    this.header.setId(JardinView.HEADER_AREA);
//
//    RowData rd = new RowData(1, 32);
//    rd.setMargins(new Margins(0));
//    this.viewport.add(this.header, rd);
//  }
//
//  private void createMain() {
//    this.main = new TabPanel();
//    this.main.setId(JardinView.MAIN_AREA);
//    this.main.setAnimScroll(true);
//    this.main.setTabScroll(true);
//
//    RowData rd = new RowData(1, 1);
//    this.viewport.add(this.main, rd);
//  }

	void guiCreate(){
		//dbSchema =  new DBSchema();
		mainDlp = new DockLayoutPanel(Unit.EM);
		this.header = new HeaderArea(this.controller.getUser());
		mainDlp.addNorth(header,3);		
//		mainTablesButtonBar = createAllTablesButtons();		
		mainTablesButtonBar = new FlowPanel();		
		List <ResultsetImproved> rsList = controller.getUser().getResultsets();
	    for (ResultsetImproved resultset : rsList) {
	    	createSingleMainTableButton(resultset);
	    	//Dispatcher.forwardEvent(EventList.CreateUI);
	    }	
		
		allTablesDlp = new DockLayoutPanel(Unit.EM);	
		allTablesDlp.addNorth(mainTablesButtonBar, 3);
		// MultiResGui multiResGui = new MultiResGui(dbSchema.mainResultSetSillyArrayList.get(0));
		vertMainResulSetsArea = new MainResultSetsArea(false);
		allTablesDlp.add(vertMainResulSetsArea);
		mainDlp.add(allTablesDlp);
		RootLayoutPanel.get().add(mainDlp);			
	}

	
	
	
	
	
	private void updateLayout(){
		vertMainResulSetsArea.updateGUI();
	}

		
	private void createSingleMainTableButton (ResultsetImproved resultset ){
		//FlowPanel fp = new FlowPanel();
	    
//		for (ResultsetImproved resultset : this.user.getResultsets()) {
	       // final Integer resultsetId = resultset.getId();
//		int allTablesNumber = dbSchema.mainResultSetSillyArrayList.size();
//		for (int i = 1; i <  allTablesNumber  + 1  ; i++) {
	        String resulSetAlias = resultset.getAlias();
	        Button srsB = new Button(resulSetAlias);
			srsB.setStylePrimaryName("unselectedButton");
			srsB.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					Object obj = event.getSource();
					Button sourceButton = (Button)obj;
					doClickOnMainTableButton(sourceButton);
					//System.out.println(buttonName);
					//String resultSetIAliaString = buttonName.substring(6, buttonName.length());	
				}
			  });
		mainTablesButtonBar.add(srsB);
//		}
//		return fp;
	}

	private void doClickOnMainTableButton(Button sourceButton){
		String buttonName = sourceButton.getText();
		try {
			//int resultSetId = Integer.parseInt(resultSetIdstring) ;	
			ResultsetImproved rs = controller.getUser().getResultsetFromAlias(buttonName);
			int resultSetId = rs.getId();
			boolean removed = false;
//			if ( horizMainResulSetsArea.isShownResultSetbyIncomingKeysRelativeResultSetId(resultSetId) ) {
//				horizMainResulSetsArea.removeResultSetbyIncomingKeysRelativeResultSetId(resultSetId);
//				removed = true;
//			} 
			if ( vertMainResulSetsArea.isShownResultSetbyIncomingKeysRelativeResultSetId(resultSetId) ) {
				vertMainResulSetsArea.removeResultSetbyIncomingKeysRelativeResultSetId(resultSetId);
				removed = true;
			} 						
			if ( !removed) {
				createNewMainResultSetGui(rs);	
				sourceButton.setStylePrimaryName("selectedButton");
			} else {
				sourceButton.setStylePrimaryName("unselectedButton");
			}
			vertMainResulSetsArea.updateGUI();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}					

	}
	
	private void createNewMainResultSetGui (ResultsetImproved rs){
//		if (mainResultSet.correlatedResultSetSillyArrayList.get(incomingKeysRelativeResultSetId -1).isLarge) {
//			ResultSetSilly rs = new ResultSetSilly( incomingKeysRelativeResultSetId,
//					mainResultSet.correlatedResultSetSillyArrayList.get(incomingKeysRelativeResultSetId -1 ).isLarge);
//			ResultSetGui newResultSetGui = new ResultSetGui(rs );			
//			horizCorrelatedResulSetsArea.insertNonExistentNewResultSetGui(newResultSetGui);
//		} else {
		// ResultSetSilly rs = dbSchema.mainResultSetSillyArrayList.get(resultSetId -1); 
			MultiResGui newMultiResultSetGui = new MultiResGui(rs );			
			vertMainResulSetsArea.insertNonExistentNewResultSetGui(newMultiResultSetGui);
//		}
		updateLayout();
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
    createSingleMainTableButton(resultset);
    //JardinTabItem item = new JardinTabItem(resultset);
    //item.setId(ITEM_PREFIX + resultsetId);
   // this.main.add(item);
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

  private void onSearch(SearchParams searchParams) {
    // TODO Auto-generated method stub
    JardinTabItem item = getItemByResultsetId(searchParams.getResultsetId());
    if (item != null) {
      if (item.getGrid() != null) {
        item.getGrid().setSearchparams(searchParams);
      }
    }
  }

  private void onSearched(SearchResult result) {
    JardinTabItem item = getItemByResultsetId(result.getResultsetId());

    if (item != null) {
      if (item.getGrid() != null) {
        /* Aggiornamento dello store della griglia del tabItem */
        // item.updateStore(this.getStore(searchParams, true));
        item.updateStore(result.getStore());
        // item.getGrid().setSearchparams(searchParams);
      }
    }
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
