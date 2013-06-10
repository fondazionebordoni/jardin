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
import it.fub.jardin.client.model.IncomingForeignKeyInformation;
import it.fub.jardin.client.model.Resultset;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.User;
import it.fub.jardin.client.tools.FieldDataType;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBar;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

public class JardinGrid extends Grid<BaseModelData> {

  private ResultsetImproved resultset;
  // private JardinRowEditor<BaseModelData> editor;
  private RowEditor<BaseModelData> editor;
  private SearchParams searchparams;
  private Integer userPreferenceHeaderId;
  // POPUP MODALI:
  private JardinDetailPopUp jardinDetailPopup;
  private JardinAddingPopUp jardinAddingPopUp;
  private MassiveUpdateDialog massiveUpdateDialog;
  private AddRowForm addRowForm;
  // al massimo uno alla volta per tipo
  private User user;
  private Menu m;

  // private ListStore<BaseModelData> completeSearchedStore;

  public JardinGrid(ListStore<BaseModelData> store, JardinColumnModel cm,
      ResultsetImproved resultset, User user) {
    super(store, cm);

    this.resultset = resultset;
    this.user = user;

    this.m = new Menu();

    this.setBorders(false);
    this.setStripeRows(true);
    // this.setClicksToEdit(ClicksToEdit.TWO);
    this.setTrackMouseOver(true);
    if (GXT.isChrome) {
      this.setAutoWidth(true);
    }

    JardinGridView gw = new JardinGridView(resultset, cm);
    this.setView(gw);

    this.setLoadMask(true);

    addContextMenu();
  }

