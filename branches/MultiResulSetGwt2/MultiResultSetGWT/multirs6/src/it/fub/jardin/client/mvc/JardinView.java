/**
 * 
 */
package it.fub.jardin.client.mvc;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.Jardin;
import it.fub.jardin.client.ManagerServiceAsync;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.User;
import it.fub.jardin.client.widget.HeaderArea;
import it.fub.jardin.client.widget.JardinColumnModel;
import it.fub.jardin.client.widget.JardinDetail;
import it.fub.jardin.client.widget.JardinGrid;
import it.fub.jardin.client.widget.JardinMultiRsSingularCenter;
import it.fub.jardin.client.widget.JardinTabItem;
import it.fub.jardin.client.widget.LoginDialog;
import it.fub.jardin.client.widget.SearchAreaAdvanced;
import it.fub.jardin.client.widget.SearchAreaBase;
import java.util.ArrayList;
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
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
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
	private static final int MARGIN = 2;

	private JardinController controller;
	private Viewport viewport;
	// ContentPanel viewPortContentPanel;
	// private ContentPanel search;
	private TabPanel main;
	private HeaderArea header;
	private LoginDialog dialog;
	//int rootPanelWidth ;
	//int rootPanelHeight;
	
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
				message = "ErrorContentPanel viewPortContentPanele durante l'accesso";
			}
			loginError(message);
//		} else if (t == EventList.NewResultset) {
//			if (event.getData() instanceof Integer) {
//				newResultset((Integer) event.getData());
//			}
//		} else if (t == EventList.GotValuesOfFields) {
//			if (event.getData() instanceof Integer) {
//				gotValuesOfFields((Integer) event.getData());
//			}
		} else if (t == EventList.GotValuesOfForeignKeys) {
			if (event.getData() instanceof Integer) {
				gotValuesOfForeignKeys((Integer) event.getData());
			}
		}   else if (t == EventList.Search) {
			if (event.getData() instanceof SearchParams) {
				onSearch((SearchParams) event.getData());
			}
			/*
			 * ------------------------------------------------------------------
			 * ----- Gestione eventi della toolbar
			 * ------------------------------
			 * -----------------------------------------
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
			/*
			 * ------------------------------------------------------------------
			 * ----- Altri eventi (t == EventList.ViewPopUpDetail)
			 * --------------
			 * ---------------------------------------------------------
			 */
		}
	}

	private void onShowAllColumns(int resultset) {
//		JardinTabItem item = getItemByResultsetId(resultset);
//		item.getGrid(resultset).showAllColumns();
		for (JardinGrid currGrid : this.getGridListByResultSetId(resultset)){
			currGrid.showAllColumns();
		}		
	}

	private void onAddRow(int resultset) {
//		JardinTabItem item = getItemByResultsetId(resultset);
//		item.getGrid(resultset).addRow();
		for (JardinGrid currGrid : this.getGridListByResultSetId(resultset)){
			currGrid.addRow();
		}		
	}

	private void onViewPopUpDetail(ArrayList<BaseModelData> infoToView) {
		BaseModelData data = infoToView.get(0);
		//ResultsetImproved rsLinked = data.get("RSLINKED");
		// System.out.println(rs.getAlias()+"->"+rs.getId());
		Integer rsId = data.get("RSID");
		JardinTabItem item = getItemByResultsetId(rsId);
		item.getGridFromResultSetId(rsId).viewDetailPopUp(infoToView); 
	}

