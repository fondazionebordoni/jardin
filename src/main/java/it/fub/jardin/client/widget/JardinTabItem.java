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

package it.fub.jardin.client.widget;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.HeaderPreferenceList;
import it.fub.jardin.client.model.ResultsetImproved;

import java.util.ArrayList;
import java.util.Date;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.binding.SimpleComboBoxFieldBinding;
import com.extjs.gxt.ui.client.binding.TimeFieldBinding;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;

public class JardinTabItem extends TabItem {

  private static final int PAGESIZE = 20;
  private static final int MARGIN = 2;
  private final ContentPanel main;
  private ContentPanel west;
  private ContentPanel center;
  private ContentPanel center_center;
  private ContentPanel center_south;
  private JardinGrid grid;
  private JardinGridToolBar toolbar;
  private SearchAreaAdvanced searchAreaAdvanced;
  private FormPanel detail;
  private FormBinding formbinding;

  public JardinTabItem(final ResultsetImproved resultset) {
    super(resultset.getAlias());

    this.setClosable(true);
    this.setLayout(new FitLayout());

    this.main = new ContentPanel(new BorderLayout());
    this.main.setHeaderVisible(false);
    this.main.setBorders(false);
    this.main.setFrame(false);
    this.main.setBodyBorder(false);
    this.add(this.main);

    this.createWest();
    this.createCenter();

    // this.grid.getStore().addListener(Store.Update,
    // new Listener<StoreEvent<BaseModelData>>() {
    // public void handleEvent(final StoreEvent<BaseModelData> be) {
    // if (be.getOperation() == RecordUpdate.EDIT) {
    // JardinTabItem.this.formbinding.bind(JardinTabItem.this.grid.getSelectionModel().getSelectedItem());
    // System.out.println("tabitem:store modificato");
    // // Dispatcher.forwardEvent(EventList.CommitChanges,
    // // JardinTabItem.this.grid);
    // }
    // //
    // JardinTabItem.this.formbinding.bind(JardinTabItem.this.grid.getSelectionModel().getSelectedItem());
    // }
    // });
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
    this.toolbar = new JardinGridToolBar();
    this.center_center.setTopComponent(this.toolbar);
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
    this.center_south.setTitle("Dettaglio del record della griglia");
    // this.center_south.setLayoutOnChange(true);
    this.center_south.add(new WaitPanel());

    BorderLayoutData data = new BorderLayoutData(LayoutRegion.SOUTH, 300);
    data.setCollapsible(true);
    data.setSplit(true);
    data.setFloatable(false);
    data.setMargins(new Margins(MARGIN, 0, 0, 0));

    this.center.add(this.center_south, data);
  }

