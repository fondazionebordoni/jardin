package it.fub.jardin.client.widget;

import org.apache.batik.ext.awt.image.spi.JDKRegistryEntry.MyImgObs;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.IncomingForeignKeyInformation;
import it.fub.jardin.client.model.ResultsetImproved; //import it.fub.jardin.client.widget.JardinTabItem.WaitPanel;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
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
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;

public class JardinMultiRsSingularCenter {
	private static final int PAGESIZE = 20;
	private static final int MARGIN = 2;
	//private ContentPanel singularRsContentPanel;
	private LayoutContainer fatherOfChildren;
	private ContentPanel main;
	private ContentPanel north;
	private ContentPanel west;
	private ContentPanel center;
	private ContentPanel center_center;
	private ContentPanel center_south;
	private JardinGrid grid;
	private JardinGridToolBar toolbar;
	private FormPanel detail;
	private FormBinding formbinding;
	private Integer resultsetId;
	ResultsetImproved resultSet;
	IncomingForeignKeyInformation foreignKey;

	

	public JardinMultiRsSingularCenter(ResultsetImproved resultSet,
			LayoutContainer fatherOfChildren, IncomingForeignKeyInformation foreignKey) {
		// todo creare titolo
		this.fatherOfChildren = fatherOfChildren;
		this.resultSet = resultSet;
		this.foreignKey = foreignKey;
		main = new ContentPanel(new BorderLayout());
		main.setVisible(true);
		main.setHeaderVisible(true);
		//main.setClosable(false);
		
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER, 40);
	    data.setCollapsible(true);
	    data.setSplit(true);
	    data.setFloatable(false);
	    data.setMargins(new Margins(MARGIN, MARGIN, MARGIN, MARGIN));
	    
	    fatherOfChildren.add(main, data);	
	    fatherOfChildren.layout();
	    fatherOfChildren.repaint();
		//main.setHeading("pluto : " + resultSet.getId() );

		// super(resultset.getAlias());
		// singularRsContentPanel = cp;
		// this.setClosable(false);
		// singularRsContentPanel.setLayout(new BorderLayout());
		// this.setLayoutOnChange(true);

		this.createNorth();
		this.createWest();
		this.createCenter();

