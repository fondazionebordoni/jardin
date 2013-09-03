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
import it.fub.jardin.client.tools.PopupOperations;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBar;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

public class JardinGrid extends Grid<BaseModelData> {

  private ResultsetImproved resultset;
  private RowEditor<BaseModelData> editor;
  private SearchParams searchparams;
  private Integer userPreferenceHeaderId;
  // POPUP MODALI:
  private JardinAddingPopUp jardinAddingPopUp;
  // PROVA
  private JardinFormPopup jardinFormPopUp;
  private MassiveUpdateDialog massiveUpdateDialog;
  private AddRowForm addRowForm;
  // al massimo uno alla volta per tipo
  private User user;
  private Menu m;
  private FormBinding formBinding;

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

//    onRowClickEvent();

    onCellDoubleClickEvent();

    onRenderEvent();
  }

  private void onRenderEvent() {
    // TODO Auto-generated method stub
    this.addListener(Events.Render, new Listener<GridEvent<BaseModelData>>() {

      public void handleEvent(final GridEvent<BaseModelData> be) {
        // TODO Auto-generated method stub
        ((JardinGridView) JardinGrid.this.getView()).setGridHeader();
        ((JardinGridView) JardinGrid.this.getView()).getHeader().setToolTip(
            "La colonna in grassetto sottolineato è la chiave primaria della tabella");
      }

    });
  }

  private void onCellDoubleClickEvent() {
    // TODO Auto-generated method stub
    this.addListener(Events.CellDoubleClick,
        new Listener<GridEvent<BaseModelData>>() {
          public void handleEvent(final GridEvent<BaseModelData> be) {
            // System.out.println("evento doppio clicl");
            final BaseModelData record =
                be.getGrid().getSelectionModel().getSelection().get(0);
            // setJardinEditorPopUp(new JardinEditorPopUp(resultset,
            // user.getUsername(), record));
            setJardinFormPopUp(new JardinFormPopup(resultset, record, null,
                user.getUsername(), PopupOperations.MODRECORD));
          }
        });
  }
  
  