  private void addContextMenu() {

    this.setContextMenu(m);

    this.addListener(Events.ContextMenu, new Listener<GridEvent>() {
      public void handleEvent(final GridEvent be) {
        m.removeAll();
        m.setWidth(400);

        final MenuBar sep = new MenuBar();
        final ModelData selectedRow =
            (ModelData) be.getGrid().getSelectionModel().getSelection().get(0);

        List<Resultset> resultsetList = user.getResultsetList();

        // Creazione del menu contestuale per le foreignkey e le foreignkey
        // entranti
        for (final ResultsetField field : resultset.getFields()) {
          if ((field.getForeignKey().compareToIgnoreCase("") != 0)
              && field.getForeignKey() != null) {
            
            String fkinfo = field.getForeignKey();
//            System.out.println("fk " + fkinfo + " per campo " + field.getName());
            String[] fksplitted = fkinfo.split("\\.");
            // for (final ResultsetImproved rs : resultsets) {
            for (final Resultset rs : resultsetList) {
              if (rs.getName().compareTo(fksplitted[0]) == 0
                  && rs.getPermissions().isReadperm()) {
                final BaseModelData fk = new BaseModelData();
                fk.set("RSID", resultset.getId());
                // fk.set("TABLE", fksplitted[0]);
                fk.set("FK", fksplitted[1]);
                fk.set("VALUE", selectedRow.get(field.getName()));
                fk.set("RSLINKED", rs);
                fk.set("GID", user.getGid());
                // fk.set("RSLINKEDID", rs.getId());
                MenuItem item =
                    new MenuItem("Visualizza elemento corrispondente in "
                        + rs.getAlias());

                m.add(item);

                item.addListener(Events.Select, new Listener<BaseEvent>() {
                  public void handleEvent(final BaseEvent be) {
                    Dispatcher.forwardEvent(EventList.ViewPopUpDetail, fk);
                  }
                });
              }
            }
          }
        }

        m.add(sep);

        for (final IncomingForeignKeyInformation fk : resultset.getForeignKeyIn()) {
          // final String linkedTable = fk.getLinkingTable();
          // final String linkedField = fk.getLinkingField();
          if (!user.getResultsets().contains(fk.getInterestedResultset())) {
            user.addResultsetToList(fk.getInterestedResultset());
          }
          final String field = fk.getField();
          // TODO sarebbe meglio spedire direttamente fk e non ricreare un
          // nuovo

          fk.setFieldValue("" + selectedRow.get(field));

          MenuItem mitem =
              new MenuItem("Visualizza corrispondenze in "
                  + fk.getInterestedResultset().getAlias());
          mitem.addListener(Events.Select, new Listener() {
            public void handleEvent(final BaseEvent be) {

              Dispatcher.forwardEvent(EventList.ViewLinkedTable, fk);

            }
          });
          m.add(mitem);
        }

        m.add(sep);

        for (final IncomingForeignKeyInformation fk : resultset.getForeignKeyIn()) {
          final String field = fk.getField();

          if (!user.getResultsets().contains(fk.getInterestedResultset())) {
            user.addResultsetToList(fk.getInterestedResultset());
          }
          if (fk.getInterestedResultset().isInsert()) {

            // TODO sarebbe meglio spedire direttamente fk e non ricreare un
            // nuovo
            fk.setFieldValue("" + selectedRow.get(field));

            MenuItem mitem =
                new MenuItem("Aggiungi nuovo "
                    + fk.getInterestedResultset().getAlias() + " per questo "
                    + field + " " + selectedRow.get(field));
            mitem.addListener(Events.Select, new Listener() {
              public void handleEvent(final BaseEvent be) {

                setJardinAddingPopUp(new JardinAddingPopUp(fk, user.getUsername()));

              }
            });
            m.add(mitem);
          }
        }
      }
    });

    this.addListener(Events.CellClick,
        new Listener<GridEvent<BaseModelData>>() {
          public void handleEvent(final GridEvent<BaseModelData> be) {
            if (be.isControlKey()) {
              List<BaseModelData> selected =
                  be.getGrid().getSelectionModel().getSelectedItems();
              selected.add(store.getAt(be.getRowIndex()));
              be.getGrid().getSelectionModel().select(selected, true);
            } else {
              be.getGrid().getSelectionModel().select(be.getRowIndex(), false);
            }
          }
        });

    this.addListener(Events.Render, new Listener<GridEvent<BaseModelData>>() {

      public void handleEvent(final GridEvent<BaseModelData> be) {
        // TODO Auto-generated method stub
        ((JardinGridView) JardinGrid.this.getView()).setGridHeader();
        ((JardinGridView) JardinGrid.this.getView()).getHeader().setToolTip(
            "La colonna in grassetto sottolineato è la chiave primaria della tabella");
      }

    });

    // Set del valore dei SimpleComboBox
    this.addListener(Events.CellDoubleClick,
        new Listener<GridEvent<BaseModelData>>() {
          public void handleEvent(final GridEvent<BaseModelData> be) {
            final ModelData selectedRow =
                be.getGrid().getSelectionModel().getSelection().get(0);
            for (final ResultsetField field : resultset.getFields()) {
              if (JardinGrid.this.getColumnModel().getColumnById(
                  field.getName()).getEditor().getField() instanceof SimpleComboBox) {
                String fieldType = field.getSpecificType();
                if (fieldType.compareToIgnoreCase(FieldDataType.INT) == 0) {
                  Integer defaultValue =
                      Integer.parseInt(selectedRow.get(field.getName()).toString());
                  // cm.getColumnById(field.getName()).getEditor().getField().setRawValue(defaultValue.toString());
                  if (((SimpleComboBox<Integer>) cm.getColumnById(
                      field.getName()).getEditor().getField()).getStore().getCount() == 0) {
                    List<Integer> comboStore = new ArrayList<Integer>();
                    comboStore.add(defaultValue);
                    ((SimpleComboBox<Integer>) cm.getColumnById(field.getName()).getEditor().getField()).add(comboStore);
                  }
                  // ((SimpleComboBox<Integer>)
                  // cm.getColumnById(field.getName()).getEditor().getField()).add(defaultValue);
                  ((SimpleComboBox<Integer>) cm.getColumnById(field.getName()).getEditor().getField()).setSimpleValue(defaultValue);
                } else if (fieldType.compareToIgnoreCase(FieldDataType.FLOAT) == 0) {
                  Float defaultValue =
                      Float.parseFloat(selectedRow.get(field.getName()).toString());
                  if (((SimpleComboBox<Float>) cm.getColumnById(
                      field.getName()).getEditor().getField()).getStore().getCount() == 0) {
                    List<Float> comboStore = new ArrayList<Float>();
                    comboStore.add(defaultValue);
                    ((SimpleComboBox<Float>) cm.getColumnById(field.getName()).getEditor().getField()).add(comboStore);
                  }
                } else if (fieldType.compareToIgnoreCase(FieldDataType.DOUBLE) == 0) {
                  Double defaultValue =
                      Double.parseDouble(selectedRow.get(field.getName()).toString());
                  if (((SimpleComboBox<Double>) cm.getColumnById(
                      field.getName()).getEditor().getField()).getStore().getCount() == 0) {
                    List<Double> comboStore = new ArrayList<Double>();
                    comboStore.add(defaultValue);
                    ((SimpleComboBox<Double>) cm.getColumnById(field.getName()).getEditor().getField()).add(comboStore);
                  }
                } else if (field.getSpecificType().compareToIgnoreCase(FieldDataType.ENUM) == 0) {
                  String defaultValue = selectedRow.get(field.getName());
                  ((SimpleComboBox<String>) cm.getColumnById(field.getName()).getEditor().getField()).add(field.getFixedElements());
                  ((SimpleComboBox<String>) cm.getColumnById(field.getName()).getEditor().getField()).setSimpleValue(defaultValue);
                } else {
                  String defaultValue = selectedRow.get(field.getName());
                  // cm.getColumnById(field.getName()).getEditor().getField().setRawValue(
                  // defaultValue);
                  if (((SimpleComboBox<String>) cm.getColumnById(
                      field.getName()).getEditor().getField()).getStore().getCount() == 0) {
                    List<String> comboStore = new ArrayList<String>();
                    comboStore.add(defaultValue);
                    ((SimpleComboBox<String>) cm.getColumnById(field.getName()).getEditor().getField()).add(comboStore);
                  }
                  // ((SimpleComboBox<String>)
                  // cm.getColumnById(field.getName()).getEditor().getField()).add(defaultValue);
                  ((SimpleComboBox<String>) cm.getColumnById(field.getName()).getEditor().getField()).setSimpleValue(defaultValue);
                }
              }
            }
          }
        });

    this.editor = new RowEditor<BaseModelData>();
    this.editor.setClicksToEdit(ClicksToEdit.TWO);
    this.addPlugin(this.editor);

  }

