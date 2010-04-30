/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.model.IncomingForeignKeyInformation;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;
import it.fub.jardin.client.model.User;
import it.fub.jardin.client.mvc.JardinController;

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
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuBar;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

/**
 * @author gpantanetti
 * 
 */
public class JardinGrid extends Grid<BaseModelData> {

  private ResultsetImproved resultset;
  private ResultsetImproved parentResultset;
  // private JardinRowEditor<BaseModelData> editor;
  private RowEditor<BaseModelData> editor;
  private SearchParams searchparams;
  private Integer userPreferenceHeaderId;

   private ListStore<BaseModelData> store;

  public JardinGrid(final ListStore<BaseModelData> store,
      final JardinColumnModel cm, 
      final ResultsetImproved resultset,
      final ResultsetImproved parentResultset ) {
    super(store, cm);

     this.store = store;
    this.resultset = resultset;
    this.parentResultset = parentResultset;
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

    final Menu m = new Menu();
    this.setContextMenu(m);

    this.addListener(Events.ContextMenu, new Listener<GridEvent>() {
      public void handleEvent(GridEvent be) {
        m.removeAll();
        m.setWidth(300);

        final MenuBar sep = new MenuBar();
        final ModelData selectedRow =
            (ModelData) be.getGrid().getSelectionModel().getSelection().get(0);

        User user =
            ((JardinController) Dispatcher.get().getControllers().get(0)).getUser();
        List<ResultsetImproved> resultsets = user.getResultsets();

        // Creazione del menu contestuale per le foreignkey e le foreignkey
        // entranti
        for (final ResultsetField field : resultset.getFields()) {
          if ((field.getForeignKey().compareToIgnoreCase("") != 0)) {
            String fkinfo = field.getForeignKey();
            String[] fksplitted = fkinfo.split("\\.");
            for (final ResultsetImproved rs : resultsets) {
              if (rs.getName().compareTo(fksplitted[0]) == 0) {
                final BaseModelData fk = new BaseModelData();
                fk.set("RSID", resultset.getId());
                // fk.set("TABLE", fksplitted[0]);
                fk.set("FK", fksplitted[1]);
                fk.set("VALUE", selectedRow.get(field.getName()));
                fk.set("RSLINKED", rs);
                MenuItem item =
                    new MenuItem("Visualizza record corrispondente in "
                        + rs.getAlias());

                m.add(item);

                item.addListener(Events.Select, new Listener<BaseEvent>() {
                  public void handleEvent(BaseEvent be) {
                    Dispatcher.forwardEvent(EventList.ViewPopUpDetail, fk);
                  }
                });
              }
            }
          }
        }

        m.add(sep);

        for (final IncomingForeignKeyInformation fk : resultset.getForeignKeyIn()) {
          final String linkedTable = fk.getLinkingTable();
          final String linkedField = fk.getLinkingField();
          final String field = fk.getField();
          // TODO sarebbe meglio spedire direttamente fk e non ricreare un nuovo
          // IncomingForeignKeyInformation
          final IncomingForeignKeyInformation fkIN =
              new IncomingForeignKeyInformation(linkedTable, linkedField, field);
          fkIN.setFieldValue("" + selectedRow.get(field));
          fkIN.setInterestedResultset(fk.getInterestedResultset());
          fkIN.setResultsetId(fk.getResultsetId());

          MenuItem mitem =
              new MenuItem("Visualizza corrispondenze in "
                  + fk.getInterestedResultset().getAlias());
          mitem.addListener(Events.Select, new Listener() {
            public void handleEvent(BaseEvent be) {

              // Log.debug(linkedTable + "." + linkedField + "->" + field + "="
              // + selectedRow.get(field));
              Dispatcher.forwardEvent(EventList.ViewLinkedTable, fkIN);

            }
          });
          m.add(mitem);
        }
      }
    });

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
            
			// result Set Correlati
			//recuperare LaPrimariKey della Riga
			final ModelData selectedRow = (ModelData) be.getGrid()
			.getSelectionModel().getSelection().get(0);

			User user = ((JardinController) Dispatcher.get()
			.getControllers().get(0)).getUser();
			List<ResultsetImproved> resultsets = user.getResultsets();
			for (final ResultsetImproved rs : resultsets) {
				for (final IncomingForeignKeyInformation fk : resultset
						.getForeignKeyIn()) {

					if (rs.getName().compareTo(fk.getLinkingTable()) == 0) {
						//System.out.println(rs.getAlias() + "(" + rs.getId()
						//		+ ")" + "->" + rs.getName() + "="
						//		+ fk.getLinkingTable());

						final String linkedTable = fk.getLinkingTable();
						final String linkedField = fk.getLinkingField();
						final String field = fk.getField();

						final IncomingForeignKeyInformation fkIN = new IncomingForeignKeyInformation(
								linkedTable, linkedField, field);
						fkIN.setFieldValue("" + selectedRow.get(field));

						fkIN.setResultsetId(resultset.getId());					
						fkIN.setInterestedResultset(rs);

//						fkIN.setParentResultsetId(yyyyyyyyyyyy);
//						fkIN.setInterestedParentResultset( xxxxxxxxxxxxxx   );
						
						
						Dispatcher.forwardEvent( EventList.UpdateCorrelatedResultset, fkIN);
					}
				}						
			}
			//SearchParams searchParams = new SearchParams(resultsetId);
			//jardinTabItem.setSearchOfOtherChildren(searchparams);

          }
        });       
    
    this.addListener(Events.Render, new Listener<GridEvent<BaseModelData>>() {

      public void handleEvent(GridEvent<BaseModelData> be) {
        // TODO Auto-generated method stub
        ((JardinGridView) getView()).setGridHeader();
        ((JardinGridView) getView()).getHeader().setToolTip(
            "La colonna in grassetto sottolineato è la chiave primaria della tabella");
      }

    });

    // Set del valore dei SimpleComboBox
    this.addListener(Events.CellDoubleClick,
        new Listener<GridEvent<BaseModelData>>() {
          public void handleEvent(GridEvent<BaseModelData> be) {
            final ModelData selectedRow =
                (ModelData) be.getGrid().getSelectionModel().getSelection().get(
                    0);
            for (final ResultsetField field : resultset.getFields()) {
              if (cm.getColumnById(field.getName()).getEditor().getField() instanceof SimpleComboBox) {

                if ((field.getType().compareToIgnoreCase("int") == 0)
                    || (field.getType().compareToIgnoreCase("real") == 0)) {
                  Integer defaultValue =
                      Integer.parseInt((String) selectedRow.get(field.getName()));
                  // cm.getColumnById(field.getName()).getEditor().getField().setRawValue(defaultValue.toString());
                  ((SimpleComboBox<Integer>) cm.getColumnById(field.getName()).getEditor().getField()).add(defaultValue);
                  ((SimpleComboBox<Integer>) cm.getColumnById(field.getName()).getEditor().getField()).setSimpleValue(defaultValue);
                } else {
                  String defaultValue = selectedRow.get(field.getName());
                  // cm.getColumnById(field.getName()).getEditor().getField().setRawValue(
                  // defaultValue);
                  ((SimpleComboBox<String>) cm.getColumnById(field.getName()).getEditor().getField()).add(defaultValue);
                  ((SimpleComboBox<String>) cm.getColumnById(field.getName()).getEditor().getField()).setSimpleValue(defaultValue);
                }
              }
            }
          }
        });

    this.editor = new RowEditor<BaseModelData>();
    this.editor.setClicksToEdit(ClicksToEdit.TWO);
    this.addPlugin(editor);

  }

  public ResultsetImproved getResultset() {
    return this.resultset;
  }
 
  public ResultsetImproved getParentResultset() {
	    return this.parentResultset;
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

  public void viewDetailPopUp(ArrayList<BaseModelData> data) {
    new JardinDetailPopUp(data);
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
