package it.fub.jardin.client.widget;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.ResultsetImproved;

import java.util.ArrayList;

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
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;

/**
 * @author gpantanetti
 */
public class JardinTabItem extends TabItem {

  private static final int PAGESIZE = 20;
  private static final int MARGIN = 2;
  private ContentPanel main;
  private ContentPanel west;
  private ContentPanel center;
  private ContentPanel center_center;
  private ContentPanel center_south;
  private JardinGrid grid;
  private JardinGridToolBar toolbar;
  private FormPanel detail;
  private FormBinding formbinding;

  public JardinTabItem(ResultsetImproved resultset) {
    super(resultset.getAlias());

    this.setClosable(false);
    this.setLayout(new FitLayout());

    this.main = new ContentPanel(new BorderLayout());
    this.main.setHeaderVisible(false);
    this.main.setBorders(false);
    this.main.setFrame(false);
    this.main.setBodyBorder(false);
    this.add(this.main);

    this.createWest();
    this.createCenter();
  }

  private void createWest() {
    this.west = new ContentPanel(new FitLayout());
    this.west.setHeaderVisible(true);
    this.west.add(new WaitPanel());

    BorderLayoutData data = new BorderLayoutData(LayoutRegion.WEST, 340);
    data.setCollapsible(true);
    data.setSplit(false);
    data.setFloatable(false);
    data.setMargins(new Margins(MARGIN, 0, MARGIN, MARGIN));

    this.main.add(this.west, data);
  }

  private void createCenter() {
    this.center = new ContentPanel(new BorderLayout());
    this.center.setBodyBorder(false);
    this.center.setHeaderVisible(false);

    this.createDetail();
    this.createGrid();

    BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
    centerData.setMargins(new Margins(MARGIN));

    this.main.add(this.center, centerData);
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

  public void addSearchAreaAdvanced(SearchAreaAdvanced searchAreaAdvanced) {
    this.west.expand();
    this.west.removeAll();
    this.west.add(searchAreaAdvanced);
    this.west.layout();
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
    JardinGridToolBar toolbar =
        (JardinGridToolBar) this.center_center.getTopComponent();
    toolbar.setGrid(grid);
    this.center_center.add(grid);
  }

  public void addDetail(JardinDetail detail) {
    this.detail = detail;
    this.center_south.expand();
    this.center_south.removeAll();
    this.center_south.add(detail);
    this.center_south.layout();
    this.center_south.collapse();

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
  }

  public void updateStore(final ListStore<BaseModelData> store) {

    /* Loading dello store */
    final PagingLoader<PagingLoadResult<BaseModelData>> loader =
        (PagingLoader<PagingLoadResult<BaseModelData>>) store.getLoader();
    loader.load(0, PAGESIZE);

    /* Aggancio PaginToolbar */
    PagingToolBar bottomBar =
        (PagingToolBar) this.center_center.getBottomComponent();
    bottomBar.bind(loader);

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
    formbinding.setStore(this.grid.getStore());

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
        });
    // //////////////////////////////////////////////////////////////////////////////////
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