  public ResultsetImproved getResultset() {
    return this.resultset;
  }

  public void setResultsetImproved(final ResultsetImproved rs) {
    this.resultset = rs;
  }

  // public void addRow() {
  //
  // JardinColumnModel cm = (JardinColumnModel) this.getColumnModel();
  // ResultsetImproved resultset = this.getResultset();
  //
  // BaseModelData item = new BaseModelData();
  //
  // List<ResultsetField> fl = resultset.getFields();
  //
  // for (int i = 0; i < cm.getColumnCount(); i++) {
  // ResultsetField f = fl.get(i);
  // if (f.getType().compareToIgnoreCase("timestamp") == 0
  // || f.getType().compareToIgnoreCase("datetime") == 0
  // || f.getType().compareToIgnoreCase("date") == 0 || f.getDefaultVAlue() ==
  // null) {
  // item.set(cm.getColumnId(i), null);
  // } else {
  // item.set(cm.getColumnId(i), f.getDefaultVAlue());
  // }
  // }
  //
  // editor.stopEditing(true);
  // this.store.insert(item, 0);
  // editor.startEditing(store.indexOf(item), false);
  // }

  // public void addRow() {
  // new AddRowForm(this.getResultset());
  // }

  public void viewDetailPopUp(final ArrayList<BaseModelData> data) {
    this.setJardinDetailPopup(new JardinDetailPopUp(data));
  }

  public void showAllColumns() {
    ColumnModel cm = this.getColumnModel();
    for (int i = 0; i < cm.getColumnCount(); i++) {
      cm.setHidden(i, false);
    }
  }