		main.layout();

	}
	
	public void expand(){
		this.main.expand();
	}

	public void collapse(){
		this.main.collapse();
	}

	
	private void createNorth() {
		this.north = new ContentPanel(new FitLayout());
		this.north.setHeaderVisible(false);
		// this.north.setLayoutOnChange(true);
		this.north.add(new WaitPanel());

		BorderLayoutData data = new BorderLayoutData(LayoutRegion.NORTH, 40);
		data.setCollapsible(false);
		data.setSplit(false);
		data.setFloatable(false);
		data.setMargins(new Margins(MARGIN, MARGIN, 0, MARGIN));

		main.add(this.north, data);
	}

	private void createWest() {
		this.west = new ContentPanel(new FitLayout());
		this.west.setHeaderVisible(true);
		// this.west.setLayoutOnChange(true);
		this.west.add(new WaitPanel());

		BorderLayoutData data = new BorderLayoutData(LayoutRegion.WEST, 340);
		data.setCollapsible(true);
		data.setSplit(false);
		data.setFloatable(false);
		data.setMargins(new Margins(MARGIN, 0, MARGIN, MARGIN));

		main.add(this.west, data);
	}

	private void createCenter() {
		this.center = new ContentPanel(new BorderLayout());
		this.center.setBodyBorder(false);
		// this.center.setLayoutOnChange(true);
		this.center.setHeaderVisible(false);

		this.createDetail();
		this.createGrid();

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(MARGIN));

		main.add(this.center, centerData);
	}

	private void createGrid() {
		this.center_center = new ContentPanel(new FitLayout());
		this.center_center.setHeaderVisible(false);
		// this.center_center.setLayoutOnChange(true);
		this.center_center.add(new WaitPanel());
		toolbar = new JardinGridToolBar();
		this.center_center.setTopComponent(toolbar);
		this.center_center.setBottomComponent(new PagingToolBar(PAGESIZE));

		BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
		data.setCollapsible(false);
		data.setSplit(true);
		data.setFloatable(false);
		data.setMargins(new Margins(0));

		this.center.add(this.center_center, data);

	}

	private void createDetail() {
		this.center_south = new ContentPanel(new FitLayout());
		this.center_south.setHeaderVisible(true);
		this.center_south.setHeading("Dettaglio del record della griglia");
		// this.center_south.setLayoutOnChange(true);
		this.center_south.add(new WaitPanel());

		BorderLayoutData data = new BorderLayoutData(LayoutRegion.SOUTH, 300);
		data.setCollapsible(true);
		data.setSplit(true);
		data.setFloatable(false);
		data.setMargins(new Margins(MARGIN, 0, 0, 0));

		this.center.add(this.center_south, data);
	}

	public void addSearchAreaBase(SearchAreaBase searchAreaBase) {
		this.north.removeAll();
		this.north.add(searchAreaBase);
		this.north.layout();
	}

	public void addSearchAreaAdvanced(SearchAreaAdvanced searchAreaAdvanced) {
		this.west.expand();
		this.west.removeAll();
		this.west.add(searchAreaAdvanced);
		this.west.collapse();
	}

	public JardinGridToolBar getToolbar() {
		return this.toolbar;
	}

	public FormPanel getDetail() {
		return this.detail;
	}

	public JardinGrid getGrid() {
		return this.grid;
	}

	public void setGrid(JardinGrid grid) {
		this.grid = grid;
		this.center_center.removeAll();
		JardinGridToolBar toolbar = (JardinGridToolBar) this.center_center
				.getTopComponent();
		toolbar.setGrid(grid);
		this.center_center.add(grid);
	}

	public void addDetail(JardinDetail detail) {
		this.detail = detail;
		this.center_south.expand();
		this.center_south.removeAll();
		this.center_south.add(detail);
		this.center_south.collapse();
		// this.center_south.layout();

		/* Binding con l'area di dettaglio */
		this.formbinding = new FormBinding(this.detail, false);
		for (Field field : this.detail.getFields()) {
			if (field instanceof SimpleComboBox) {
				this.formbinding
						.addFieldBinding(new SimpleComboBoxFieldBinding(
								(SimpleComboBox) field, field.getName()));
			} else {
				this.formbinding.addFieldBinding(new FieldBinding(field, field
						.getName()));
			}
		}
	}

	public void updateStore(final ListStore<BaseModelData> store) {

		/* Loading dello store */
		final PagingLoader<PagingLoadResult<BaseModelData>> loader = (PagingLoader<PagingLoadResult<BaseModelData>>) store
				.getLoader();
		loader.load(0, PAGESIZE);

		/* Aggancio PaginToolbar */
		PagingToolBar bottomBar = (PagingToolBar) this.center_center
				.getBottomComponent();
		bottomBar.bind(loader);

		/* Riconfigurazione della griglia col nuovo store */
		this.grid.reconfigure(store, this.grid.getColumnModel());
		((JardinGridView) this.grid.getView()).setGridHeader();

		this.grid.getStore().addListener(Store.Update,
				new Listener<StoreEvent<BaseModelData>>() {
					public void handleEvent(StoreEvent<BaseModelData> be) {
						if (be.getOperation() == RecordUpdate.EDIT) {
							Dispatcher.forwardEvent(EventList.CommitChanges,
									grid);
						}
						formbinding.bind(grid.getSelectionModel()
								.getSelectedItem());
					}
				});

		/* Binding con il nuovo store */
		formbinding.setStore(this.grid.getStore());

		this.grid.getSelectionModel().addListener(Events.SelectionChange,
				new Listener<SelectionChangedEvent<BaseModelData>>() {
					public void handleEvent(
							SelectionChangedEvent<BaseModelData> be) {
						if (be.getSelection().size() > 0) {
							BaseModelData record = be.getSelection().get(0);
							formbinding.bind(record);
						} else {
							formbinding.unbind();
						}
					}
				});

	}

	private class WaitPanel extends ContentPanel {
		public WaitPanel() {
			super();
			this.setStyleName("wait");
			this.setHeaderVisible(false);
			this.setBodyBorder(false);
		}
	}

	public void updatePreference(HeaderPreferenceList data) {
		toolbar.updatePreferenceButton(data);
	}

}