//  private void onSelectionChange(){
//    this.getSelectionModel().addListener(Events.SelectionChange,
//        new Listener<SelectionChangedEvent<BaseModelData>>() {
//          public void handleEvent(final SelectionChangedEvent<BaseModelData> be) {
//            if (be.getSelection().size() > 0) {
//              BaseModelData record = be.getSelection().get(0);
//              for (final Field field : detail.getFields()) {
//
//                if (field instanceof TimeField) {
//                  Time defaultValue = new Time();
//                  if (record.get(field.getName()) != null) {
//
//                    int hours =
//                        Integer.parseInt(record.get(field.getName()).toString().substring(
//                            0, 2));
//                    int mins =
//                        Integer.parseInt(record.get(field.getName()).toString().substring(
//                            3, 5));
//                    defaultValue.setHour(hours);
//                    defaultValue.setMinutes(mins);
//
//                    ((TimeField) field).setValue(defaultValue);
//                    // System.out.println("valore time: "
//                    // + record.get(field.getName()));
//                  }
//
//                } else if (field instanceof SimpleComboBox) {
//                  if (record.get(field.getName()) instanceof Integer) {
//                    Integer defaultValue = record.get(field.getName());
//                    ArrayList<Integer> defval = new ArrayList<Integer>();
//                    if (defaultValue != null) {
//                      defval.add(defaultValue);
//                    }
//                    ((SimpleComboBox) field).add(defval);
//                  } else {
//                    String defaultValue = record.get(field.getName());
//                    ArrayList<String> defval = new ArrayList<String>();
//                    if (defaultValue != null) {
//                      defval.add(defaultValue);
//                    }
//                    ((SimpleComboBox) field).add(defval);
//                  }
//                }
//              }
//              this.formBinding.bind(record);
//            } else {
//              this.formBinding.unbind();
//            }
//          }
//        });
//  }
//  private void onRowClickEvent() {
//    // TODO Auto-generated method stub
//    this.addListener(Events.RowClick, new Listener<GridEvent<BaseModelData>>() {
//      public void handleEvent(final GridEvent<BaseModelData> be) {
//        if (be.isControlKey()) {
//          List<BaseModelData> selected =
//              be.getGrid().getSelectionModel().getSelectedItems();
//          selected.add(store.getAt(be.getRowIndex()));
//          be.getGrid().getSelectionModel().select(selected, true);
//          formBinding.bind(getSelectionModel().getSelectedItem());
//        } else {
//          be.getGrid().getSelectionModel().select(be.getRowIndex(), false);
//          BaseModelData record = be.getGrid().getSelectionModel().getSelectedItems().get(0);
//          for (final ColumnConfig column : be.getGrid().getColumnModel().getColumns()) {
//            
//            Field field = column.getEditor().getField();
//            
//            if (field instanceof TimeField) {
//              Time defaultValue = new Time();
//              if (record.get(field.getName()) != null) {
//
//                int hours =
//                    Integer.parseInt(record.get(field.getName()).toString().substring(
//                        0, 2));
//                int mins =
//                    Integer.parseInt(record.get(field.getName()).toString().substring(
//                        3, 5));
//                defaultValue.setHour(hours);
//                defaultValue.setMinutes(mins);
//
//                ((TimeField) field).setValue(defaultValue);
//                // System.out.println("valore time: "
//                // + record.get(field.getName()));
//              }
//
//            } else if (field instanceof SimpleComboBox) {   
//              if (record.get(field.getName()) instanceof Integer) {
//                Integer defaultValue = record.get(field.getName());
//                ArrayList<Integer> defval = new ArrayList<Integer>();
//                if (defaultValue != null) {
//                  defval.add(defaultValue);
//                }
//                ((SimpleComboBox) field).add(defval);
//              } else if (record.get(field.getName()) instanceof Integer) {
//                Integer defaultValue = record.get(field.getName());
//                ArrayList<Integer> defval = new ArrayList<Integer>();
//                if (defaultValue != null) {
//                  defval.add(defaultValue);
//                }
//                ((SimpleComboBox) field).add(defval);
//              } else {
//                String defaultValue = record.get(field.getName());
//                ArrayList<String> defval = new ArrayList<String>();
//                if (defaultValue != null) {
//                  defval.add(defaultValue);
//                }
//                ((SimpleComboBox) field).add(defval);
//              }
//            } else if (field instanceof ComboBox) {
//                //BOOLEANO
//              System.out.println("griglia: bind su boolean");
//                String defaultValue = record.get(field.getName());
//                SimpleBoolean sb = new SimpleBoolean();
//                if (defaultValue.compareToIgnoreCase("true") == 0) {
//                  sb.setValue(1);
//                  sb.setText("true");
//                } else {
//                  sb.setValue(0);
//                  sb.setText("false");
//                }
//                ListStore<SimpleBoolean> theStore = new ListStore<SimpleBoolean>();
//                theStore.add(SimpleBoolean.getSimpleBoobleans());
////                if (defaultValue != null) {
////                  theStore.add(sb);
////                }
//                ((ComboBox) field).setStore(theStore);
//                
//            }
//          }
//          
//          formBinding.bind(getSelectionModel().getSelectedItem());
//        }
//      }
//    });
//  }

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
            // System.out.println("fk " + fkinfo + " per campo " +
            // field.getName());
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

          if (!user.getResultsets().contains(fk.getInterestedResultset())) {
            user.addResultsetToList(fk.getInterestedResultset());
          }
          final String field = fk.getField();

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

                setJardinAddingPopUp(new JardinAddingPopUp(fk,
                    user.getUsername()));

              }
            });
            m.add(mitem);
          }
        }
      }
    });

    // this.addListener(Events.CellClick,
    // this.addListener(Events.RowClick,
    // new Listener<GridEvent<BaseModelData>>() {
    // public void handleEvent(final GridEvent<BaseModelData> be) {
    // if (be.isControlKey()) {
    // List<BaseModelData> selected =
    // be.getGrid().getSelectionModel().getSelectedItems();
    // selected.add(store.getAt(be.getRowIndex()));
    // be.getGrid().getSelectionModel().select(selected, true);
    // } else {
    // be.getGrid().getSelectionModel().select(be.getRowIndex(), false);
    // }
    // }
    // });

    // this.addListener(Events.Render, new Listener<GridEvent<BaseModelData>>()
    // {
    //
    // public void handleEvent(final GridEvent<BaseModelData> be) {
    // // TODO Auto-generated method stub
    // ((JardinGridView) JardinGrid.this.getView()).setGridHeader();
    // ((JardinGridView) JardinGrid.this.getView()).getHeader().setToolTip(
    // "La colonna in grassetto sottolineato è la chiave primaria della tabella");
    // }
    //
    // });

    // Set del valore dei SimpleComboBox
    // this.addListener(Events.CellDoubleClick,
    // new Listener<GridEvent<BaseModelData>>() {
    // public void handleEvent(final GridEvent<BaseModelData> be) {
    // // System.out.println("evento doppio clicl");
    // final BaseModelData record =
    // be.getGrid().getSelectionModel().getSelection().get(0);
    // // setJardinEditorPopUp(new JardinEditorPopUp(resultset,
    // user.getUsername(), record));
    // setJardinFormPopUp(new JardinFormPopup(resultset, record, null,
    // user.getUsername(), PopupOperations.MODRECORD));
    // }
    // });

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

  // public void viewDetailPopUp(final ArrayList<BaseModelData> data) {
  // this.setJardinDetailPopup(new JardinDetailPopUp(data));
  // }

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
  // public JardinDetailPopUp getJardinDetailPopup() {
  // return jardinDetailPopup;
  // }

  /**
   * @param jardinDetailPopup
   *          the jardinDetailPopup to set
   */
  // public void setJardinDetailPopup(JardinDetailPopUp jardinDetailPopup) {
  // this.jardinDetailPopup = jardinDetailPopup;
  // }

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

  /**
   * @return the jardinEditorPopUp
   */
  // public JardinEditorPopUp getJardinEditorPopUp() {
  // return jardinEditorPopUp;
  // }

  /**
   * @param jardinEditorPopUp
   *          the jardinEditorPopUp to set
   */
  // public void setJardinEditorPopUp(JardinEditorPopUp jardinEditorPopUp) {
  // this.jardinEditorPopUp = jardinEditorPopUp;
  // }

  /**
   * @return the jardinFormPopUp
   */
  public JardinFormPopup getJardinFormPopUp() {
    return jardinFormPopUp;
  }

  /**
   * @param jardinFormPopUp
   *          the jardinFormPopUp to set
   */
  public void setJardinFormPopUp(JardinFormPopup jardinFormPopUp) {
    this.jardinFormPopUp = jardinFormPopUp;
  }

  public void setFormBinding(FormBinding formBinding) {
    // TODO Auto-generated method stub
    this.formBinding = formBinding;
  }

}