  /**
   * @param searchparams
   *          the searchparams to set
   */
  public void setSearchparams(final SearchParams searchparams) {
    this.searchparams = searchparams;
  }

  /**
   * @return the searchparams
   */
  public SearchParams getSearchparams() {
    return this.searchparams;
  }

  public void saveGridView(final User user, final String value) {
    JardinColumnModel columnModel = (JardinColumnModel) this.getColumnModel();
    ArrayList<Integer> headerFields = new ArrayList<Integer>();
    for (int i = 0; i < columnModel.getColumnCount(); i++) {
      if (!columnModel.getColumn(i).isHidden()) {
        headerFields.add(columnModel.getColumn(i).getFieldId());
      }
    }

    user.setResultsetHeaderPreferencesNoDefault(this.resultset.getId(),
        headerFields, value);
  }

  /**
   * @param userPreferenceHeaderId
   *          the userPreferenceHeaderId to set
   */
  public void setUserPreferenceHeaderId(final Integer userPreferenceHeaderId) {
    this.userPreferenceHeaderId = userPreferenceHeaderId;
  }

  /**
   * @return the userPreferenceHeaderId
   */
  public Integer getUserPreferenceHeaderId() {
    return this.userPreferenceHeaderId;
  }

  public void updateGridHeader(final List<Integer> result) {
    JardinColumnModel cm = (JardinColumnModel) this.getColumnModel();

    for (int i = 0; i < cm.getColumnCount(); i++) {
      if (this.isInList(cm.getColumn(i).getFieldId(), result)) {
        cm.setHidden(i, false);
      } else {
        cm.setHidden(i, true);
      }
    }

    ((JardinGridView) this.getView()).setGridHeader();

    Info.display("Informazione", "Impostata visualizzazione richiesta");
  }

  private Boolean isInList(final Integer searched, final List<Integer> list) {
    // boolean found = false;
    for (Integer i : list) {
      if (i.compareTo(searched) == 0) {
        return true;
      }
    }

    return false;
  }

  /**
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * @param user
   *          the user to set
   */
  public void setUser(User user) {
    this.user = user;
  }

  /**
   * @return the jardinDetailPopup
   */
  public JardinDetailPopUp getJardinDetailPopup() {
    return jardinDetailPopup;
  }

  /**
   * @param jardinDetailPopup
   *          the jardinDetailPopup to set
   */
  public void setJardinDetailPopup(JardinDetailPopUp jardinDetailPopup) {
    this.jardinDetailPopup = jardinDetailPopup;
  }

  /**
   * @return the jardinAddingPopUp
   */
  public JardinAddingPopUp getJardinAddingPopUp() {
    return jardinAddingPopUp;
  }

  /**
   * @param jardinAddingPopUp
   *          the jardinAddingPopUp to set
   */
  public void setJardinAddingPopUp(JardinAddingPopUp jardinAddingPopUp) {
    this.jardinAddingPopUp = jardinAddingPopUp;
  }

  /**
   * @return the massiveUpdateDialog
   */
  public MassiveUpdateDialog getMassiveUpdateDialog() {
    return massiveUpdateDialog;
  }

  /**
   * @param massiveUpdateDialog
   *          the massiveUpdateDialog to set
   */
  public void setMassiveUpdateDialog(MassiveUpdateDialog massiveUpdateDialog) {
    this.massiveUpdateDialog = massiveUpdateDialog;
  }

  /**
   * @return the addRowForm
   */
  public AddRowForm getAddRowForm() {
    return addRowForm;
  }

  /**
   * @param addRowForm
   *          the addRowForm to set
   */
  public void setAddRowForm(AddRowForm addRowForm) {
    this.addRowForm = addRowForm;
  }

  // /**
  // * @return the completeSearchedStore
  // */
  // public ListStore<BaseModelData> getCompleteSearchedStore() {
  // return completeSearchedStore;
  // }
  //
  // /**
  // * @param completeSearchedStore the completeSearchedStore to set
  // */
  // public void setCompleteSearchedStore(ListStore<BaseModelData>
  // completeSearchedStore) {
  // this.completeSearchedStore = completeSearchedStore;
  // }

}
