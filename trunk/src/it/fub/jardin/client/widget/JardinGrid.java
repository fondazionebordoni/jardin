/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.User;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;

/**
 * @author gpantanetti
 * 
 */
public class JardinGrid extends Grid<BaseModelData> {

  private ResultsetImproved resultset;
  // private JardinRowEditor<BaseModelData> editor;
  private RowEditor<BaseModelData> editor;
  private SearchParams searchparams;
  private Integer userPreferenceHeaderId;

  // private ListStore<BaseModelData> store;

  public JardinGrid(final ListStore<BaseModelData> store, JardinColumnModel cm,
      final ResultsetImproved resultset) {
    super(store, cm);

    // this.store = store;
    this.resultset = resultset;

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

    this.addListener(Events.CellClick,
        new Listener<GridEvent<BaseModelData>>() {
          public void handleEvent(GridEvent<BaseModelData> be) {
            if (be.isControlKey()) {
              List<BaseModelData> selected =
                  be.getGrid().getSelectionModel().getSelectedItems();
              selected.add(store.getAt(be.getRowIndex()));
              be.getGrid().getSelectionModel().select(selected, true);
            } else
              be.getGrid().getSelectionModel().select(be.getRowIndex(), false);
          }

        });

    this.addListener(Events.Render, new Listener<GridEvent<BaseModelData>>() {

      @Override
      public void handleEvent(GridEvent<BaseModelData> be) {
        // TODO Auto-generated method stub
        ((JardinGridView) getView()).setGridHeader();
        ((JardinGridView) getView()).getHeader().setToolTip(
            "La colonna in grassetto sottolineato è la chiave primaria della tabella");
      }

    });

    this.editor = new RowEditor<BaseModelData>();
    this.editor.setClicksToEdit(ClicksToEdit.TWO);
    this.addPlugin(editor);

  }

  public ResultsetImproved getResultset() {
    return this.resultset;
  }

  public void setResultsetImproved(ResultsetImproved rs) {
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

  public void addRow() {
    new AddRowForm(this);
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
  public void setSearchparams(SearchParams searchparams) {
    this.searchparams = searchparams;
  }

  /**
   * @return the searchparams
   */
  public SearchParams getSearchparams() {
    return searchparams;
  }

  public void saveGridView(User user, String value) {
    JardinColumnModel columnModel = (JardinColumnModel) getColumnModel();
    ArrayList<Integer> headerFields = new ArrayList<Integer>();
    for (int i = 0; i < columnModel.getColumnCount(); i++) {
      if (!columnModel.getColumn(i).isHidden()) {
        headerFields.add(columnModel.getColumn(i).getFieldId());
      }
    }

    user.setResultsetHeaderPreferencesNoDefault(resultset.getId(),
        headerFields, value);
  }

  /**
   * @param userPreferenceHeaderId
   *          the userPreferenceHeaderId to set
   */
  public void setUserPreferenceHeaderId(Integer userPreferenceHeaderId) {
    this.userPreferenceHeaderId = userPreferenceHeaderId;
  }

  /**
   * @return the userPreferenceHeaderId
   */
  public Integer getUserPreferenceHeaderId() {
    return userPreferenceHeaderId;
  }

  public void updateGridHeader(List<Integer> result) {
    JardinColumnModel cm = (JardinColumnModel) this.getColumnModel();

    for (int i = 0; i < cm.getColumnCount(); i++) {
      if (isInList(cm.getColumn(i).getFieldId(), result)) {
        cm.setHidden(i, false);
      } else {
        cm.setHidden(i, true);
      }
    }

    ((JardinGridView) this.getView()).setGridHeader();

    Info.display("Informazione", "Impostata visualizzazione richiesta");
  }

  private Boolean isInList(Integer searched, List<Integer> list) {
    // boolean found = false;
    for (Integer i : list) {
      if (i.compareTo(searched) == 0) {
        return true;
      }
    }

    return false;
  }

}