//	private void initUI() {
//		viewport = new Viewport();
//		viewport.setLayout(new RowLayout(Orientation.VERTICAL));
//
//		createHeader();
//		createMain();
//
//		RootPanel.get().add(viewport);
//		Dispatcher.forwardEvent(EventList.CreateUI);
//	}

	private void initUI() {
		viewport = new Viewport();
		//viewport.setLayout(new BorderLayout());
		viewport.setLayout(new RowLayout(Orientation.VERTICAL));
//		viewPortContentPanel = new ContentPanel();
//		viewPortContentPanel.setBorders(false);
		//viewPortContentPanel.setHeight(700);
		//RootPanel.get(). 
//		viewPortContentPanel.setHeaderVisible(false);
//		viewport.setAutoHeight(true);
//		viewport.setAutoWidth(true);
//		viewport.setHeight(700);
		//viewPortContentPanel.expand();
		//rootPanelHeight = viewPortContentPanel.getHeight();
//		viewPortContentPanel.setLayout(new BorderLayout());
		
		createHeader();
		createMain();
		//rootPanelWidth = RootPanel.get().getOffsetWidth();
		//rootPanelHeight = RootPanel.get().getOffsetHeight();
		//viewPortContentPanel.layout(); 	
//		viewport.add(viewPortContentPanel, new MarginData(0));
		RootPanel.get().add(viewport);
		this.createUI();
	}

	
	private void createHeader() {
		this.header = new HeaderArea(this.controller.getUser());
		this.header.setId(JardinView.HEADER_AREA);
		this.header.setHeight(32);
		//this.header.setWidth("100%");
		// this.header.setAutoHeight(true);
		this.header.setAutoWidth(true);
		
		// this.header.setIntStyleAttribute("height", 32);
		this.header.setStyleAttribute("height", "32px");
//		RowData rd = new RowData(1, 32);
//		rd.setMargins(new Margins(0));
		//this.viewport.add(this.header, rd);
//		ContentPanel cp1 = new ContentPanel();
//		cp1.add(this.header);
//		cp1.setHeaderVisible(false);
//		cp1.setBorders(true);		
		//this.viewPortContentPanel.add(cp1, rd);

//		BorderLayoutData borderLayoutData = new BorderLayoutData(LayoutRegion.NORTH);
//		borderLayoutData.setSplit(true);
		//borderLayoutData.setCollapsible(true);
//		borderLayoutData.setMargins(new Margins(MARGIN));
		//borderLayoutData.setMaxSize(32);
		//this.viewPortContentPanel.add(this.header, borderLayoutData);
//		this.viewport.add(this.header, borderLayoutData);
		this.viewport.add(this.header);
	}

	private void createMain() {
		this.main = new TabPanel();
		this.main.setId(JardinView.MAIN_AREA);
		this.main.setAnimScroll(true);
		this.main.setTabScroll(true);
//		this.main.setWidth("100%");
		//this.main.setHeight(500);
		//this.main.setHeight("100%");
		this.header.setAutoHeight(true);
		this.header.setAutoWidth(true);
		
		//this.main.setAutoHeight(true);		
//		ContentPanel cp2 = new ContentPanel();
//		cp2.add(this.main);
//		cp2.setHeaderVisible(false);
//		cp2.setBorders(true);
		//cp2.setHeight("100%");
		//cp2.expand();
//		RowData rd = new RowData(1, -1);
//		rd.setMargins(new Margins(1));
//		//rd.setHeight(600);
//		this.viewPortContentPanel.add(this.main, rd);
		
//		BorderLayoutData borderLayoutData = new BorderLayoutData(LayoutRegion.CENTER);
//		borderLayoutData.setSplit(false);
		//this.viewPortContentPanel.add(this.main, borderLayoutData);
//		this.viewport.add(this.main, borderLayoutData);
		this.viewport.add(this.main);
	}

	/**
	 * Restituisce il tabItem che rappresenta il resultset in base all'id o null
	 * se non esiste
	 * 
	 * @param resultsetId
	 *            l'id del resultset
	 * @return un TabItem o null se non esiste
	 */
	public synchronized JardinTabItem getItemByResultsetId(Integer resultsetId) {
		if (this.main.getItemByItemId(ITEM_PREFIX + resultsetId) instanceof JardinTabItem) {
			JardinTabItem item = (JardinTabItem) this.main
					.getItemByItemId(ITEM_PREFIX + resultsetId);
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

//	private void newResultset(Integer resultsetId) {
//		/* Prendi le proprietà del resultset in base all'id dall'utente */
//		ResultsetImproved resultset = controller.getUser().getResultsetFromId(
//				resultsetId);
//		JardinTabItem item = new JardinTabItem(resultset);
//		item.setId(ITEM_PREFIX + resultsetId);
//
//		this.main.add(item);
//		//item.createAllOtherChildren();
//	}

	void createUI (){	
		User user = this.controller.getUser();
		createAllTabItemsEmpty (user);
		createAllAdvancedSearchAreas(user);
		Dispatcher.forwardEvent(EventList.CreateUI);
	}
	
	void createAllTabItemsEmpty (User user){
		for (ResultsetImproved resultset : user.getResultsets()) {
			final Integer resultsetId = resultset.getId();
//			if ( (resultsetId == 9629299) || (resultsetId == 9629302) || (resultsetId == 9629306)) {// SOLO PER TEST DA ELIMINARE
				JardinTabItem item = new JardinTabItem(resultset);
				item.setId(ITEM_PREFIX + resultsetId);
				this.main.add(item);
			}
//		}
	}

	void createAllAdvancedSearchAreas (User user){
		for (ResultsetImproved resultset : user.getResultsets()) {
			final Integer resultsetId = resultset.getId();
//			if ( (resultsetId == 9629299) || (resultsetId == 9629302) || (resultsetId == 9629306)){ // SOLO PER TEST DA ELIMINARE 				
			//if ( (resultsetId == 9629299) || (resultsetId == 9629302) ){ // SOLO PER TEST DA ELIMINARE 				
				ArrayList<JardinMultiRsSingularCenter> jardinMultiRsSingularCenterListByResultSetId = this.getJardinMultiRsSingularCenterListByResultSetId(resultsetId); 
				for (JardinMultiRsSingularCenter jardinMultiRsSingularCenter : jardinMultiRsSingularCenterListByResultSetId){				
					/* Creazione dell'area di ricerca avanzata */
					SearchAreaAdvanced searchAreaAdvanced = new SearchAreaAdvanced(
							resultset);
					//JardinTabItem item = getItemByResultsetId(resultsetId);
					//if (item != null) {
						/* Aggiungere la ricerca avanzata al tabitem */
					jardinMultiRsSingularCenter.addSearchAreaAdvanced(searchAreaAdvanced);
					//} 
				}
//			}
		}
	}

	
//	private synchronized void gotValuesOfFields(Integer resultsetId) {
//		ResultsetImproved resultset = controller.getUser().getResultsetFromId(
//				resultsetId);
//		ArrayList<JardinMultiRsSingularCenter> jardinMultiRsSingularCenterListByResultSetId = this.getJardinMultiRsSingularCenterListByResultSetId(resultsetId); 
//		for (JardinMultiRsSingularCenter item : jardinMultiRsSingularCenterListByResultSetId){				
//			/* Creazione dell'area di ricerca avanzata */
//			SearchAreaAdvanced searchAreaAdvanced = new SearchAreaAdvanced(
//					resultset);
//			//JardinTabItem item = getItemByResultsetId(resultsetId);
//			if (item != null) {
//				/* Aggiungere la ricerca avanzata al tabitem */
//				item.addSearchAreaAdvanced(searchAreaAdvanced);
//			}
//		}
//	}

	private synchronized void gotValuesOfForeignKeys(Integer resultsetId) {
		ResultsetImproved resultset = controller.getUser().getResultsetFromId(
				resultsetId);

		ArrayList<JardinMultiRsSingularCenter> jardinMultiRsSingularCenterListByResultSetId = this.getJardinMultiRsSingularCenterListByResultSetId(resultsetId); 
		for (JardinMultiRsSingularCenter jardinMultiRsSingularCenter : jardinMultiRsSingularCenterListByResultSetId){			
			/* Creazione della griglia */
			ListStore<BaseModelData> store = new ListStore<BaseModelData>();
			JardinColumnModel cm = new JardinColumnModel(resultset);
			JardinGrid grid = new JardinGrid(store, cm, resultset);
	
			/* Creazione dell'area di ricerca semplice */
			SearchAreaBase searchAreaBase = new SearchAreaBase(resultset);
	
			/* Creazione del dettaglio */
			JardinDetail detail = new JardinDetail(resultset);
	
			/* Aggiungere la griglia al tabItem */
			jardinMultiRsSingularCenter.setGrid(grid);
	
			/* Aggiungere la ricerca semplice al tabItem */
			jardinMultiRsSingularCenter.addSearchAreaBase(searchAreaBase);
	
			/* Aggiungere il dettaglio al tabitem */
			jardinMultiRsSingularCenter.addDetail(detail);
			
			jardinMultiRsSingularCenter.layout();
			//item.collapseAdvSearchAndDetailArea();
			//item.layout();
	
			// Eseguo una ricerca per riempire la il resultset
	//		SearchParams searchParams = new SearchParams(resultsetId);
	//		List<BaseModelData> queryFieldList = new ArrayList<BaseModelData>();
	//		searchParams.setFieldsValuesList(queryFieldList);
	//		Dispatcher.forwardEvent(EventList.Search, searchParams);
		}
	}

//	private synchronized void gotForeignKeyInForATable(Integer resultsetId) {
//		// recuperiamo le fk entranti del resultset attuale
//		ResultsetImproved resultset = controller.getUser().getResultsetFromId(
//				resultsetId);
//
//		ArrayList<IncomingForeignKeyInformation> foreignKeyIn = new ArrayList<IncomingForeignKeyInformation>();
//		foreignKeyIn = resultset.getForeignKeyIn();
//		System.out.println("Foreign Key Entranti");
//		for (IncomingForeignKeyInformation fk : foreignKeyIn) {
//			System.out.println(fk.getField() + "<-" + fk.getLinkingTable()
//					+ "." + fk.getLinkingField());
//		}
//		JardinTabItem item = getItemByResultsetId(resultset.getId());
//		//item.createAllOtherChildren();
//	}
	
//	private void onGotExport(String url) {
//		Window.open(url, "Download", null);
//	}

	private void onSearch(SearchParams searchParams) {
		int resultSetId = searchParams.getResultsetId();
		//JardinTabItem item = getItemByResultsetId(resultSetId);
		ArrayList<JardinMultiRsSingularCenter> jardinMultiRsSingularCenterListByResultSetId = this.getJardinMultiRsSingularCenterListByResultSetId(resultSetId); 
		for (JardinMultiRsSingularCenter currJardinMultiRsSingularCenter : jardinMultiRsSingularCenterListByResultSetId){
			currJardinMultiRsSingularCenter.updateStore(this.getStore(searchParams));
			currJardinMultiRsSingularCenter.setSearchparams(searchParams);			
		}						
	}

	private ListStore<BaseModelData> getStore(final SearchParams searchParams) {

		final ManagerServiceAsync service = (ManagerServiceAsync) Registry
				.get(Jardin.SERVICE);

		RpcProxy<PagingLoadResult<BaseModelData>> proxy = new RpcProxy<PagingLoadResult<BaseModelData>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<BaseModelData>> callback) {
				service.getRecords((PagingLoadConfig) loadConfig, searchParams,
						callback);
			}
		};

		PagingLoader<PagingLoadResult<BaseModelData>> loader = new BasePagingLoader<PagingLoadResult<BaseModelData>>(
				proxy);
		loader.setRemoteSort(true);
		ListStore<BaseModelData> store = new ListStore<BaseModelData>(loader);
		return store;
	}

	private void onSaveGridView(int resultset) {
		final JardinGrid grid = getItemByResultsetId(resultset).getGridFromResultSetId(resultset);
		final MessageBox box = MessageBox.prompt("Nome",
				"Salva visualizzazione");
		box.addCallback(new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				grid.saveGridView(controller.getUser(), be.getValue());
			}
		});
	}

	private void updatePreferenceListMenu(HeaderPreferenceList data) {
		Integer resultSetId = data.getResultsetId();
		JardinTabItem item = getItemByResultsetId(resultSetId);
		if (item != null) {
			/* Aggiungere la ricerca avanzata al tabitem */
			item.updatePreference(data, resultSetId);
		}
	}  
	
	public ArrayList<JardinGrid> getGridListByResultSetId (int resultSetId){
		ArrayList<JardinGrid> gridList = new ArrayList<JardinGrid>() ;
		for (TabItem currentTabItem : main.getItems() ){
			JardinTabItem currJardinTabItem = (JardinTabItem) currentTabItem;
			JardinGrid foundGrid = currJardinTabItem.getGridFromResultSetId(resultSetId);
			gridList.add(foundGrid);
		}
		return gridList;
	}
	
	private ArrayList<JardinMultiRsSingularCenter> getJardinMultiRsSingularCenterListByResultSetId (int resultSetId){
		ArrayList<JardinMultiRsSingularCenter> JardinMultiRsSingularCenterList = new ArrayList<JardinMultiRsSingularCenter>() ;
		for (TabItem currentTabItem : main.getItems() ){
			JardinTabItem currJardinTabItem = (JardinTabItem) currentTabItem;
			JardinMultiRsSingularCenter foundJardinMultiRsSingularCenter = currJardinTabItem.findInterestedJardinMultiRsSingularCenter(resultSetId);
			if (foundJardinMultiRsSingularCenter != null) {
				JardinMultiRsSingularCenterList.add(foundJardinMultiRsSingularCenter);				
			}	
		}
		return JardinMultiRsSingularCenterList;
	}

//	private JardinTabItem getJardinTabItemOfAJardinMultiRsSingularCenter (JardinMultiRsSingularCenter jardinMultiRsSingularCenter){
//		for (TabItem currentTabItem : main.getItems() ){
//			JardinTabItem currJardinTabItem = (JardinTabItem) currentTabItem;
//			if (currJardinTabItem.isFather (jardinMultiRsSingularCenter)){
//				return currJardinTabItem;
//			}	
//		}
//		return null;
//	}

	
}
