package it.fub.jardin.client.testLayoutGWTPKG;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.SearchResult;
import it.fub.jardin.client.widget.JardinColumnModel;
import it.fub.jardin.client.widget.JardinDetail;
import it.fub.jardin.client.widget.JardinGrid;
import it.fub.jardin.client.widget.JardinGridToolBar;
import it.fub.jardin.client.widget.JardinGridView;
import it.fub.jardin.client.widget.SearchAreaAdvanced;

import java.util.ArrayList;
import java.util.Random;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.binding.SimpleComboBoxFieldBinding;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.Record.RecordUpdate;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public class ResultSetGui extends DockLayoutPanel {
	FlowPanel toolbarGWT ;	
	SplitLayoutPanel gridAndDetailSplitPanel;
	SplitLayoutPanel navAndOthersSplitPanel;
	DockLayoutPanel jardinGridHisToolbarAndHisBottomDLP;
//	WaitPanel detailWaitPanel;
//	WaitPanel gridWaitPanel;
//	WaitPanel navWaitPanel;
	HTML detailWaitPanel ;
	HTML gridWaitPanel ;
	HTML navWaitPanel ;
    private JardinGrid grid;
	private JardinGridToolBar toolbar;
	private FormPanel detail;
	private FormBinding formbinding;
	boolean toolbarShown = true;;
	boolean navShown = false;
	boolean detailShown = false;
	boolean gridShown = true;
	ResultsetImproved resultSetImproved;
	ResultsetImproved mainParentResultSetImproved;
	boolean isRootResultSet;
	boolean isLarge;	
	Button gridButton;
	Button navButton;
	Button detailButton;
	Random random = new Random();
	SearchAreaAdvanced searchAreaAdvanced;
    private static final int PAGESIZE = 20;
	PagingToolBar pagingToolbar; // = new PagingToolBar(PAGESIZE);
	
	
	public ResultSetGui(ResultsetImproved resultSetSilly, ResultsetImproved mainParentResultSetImproved, boolean  itsRootResultSet){
		super(Unit.EM);	
		this.resultSetImproved = resultSetSilly;
		this.mainParentResultSetImproved = mainParentResultSetImproved;
		this.isRootResultSet = itsRootResultSet;
//		if (resultSetSilly.id == 0){
		if (itsRootResultSet){
			this.setStylePrimaryName("mainArea");
		}
		createAndPlaceObjects ();
	}

	public ResultsetImproved getResultSetImproved() {
		return resultSetImproved;
	}

	public ResultsetImproved getMainParentResultSetImproved() {
		return mainParentResultSetImproved;
	}

	void checkIfLarge(){
		if (resultSetImproved.getFields().size()> 3){
			this.isLarge = true;
		} else {
			this.isLarge = false;			
		}
	}

	private void createAndPlaceObjects (){
		removeAllObjects();
		createObjects ();
		placeObjects();
	}

	private void createObjects (){
		if (toolbarShown) {
			if (toolbarGWT == null) {
				toolbarGWT = createToolbarButtons();	
			} 		
		} else {
			toolbarGWT = null;
		}		
		if (detailShown) {
			if (detailWaitPanel == null){
				//detailHTML = new HTML ("detail - " + "Main");
				detailWaitPanel =  new HTML ("wait detail please") ;
				// detailWaitPanel =  new WaitPanel() ;
			}
		} else {
			detailWaitPanel = null;
		}
		if (navShown) {
			if (navWaitPanel == null){
				//navHTML = new HTML ("Nav - " + "Main");
				navWaitPanel = new HTML ("wait nav please");
				//navWaitPanel =  new WaitPanel();
			}
		} else {
			navWaitPanel = null;
		}
		if (gridShown){
			createCentralGrid2();
			if (jardinGridHisToolbarAndHisBottomDLP == null ){
				jardinGridHisToolbarAndHisBottomDLP = new DockLayoutPanel(Unit.EM);
			}			
			if (toolbar==null){
				toolbar = new JardinGridToolBar();
				toolbar.setGrid(grid);
			}
			if (pagingToolbar == null){
				pagingToolbar = new PagingToolBar(PAGESIZE);
			}
			if (gridWaitPanel == null){
				gridWaitPanel = new HTML ("wait grid please");
				//gridWaitPanel =  new WaitPanel();
			}
		} else {
			gridWaitPanel = null;
		}
		
	}

	private void removeAllObjects(){
		if (detailWaitPanel != null) {
			detailWaitPanel.removeFromParent();
		}		    	
		if (gridWaitPanel != null) {
			gridWaitPanel.removeFromParent();
		} 
		if (navWaitPanel != null) {
			navWaitPanel.removeFromParent();
		}
		if (toolbar != null) {
			toolbar.removeFromParent();
		} 
		if (grid != null) {
			grid.removeFromParent();
		} 
		if (detail != null) {
			detail.removeFromParent();
		}		    	
		if (jardinGridHisToolbarAndHisBottomDLP != null) {
			jardinGridHisToolbarAndHisBottomDLP.removeFromParent();
		} 		
		if (gridAndDetailSplitPanel != null) {
			gridAndDetailSplitPanel.removeFromParent();
		}		    	
		if (navAndOthersSplitPanel != null) {
			navAndOthersSplitPanel.removeFromParent();
		}		    	
		if (toolbarGWT != null) {
			toolbarGWT.removeFromParent();
		} 
		if (searchAreaAdvanced != null) {
			searchAreaAdvanced.removeFromParent();
		} 
		if (pagingToolbar != null){
			pagingToolbar.removeFromParent();
		}
	}
	
	private void placeObjects(){
//		if (isRootResultSet) {
//		}
		if (toolbarShown) {
			addNorth(toolbarGWT, 2);			
		} 		
		if (gridShown) {
			if (grid != null){
				jardinGridHisToolbarAndHisBottomDLP.addNorth(toolbar, 2);
				jardinGridHisToolbarAndHisBottomDLP.addSouth(pagingToolbar, 2);				
				jardinGridHisToolbarAndHisBottomDLP.add(grid);		
			}
			if ( (!detailShown) && (!navShown) ) {
				if (grid != null){
					add (jardinGridHisToolbarAndHisBottomDLP);
				} else {
					add (gridWaitPanel);
				}
			} else {		
				if (navShown) {
					navAndOthersSplitPanel = new SplitLayoutPanel();
//					if (resultSetSilly.id == 0){
					if (isRootResultSet){
						navAndOthersSplitPanel.setStylePrimaryName("mainNavAndOthersSplitPanel");
					}
					if (searchAreaAdvanced != null){
						navAndOthersSplitPanel.addWest(searchAreaAdvanced, 200);
					} else {
						navAndOthersSplitPanel.addWest (navWaitPanel,200);
					}
					if (detailShown) {					
						gridAndDetailSplitPanel = new SplitLayoutPanel();
						gridAndDetailSplitPanel.setStylePrimaryName("gridAndDetailSplitPanel");
 						//if (resultSetSilly.id == 0){
 	 					if (isRootResultSet){
							gridAndDetailSplitPanel.setStyleName("mainGridAndDetailSplitPanel");
						}
						if (detail != null){
							gridAndDetailSplitPanel.addSouth(detail, 50);
						} else {
							gridAndDetailSplitPanel.addSouth(detailWaitPanel, 50);
						}
						if (grid != null){
							gridAndDetailSplitPanel.add(jardinGridHisToolbarAndHisBottomDLP);
						} else {
							gridAndDetailSplitPanel.add (gridWaitPanel);
						}
						navAndOthersSplitPanel.add(gridAndDetailSplitPanel);
					} else {
						if (grid != null){
							navAndOthersSplitPanel.add(jardinGridHisToolbarAndHisBottomDLP);
						} else {
							navAndOthersSplitPanel.add(gridWaitPanel);
						}
					}
					add (navAndOthersSplitPanel);
				} else {					
						gridAndDetailSplitPanel = new SplitLayoutPanel();
						if (detail != null){
							gridAndDetailSplitPanel.addSouth(detail, 50);
						} else {
							gridAndDetailSplitPanel.addSouth(detailWaitPanel, 50);							
						}
						if (grid!= null){												
							gridAndDetailSplitPanel.add(jardinGridHisToolbarAndHisBottomDLP);
						} else {
							gridAndDetailSplitPanel.add(gridWaitPanel);							
						}
						
						add(gridAndDetailSplitPanel);
				}
			}
		}
	}
	
	private void createCentralGrid2(){	
	    if (grid == null){
			ListStore<BaseModelData> store = new ListStore<BaseModelData>();
		    JardinColumnModel cm = new JardinColumnModel(resultSetImproved);
		    this.grid = new JardinGrid(store, cm, resultSetImproved, mainParentResultSetImproved);
	    }	    
	}
	
	private void updateButtonState(Button button,  boolean isPressed){
		if (isPressed) {
			button.setStylePrimaryName("selectedButton");		
		} else {
			button.setStylePrimaryName("unselectedButton");
		}
	}

	private void updateGridNavAndDetailButtonsState(){
		updateButtonState (gridButton, gridShown);	
		updateButtonState (navButton, navShown);	
		updateButtonState (detailButton, detailShown);	
	}

	
	private FlowPanel createToolbarButtons(){
		FlowPanel fp = new FlowPanel();
		
		gridButton = new Button("Grid");
		gridButton.setStylePrimaryName("unselectedButton");
		gridButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				gridShown = !gridShown;
				createAndPlaceObjects();
				updateGridNavAndDetailButtonsState();
		    }
		  });
		fp.add(gridButton);	
		
		navButton  = new Button("Nav");			
		navButton.setStylePrimaryName("unselectedButton");
		navButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				navShown = !navShown;
				createAndPlaceObjects();
				updateGridNavAndDetailButtonsState();
		    }
		});
		fp.add(navButton);
		
		detailButton = new Button("Detail");
		detailButton.setStylePrimaryName("unselectedButton");
		detailButton.addClickHandler(new ClickHandler() {
		    public void onClick(ClickEvent event) {
				detailShown = !detailShown;
				createAndPlaceObjects();
				updateGridNavAndDetailButtonsState();
		    }
		  });
		fp.add(detailButton);	
		updateGridNavAndDetailButtonsState();
		
		TextBox textBox = new TextBox();
		//textBox.setText("Rs - " + this.resultSetImproved.getId());
		textBox.setText(this.resultSetImproved.getAlias());
		textBox.setWidth("100px");
		textBox.setEnabled(false);
		
		fp.add(textBox);
		if (isRootResultSet){
			fp.setStylePrimaryName("mainMultiRsToolbar");
		}
		return fp;
	}	

	  public void addSearchAreaAdvanced(SearchAreaAdvanced searchAreaAdvanced) {
		  this.searchAreaAdvanced = searchAreaAdvanced;
		  createAndPlaceObjects();
	  }

		  public JardinGridToolBar getToolbar() {
		    return this.toolbar;
		  }

		  public FormPanel getDetail() {
		    return this.detail;
		  }

		  public int getResultSetId() {
			    return this.resultSetImproved.getId();
			  }

		  public JardinGrid getGrid() {
		    return this.grid;
		  }

		  public void setGrid(JardinGrid grid) {
		    this.grid = grid;
//		    this.center_center.removeAll();
//		    JardinGridToolBar toolbar =
//		        (JardinGridToolBar) this.center_center.getTopComponent();
		    toolbar.setGrid(grid);
//		    this.center_center.add(grid);
		    createAndPlaceObjects();
		  }

		  public void addDetail(JardinDetail detail) {
		    this.detail = detail;
//		    this.center_south.expand();
//		    this.center_south.removeAll();
//		    this.center_south.add(detail);
//		    this.center_south.layout();
//		    this.center_south.collapse();

		    /* Binding con l'area di dettaglio */
		    this.formbinding = new FormBinding(this.detail, false);
		    for (Field<?> field : this.detail.getFields()) {
		      if (field instanceof SimpleComboBox<?>) {
		        this.formbinding.addFieldBinding(new SimpleComboBoxFieldBinding(
		            (SimpleComboBox<?>) field, field.getName()));
		      } else {
		        this.formbinding.addFieldBinding(new FieldBinding(field,
		            field.getName()));
		      }
		    }		    
		    createAndPlaceObjects();

		  }
		  public void updateStore(SearchResult result ) {
			  updateStore (result.getStore());
			  createAndPlaceObjects();
		  }

		  private void updateStore(final ListStore<BaseModelData> store) {

		    /* Loading dello store */
		    final PagingLoader<PagingLoadResult<BaseModelData>> loader =
		        (PagingLoader<PagingLoadResult<BaseModelData>>) store.getLoader();
		    loader.load(0, PAGESIZE);

		    /* Aggancio PaginToolbar */
		    pagingToolbar.bind(loader);
		    
		    //jardinGridHisToolbarAndHisBottomDLP.addSouth(bottomBarContentPanel, 4);
		    /* Riconfigurazione della griglia col nuovo store */
		    this.grid.reconfigure(store, this.grid.getColumnModel());

		    ((JardinGridView) this.grid.getView()).setGridHeader();

		    this.grid.getStore().addListener(Store.Update,
		        new Listener<StoreEvent<BaseModelData>>() {
		          public void handleEvent(StoreEvent<BaseModelData> be) {
		            if (be.getOperation() == RecordUpdate.EDIT) {
		              Dispatcher.forwardEvent(EventList.CommitChanges, grid);
		            }
		            formbinding.bind(grid.getSelectionModel().getSelectedItem());
		          }
		        });

		    
		    /* Binding con il nuovo store */
		    if (detail != null){
		    	formbinding.setStore(this.grid.getStore());
		    }

		    // this.grid.getSelectionModel().addListener(Events.SelectionChange,
		    // new Listener<SelectionChangedEvent<BaseModelData>>() {
		    // public void handleEvent(SelectionChangedEvent<BaseModelData> be) {
		    // if (be.getSelection().size() > 0) {
		    // BaseModelData record = be.getSelection().get(0);
		    // formbinding.bind(record);
		    // } else {
		    // formbinding.unbind();
		    // }
		    // }
		    // });
		    // ////////////////////////////////////////////////////////////////////////////////
		    this.grid.getSelectionModel().addListener(Events.SelectionChange,
		        new Listener<SelectionChangedEvent<BaseModelData>>() {
		          public void handleEvent(SelectionChangedEvent<BaseModelData> be) {
  	                if (detail!=null){
		            if (be.getSelection().size() > 0) {
		              BaseModelData record = be.getSelection().get(0);
		              for (final Field field : detail.getFields()) {
		                if (field instanceof SimpleComboBox) {
		                  if (record.get(field.getName()) instanceof Integer) {
		                    Integer defaultValue = record.get(field.getName());
		                    ArrayList<Integer> defval = new ArrayList<Integer>();
		                    defval.add(defaultValue);
		                    ((SimpleComboBox) field).add(defval);
		                  } else {
		                    String defaultValue = record.get(field.getName());
		                    ArrayList<String> defval = new ArrayList<String>();
		                    defval.add(defaultValue);
		                    ((SimpleComboBox) field).add(defval);
		                  }
		                }
		              }
	            	  formbinding.bind(record);
		            } else {
			           formbinding.unbind();
		            }
		            }
		          }
		        });
		    // //////////////////////////////////////////////////////////////////////////////////
		  }

//		  private class WaitPanel extends ContentPanel {
//		    public WaitPanel() {
//		      super();
//		      this.setStyleName("wait");
//		      this.setHeaderVisible(false);
//		      this.setBodyBorder(false);
//		    }
//		  }

		  public void updatePreference(HeaderPreferenceList data) {
		    toolbar.updatePreferenceButton(data);
		  }

		  public void setSearchParams(SearchParams searchParams){
			this.grid.setSearchparams(searchParams);
		  }
}