  public void addSearchAreaAdvanced(final SearchAreaAdvanced searchAreaAdvanced) {
    this.searchAreaAdvanced = searchAreaAdvanced;
    this.west.expand();
    this.west.removeAll();
    this.west.add(this.searchAreaAdvanced);
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

  public void addGrid(final JardinGrid grid) {
    this.grid = grid;
    this.center_center.removeAll();
    JardinGridToolBar toolbar =
        (JardinGridToolBar) this.center_center.getTopComponent();
    toolbar.setGrid(grid);
    this.center_center.add(grid);
  }

  public void addDetail(final JardinDetail detail) {
    this.detail = detail;
    this.center_south.expand();
    this.center_south.removeAll();
    this.center_south.add(detail);
    this.center_south.layout();
    this.center_south.collapse();

    /* Binding con l'area di dettaglio */
    // this.formbinding = new FormBinding(this.detail, false);
    this.grid.getSelectionModel().addListener(Events.SelectionChange,
        new Listener<SelectionChangedEvent<BaseModelData>>() {
          public void handleEvent(final SelectionChangedEvent<BaseModelData> be) {
            if (be.getSelection().size() > 0) {
              BaseModelData record = be.getSelection().get(0);
              for (final Field field : JardinTabItem.this.detail.getFields()) {

                if (field instanceof TimeField) {
                  Time defaultValue = new Time();
                  if (record.get(field.getName()) != null) {

                    int hours =
                        Integer.parseInt(record.get(field.getName()).toString().substring(
                            0, 2));
                    int mins =
                        Integer.parseInt(record.get(field.getName()).toString().substring(
                            3, 5));
                    defaultValue.setHour(hours);
                    defaultValue.setMinutes(mins);

                    ((TimeField) field).setValue(defaultValue);
                    // System.out.println("valore time: "
                    // + record.get(field.getName()));
                  }

                } else if (field instanceof SimpleComboBox) {
                  if (record.get(field.getName()) instanceof Integer) {
                    Integer defaultValue = record.get(field.getName());
                    ArrayList<Integer> defval = new ArrayList<Integer>();
                    if (defaultValue != null) {
                      defval.add(defaultValue);
                    }
                    ((SimpleComboBox) field).add(defval);
                  } else if (record.get(field.getName()) instanceof Boolean) {
                    Boolean defaultValue = record.get(field.getName());
                    ArrayList<Boolean> defval = new ArrayList<Boolean>();
                    if (defaultValue != null) {
                      defval.add(defaultValue);
                    }
                    ((SimpleComboBox) field).add(defval);
                  } else {
                    String defaultValue = record.get(field.getName());
                    ArrayList<String> defval = new ArrayList<String>();
                    if (defaultValue != null) {
                      defval.add(defaultValue);
                    }
                    ((SimpleComboBox) field).add(defval);
                  }
                }
                // else if (field instanceof ComboBox) {
                // // BOOLEANO
                // // System.out.println("tabitem: bind su boolean...valore: "
                // // + record.get(field.getName()));
                // String defaultValue = record.get(field.getName());
                // SimpleBoolean sb = new SimpleBoolean();
                // if (defaultValue.compareToIgnoreCase("1") == 0) {
                // sb.setValue(1);
                // sb.setName("true");
                // } else {
                // sb.setValue(0);
                // sb.setName("false");
                // }
                // ListStore<SimpleBoolean> theStore =
                // new ListStore<SimpleBoolean>();
                // theStore.add(FieldCreator.getSimpleBooleans());
                // ((ComboBox) field).setStore(theStore);
                // ((ComboBox) field).setValue(sb);
                //
                // }
              }
              JardinTabItem.this.formbinding.bind(record);
            } else {
              JardinTabItem.this.formbinding.unbind();
            }
          }
        });
    for (Field field : this.detail.getFields()) {

      field.addListener(Events.Blur, new Listener<BaseEvent>() {

        @Override
        public void handleEvent(BaseEvent be) {
          // TODO Auto-generated method stub
          Dispatcher.forwardEvent(EventList.CommitChanges,
              JardinTabItem.this.grid);
        }
      });

      // if (field instanceof CheckBox) {
      // // System.out.println("binding checkbox!!!");
      // this.formbinding.addFieldBinding(new FieldBinding((CheckBox) field,
      // field.getName()) {
      // @Override
      // protected Object onConvertModelValue(Object value) {
      // Boolean newValue = new Boolean(Boolean.TRUE);
      // if (value instanceof String) {
      // if (((String) value).compareToIgnoreCase("1") == 0
      // || ((String) value).compareToIgnoreCase("true") == 0) {
      // newValue = Boolean.TRUE;
      // } else
      // newValue = Boolean.FALSE;
      // } else if (value instanceof Integer) {
      // if (Integer.parseInt(value.toString()) == 1) {
      // newValue = Boolean.TRUE;
      // }
      // newValue = Boolean.FALSE;
      // }
      // return newValue;
      // }
      // });
      // } else
      if (field instanceof SimpleComboBox) {
        this.formbinding.addFieldBinding(new SimpleComboBoxFieldBinding(
            (SimpleComboBox) field, field.getName()));
      } else if (field instanceof TimeField) {

        this.formbinding.addFieldBinding(new TimeFieldBinding(
            (TimeField) field, field.getName()) {
          @Override
          protected Object onConvertModelValue(Object value) {
            // System.out.println("binding!");
            if (value == null) {
              return null;
            }

            Time time = new Time();
            if (value instanceof String) {
              int hours = Integer.parseInt(((String) value).substring(0, 2));
              int mins = Integer.parseInt(((String) value).substring(3, 5));
              // System.out.println("ore: " + hours + "; minuti: " + mins);
              time.setHour(hours);
              time.setMinutes(mins);
            }
            Date date = time.getDate();
            return ((TimeField) field).findModel(date);
          }
        });
      } else {
        this.formbinding.addFieldBinding(new FieldBinding(field,
            field.getName()));
      }
    }
  }

  public void updateStore(final ListStore<BaseModelData> store) {

    /* Loading dello store */
    // setStore(store);
    // this.grid.setCompleteSearchedStore(store);
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

    // this.grid.getStore().addListener(Store.Update,
    // new Listener<StoreEvent<BaseModelData>>() {
    // public void handleEvent(final StoreEvent<BaseModelData> be) {
    // if (be.getOperation() == RecordUpdate.EDIT) {
    // System.out.println("tabitem:store modificato");
    // Dispatcher.forwardEvent(EventList.CommitChanges,
    // JardinTabItem.this.grid);
    // }
    // JardinTabItem.this.formbinding.bind(JardinTabItem.this.grid.getSelectionModel().getSelectedItem());
    // }
    // });

    /* Binding con il nuovo store */
    this.formbinding.setStore(this.grid.getStore());

    // ////////////////////////////////////////////////////////////////////////////////
    // this.grid.getSelectionModel().addListener(Events.SelectionChange,
    // new Listener<SelectionChangedEvent<BaseModelData>>() {
    // public void handleEvent(final SelectionChangedEvent<BaseModelData> be) {
    // if (be.getSelection().size() > 0) {
    // BaseModelData record = be.getSelection().get(0);
    // for (final Field field : JardinTabItem.this.detail.getFields()) {
    //
    // if (field instanceof TimeField) {
    // Time defaultValue = new Time();
    // if (record.get(field.getName()) != null) {
    //
    // int hours =
    // Integer.parseInt(record.get(field.getName()).toString().substring(
    // 0, 2));
    // int mins =
    // Integer.parseInt(record.get(field.getName()).toString().substring(
    // 3, 5));
    // defaultValue.setHour(hours);
    // defaultValue.setMinutes(mins);
    //
    // ((TimeField) field).setValue(defaultValue);
    // // System.out.println("valore time: "
    // // + record.get(field.getName()));
    // }
    //
    // } else if (field instanceof SimpleComboBox) {
    // if (record.get(field.getName()) instanceof Integer) {
    // Integer defaultValue = record.get(field.getName());
    // ArrayList<Integer> defval = new ArrayList<Integer>();
    // if (defaultValue != null) {
    // defval.add(defaultValue);
    // }
    // ((SimpleComboBox) field).add(defval);
    // } else {
    // String defaultValue = record.get(field.getName());
    // ArrayList<String> defval = new ArrayList<String>();
    // if (defaultValue != null) {
    // defval.add(defaultValue);
    // }
    // ((SimpleComboBox) field).add(defval);
    // }
    // }
    // }
    // JardinTabItem.this.formbinding.bind(record);
    // } else {
    // JardinTabItem.this.formbinding.unbind();
    // }
    // }
    // });
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

  public void updatePreference(final HeaderPreferenceList data) {
    this.toolbar.updatePreferenceButton(data);
  }

  /**
   * @return the searchAreaAdvanced
   */
  public SearchAreaAdvanced getSearchAreaAdvanced() {
    return searchAreaAdvanced;
  }

  /**
   * @param searchAreaAdvanced
   *          the searchAreaAdvanced to set
   */
  public void setSearchAreaAdvanced(SearchAreaAdvanced searchAreaAdvanced) {
    this.searchAreaAdvanced = searchAreaAdvanced;
  }

  public void setFormBinding(FormBinding formBinding) {
    // TODO Auto-generated method stub
    this.formbinding = formBinding;
  }

}
