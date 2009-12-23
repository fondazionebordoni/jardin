/**
 * 
 */
package it.fub.jardin.client.widget;

import it.fub.jardin.client.EventList;
import it.fub.jardin.client.Jardin;
import it.fub.jardin.client.ManagerServiceAsync;
import it.fub.jardin.client.model.ResultsetField;
import it.fub.jardin.client.model.ResultsetImproved;
import it.fub.jardin.client.model.SearchParams;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.dev.asm.Label;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;

/**
 * @author mavellino
 */
public class JardinSelectColumnsForChartPopUp extends Window {

  final FormPanel formPanel;
  final Button button;
  // SearchParams searchData;
  ResultsetImproved resultset;

  private JardinGrid grid;

  /**
   * Create a new Detail Area for Impianti printing all available fields
   */
  public JardinSelectColumnsForChartPopUp(JardinGrid grid, final String ct) {

    this.grid = grid;

    /* Impostazione caratteristiche di Window */
    this.setMinHeight(210);
    // this.setMinWidth(1000);
    this.setPlain(true);
    this.setHeading("Selezione le colonne");
    this.setLayout(new FitLayout());

    this.resultset = (ResultsetImproved) grid.getResultset();

    /* Creazione FormPanel */
    formPanel = new FormPanel();
    formPanel.setBodyBorder(false);
    formPanel.setHeaderVisible(false);
    formPanel.setScrollMode(Scroll.AUTO);

    final ListBox lbTitle = new ListBox();
    lbTitle.setVisibleItemCount(1);
    lbTitle.setName("title");
    lbTitle.setTitle("Colonna titolo");

    final ListBox lbValue = new ListBox();
    lbValue.setVisibleItemCount(1);
    lbValue.setName("value");
    lbValue.setTitle("Colonna valore");

    /* Recupero le informazioni sui campi */
    BaseModelData fieldsInfo = new BaseModelData();
    for (ResultsetField field : resultset.getFields()) {
      fieldsInfo.set(field.getName(), field.getType());
      // System.out.println(field.getType());
    }

    ColumnModel cm = this.grid.getColumnModel();
    for (int i = 0; i < cm.getColumnCount(); i++) {
      if (!(cm.getColumn(i).isHidden())) {
        lbTitle.addItem(cm.getColumn(i).getId());
        if (fieldsInfo.get(cm.getColumn(i).getId()).toString().compareToIgnoreCase(
            "int") == 0
            || fieldsInfo.get(cm.getColumn(i).getId()).toString().compareToIgnoreCase(
                "real") == 0) {
          lbValue.addItem(cm.getColumn(i).getId());
        }
      }
    }
    formPanel.addText("Verranno visualizzate solo le prime 50 righe.<br /><br />");
    formPanel.addText("Colonna titolo:<br />");
    formPanel.add(lbTitle);
    formPanel.addText("<br />Colonna valore:<br />");
    formPanel.add(lbValue);
    formPanel.addText("<br />");
    button = new Button("Crea grafico");
    formPanel.add(button);

    button.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        ArrayList<String> dataToChart = new ArrayList<String>();
        dataToChart.add(ct);
        dataToChart.add("" + resultset.getId());
        dataToChart.add(lbTitle.getValue(lbTitle.getSelectedIndex()));
        dataToChart.add(lbValue.getValue(lbValue.getSelectedIndex()));
        Dispatcher.forwardEvent(EventList.ShowChart, dataToChart);
        //
        removeAll();
        hide();
      }
    });

    add(formPanel);

  }
}
